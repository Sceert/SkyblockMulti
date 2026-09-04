package cl.treecs.skyblockmulti.compatibility.openpac;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xaero.pac.common.claims.player.api.IPlayerChunkClaimAPI;
import xaero.pac.common.claims.result.api.AreaClaimResult;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.claims.api.IServerClaimsManagerAPI;
import xaero.pac.common.server.parties.party.api.IServerPartyAPI;
import xaero.pac.common.server.player.config.api.v2.IPlayerConfigAPI;
import xaero.pac.common.server.player.config.api.v2.PlayerConfigOptions;

import java.util.UUID;

public final class OpenPacCompat {

    public static final String MOD_ID = "openpartiesandclaims";
    public static final String ASCENSION_NEXUS_CANONICAL_NAME = "The Ascension Nexus";
    private static final String ASCENSION_NEXUS_SUBCLAIM_ID = "ascension_nexus";
    private static final int ASCENSION_NEXUS_CLAIM_COLOR = 0x55D6FF; // celeste
    private static final int RESERVED_ISLAND_CLAIM_COLOR = 0x55CC66; // verde

    private static final UUID SERVER_CLAIM_UUID = new UUID(0L, 0L);

    // Futuras islas: reserva 5x5. Al asignarse, el jugador recibe 3x3.
    private static final int RESERVED_RADIUS_CHUNKS = 2;
    private static final int PLAYER_RADIUS_CHUNKS = 1;

    // OpenPAC es una capa secundaria centrada en el domo. Se incluyen todos
    // los chunks que intersectan su círculo físico de radio 45, no un círculo
    // calculado a partir de índices de chunks.
    private static final int ASCENSION_NEXUS_DOME_RADIUS_BLOCKS = 45;
    private static final int LEGACY_NEXUS_SCAN_RADIUS_CHUNKS = 6;

    /*
     * El Server Claim central del Nexus se usa además como marca persistente.
     * Si existe al reiniciar, SkyblockMulti entiende que las reservas territoriales
     * iniciales ya fueron creadas y NO vuelve a recorrer/reclamar los slots.
     */
    private static MinecraftServer claimInitializationServer;
    private static boolean initializeIslandReservationsThisSession;
    private static boolean claimInitializationDecisionLogged;

    private OpenPacCompat() {
    }

