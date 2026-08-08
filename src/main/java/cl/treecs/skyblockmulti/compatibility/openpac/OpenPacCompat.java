package cl.treecs.skyblockmulti.compatibility.openpac;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.parties.party.api.IServerPartyAPI;

import java.util.UUID;
import net.minecraft.resources.Identifier;
import xaero.pac.common.claims.result.api.AreaClaimResult;

public final class OpenPacCompat {

    public static final String MOD_ID = "openpartiesandclaims";

    private OpenPacCompat() {
    }

    public static boolean isInstalled() {
        return FabricLoader.getInstance().isModLoaded(MOD_ID);
    }

    public static void initialize() {
        if (isInstalled()) {
            System.out.println(
                    "[SkyblockMulti] OpenPAC detectado; integración opcional disponible."
            );
        } else {
            System.out.println(
                    "[SkyblockMulti] OpenPAC no instalado; integración desactivada."
            );
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