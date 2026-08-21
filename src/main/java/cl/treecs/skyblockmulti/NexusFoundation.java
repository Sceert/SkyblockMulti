package cl.treecs.skyblockmulti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;

/**
 * World Foundation del Ascension Nexus.
 *
 * Esta clase queda separada de SkyblockMultiMod para que la protección y la
 * configuración estructural del Nexus no dependan del diseño visual del HUB.
 */
public final class NexusFoundation implements ModInitializer {

    private static final int NEXUS_RADIUS_CHUNKS = 15;
    private static final BlockPos END_PORTAL_LOCATOR_TARGET = new BlockPos(0, 6, 0);

    public static final int END_EYES_0 = 0;
    public static final int END_EYES_25 = 25;
    public static final int END_EYES_50 = 50;
    public static final int END_EYES_75 = 75;
    public static final int DEFAULT_END_EYES = END_EYES_0;

    private static final String CONFIG_FILE = "skyblockmulti_nexus.json";
    private static final String WORLD_STATE_FILE = "skyblockmulti_nexus.properties";

    private static final List<BlockPos> RENEWABLE_LAVA_SOURCES = List.of(
            new BlockPos(0, 15, -66),
            new BlockPos(66, 15, 0),
            new BlockPos(0, 15, 66),
            new BlockPos(-66, 15, 0)
    );

    private static Path configPath;

    private static volatile MinecraftServer activeServer;
    private static volatile boolean endPortalConfigurationLocked;
    private static volatile int activeWorldEndEyes = -1;

    private static int portalInitializationDelayTicks = -1;

    public static BlockPos getEndPortalLocatorTarget(ServerLevel level) {
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return null;
        }

        // La presencia de los doce marcos confirma la estructura funcional,
        // sin depender de la versión visual concreta de la fortaleza.
        for (PortalFrame frame : PORTAL_FRAMES) {
            if (!level.getBlockState(frame.pos()).is(Blocks.END_PORTAL_FRAME)) {
                return null;
            }
        }