    public static boolean isInstalled() {
        return FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static void initialize() {
        if (isInstalled()) {
            System.out.println("[Skyblock Multi] OpenPAC detectado; integración opcional disponible.");
        } else {
            System.out.println("[Skyblock Multi] OpenPAC no instalado; integración desactivada.");
        }
    }

    /**
     * Prepara inmediatamente el claim central al iniciar el mundo. La decisión
     * de crear reservas de islas queda almacenada para que su procesamiento
     * pueda seguir esperando a que el datapack restaure los slots usados.
     */
    public static void prepareWorldClaims(MinecraftServer server) {
        if (isInstalled() && server != null) {
            shouldInitializeWorldClaims(server);
        }
    }

    public static PartyInfo getPartyInfo(ServerPlayer player) {

        if (!isInstalled()) {
            return PartyInfo.noParty();
        }

        MinecraftServer server = player.level().getServer();

        if (server == null) {
            return PartyInfo.noParty();
        }

        IServerPartyAPI party = OpenPACServerAPI
                .get(server)
                .getPartyManager()
                .getPartyByMember(player.getUUID());

        if (party == null) {
            return PartyInfo.noParty();
        }

        UUID ownerUuid = party.getOwner().getUUID();
        String ownerName = party.getOwner().getUsername();

        return new PartyInfo(
                true,
                party.getId(),
                ownerUuid,
                ownerName,
                ownerUuid.equals(player.getUUID())
        );
    }

    /**
     * Determina una única vez por instancia de servidor si este mundo necesita
     * inicializar sus Server Claims de SkyblockMulti.
     *
     * - Si el Nexus central todavía no existe: lo crea y permite crear los 5x5.
     * - Si el Nexus ya existe: no vuelve a crear/modificar reservas de islas.
     *
     * Esto hace que la reserva de futuros slots sea una operación de creación /
     * migración de mundo, no una tarea de cada arranque.
     */
    private static boolean shouldInitializeWorldClaims(MinecraftServer server) {

        if (claimInitializationServer != server) {
            claimInitializationServer = server;
            claimInitializationDecisionLogged = false;

            boolean nexusAlreadyPresent = isAscensionNexusClaimPresent(server);

            initializeIslandReservationsThisSession = !nexusAlreadyPresent;

            if (nexusAlreadyPresent) {
                configureExistingNexusInteractions(server);
                reserveAscensionNexus(server);
            }

            if (initializeIslandReservationsThisSession) {
                boolean nexusCreated = reserveAscensionNexus(server);

                if (!nexusCreated) {
                    initializeIslandReservationsThisSession = false;
                }
            }
        }

        if (!claimInitializationDecisionLogged) {
            claimInitializationDecisionLogged = true;

            if (initializeIslandReservationsThisSession) {
                System.out.println(
                        "[Skyblock Multi] OpenPAC: inicialización territorial activada. "
                                + "Las reservas 5x5 de futuras islas se crearán una única vez."
                );
            } else {
                System.out.println(
                        "[Skyblock Multi] OpenPAC: "
                                + ASCENSION_NEXUS_CANONICAL_NAME
                                + " ya existe o no pudo inicializarse. "
                                + "No se recrearán reservas de futuras islas en este arranque."
                );
            }
        }

        return initializeIslandReservationsThisSession;
    }

    /**
     * Keeps the non-destructive interaction policy in sync for an existing
     * Nexus claim without touching claims, structures or world progression.
     */
    private static void configureExistingNexusInteractions(MinecraftServer server) {
        IPlayerConfigAPI serverClaimsConfig = OpenPACServerAPI
                .get(server)
                .getPlayerConfigManager()
                .getServerClaimConfig();
        IPlayerConfigAPI nexusSubConfig =
                serverClaimsConfig.getSubConfig(ASCENSION_NEXUS_SUBCLAIM_ID);
        if (nexusSubConfig == null) {
            System.err.println(
                    "[Skyblock Multi] OpenPAC: el claim del Nexo existe, pero no se encontró su subconfiguración."
            );
            return;
        }
        IPlayerConfigAPI.SetResult blockResult = nexusSubConfig.tryToSet(
                PlayerConfigOptions.CLAIM_EXCEPTION_BLOCKS_BY_PLAYERS,
                "E"
        );
        IPlayerConfigAPI.SetResult itemResult = nexusSubConfig.tryToSet(
                PlayerConfigOptions.CLAIM_EXCEPTION_ITEM_USE,
                "E"
        );
        IPlayerConfigAPI.SetResult entityResult = nexusSubConfig.tryToSet(
                PlayerConfigOptions.CLAIM_EXCEPTION_ENTITIES_BY_PLAYERS,
                "E"
        );
        System.out.println(
                "[Skyblock Multi] OpenPAC: interacciones del Nexo sincronizadas. "
                        + "bloques=" + blockResult + ", items=" + itemResult
                        + ", entidades=" + entityResult + "."
        );
    }

    private static boolean isAscensionNexusClaimPresent(MinecraftServer server) {

        if (!isInstalled() || server == null) {
            return false;
        }

        Identifier overworld = Identifier.parse("minecraft:overworld");

        IServerClaimsManagerAPI claims = OpenPACServerAPI
                .get(server)
                .getServerClaimsManager();

        // 0,0 pertenece únicamente al área central del Nexus y funciona como
        // marcador persistente de que la inicialización territorial ya ocurrió.
        IPlayerChunkClaimAPI center = claims.get(overworld, 0, 0);

        return center != null
                && SERVER_CLAIM_UUID.equals(center.getPlayerId());
    }

    /**
     * Server Claim permanente para la zona central.
     *
     * Incluye exactamente los chunks que intersectan el círculo físico del
     * domo de radio 45. OpenPAC protege toda la columna de esos chunks, pero
     * la fortaleza exterior depende únicamente de la protección nativa.
     *
     * Este claim nunca se transforma en player claim.
     */
    private static boolean reserveAscensionNexus(MinecraftServer server) {

        if (!isInstalled() || server == null) {
            return false;
        }

        Identifier overworld = Identifier.parse("minecraft:overworld");

        OpenPACServerAPI api = OpenPACServerAPI.get(server);

        IServerClaimsManagerAPI claims = api.getServerClaimsManager();

        /*
         * Usamos un sub-claim propio del servidor para que SOLO el centro
         * aparezca con el nombre "The Ascension Nexus".
         *
         * Los futuros slots continúan usando la configuración principal
         * del Server Claim y, por tanto, no heredan este nombre.
         */
        IPlayerConfigAPI serverClaimsConfig = api
                .getPlayerConfigManager()
                .getServerClaimConfig();

        // Identidad visual inicial de los territorios SkyblockMulti.
        // Las reservas de futuras islas usan el Server Claim principal (verde).
        // The Ascension Nexus usa su sub-claim propio (celeste).
        IPlayerConfigAPI.SetResult serverColorResult = serverClaimsConfig.tryToSet(
                PlayerConfigOptions.CLAIMS_COLOR,
                RESERVED_ISLAND_CLAIM_COLOR
        );

        IPlayerConfigAPI nexusSubConfig =
                serverClaimsConfig.getSubConfig(ASCENSION_NEXUS_SUBCLAIM_ID);

        if (nexusSubConfig == null) {
            nexusSubConfig =
                    serverClaimsConfig.createSubConfig(ASCENSION_NEXUS_SUBCLAIM_ID);
        }

        if (nexusSubConfig == null) {
            System.err.println(
                    "[Skyblock Multi] OpenPAC: no fue posible crear el sub-claim "
                            + ASCENSION_NEXUS_SUBCLAIM_ID
                            + " para "
                            + ASCENSION_NEXUS_CANONICAL_NAME
                            + "."
            );
            return false;
        }

        IPlayerConfigAPI.SetResult nameResult = nexusSubConfig.tryToSet(
                PlayerConfigOptions.CLAIMS_NAME,
                ASCENSION_NEXUS_CANONICAL_NAME
        );

        IPlayerConfigAPI.SetResult nexusColorResult = nexusSubConfig.tryToSet(
                PlayerConfigOptions.CLAIMS_COLOR,
                ASCENSION_NEXUS_CLAIM_COLOR
        );

        /*
         * OpenPAC normally treats opening containers and trapdoors as a
         * protected block interaction. Let it delegate player interactions
         * inside the Nexus to SkyblockMulti's native protection instead:
         * native protection still denies breaking, placing and destructive
         * item use, while allowing the intended doors, lecterns and chests.
         */
        IPlayerConfigAPI.SetResult interactionResult = nexusSubConfig.tryToSet(
                PlayerConfigOptions.CLAIM_EXCEPTION_BLOCKS_BY_PLAYERS,
                "E"
        );
        IPlayerConfigAPI.SetResult itemUseResult = nexusSubConfig.tryToSet(
                PlayerConfigOptions.CLAIM_EXCEPTION_ITEM_USE,
                "E"
        );
        IPlayerConfigAPI.SetResult entityInteractionResult = nexusSubConfig.tryToSet(
                PlayerConfigOptions.CLAIM_EXCEPTION_ENTITIES_BY_PLAYERS,
                "E"
        );

        int nexusSubConfigIndex = nexusSubConfig.getSubIndex();

        int minChunk = Math.floorDiv(-ASCENSION_NEXUS_DOME_RADIUS_BLOCKS, 16);
        int maxChunk = Math.floorDiv(ASCENSION_NEXUS_DOME_RADIUS_BLOCKS, 16);

        /*
         * Primer pase: seguridad.
         * Se revisan únicamente los chunks que forman el círculo.
         * Nunca se pisa un claim de jugador.
         */
        for (int x = minChunk; x <= maxChunk; x++) {
            for (int z = minChunk; z <= maxChunk; z++) {

                if (!chunkIntersectsNexusDome(x, z)) {
                    continue;
                }

                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);

                if (existing != null
                        && !SERVER_CLAIM_UUID.equals(existing.getPlayerId())) {

                    System.err.println(
                            "[Skyblock Multi] OpenPAC: no fue posible reservar completamente "
                                    + ASCENSION_NEXUS_CANONICAL_NAME
                                    + ". El chunk "
                                    + x + "," + z
                                    + " pertenece a "
                                    + existing.getPlayerId()
                                    + "."
                    );

                    return false;
                }
            }
        }

        int claimedChunks = 0;

        /*
         * Segundo pase: reclamar cada chunk que contenga al menos un bloque
         * dentro del círculo físico del domo.
         */
        for (int x = minChunk; x <= maxChunk; x++) {
            for (int z = minChunk; z <= maxChunk; z++) {

                if (!chunkIntersectsNexusDome(x, z)) {
                    continue;
                }

                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);

                boolean alreadyCorrectNexusClaim =
                        existing != null
                                && SERVER_CLAIM_UUID.equals(existing.getPlayerId())
                                && existing.getSubConfigIndex() == nexusSubConfigIndex;

                if (!alreadyCorrectNexusClaim) {
                    claims.claim(
                            overworld,
                            SERVER_CLAIM_UUID,
                            nexusSubConfigIndex,
                            x,
                            z,
                            false
                    );
                }

                claimedChunks++;
            }
        }

