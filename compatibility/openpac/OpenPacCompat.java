package cl.treecs.skyblockmulti.compatibility.openpac;

import net.fabricmc.loader.api.FabricLoader;

public final class OpenPacCompat {

    public static final String MOD_ID = "openpartiesandclaims";

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
}