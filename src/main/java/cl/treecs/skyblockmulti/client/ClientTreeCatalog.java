package cl.treecs.skyblockmulti.client;

import cl.treecs.skyblockmulti.network.TreeCatalogPayload;

import java.util.List;

/** Copia de solo lectura del último catálogo enviado por el servidor. */
public final class ClientTreeCatalog {
    private static volatile List<TreeCatalogPayload.Entry> entries = List.of();

    private ClientTreeCatalog() {
    }

    public static void replace(List<TreeCatalogPayload.Entry> received) {
        entries = List.copyOf(received);
    }

    public static List<TreeCatalogPayload.Entry> entries() {
        return entries;
    }

    public static void clear() {
        entries = List.of();
    }
}