        int removedLegacyChunks = 0;
        for (int x = -LEGACY_NEXUS_SCAN_RADIUS_CHUNKS;
             x <= LEGACY_NEXUS_SCAN_RADIUS_CHUNKS; x++) {
            for (int z = -LEGACY_NEXUS_SCAN_RADIUS_CHUNKS;
                 z <= LEGACY_NEXUS_SCAN_RADIUS_CHUNKS; z++) {
                if (chunkIntersectsNexusDome(x, z)) {
                    continue;
                }

                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);
                if (existing != null
                        && SERVER_CLAIM_UUID.equals(existing.getPlayerId())
                        && existing.getSubConfigIndex() == nexusSubConfigIndex) {
                    claims.unclaim(overworld, x, z);
                    removedLegacyChunks++;
                }
            }
        }

        System.out.println(
                "[Skyblock Multi] OpenPAC: "
                        + ASCENSION_NEXUS_CANONICAL_NAME
                        + " reservado permanentemente alrededor del domo. "
                        + "Radio=" + ASCENSION_NEXUS_DOME_RADIUS_BLOCKS
                        + " bloques, chunks reclamados=" + claimedChunks
                        + ", chunks heredados retirados=" + removedLegacyChunks
                        + ", sub-claim=" + ASCENSION_NEXUS_SUBCLAIM_ID
                        + ", nombre_resultado=" + nameResult
                        + ", color_nexus_resultado=" + nexusColorResult
                        + ", interacciones_resultado=" + interactionResult
                        + ", uso_items_resultado=" + itemUseResult
                        + ", entidades_resultado=" + entityInteractionResult
                        + ", color_reservas_resultado=" + serverColorResult
                        + "."
        );

        return true;
    }

    private static boolean chunkIntersectsNexusDome(int chunkX, int chunkZ) {
        int closestX = closestCoordinateToOrigin(chunkX);
        int closestZ = closestCoordinateToOrigin(chunkZ);
        int radius = ASCENSION_NEXUS_DOME_RADIUS_BLOCKS;
        return closestX * closestX + closestZ * closestZ <= radius * radius;
    }

    private static int closestCoordinateToOrigin(int chunkCoordinate) {
        int min = chunkCoordinate * 16;
        int max = min + 15;
        if (min > 0) {
            return min;
        }
        if (max < 0) {
            return -max;
        }
        return 0;
    }

    /**
     * Reserva un futuro slot como Server Claim 5x5.
     *
     * IMPORTANTE:
     * solo funciona durante la primera inicialización territorial del mundo.
     * En reinicios posteriores retorna false sin modificar ningún claim.
     */
    public static boolean reserveIslandSlot(
            MinecraftServer server,
            int blockX,
            int blockZ
    ) {

        if (!isInstalled() || server == null) {
            return false;
        }

        if (!shouldInitializeWorldClaims(server)) {
            return false;
        }

        int chunkX = Math.floorDiv(blockX, 16);
        int chunkZ = Math.floorDiv(blockZ, 16);

        int left = chunkX - RESERVED_RADIUS_CHUNKS;
        int top = chunkZ - RESERVED_RADIUS_CHUNKS;
        int right = chunkX + RESERVED_RADIUS_CHUNKS;
        int bottom = chunkZ + RESERVED_RADIUS_CHUNKS;

        Identifier overworld = Identifier.parse("minecraft:overworld");

        IServerClaimsManagerAPI claims = OpenPACServerAPI
                .get(server)
                .getServerClaimsManager();

        // No sobrescribir nunca un claim de jugador.
        for (int x = left; x <= right; x++) {
            for (int z = top; z <= bottom; z++) {

                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);

                if (existing != null
                        && !SERVER_CLAIM_UUID.equals(existing.getPlayerId())) {

                    System.out.println(
                            "[Skyblock Multi] OpenPAC: reserva 5x5 omitida en "
                                    + blockX + "," + blockZ
                                    + " porque el chunk "
                                    + x + "," + z
                                    + " ya pertenece a "
                                    + existing.getPlayerId()
                                    + "."
                    );

                    return false;
                }
            }
        }

        for (int x = left; x <= right; x++) {
            for (int z = top; z <= bottom; z++) {

                if (claims.get(overworld, x, z) == null) {
                    claims.claim(
                            overworld,
                            SERVER_CLAIM_UUID,
                            -1,
                            x,
                            z,
                            false
                    );
                }
            }
        }

        System.out.println(
                "[Skyblock Multi] OpenPAC: slot reservado como Server Claim 5x5 en chunks "
                        + left + "," + top
                        + " -> "
                        + right + "," + bottom
                        + "."
        );

        return true;
    }

    /**
     * Convierte la reserva 5x5 de un slot en claim personal 3x3.
     *
     * El 3x3 pasa al jugador y los 16 chunks exteriores dejan de ser Server Claim.
     */
    public static boolean assignReservedIsland(
            ServerPlayer player,
            int blockX,
            int blockZ
    ) {

        if (!isInstalled()) {
            return true;
        }

        MinecraftServer server = player.level().getServer();

        if (server == null) {
            return false;
        }

        int chunkX = Math.floorDiv(blockX, 16);
        int chunkZ = Math.floorDiv(blockZ, 16);

        int reserveLeft = chunkX - RESERVED_RADIUS_CHUNKS;
        int reserveTop = chunkZ - RESERVED_RADIUS_CHUNKS;
        int reserveRight = chunkX + RESERVED_RADIUS_CHUNKS;
        int reserveBottom = chunkZ + RESERVED_RADIUS_CHUNKS;

        int playerLeft = chunkX - PLAYER_RADIUS_CHUNKS;
        int playerTop = chunkZ - PLAYER_RADIUS_CHUNKS;
        int playerRight = chunkX + PLAYER_RADIUS_CHUNKS;
        int playerBottom = chunkZ + PLAYER_RADIUS_CHUNKS;

        Identifier overworld = Identifier.parse("minecraft:overworld");

        IServerClaimsManagerAPI claims = OpenPACServerAPI
                .get(server)
                .getServerClaimsManager();

        UUID playerUuid = player.getUUID();

        // El 3x3 central jamás puede pisar un claim de otro jugador.
        for (int x = playerLeft; x <= playerRight; x++) {
            for (int z = playerTop; z <= playerBottom; z++) {

                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);

                if (existing != null
                        && !SERVER_CLAIM_UUID.equals(existing.getPlayerId())
                        && !playerUuid.equals(existing.getPlayerId())) {

                    System.err.println(
                            "[Skyblock Multi] OpenPAC: NO se asignó el claim 3x3 de "
                                    + player.getGameProfile().name()
                                    + ". El chunk "
                                    + x + "," + z
                                    + " pertenece a otro jugador: "
                                    + existing.getPlayerId()
                    );

                    return false;
                }
            }
        }

        // Transferir el 3x3 central al jugador.
        for (int x = playerLeft; x <= playerRight; x++) {
            for (int z = playerTop; z <= playerBottom; z++) {

                claims.claim(
                        overworld,
                        playerUuid,
                        -1,
                        x,
                        z,
                        false
                );
            }
        }

        // Liberar el anillo exterior del antiguo 5x5.
        for (int x = reserveLeft; x <= reserveRight; x++) {
            for (int z = reserveTop; z <= reserveBottom; z++) {

                boolean insidePlayerClaim =
                        x >= playerLeft
                                && x <= playerRight
                                && z >= playerTop
                                && z <= playerBottom;

                if (insidePlayerClaim) {
                    continue;
                }

                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);

                if (existing != null
                        && SERVER_CLAIM_UUID.equals(existing.getPlayerId())) {

                    claims.unclaim(overworld, x, z);
                }
            }
        }

        System.out.println(
                "[Skyblock Multi] OpenPAC: reserva 5x5 transferida a "
                        + player.getGameProfile().name()
                        + ". Claim personal 3x3: "
                        + playerLeft + "," + playerTop
                        + " -> "
                        + playerRight + "," + playerBottom
                        + "."
        );

        return true;
    }

    /**
     * Método antiguo conservado para compatibilidad.
     */
    public static void claimInitialIsland(
            ServerPlayer player,
            int blockX,
            int blockZ
    ) {

        if (!isInstalled()) {
            return;
        }

        MinecraftServer server = player.level().getServer();

        if (server == null) {
            return;
        }

        int chunkX = Math.floorDiv(blockX, 16);
        int chunkZ = Math.floorDiv(blockZ, 16);

        int left = chunkX - 1;
        int top = chunkZ - 1;
        int right = chunkX + 1;
        int bottom = chunkZ + 1;

        Identifier overworld = Identifier.parse("minecraft:overworld");

        AreaClaimResult result = OpenPACServerAPI
                .get(server)
                .getServerClaimsManager()
                .tryToClaimArea(
                        overworld,
                        player.getUUID(),
                        0,
                        chunkX,
                        chunkZ,
                        left,
                        top,
                        right,
                        bottom,
                        false
                );

        System.out.println(
                "[Skyblock Multi] OpenPAC: claim inicial 3x3 solicitado para "
                        + player.getGameProfile().name()
                        + " en chunks "
                        + left + "," + top
                        + " -> "
                        + right + "," + bottom
                        + ". Resultados: "
                        + result.getResultTypesStream().toList()
        );
    }

    public record PartyInfo(
            boolean inParty,
            UUID partyId,
            UUID ownerUuid,
            String ownerName,
            boolean owner
    ) {

        public static PartyInfo noParty() {
            return new PartyInfo(
                    false,
                    null,
                    null,
                    null,
                    false
            );
        }
    }
}
