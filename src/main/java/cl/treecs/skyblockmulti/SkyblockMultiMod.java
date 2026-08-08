package cl.treecs.skyblockmulti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cl.treecs.skyblockmulti.compatibility.openpac.OpenPacCompat;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import net.minecraft.network.chat.Component;

public final class SkyblockMultiMod implements ModInitializer {
    public static final String MOD_ID = "skyblockmulti";
    public static final int DEFAULT_DISTANCE = 2048;
    public static final int MIN_DISTANCE = 256;
    public static final int MAX_DISTANCE = 100000;
    public static final int PLAYER_CAPACITY = 24;

    private static final Pattern DISTANCE_PATTERN = Pattern.compile("\\\"islandDistance\\\"\\s*:\\s*(-?\\d+)");
    private static final Pattern BONUS_CHEST_MODE_PATTERN = Pattern.compile("\\\"bonusChestMode\\\"\\s*:\\s*\\\"([a-z_]+)\\\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEGACY_BONUS_CHEST_ENABLED_PATTERN = Pattern.compile("\\\"bonusChestEnabled\\\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEGACY_BONUS_CHEST_TIER_PATTERN = Pattern.compile("\\\"bonusChestTier\\\"\\s*:\\s*\\\"([a-z_]+)\\\"", Pattern.CASE_INSENSITIVE);
    private static Path configPath;
    private static volatile Object activeServer;

    public enum TreeOption {
        OAK("oak", "Roble", 1),
        SPRUCE("spruce", "Abeto", 2),
        BIRCH("birch", "Abedul", 3),
        JUNGLE("jungle", "Jungla", 4),
        ACACIA("acacia", "Acacia", 5),
        CHERRY("cherry", "Cerezo", 6),
        MANGROVE("mangrove", "Manglar", 7),
        DARK_OAK("dark_oak", "Roble oscuro", 8),
        PALE_OAK("pale_oak", "Roble pálido", 9),
        AZALEA("azalea", "Azalea", 10),
        FLOWERING_AZALEA("flowering_azalea", "Azalea florecida", 11);

        private final String configKey;
        private final String displayName;
        private final int triggerValue;

        TreeOption(String configKey, String displayName, int triggerValue) {
            this.configKey = configKey;
            this.displayName = displayName;
            this.triggerValue = triggerValue;
        }

        public String configKey() {
            return configKey;
        }

        public String displayName() {
            return displayName;
        }

        public int triggerValue() {
            return triggerValue;
        }

        public String scoreHolder() {
            return "#tree_" + configKey;
        }
    }


    public enum BonusChestMode {
        EMPTY("empty", 0),
        BASIC("basic", 1),
        STANDARD("standard", 2),
        BEGINNER("beginner", 3);

        private final String configKey;
        private final int scoreValue;

        BonusChestMode(String configKey, int scoreValue) {
            this.configKey = configKey;
            this.scoreValue = scoreValue;
        }

        public String configKey() {
            return configKey;
        }

        public int scoreValue() {
            return scoreValue;
        }

        public BonusChestMode next() {
            return switch (this) {
                case EMPTY -> BASIC;
                case BASIC -> STANDARD;
                case STANDARD -> BEGINNER;
                case BEGINNER -> EMPTY;
            };
        }

        public static BonusChestMode fromConfig(String value) {
            if (value != null) {
                for (BonusChestMode mode : values()) {
                    if (mode.configKey.equalsIgnoreCase(value.trim())) return mode;
                }
            }
            return STANDARD;
        }
    }

    @Override
    public void onInitialize() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("skyblockmulti.json");
        ensureConfigExists();
	OpenPacCompat.initialize();
	
	CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
    dispatcher.register(
            literal("skyblockmulti")
                    .then(
                            literal("openpac_claim")
                                    .then(
                                            argument("x", IntegerArgumentType.integer())
                                                    .then(
                                                            argument("z", IntegerArgumentType.integer())
                                                                    .executes(context -> {
                                                                        if (!OpenPacCompat.isInstalled()) {
                                                                            return 0;
                                                                        }

                                                                        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                                                                            return 0;
                                                                        }

                                                                        int x = IntegerArgumentType.getInteger(context, "x");
                                                                        int z = IntegerArgumentType.getInteger(context, "z");

                                                                        OpenPacCompat.claimInitialIsland(player, x, z);

                                                                        return 1;
                                                                    })
                                                    )
                                    )
                    )
    );
});
	
ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
    var player = handler.player;

    // Mensaje informativo mostrado una sola vez por jugador.
    if (!player.getTags().contains("skyblock_openpac_info_v1")) {

        if (OpenPacCompat.isInstalled()) {
            player.sendSystemMessage(
                    Component.literal(
                            "[SkyblockMulti] Open Parties and Claims detectado. " +
                            "Para jugar en equipo puedes crear una party o aceptar una invitación " +
                            "y compartir la isla del propietario. " +
                            "Al abandonar una party, SkyblockMulti puede aplicar una dificultad mínima " +
                            "más exigente según la configuración del mundo."
                    )
            );
        } else {
            player.sendSystemMessage(
                    Component.literal(
                            "[SkyblockMulti] Modo individual activo. " +
                            "Para jugar en equipo puedes instalar Open Parties and Claims. " +
                            "Este mod es opcional y SkyblockMulti funciona normalmente sin él."
                    )
            );
        }

        player.addTag("skyblock_openpac_info_v1");
    }

    // Sin OpenPAC no debemos intentar consultar su API.
    if (!OpenPacCompat.isInstalled()) {
        return;
    }

    OpenPacCompat.PartyInfo partyInfo =
            OpenPacCompat.getPartyInfo(player);

    if (!partyInfo.inParty()) {
        System.out.println(
                "[SkyblockMulti] OpenPAC: "
                        + player.getGameProfile().name()
                        + " no pertenece a una party."
        );
        return;
    }

    if (partyInfo.owner()) {
        System.out.println(
                "[SkyblockMulti] OpenPAC: "
                        + player.getGameProfile().name()
                        + " es owner de la party "
                        + partyInfo.partyId()
                        + "."
        );
    } else {
        System.out.println(
                "[SkyblockMulti] OpenPAC: "
                        + player.getGameProfile().name()
                        + " pertenece a la party "
                        + partyInfo.partyId()
                        + ". Owner: "
                        + partyInfo.ownerName()
                        + "."
        );
    }
});

        registerServerStartedEvent();
        System.out.println("[SkyblockMulti] Mod 0.1.1-beta inicializado. Configuración: " + configPath);
    }

    public static int getConfiguredDistance() {
        return loadConfig().distance();
    }

    public static EnumMap<TreeOption, Boolean> getConfiguredTreeStates() {
        return new EnumMap<>(loadConfig().trees());
    }

    public static int getPlayerCapacity(int distance) {
        return PLAYER_CAPACITY;
    }

    public static BonusChestMode getConfiguredBonusChestMode() {
        return loadConfig().bonusChestMode();
    }

    public static boolean saveConfiguredDistance(int requestedDistance) {
        ConfigData current = loadConfig();
        return saveConfiguration(requestedDistance, current.trees(), current.bonusChestMode());
    }

    public static boolean saveConfiguration(int requestedDistance, Map<TreeOption, Boolean> requestedTrees) {
        ConfigData current = loadConfig();
        return saveConfiguration(requestedDistance, requestedTrees, current.bonusChestMode());
    }

    public static boolean saveConfiguration(int requestedDistance, Map<TreeOption, Boolean> requestedTrees,
                                            BonusChestMode bonusChestMode) {
        ensureConfigReady();
        int normalized = normalizeDistance(requestedDistance);
        EnumMap<TreeOption, Boolean> trees = normalizeTrees(requestedTrees);
        BonusChestMode safeMode = bonusChestMode == null ? BonusChestMode.STANDARD : bonusChestMode;
        try {
            writeConfig(normalized, trees, safeMode);
            System.out.println("[SkyblockMulti] Configuración guardada: distancia=" + normalized
                    + ", árboles=" + countEnabled(trees)
                    + ", cofre=" + safeMode.configKey());
            Object server = activeServer;
            if (server != null) {
                applyConfiguration(server);
            }
            return true;
        } catch (IOException e) {
            System.err.println("[SkyblockMulti] No se pudo guardar la configuración: " + e.getMessage());
            return false;
        }
    }

    public static int normalizeDistance(int value) {
        value = Math.max(MIN_DISTANCE, Math.min(MAX_DISTANCE, value));
        return Math.max(MIN_DISTANCE, Math.round(value / 16.0f) * 16);
    }

    private static void ensureConfigReady() {
        if (configPath == null) {
            configPath = FabricLoader.getInstance().getConfigDir().resolve("skyblockmulti.json");
        }
        ensureConfigExists();
    }

    private static void ensureConfigExists() {
        if (configPath == null) return;
        try {
            Files.createDirectories(configPath.getParent());
            if (Files.notExists(configPath)) {
                writeConfig(DEFAULT_DISTANCE, defaultTrees(), BonusChestMode.STANDARD);
            }
        } catch (IOException e) {
            System.err.println("[SkyblockMulti] No se pudo crear el archivo de configuración: " + e.getMessage());
        }
    }

    private static void writeConfig(int distance, Map<TreeOption, Boolean> trees,
                                    BonusChestMode bonusChestMode) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"islandDistance\": ").append(distance).append(",\n");
        json.append("  \"bonusChestMode\": \"").append(bonusChestMode.configKey()).append("\",\n");
        json.append("  \"enabledTrees\": {\n");
        TreeOption[] values = TreeOption.values();
        for (int i = 0; i < values.length; i++) {
            TreeOption tree = values[i];
            json.append("    \"").append(tree.configKey()).append("\": ")
                    .append(Boolean.TRUE.equals(trees.get(tree)));
            if (i + 1 < values.length) json.append(',');
            json.append("\n");
        }
        json.append("  }\n");
        json.append("}\n");
        Files.writeString(configPath, json.toString(), StandardCharsets.UTF_8);
    }

    private static ConfigData loadConfig() {
        ensureConfigReady();
        int distance = DEFAULT_DISTANCE;
        EnumMap<TreeOption, Boolean> trees = defaultTrees();
        BonusChestMode bonusChestMode = BonusChestMode.STANDARD;
        try {
            String raw = Files.readString(configPath, StandardCharsets.UTF_8);
            Matcher matcher = DISTANCE_PATTERN.matcher(raw);
            if (matcher.find()) {
                distance = Integer.parseInt(matcher.group(1));
            }

            Matcher modeMatcher = BONUS_CHEST_MODE_PATTERN.matcher(raw);
            if (modeMatcher.find()) {
                bonusChestMode = BonusChestMode.fromConfig(modeMatcher.group(1));
            } else {
                // Migración transparente desde la configuración anterior.
                boolean legacyEnabled = true;
                Matcher enabledMatcher = LEGACY_BONUS_CHEST_ENABLED_PATTERN.matcher(raw);
                if (enabledMatcher.find()) {
                    legacyEnabled = Boolean.parseBoolean(enabledMatcher.group(1));
                }
                if (!legacyEnabled) {
                    bonusChestMode = BonusChestMode.EMPTY;
                } else {
                    Matcher tierMatcher = LEGACY_BONUS_CHEST_TIER_PATTERN.matcher(raw);
                    if (tierMatcher.find()) {
                        bonusChestMode = BonusChestMode.fromConfig(tierMatcher.group(1));
                    }
                }
            }

            for (TreeOption tree : TreeOption.values()) {
                Pattern pattern = Pattern.compile("\\\"" + Pattern.quote(tree.configKey()) + "\\\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
                Matcher treeMatcher = pattern.matcher(raw);
                if (treeMatcher.find()) {
                    trees.put(tree, Boolean.parseBoolean(treeMatcher.group(1)));
                }
            }
        } catch (Exception e) {
            System.err.println("[SkyblockMulti] Configuración inválida; se usarán valores seguros: " + e.getMessage());
        }
        return new ConfigData(normalizeDistance(distance), normalizeTrees(trees), bonusChestMode);
    }

    private static EnumMap<TreeOption, Boolean> defaultTrees() {
        EnumMap<TreeOption, Boolean> trees = new EnumMap<>(TreeOption.class);
        for (TreeOption tree : TreeOption.values()) trees.put(tree, true);
        return trees;
    }

    private static EnumMap<TreeOption, Boolean> normalizeTrees(Map<TreeOption, Boolean> requested) {
        EnumMap<TreeOption, Boolean> trees = new EnumMap<>(TreeOption.class);
        for (TreeOption tree : TreeOption.values()) {
            trees.put(tree, requested == null || !requested.containsKey(tree) || Boolean.TRUE.equals(requested.get(tree)));
        }
        if (countEnabled(trees) == 0) {
            trees.put(TreeOption.OAK, true);
        }
        return trees;
    }

    private static int countEnabled(Map<TreeOption, Boolean> trees) {
        int count = 0;
        for (TreeOption tree : TreeOption.values()) {
            if (Boolean.TRUE.equals(trees.get(tree))) count++;
        }
        return count;
    }

    private static void registerServerStartedEvent() {
        try {
            Class<?> eventsClass = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents");
            Class<?> callbackClass = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents$ServerStarted");
            Class<?> publicEventClass = Class.forName("net.fabricmc.fabric.api.event.Event");

            Object event = eventsClass.getField("SERVER_STARTED").get(null);
            Object callback = java.lang.reflect.Proxy.newProxyInstance(
                    callbackClass.getClassLoader(),
                    new Class<?>[]{callbackClass},
                    (proxy, method, args) -> {
                        if (method.getDeclaringClass() == Object.class) {
                            return switch (method.getName()) {
                                case "toString" -> "SkyblockMultiServerStartedCallback";
                                case "hashCode" -> System.identityHashCode(proxy);
                                case "equals" -> args != null && args.length == 1 && proxy == args[0];
                                default -> null;
                            };
                        }
                        if (method.getName().equals("onServerStarted") && args != null && args.length == 1) {
                            activeServer = args[0];
                            applyConfiguration(args[0]);
                        }
                        return null;
                    }
            );

            Method register = publicEventClass.getMethod("register", Object.class);
            register.invoke(event, callback);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No fue posible registrar el evento de inicio con Fabric API.", e);
        }
    }

    private static void applyConfiguration(Object server) {
        ConfigData config = loadConfig();
        int distance = config.distance();
        try {
            ServerCommandExecutor executor = new ServerCommandExecutor(server);

            // SERVER_STARTED puede ejecutarse antes que la etiqueta minecraft:load en el servidor integrado.
            // Estos tres objetivos son necesarios para aplicar la configuración sin perder el valor Fácil.
            executor.run("scoreboard objectives add sb3_const dummy");
            executor.run("scoreboard objectives add sb3_cfg dummy");
            executor.run("scoreboard objectives add sb3_used dummy");

            executor.run("execute in minecraft:overworld if biome 0 64 0 minecraft:the_void run function skyblock:slots/remove_forceload");
            executor.run("execute in minecraft:overworld run kill @e[type=minecraft:marker,tag=skyblock_slots_ready_v1]");
            executor.run("execute in minecraft:overworld run kill @e[type=minecraft:marker,tag=skyblock_slots_ready_v2]");

            executor.run("data modify storage skyblock:config island_distance set value " + distance);
            executor.run("scoreboard players set #distance sb3_const " + distance);
            executor.run("scoreboard players set #capacity sb3_cfg " + PLAYER_CAPACITY);
            executor.run("scoreboard players set #enabled_count sb3_cfg " + countEnabled(config.trees()));
            executor.run("scoreboard players set #bonus_tier sb3_cfg " + config.bonusChestMode().scoreValue());

            for (TreeOption tree : TreeOption.values()) {
                executor.run("scoreboard players set " + tree.scoreHolder() + " sb3_cfg "
                        + (Boolean.TRUE.equals(config.trees().get(tree)) ? 1 : 0));
            }

            List<Slot> slots = calculateSlots(distance);
            for (Slot slot : slots) {
                String key = String.format(Locale.ROOT, "%02d", slot.index());
                String snbt = String.format(Locale.ROOT,
                        "{x:%d,z:%d,slot:%d,key:\"%s\"}",
                        slot.x(), slot.z(), slot.index(), key);
                executor.run("data modify storage skyblock:slots s" + key + " set value " + snbt);
                executor.run("scoreboard players set #" + key + " sb3_used 0");
            }

            executor.run("tag @a[scores={sb3_state=1}] remove skyblock_menu_shown_v1");
            executor.run("execute in minecraft:overworld if biome 0 64 0 minecraft:the_void run function skyblock:slots/forceload");
            executor.run("execute in minecraft:overworld if biome 0 64 0 minecraft:the_void run scoreboard players set #slotgen sb3_const 60");
            System.out.println("[SkyblockMulti] Configuración aplicada: distancia=" + distance
                    + ", capacidad=" + PLAYER_CAPACITY + ", árboles=" + countEnabled(config.trees())
                    + ", cofre=" + config.bonusChestMode().configKey());
        } catch (Exception e) {
            System.err.println("[SkyblockMulti] No fue posible aplicar la configuración al servidor: " + e);
            e.printStackTrace(System.err);
        }
    }

    private static List<Slot> calculateSlots(int distance) {
        List<Slot> slots = new ArrayList<>(PLAYER_CAPACITY);
        int index = 1;
        for (int gridZ = -2; gridZ <= 2; gridZ++) {
            for (int gridX = -2; gridX <= 2; gridX++) {
                if (gridX == 0 && gridZ == 0) continue;
                slots.add(new Slot(index++, gridX * distance, gridZ * distance));
            }
        }
        return slots;
    }

    private record ConfigData(int distance, EnumMap<TreeOption, Boolean> trees,
                              BonusChestMode bonusChestMode) {}
    private record Slot(int index, int x, int z) {}

    private static final class ServerCommandExecutor {
        private final Object source;
        private final Object commands;
        private final Object dispatcher;
        private final Method commandMethod;
        private final Method dispatcherMethod;

        private ServerCommandExecutor(Object server) throws ReflectiveOperationException {
            Class<?> commandsClass = runtimeClass(
                    "net.minecraft.commands.Commands",
                    "net.minecraft.class_2170"
            );
            Class<?> sourceClass = runtimeClass(
                    "net.minecraft.commands.CommandSourceStack",
                    "net.minecraft.class_2168"
            );
            this.commands = invokeNoArgReturning(server, commandsClass);
            this.source = invokeNoArgReturning(server, sourceClass);
            this.commandMethod = findCommandMethod(commands.getClass(), sourceClass);
            if (this.commandMethod == null) {
                this.dispatcher = findDispatcher(commands);
                this.dispatcherMethod = findExecuteMethod(dispatcher.getClass());
            } else {
                this.dispatcher = null;
                this.dispatcherMethod = null;
            }
        }

        private int run(String command) throws ReflectiveOperationException {
            String clean = command.startsWith("/") ? command.substring(1) : command;
            Object result;
            if (commandMethod != null) {
                Class<?>[] params = commandMethod.getParameterTypes();
                result = params[0] == String.class
                        ? commandMethod.invoke(commands, clean, source)
                        : commandMethod.invoke(commands, source, clean);
            } else {
                result = dispatcherMethod.invoke(dispatcher, clean, source);
            }
            return result instanceof Number number ? number.intValue() : 0;
        }

        private static Method findCommandMethod(Class<?> commandsClass, Class<?> sourceClass) {
            Method fallback = null;
            for (Method method : commandsClass.getMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 2) continue;
                boolean sourceString = sourceClass.isAssignableFrom(params[0]) && params[1] == String.class;
                boolean stringSource = params[0] == String.class && sourceClass.isAssignableFrom(params[1]);
                if (!sourceString && !stringSource) continue;
                String name = method.getName().toLowerCase(Locale.ROOT);
                if (name.contains("perform") || name.contains("prefixed") || name.contains("execute")) return method;
                fallback = method;
            }
            return fallback;
        }

        private static Object findDispatcher(Object commands) throws ReflectiveOperationException {
            for (Method method : commands.getClass().getMethods()) {
                if (method.getParameterCount() == 0 && method.getReturnType().getName().equals("com.mojang.brigadier.CommandDispatcher")) {
                    return method.invoke(commands);
                }
            }
            throw new NoSuchMethodException("Commands -> CommandDispatcher");
        }

        private static Method findExecuteMethod(Class<?> dispatcherClass) throws NoSuchMethodException {
            for (Method method : dispatcherClass.getMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (method.getName().equals("execute") && params.length == 2 && params[0] == String.class) {
                    return method;
                }
            }
            throw new NoSuchMethodException("CommandDispatcher.execute(String, source)");
        }

        private static Object invokeNoArgReturning(Object owner, Class<?> returnType) throws ReflectiveOperationException {
            for (Method method : owner.getClass().getMethods()) {
                if (method.getParameterCount() == 0 && returnType.isAssignableFrom(method.getReturnType())) {
                    return method.invoke(owner);
                }
            }
            throw new NoSuchMethodException(owner.getClass().getName() + " -> " + returnType.getName());
        }

        private static Class<?> runtimeClass(String namedClass, String intermediaryClass) throws ClassNotFoundException {
            MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
            Collection<String> namespaces = resolver.getNamespaces();

            // Production Fabric uses intermediary names. Development environments may use named names.
            if (namespaces.contains("intermediary")) {
                try {
                    return Class.forName(resolver.mapClassName("intermediary", intermediaryClass));
                } catch (RuntimeException | ClassNotFoundException ignored) {
                }
            }
            if (namespaces.contains("named")) {
                try {
                    return Class.forName(resolver.mapClassName("named", namedClass));
                } catch (RuntimeException | ClassNotFoundException ignored) {
                }
            }

            // Direct fallbacks for unusual loaders or development launchers.
            try {
                return Class.forName(intermediaryClass);
            } catch (ClassNotFoundException ignored) {
                return Class.forName(namedClass);
            }
        }
    }
}
