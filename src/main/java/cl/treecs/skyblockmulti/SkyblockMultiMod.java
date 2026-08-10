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
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class SkyblockMultiMod implements ModInitializer {
    public static final String MOD_ID = "skyblockmulti";

    /**
     * Geometría del mapa SkyblockMulti.
     * La distancia configurada pasa a representar el RADIO desde el HUB (0,0),
     * no la separación entre islas.
     */
    public static final int DEFAULT_RADIUS = 1024;
    public static final int DEFAULT_DISTANCE = DEFAULT_RADIUS; // Alias de compatibilidad.
    public static final int[] RADIUS_OPTIONS_8 = {512, 1024, 2048, 4096, 8192};
    public static final int[] RADIUS_OPTIONS_16 = {1024, 2048, 4096, 8192, 16384};

    public static final int PLAYER_CAPACITY = 16;
    private static final int LEGACY_PLAYER_CAPACITY = 24;
    public static final int DEFAULT_CAPACITY = 8;
    public static final int MIN_CAPACITY = 8;
    public static final int MAX_CAPACITY = 16;
    private static final int OPENPAC_PARTY_CHECK_INTERVAL_TICKS = 20;
    private static final int LAYOUT_VERSION = 2;

    private static final Pattern RADIUS_PATTERN = Pattern.compile("\\\"islandRadius\\\"\\s*:\\s*(-?\\d+)");
    private static final Pattern LEGACY_DISTANCE_PATTERN = Pattern.compile("\\\"islandDistance\\\"\\s*:\\s*(-?\\d+)");
    private static final Pattern CAPACITY_PATTERN = Pattern.compile("\\\"islandCapacity\\\"\\s*:\\s*(-?\\d+)");
    private static final Pattern BONUS_CHEST_MODE_PATTERN = Pattern.compile("\\\"bonusChestMode\\\"\\s*:\\s*\\\"([a-z_]+)\\\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern PARTY_LEAVE_DIFFICULTY_PATTERN = Pattern.compile("\\\"partyLeaveDifficultyMode\\\"\\s*:\\s*\\\"([a-z_]+)\\\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEGACY_BONUS_CHEST_ENABLED_PATTERN = Pattern.compile("\\\"bonusChestEnabled\\\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LEGACY_BONUS_CHEST_TIER_PATTERN = Pattern.compile("\\\"bonusChestTier\\\"\\s*:\\s*\\\"([a-z_]+)\\\"", Pattern.CASE_INSENSITIVE);
    private static Path configPath;
    private static volatile Object activeServer;
    private static volatile boolean worldGeometryLocked = false;
	
	// Estado conocido de las parties de OpenPAC.
	private static final Map<UUID, PartyState> OPENPAC_PARTY_STATES = new HashMap<>();
	private static int openPacPartyCheckTicks = 0;
	
	private record PartyState(
        boolean inParty,
        UUID partyId,
        UUID ownerUuid
) {
    private static PartyState from(OpenPacCompat.PartyInfo info) {
        return new PartyState(
                info.inParty(),
                info.partyId(),
                info.ownerUuid()
        );
    }
}

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
                                                                                if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                                                                                    return 0;
                                                                                }

                                                                                // La primera isla asignada fija la geometría de este mundo.
                                                                                worldGeometryLocked = true;

                                                                                int x = IntegerArgumentType.getInteger(context, "x");
                                                                                int z = IntegerArgumentType.getInteger(context, "z");

                                                                                if (OpenPacCompat.isInstalled()) {
                                                                                    OpenPacCompat.claimInitialIsland(player, x, z);

                                                                                    // La isla ya tiene sb3_state=2 cuando este comando se ejecuta.
                                                                                    // Reconciliamos a todos para que los miembros de la party
                                                                                    // adopten inmediatamente la isla del owner recién creada.
                                                                                    reconcileAllOnlinePlayers(
                                                                                            context.getSource().getServer(),
                                                                                            player.getUUID()
                                                                                    );
                                                                                }

                                                                                return 1;
                                                                            })
                                                            )
                                            )
                            )
            );
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;

            try {
                String playerName = player.getGameProfile().name();
                if (new ServerCommandExecutor(server).run(
                        "execute if score " + playerName + " sb3_state matches 2"
                ) > 0) {
                    worldGeometryLocked = true;
                }
            } catch (Exception ignored) {
                // En un mundo nuevo los objetivos pueden no estar listos durante los primeros instantes.
            }

            OpenPacCompat.PartyInfo partyInfo = OpenPacCompat.isInstalled()
                    ? OpenPacCompat.getPartyInfo(player)
                    : OpenPacCompat.PartyInfo.noParty();

            reconcileActiveIsland(server, player, partyInfo, false);

            if (OpenPacCompat.isInstalled()) {
                OPENPAC_PARTY_STATES.put(
                        player.getUUID(),
                        PartyState.from(partyInfo)
                );
            }

            // Mensaje informativo sobre integración con OpenPAC.
            if (OpenPacCompat.isInstalled()) {
                player.sendSystemMessage(
                        Component.translatable("skyblockmulti.openpac.detected")
                );
            } else {
                player.sendSystemMessage(
                        Component.translatable("skyblockmulti.openpac.solo")
                );
            }

            if (!OpenPacCompat.isInstalled()) {
                return;
            }

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

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            OPENPAC_PARTY_STATES.remove(handler.player.getUUID());
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!OpenPacCompat.isInstalled()) {
                return;
            }

            openPacPartyCheckTicks++;

            // Comprobar cambios reales de party una vez por segundo.
            if (openPacPartyCheckTicks < OPENPAC_PARTY_CHECK_INTERVAL_TICKS) {
                return;
            }

            openPacPartyCheckTicks = 0;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                checkOpenPacPartyChange(server, player);
            }
        });

        registerServerStartedEvent();
        System.out.println("[SkyblockMulti] Mod 0.1.1-beta inicializado. Configuración: " + configPath);
    }

    public static int getConfiguredRadius() {
        return loadConfig().radius();
    }

    /** Alias conservado para código antiguo: ahora devuelve el radio desde el HUB. */
    public static int getConfiguredDistance() {
        return getConfiguredRadius();
    }

    public static EnumMap<TreeOption, Boolean> getConfiguredTreeStates() {
        return new EnumMap<>(loadConfig().trees());
    }

    public static int getPlayerCapacity(int ignoredDistance) {
        return PLAYER_CAPACITY;
    }

    public static int getConfiguredCapacity() {
        return loadConfig().capacity();
    }

    public static boolean isWorldGeometryLocked() {
        return worldGeometryLocked;
    }

    public static BonusChestMode getConfiguredBonusChestMode() {
        return loadConfig().bonusChestMode();
    }

    public static BonusChestMode getConfiguredPartyLeaveDifficultyMode() {
        return loadConfig().partyLeaveDifficultyMode();
    }

    public static boolean saveConfiguredRadius(int requestedRadius) {
        ConfigData current = loadConfig();
        return saveConfiguration(
                requestedRadius,
                current.capacity(),
                current.trees(),
                current.bonusChestMode(),
                current.partyLeaveDifficultyMode()
        );
    }

    public static boolean saveConfiguredDistance(int requestedDistance) {
        return saveConfiguredRadius(requestedDistance);
    }

    public static boolean saveConfiguration(int requestedRadius, Map<TreeOption, Boolean> requestedTrees) {
        ConfigData current = loadConfig();
        return saveConfiguration(
                requestedRadius,
                current.capacity(),
                requestedTrees,
                current.bonusChestMode(),
                current.partyLeaveDifficultyMode()
        );
    }

    public static boolean saveConfiguration(int requestedRadius, Map<TreeOption, Boolean> requestedTrees,
                                            BonusChestMode bonusChestMode) {
        ConfigData current = loadConfig();
        return saveConfiguration(
                requestedRadius,
                current.capacity(),
                requestedTrees,
                bonusChestMode,
                current.partyLeaveDifficultyMode()
        );
    }

    public static boolean saveConfiguration(int requestedRadius, int requestedCapacity,
                                            Map<TreeOption, Boolean> requestedTrees,
                                            BonusChestMode bonusChestMode) {
        ConfigData current = loadConfig();
        return saveConfiguration(
                requestedRadius,
                requestedCapacity,
                requestedTrees,
                bonusChestMode,
                current.partyLeaveDifficultyMode()
        );
    }

    public static boolean saveConfiguration(int requestedRadius, int requestedCapacity,
                                            Map<TreeOption, Boolean> requestedTrees,
                                            BonusChestMode bonusChestMode,
                                            BonusChestMode partyLeaveDifficultyMode) {
        ensureConfigReady();
        ConfigData current = loadConfig();
        int normalizedCapacity = normalizeCapacity(requestedCapacity);
        int normalizedRadius = normalizeRadius(requestedRadius, normalizedCapacity);

        if (worldGeometryLocked
                && (normalizedCapacity != current.capacity() || normalizedRadius != current.radius())) {
            System.err.println(
                    "[SkyblockMulti] Radio/capacidad bloqueados: el mundo ya tiene una isla asignada."
            );
            return false;
        }

        EnumMap<TreeOption, Boolean> trees = normalizeTrees(requestedTrees);
        BonusChestMode safeMode = bonusChestMode == null ? BonusChestMode.STANDARD : bonusChestMode;
        BonusChestMode safePartyLeaveMode = partyLeaveDifficultyMode == null
                ? BonusChestMode.BASIC
                : partyLeaveDifficultyMode;
        try {
            writeConfig(normalizedRadius, normalizedCapacity, trees, safeMode, safePartyLeaveMode);
            System.out.println("[SkyblockMulti] Configuración guardada: radio_hub=" + normalizedRadius
                    + ", capacidad=" + normalizedCapacity
                    + ", árboles=" + countEnabled(trees)
                    + ", cofre=" + safeMode.configKey()
                    + ", salida_party=" + safePartyLeaveMode.configKey());
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

    public static int[] getRadiusOptions(int capacity) {
        return normalizeCapacity(capacity) == 8 ? RADIUS_OPTIONS_8 : RADIUS_OPTIONS_16;
    }

    public static int normalizeRadius(int value, int capacity) {
        int[] options = getRadiusOptions(capacity);
        int best = options[0];
        long bestDelta = Math.abs((long) value - best);

        for (int option : options) {
            long delta = Math.abs((long) value - option);
            if (delta < bestDelta) {
                best = option;
                bestDelta = delta;
            }
        }
        return best;
    }

    public static int getNextConfiguredRadius(int current, int capacity) {
        int[] options = getRadiusOptions(capacity);
        int normalized = normalizeRadius(current, capacity);
        for (int i = 0; i < options.length; i++) {
            if (options[i] == normalized) {
                return options[(i + 1) % options.length];
            }
        }
        return normalizeRadius(DEFAULT_RADIUS, capacity);
    }

    public static int getRadiusChunks(int radius, int capacity) {
        return normalizeRadius(radius, capacity) / 16;
    }

    public static int getApproxNeighborDistance(int radius, int capacity) {
        int normalizedCapacity = normalizeCapacity(capacity);
        int normalizedRadius = normalizeRadius(radius, normalizedCapacity);
        return (int) Math.round(
                2.0D * normalizedRadius * Math.sin(Math.PI / normalizedCapacity)
        );
    }

    public static int getNextConfiguredCapacity(int current) {
        return normalizeCapacity(current) == 8 ? 16 : 8;
    }

    public static int normalizeCapacity(int value) {
        return value <= 8 ? 8 : 16;
    }

    // Alias de compatibilidad con el código previo.
    public static int normalizeDistance(int value) {
        return normalizeRadius(value, getConfiguredCapacity());
    }

    public static int getNextConfiguredDistance(int current) {
        return getNextConfiguredRadius(current, getConfiguredCapacity());
    }

    public static int getDistanceChunks(int distance) {
        return getRadiusChunks(distance, getConfiguredCapacity());
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
                writeConfig(
                        DEFAULT_RADIUS,
                        DEFAULT_CAPACITY,
                        defaultTrees(),
                        BonusChestMode.STANDARD,
                        BonusChestMode.BASIC
                );
            }
        } catch (IOException e) {
            System.err.println("[SkyblockMulti] No se pudo crear el archivo de configuración: " + e.getMessage());
        }
    }

    private static void writeConfig(int radius, int capacity, Map<TreeOption, Boolean> trees,
                                    BonusChestMode bonusChestMode,
                                    BonusChestMode partyLeaveDifficultyMode) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"islandRadius\": ").append(radius).append(",\n");
        json.append("  \"islandCapacity\": ").append(capacity).append(",\n");
        json.append("  \"bonusChestMode\": \"").append(bonusChestMode.configKey()).append("\",\n");
        json.append("  \"partyLeaveDifficultyMode\": \"")
                .append(partyLeaveDifficultyMode.configKey()).append("\",\n");
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
        int radius = DEFAULT_RADIUS;
        int capacity = DEFAULT_CAPACITY;
        EnumMap<TreeOption, Boolean> trees = defaultTrees();
        BonusChestMode bonusChestMode = BonusChestMode.STANDARD;
        BonusChestMode partyLeaveDifficultyMode = BonusChestMode.BASIC;
        try {
            String raw = Files.readString(configPath, StandardCharsets.UTF_8);

            Matcher radiusMatcher = RADIUS_PATTERN.matcher(raw);
            if (radiusMatcher.find()) {
                // Nuevo esquema: islandRadius + capacidad 8/16.
                radius = Integer.parseInt(radiusMatcher.group(1));
                Matcher capacityMatcher = CAPACITY_PATTERN.matcher(raw);
                if (capacityMatcher.find()) {
                    capacity = Integer.parseInt(capacityMatcher.group(1));
                }
                capacity = normalizeCapacity(capacity);
            } else {
                // La geometría anterior usaba distancia ENTRE islas y capacidad 1..24.
                // No se puede reinterpretar de forma segura como un radio desde el HUB,
                // por lo que migra a los nuevos valores por defecto 8 / 1024.
                capacity = DEFAULT_CAPACITY;
                radius = DEFAULT_RADIUS;
            }

            Matcher modeMatcher = BONUS_CHEST_MODE_PATTERN.matcher(raw);
            if (modeMatcher.find()) {
                bonusChestMode = BonusChestMode.fromConfig(modeMatcher.group(1));
            } else {
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

            Matcher partyLeaveMatcher = PARTY_LEAVE_DIFFICULTY_PATTERN.matcher(raw);
            if (partyLeaveMatcher.find()) {
                partyLeaveDifficultyMode = BonusChestMode.fromConfig(partyLeaveMatcher.group(1));
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

        return new ConfigData(
                normalizeRadius(radius, capacity),
                capacity,
                normalizeTrees(trees),
                bonusChestMode,
                partyLeaveDifficultyMode
        );
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

    private static void checkOpenPacPartyChange(MinecraftServer server, ServerPlayer player) {
        OpenPacCompat.PartyInfo partyInfo = OpenPacCompat.getPartyInfo(player);
        PartyState currentState = PartyState.from(partyInfo);
        PartyState previousState = OPENPAC_PARTY_STATES.put(player.getUUID(), currentState);

        // La primera lectura se usa como estado base. JOIN ya hizo la reconciliación inicial.
        if (previousState == null) {
            return;
        }

        if (previousState.equals(currentState)) {
            return;
        }

        String playerName = player.getGameProfile().name();
        UUID playerUuid = player.getUUID();

        boolean wasMemberOfAnotherPlayer =
                previousState.inParty()
                        && previousState.ownerUuid() != null
                        && !playerUuid.equals(previousState.ownerUuid());

        boolean isMemberOfAnotherPlayer =
                currentState.inParty()
                        && !partyInfo.owner()
                        && currentState.ownerUuid() != null
                        && !playerUuid.equals(currentState.ownerUuid());

        boolean ownerChanged =
                previousState.ownerUuid() == null
                        ? currentState.ownerUuid() != null
                        : !previousState.ownerUuid().equals(currentState.ownerUuid());

        // Solo teletransportamos automáticamente hacia un owner cuando el jugador
        // acaba de convertirse en miembro o cambia de owner/party.
        boolean autoHomeToPartyOwner =
                isMemberOfAnotherPlayer
                        && (!wasMemberOfAnotherPlayer || ownerChanged);

        // Si antes compartía la isla de otro jugador y ahora ya no es miembro,
        // debe volver físicamente a su isla personal o, si nunca tuvo una, al HUB.
        boolean returnAfterLeavingSharedIsland =
                wasMemberOfAnotherPlayer && !isMemberOfAnotherPlayer;

        System.out.println(
                "[SkyblockMulti] OpenPAC: cambio de party detectado para "
                        + playerName
                        + "."
        );

        // Siempre recalculamos ACTIVE primero.
        reconcileActiveIsland(server, player, partyInfo, autoHomeToPartyOwner);

        if (returnAfterLeavingSharedIsland) {
            returnPlayerAfterLeavingParty(server, player);
        }
    }

    private static void returnPlayerAfterLeavingParty(
            MinecraftServer server,
            ServerPlayer player
    ) {
        String playerName = player.getGameProfile().name();

        try {
            ServerCommandExecutor executor = new ServerCommandExecutor(server);

            // Con isla personal: ACTIVE ya fue restaurado a OWN por reconcileActiveIsland().
            executor.run(
                    "execute if score " + playerName
                            + " sb3_state matches 2 as " + playerName
                            + " run function skyblock:player/home"
            );

            // Sin isla personal: vuelve al HUB. Más adelante este caso enlazará
            // con el flujo especial de dificultad/inventario tras abandonar una party.
            executor.run(
                    "execute unless score " + playerName
                            + " sb3_state matches 2 as " + playerName
                            + " run function skyblock:player/hub"
            );

            System.out.println(
                    "[SkyblockMulti] OpenPAC: retorno tras salir de party solicitado para "
                            + playerName
                            + "."
            );
        } catch (Exception e) {
            System.err.println(
                    "[SkyblockMulti] No fue posible devolver a "
                            + playerName
                            + " tras salir de la party: "
                            + e
            );
        }
    }

    private static void reconcileAllOnlinePlayers(MinecraftServer server, UUID newlyAvailableOwnerUuid) {
        try {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {

                OpenPacCompat.PartyInfo partyInfo = OpenPacCompat.isInstalled()
                        ? OpenPacCompat.getPartyInfo(player)
                        : OpenPacCompat.PartyInfo.noParty();

                boolean autoHome = newlyAvailableOwnerUuid != null
                        && partyInfo.inParty()
                        && !partyInfo.owner()
                        && newlyAvailableOwnerUuid.equals(partyInfo.ownerUuid());

                reconcileActiveIsland(server, player, partyInfo, autoHome);

                if (OpenPacCompat.isInstalled()) {
                    OPENPAC_PARTY_STATES.put(player.getUUID(), PartyState.from(partyInfo));
                }
            }
        } catch (Exception e) {
            System.err.println(
                    "[SkyblockMulti] No fue posible reconciliar las islas activas online: " + e
            );
        }
    }

    private static void reconcileActiveIsland(
            MinecraftServer server,
            ServerPlayer player,
            OpenPacCompat.PartyInfo partyInfo,
            boolean autoHomeOnPartyTarget
    ) {
        String playerName = player.getGameProfile().name();

        try {
            ServerCommandExecutor executor = new ServerCommandExecutor(server);

            // Limpiamos primero cualquier destino activo antiguo. Esto es importante para
            // jugadores que estaban en una party pero todavía no tienen isla personal.
            executor.run("scoreboard players reset " + playerName + " sb_active_x");
            executor.run("scoreboard players reset " + playerName + " sb_active_z");

            // La isla personal es siempre el fallback cuando existe.
            executor.run(
                    "execute if score " + playerName +
                            " sb3_state matches 2 run scoreboard players operation " +
                            playerName + " sb_active_x = " +
                            playerName + " sb3_x"
            );

            executor.run(
                    "execute if score " + playerName +
                            " sb3_state matches 2 run scoreboard players operation " +
                            playerName + " sb_active_z = " +
                            playerName + " sb3_z"
            );

            if (!OpenPacCompat.isInstalled() || !partyInfo.inParty() || partyInfo.owner()) {
                return;
            }

            String ownerName = partyInfo.ownerName();

            // Si el owner tiene una isla válida, esa pasa a ser la isla activa del miembro.
            executor.run(
                    "execute if score " + ownerName +
                            " sb3_state matches 2 run scoreboard players operation " +
                            playerName + " sb_active_x = " +
                            ownerName + " sb3_x"
            );

            executor.run(
                    "execute if score " + ownerName +
                            " sb3_state matches 2 run scoreboard players operation " +
                            playerName + " sb_active_z = " +
                            ownerName + " sb3_z"
            );

            System.out.println(
                    "[SkyblockMulti] OpenPAC: reconciliación de isla activa solicitada para "
                            + playerName
                            + " usando la isla de "
                            + ownerName
                            + "."
            );

            if (autoHomeOnPartyTarget) {
                // El propio execute comprueba que el owner tenga isla antes de llamar home.
                executor.run(
                        "execute if score " + ownerName +
                                " sb3_state matches 2 as " + playerName +
                                " run function skyblock:player/home"
                );
            }
        } catch (Exception e) {
            System.err.println(
                    "[SkyblockMulti] No fue posible reconciliar la isla activa de "
                            + playerName
                            + ": "
                            + e
            );
        }
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
                            worldGeometryLocked = false;
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
        int radius = config.radius();
        int capacity = config.capacity();
        try {
            ServerCommandExecutor executor = new ServerCommandExecutor(server);

            executor.run("scoreboard objectives add sb3_const dummy");
            executor.run("scoreboard objectives add sb3_cfg dummy");
            executor.run("scoreboard objectives add sb3_used dummy");

            // Retirar tickets de la geometría anterior antes de reescribir los slots.
            executor.run("execute in minecraft:overworld if biome 0 64 0 minecraft:the_void run function skyblock:slots/remove_forceload");
            executor.run("execute in minecraft:overworld if biome 0 64 0 minecraft:the_void run function skyblock:slots/remove_free_anchors");
            executor.run("execute in minecraft:overworld run kill @e[type=minecraft:marker,tag=skyblock_slots_ready_v1]");
            executor.run("execute in minecraft:overworld run kill @e[type=minecraft:marker,tag=skyblock_slots_ready_v2]");

            executor.run("data modify storage skyblock:config island_radius set value " + radius);
            // Alias interno para funciones antiguas que todavía consulten island_distance.
            executor.run("data modify storage skyblock:config island_distance set value " + radius);
            executor.run("scoreboard players set #distance sb3_const " + radius);
            executor.run("scoreboard players set #layout_version sb3_const " + LAYOUT_VERSION);
            executor.run("scoreboard players set #capacity sb3_cfg " + capacity);
            executor.run("scoreboard players set #max sb3_const " + capacity);
            executor.run("scoreboard players set #openpac sb3_cfg " + (OpenPacCompat.isInstalled() ? 1 : 0));
            executor.run("scoreboard players set #enabled_count sb3_cfg " + countEnabled(config.trees()));
            executor.run("scoreboard players set #bonus_tier sb3_cfg " + config.bonusChestMode().scoreValue());
            executor.run("scoreboard players set #party_leave_tier sb3_cfg "
                    + config.partyLeaveDifficultyMode().scoreValue());

            for (TreeOption tree : TreeOption.values()) {
                executor.run("scoreboard players set " + tree.scoreHolder() + " sb3_cfg "
                        + (Boolean.TRUE.equals(config.trees().get(tree)) ? 1 : 0));
            }

            // Borrar definiciones de slots previas y reiniciar ocupación lógica.
            for (int i = 1; i <= LEGACY_PLAYER_CAPACITY; i++) {
                String key = String.format(Locale.ROOT, "%02d", i);
                executor.run("data remove storage skyblock:slots s" + key);
                executor.run("scoreboard players set #" + key + " sb3_used 0");
            }

            List<Slot> slots = calculateSlots(radius, capacity);
            for (Slot slot : slots) {
                String key = String.format(Locale.ROOT, "%02d", slot.index());
                String snbt = String.format(Locale.ROOT,
                        "{x:%d,z:%d,slot:%d,key:\"%s\"}",
                        slot.x(), slot.z(), slot.index(), key);
                executor.run("data modify storage skyblock:slots s" + key + " set value " + snbt);
            }

            executor.run("tag @a[scores={sb3_state=1}] remove skyblock_menu_shown_v1");
            executor.run("execute in minecraft:overworld if biome 0 64 0 minecraft:the_void run function skyblock:slots/forceload");
            executor.run("execute in minecraft:overworld if biome 0 64 0 minecraft:the_void run scoreboard players set #slotgen sb3_const 60");
            System.out.println("[SkyblockMulti] Configuración aplicada: radio_hub=" + radius
                    + ", capacidad=" + capacity
                    + ", separación_aprox=" + getApproxNeighborDistance(radius, capacity)
                    + ", árboles=" + countEnabled(config.trees())
                    + ", cofre=" + config.bonusChestMode().configKey()
                    + ", salida_party=" + config.partyLeaveDifficultyMode().configKey());
        } catch (Exception e) {
            System.err.println("[SkyblockMulti] No fue posible aplicar la configuración al servidor: " + e);
            e.printStackTrace(System.err);
        }
    }

    private static List<Slot> calculateSlots(int radius, int capacity) {
        int normalizedCapacity = normalizeCapacity(capacity);
        int normalizedRadius = normalizeRadius(radius, normalizedCapacity);
        List<Slot> slots = new ArrayList<>(normalizedCapacity);

        // Slot 1 comienza al norte del HUB y los restantes avanzan en sentido horario.
        for (int i = 0; i < normalizedCapacity; i++) {
            double angle = (Math.PI * 2.0D * i) / normalizedCapacity;
            int x = roundToChunk(Math.sin(angle) * normalizedRadius);
            int z = roundToChunk(-Math.cos(angle) * normalizedRadius);
            slots.add(new Slot(i + 1, x, z));
        }
        return slots;
    }

    private static int roundToChunk(double coordinate) {
        return (int) (Math.round(coordinate / 16.0D) * 16L);
    }

    private record ConfigData(int radius, int capacity, EnumMap<TreeOption, Boolean> trees,
                              BonusChestMode bonusChestMode,
                              BonusChestMode partyLeaveDifficultyMode) {}
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
            this.source = ((CommandSourceStack) invokeNoArgReturning(server, sourceClass))
                    .withSuppressedOutput();
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
