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

import java.util.UUID;

public final class OpenPacCompat {

    public static final String MOD_ID = "openpartiesandclaims";
    private static final UUID SERVER_CLAIM_UUID = new UUID(0L, 0L);
    private static final int RESERVED_RADIUS_CHUNKS = 2; // 5x5
    private static final int PLAYER_RADIUS_CHUNKS = 1;   // 3x3

    private OpenPacCompat() {
    }

    public static boolean isInstalled() {
        return FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static void initialize() {
        if (isInstalled()) {
            System.out.println("[SkyblockMulti] OpenPAC detectado; integración opcional disponible.");
        } else {
            System.out.println("[SkyblockMulti] OpenPAC no instalado; integración desactivada.");
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

    public static boolean reserveIslandSlot(MinecraftServer server, int blockX, int blockZ) {
        if (!isInstalled() || server == null) {
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

        for (int x = left; x <= right; x++) {
            for (int z = top; z <= bottom; z++) {
                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);
                if (existing != null && !SERVER_CLAIM_UUID.equals(existing.getPlayerId())) {
                    System.out.println(
                            "[SkyblockMulti] OpenPAC: reserva 5x5 omitida en "
                                    + blockX + "," + blockZ
                                    + " porque el chunk " + x + "," + z
                                    + " ya pertenece a " + existing.getPlayerId() + "."
                    );
                    return false;
                }
            }
        }

        for (int x = left; x <= right; x++) {
            for (int z = top; z <= bottom; z++) {
                if (claims.get(overworld, x, z) == null) {
                    claims.claim(overworld, SERVER_CLAIM_UUID, 0, x, z, false);
                }
            }
        }

        System.out.println(
                "[SkyblockMulti] OpenPAC: slot reservado como Server Claim 5x5 en chunks "
                        + left + "," + top + " -> " + right + "," + bottom + "."
        );

        return true;
    }

    public static boolean assignReservedIsland(ServerPlayer player, int blockX, int blockZ) {
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

        for (int x = playerLeft; x <= playerRight; x++) {
            for (int z = playerTop; z <= playerBottom; z++) {
                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);

                if (existing != null
                        && !SERVER_CLAIM_UUID.equals(existing.getPlayerId())
                        && !playerUuid.equals(existing.getPlayerId())) {
                    System.err.println(
                            "[SkyblockMulti] OpenPAC: NO se asignó el claim 3x3 de "
                                    + player.getGameProfile().name()
                                    + ". El chunk " + x + "," + z
                                    + " pertenece a otro jugador: "
                                    + existing.getPlayerId()
                    );
                    return false;
                }
            }
        }

        for (int x = playerLeft; x <= playerRight; x++) {
            for (int z = playerTop; z <= playerBottom; z++) {
                claims.claim(overworld, playerUuid, 0, x, z, false);
            }
        }

        for (int x = reserveLeft; x <= reserveRight; x++) {
            for (int z = reserveTop; z <= reserveBottom; z++) {
                boolean insidePlayerClaim =
                        x >= playerLeft && x <= playerRight
                                && z >= playerTop && z <= playerBottom;

                if (insidePlayerClaim) {
                    continue;
                }

                IPlayerChunkClaimAPI existing = claims.get(overworld, x, z);
                if (existing != null && SERVER_CLAIM_UUID.equals(existing.getPlayerId())) {
                    claims.unclaim(overworld, x, z);
                }
            }
        }

        System.out.println(
                "[SkyblockMulti] OpenPAC: reserva 5x5 transferida a "
                        + player.getGameProfile().name()
                        + ". Claim personal 3x3: "
                        + playerLeft + "," + playerTop
                        + " -> " + playerRight + "," + playerBottom + "."
        );

        return true;
    }

    public static void claimInitialIsland(ServerPlayer player, int blockX, int blockZ) {
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
                "[SkyblockMulti] OpenPAC: claim inicial 3x3 solicitado para "
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
            return new PartyInfo(false, null, null, null, false);
        }
    }
}