        return END_PORTAL_LOCATOR_TARGET;
    }

    private record PortalFrame(BlockPos pos, Direction facing) {}

    private static final List<PortalFrame> PORTAL_FRAMES = List.of(
            new PortalFrame(new BlockPos(-1, 6, -2), Direction.SOUTH),
            new PortalFrame(new BlockPos( 0, 6, -2), Direction.SOUTH),
            new PortalFrame(new BlockPos( 1, 6, -2), Direction.SOUTH),

            new PortalFrame(new BlockPos(-1, 6,  2), Direction.NORTH),
            new PortalFrame(new BlockPos( 0, 6,  2), Direction.NORTH),
            new PortalFrame(new BlockPos( 1, 6,  2), Direction.NORTH),

            new PortalFrame(new BlockPos(-2, 6, -1), Direction.EAST),
            new PortalFrame(new BlockPos(-2, 6,  0), Direction.EAST),
            new PortalFrame(new BlockPos(-2, 6,  1), Direction.EAST),

            new PortalFrame(new BlockPos( 2, 6, -1), Direction.WEST),
            new PortalFrame(new BlockPos( 2, 6,  0), Direction.WEST),
            new PortalFrame(new BlockPos( 2, 6,  1), Direction.WEST)
    );

    @Override
    public void onInitialize() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        ensureConfigExists();

        registerNativeProtection();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            activeServer = server;

            Path statePath = getWorldStatePath(server);
            if (Files.exists(statePath)) {
                activeWorldEndEyes = readWorldEndEyes(statePath);
                endPortalConfigurationLocked = true;
                portalInitializationDelayTicks = -1;

                System.out.println(
                        "[Skyblock Multi] Nexus: configuración del Portal del End restaurada: "
                                + activeWorldEndEyes + "%."
                );
            } else {
                activeWorldEndEyes = -1;
                endPortalConfigurationLocked = false;

                // El datapack necesita terminar de construir la fortaleza primero.
                portalInitializationDelayTicks = 40;
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (portalInitializationDelayTicks < 0) {
                return;
            }

            if (portalInitializationDelayTicks > 0) {
                portalInitializationDelayTicks--;
                return;
            }

            if (tryInitializeEndPortal(server)) {
                portalInitializationDelayTicks = -1;
            } else {
                // La estructura aún no está lista. Reintentar en 1 segundo.
                portalInitializationDelayTicks = 20;
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            activeServer = null;
            activeWorldEndEyes = -1;
            endPortalConfigurationLocked = false;
            portalInitializationDelayTicks = -1;
        });

        System.out.println(
                "[Skyblock Multi] Nexus Foundation inicializado. Protección nativa del núcleo activa."
        );
    }

    private static void registerNativeProtection() {
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return true;
            }

            if (!isProtectedNexusPosition(level, pos)) {
                return true;
            }

            return hasBuilderBypass(serverPlayer);
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }

            if (hasBuilderBypass(serverPlayer)) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);

            if (!isPotentiallyDestructiveUse(stack)) {
                // Botones, puertas, cofres y otras interacciones no destructivas
                // siguen disponibles para futuras mecánicas del Nexus.
                return InteractionResult.PASS;
            }

            BlockPos hitPos = hitResult.getBlockPos();
            BlockPos adjacentPos = hitPos.relative(hitResult.getDirection());

            if (stack.is(Items.BUCKET) && isRenewableLavaSource(level, hitPos)) {
                return InteractionResult.PASS;
            }

            if (isProtectedNexusPosition(level, hitPos)
                    || isProtectedNexusPosition(level, adjacentPos)) {
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });

        // Fluid pickup is handled through item use rather than block use in
        // current mappings. Keep filled buckets blocked throughout the Nexus,
        // and permit an empty bucket only while standing at a designated well.
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return InteractionResult.PASS;
            }

            if (hasBuilderBypass(serverPlayer)) {
                return InteractionResult.PASS;
            }

            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof BucketItem)) {
                return InteractionResult.PASS;
            }

            if (!isProtectedNexusPosition(level, player.blockPosition())) {
                return InteractionResult.PASS;
            }

            if (stack.is(Items.BUCKET)
                    && isNearRenewableLavaSource(level, player.blockPosition())) {
                return InteractionResult.PASS;
            }

            return InteractionResult.FAIL;
        });
    }

    private static boolean isRenewableLavaSource(Level level, BlockPos pos) {
        return level.dimension().equals(Level.OVERWORLD)
                && RENEWABLE_LAVA_SOURCES.contains(pos);
    }

    private static boolean isNearRenewableLavaSource(Level level, BlockPos pos) {
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return false;
        }

        return RENEWABLE_LAVA_SOURCES.stream()
                .anyMatch(source -> source.distSqr(pos) <= 36.0D);
    }

    private static boolean isPotentiallyDestructiveUse(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() instanceof BlockItem) {
            return true;
        }

        if (stack.getItem() instanceof BucketItem) {
            return true;
        }

        return stack.is(Items.FLINT_AND_STEEL)
                || stack.is(Items.FIRE_CHARGE)
                || stack.is(Items.END_CRYSTAL);
    }

    public static boolean isProtectedNexusPosition(Level level, BlockPos pos) {
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return false;
        }

        int chunkX = Math.floorDiv(pos.getX(), 16);
        int chunkZ = Math.floorDiv(pos.getZ(), 16);

        return chunkX * chunkX + chunkZ * chunkZ
                <= NEXUS_RADIUS_CHUNKS * NEXUS_RADIUS_CHUNKS;
    }

    /**
     * El host de un mundo integrado suele ser operador automáticamente,
     * por lo que OP no se usa como bypass.
     *
     * Para desarrollo, únicamente Creative permite modificar el Nexus.
     * Survival y Adventure quedan protegidos aunque el jugador sea OP.
     */
    private static boolean hasBuilderBypass(ServerPlayer player) {
        return player.isCreative();
    }

    public static int getConfiguredEndPortalEyesPercent() {
        if (activeServer != null && endPortalConfigurationLocked && activeWorldEndEyes >= 0) {
            return activeWorldEndEyes;
        }

        return loadConfiguredEndEyes();
    }

    public static boolean isEndPortalConfigurationLocked() {
        return activeServer != null && endPortalConfigurationLocked;
    }

    public static int getNextEndPortalEyesPercent(int current) {
        return switch (normalizeEndEyes(current)) {
            case END_EYES_0 -> END_EYES_25;
            case END_EYES_25 -> END_EYES_50;
            case END_EYES_50 -> END_EYES_75;
            default -> END_EYES_0;
        };
    }

    /**
     * Guarda el valor predeterminado para mundos NUEVOS.
     * En un mundo ya inicializado, la estructura del Portal queda bloqueada.
     */
    public static boolean saveConfiguredEndPortalEyesPercent(int requestedPercent) {
        if (isEndPortalConfigurationLocked()) {
            return false;
        }

        int normalized = normalizeEndEyes(requestedPercent);

        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(
                    configPath,
                    "{\n  \"endPortalEyesPercent\": " + normalized + "\n}\n",
                    StandardCharsets.UTF_8
            );
            return true;
        } catch (IOException e) {
            System.err.println(
                    "[Skyblock Multi] Nexus: no se pudo guardar la configuración: "
                            + e.getMessage()
            );
            return false;
        }
    }

    private static int normalizeEndEyes(int value) {
        if (value <= 12) {
            return END_EYES_0;
        }
        if (value <= 37) {
            return END_EYES_25;
        }
        if (value <= 62) {
            return END_EYES_50;
        }
        return END_EYES_75;
    }

    private static void ensureConfigExists() {
        try {
            Files.createDirectories(configPath.getParent());
            if (Files.notExists(configPath)) {
                Files.writeString(
                        configPath,
                        "{\n  \"endPortalEyesPercent\": " + DEFAULT_END_EYES + "\n}\n",
                        StandardCharsets.UTF_8
                );
            }
        } catch (IOException e) {
            System.err.println(
                    "[Skyblock Multi] Nexus: no se pudo crear "
                            + CONFIG_FILE + ": " + e.getMessage()
            );
        }
    }

    private static int loadConfiguredEndEyes() {
        ensureConfigExists();

        try {
            String raw = Files.readString(configPath, StandardCharsets.UTF_8);
            String compact = raw.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");

            String key = "\"endportaleyespercent\":";
            int keyIndex = compact.indexOf(key);

            if (keyIndex >= 0) {
                int start = keyIndex + key.length();
                int end = start;

                while (end < compact.length()
                        && Character.isDigit(compact.charAt(end))) {
                    end++;
                }

                if (end > start) {
                    return normalizeEndEyes(
                            Integer.parseInt(compact.substring(start, end))
                    );
                }
            }
        } catch (Exception e) {
            System.err.println(
                    "[Skyblock Multi] Nexus: configuración inválida; se usará 0%: "
                            + e.getMessage()
            );
        }

        return DEFAULT_END_EYES;
    }

    private static boolean tryInitializeEndPortal(MinecraftServer server) {
        ServerLevel level = server.overworld();

        // No bloquear la configuración hasta confirmar que la fortaleza v2 existe.
        for (PortalFrame frame : PORTAL_FRAMES) {
            if (!level.getBlockState(frame.pos()).is(Blocks.END_PORTAL_FRAME)) {
                return false;
            }
        }

        int percent = loadConfiguredEndEyes();
        int eyes = switch (percent) {
            case END_EYES_25 -> 3;
            case END_EYES_50 -> 6;
            case END_EYES_75 -> 9;
            default -> 0;
        };

        // Primero normalizar los 12 marcos sin ojos.
        for (PortalFrame frame : PORTAL_FRAMES) {
            setPortalFrame(level, frame, false);
        }

        // Luego elegir exactamente 0, 3, 6 o 9 posiciones al azar.
        List<PortalFrame> shuffled = new ArrayList<>(PORTAL_FRAMES);
        Collections.shuffle(shuffled, new Random());

        for (int i = 0; i < eyes; i++) {
            setPortalFrame(level, shuffled.get(i), true);
        }

        Path statePath = getWorldStatePath(server);

        try {
            Properties properties = new Properties();
            properties.setProperty("initialized", "true");
            properties.setProperty("endPortalEyesPercent", Integer.toString(percent));
            properties.setProperty("initialEyeCount", Integer.toString(eyes));

            Files.createDirectories(statePath.getParent());

            try (var writer = Files.newBufferedWriter(statePath, StandardCharsets.UTF_8)) {
                properties.store(
                        writer,
                        "Skyblock Multi - The Ascension Nexus world state"
                );
            }
        } catch (IOException e) {
            System.err.println(
                    "[Skyblock Multi] Nexus: el portal fue configurado, pero no se pudo "
                            + "guardar su estado persistente: " + e.getMessage()
            );
            return false;
        }

        activeWorldEndEyes = percent;
        endPortalConfigurationLocked = true;

        System.out.println(
                "[Skyblock Multi] Nexus: Portal del End inicializado con "
                        + percent + "% (" + eyes + "/12 ojos). "
                        + "La configuración estructural queda bloqueada para este mundo."
        );

        return true;
    }

    private static void setPortalFrame(
            ServerLevel level,
            PortalFrame frame,
            boolean eye
    ) {
        BlockState state = Blocks.END_PORTAL_FRAME
                .defaultBlockState()
                .setValue(EndPortalFrameBlock.FACING, frame.facing())
                .setValue(EndPortalFrameBlock.HAS_EYE, eye);

        level.setBlock(frame.pos(), state, 3);
    }

    private static Path getWorldStatePath(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(WORLD_STATE_FILE);
    }

    private static int readWorldEndEyes(Path statePath) {
        try {
            Properties properties = new Properties();

            try (var reader = Files.newBufferedReader(statePath, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }

            return normalizeEndEyes(
                    Integer.parseInt(
                            properties.getProperty(
                                    "endPortalEyesPercent",
                                    Integer.toString(DEFAULT_END_EYES)
                            )
                    )
            );
        } catch (Exception e) {
            System.err.println(
                    "[Skyblock Multi] Nexus: no se pudo leer el estado del mundo: "
                            + e.getMessage()
            );
            return DEFAULT_END_EYES;
        }
    }
}
