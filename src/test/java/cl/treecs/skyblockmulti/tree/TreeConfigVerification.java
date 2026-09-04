package cl.treecs.skyblockmulti.tree;

import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import cl.treecs.skyblockmulti.network.TreeCatalogPayload;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/** Verificación sin dependencias externas para el contrato de migración de árboles. */
public final class TreeConfigVerification {
    private TreeConfigVerification() {
    }

    public static void main(String[] args) {
        require(TreeCatalog.builtIns().size() == 11, "El catálogo vanilla debe conservar 11 árboles");
        require(TreeCatalog.find("minecraft:oak").isPresent(), "Debe resolver IDs estables");
        require(TreeCatalog.findLegacyTrigger(11).orElseThrow().id().equals("minecraft:flowering_azalea"),
                "Debe conservar los triggers legacy");

        String legacyJson = "{\"enabledTrees\":{\"oak\":false,\"spruce\":true}}";
        Map<String, Boolean> migrated = TreeConfigCodec.readStates(legacyJson);
        require(Boolean.FALSE.equals(migrated.get("minecraft:oak")), "Debe migrar claves legacy");
        require(Boolean.TRUE.equals(migrated.get("minecraft:spruce")), "Debe conservar estados legacy");

        String extensibleJson = "{\"enabledTrees\":{\"minecraft:oak\":true,\"example:redwood\":false}}";
        Map<String, Boolean> extensible = TreeConfigCodec.readStates(extensibleJson);
        require(Boolean.FALSE.equals(extensible.get("example:redwood")), "Debe preservar IDs de addons");

        Map<String, Boolean> disabled = new LinkedHashMap<>();
        TreeCatalog.builtIns().forEach(tree -> disabled.put(tree.id(), false));
        disabled.put("example:unavailable", true);
        Map<String, Boolean> safe = TreeConfigCodec.normalize(disabled);
        require(Boolean.TRUE.equals(safe.get("minecraft:oak")),
                "Contenido ausente no puede dejar el selector sin árboles disponibles");
        require(Boolean.TRUE.equals(safe.get("example:unavailable")),
                "La configuración de un addon ausente debe conservarse");

        require(TreeConfigCodec.normalizeId("oak").equals("minecraft:oak"),
                "Debe normalizar claves legacy");
        require(TreeConfigCodec.normalizeId("Example:Redwood").equals("example:redwood"),
                "Debe normalizar IDs namespaced");
        require(TreeConfigCodec.normalizeId("invalid id") == null, "Debe rechazar IDs inválidos");

        String dataJson = """
                {
                  "schema_version": 1,
                  "display": {
                    "name": {"text": "Redwood"},
                    "icon": {"item": "example:redwood_sapling"}
                  },
                  "placement": {
                    "type": "skyblockmulti:sapling",
                    "item": "example:redwood_sapling",
                    "pattern": "single"
                  },
                  "categories": ["example:large"],
                  "enabled_by_default": true
                }
                """;
        DataTreeDefinition redwood = TreeDefinitionReloadListener.parse(
                "example:redwood",
                JsonParser.parseString(dataJson).getAsJsonObject()
        );
        TreeCatalog.replaceDataPackDefinitions(Map.of(redwood.id(), redwood));
        try {
            Map<String, Boolean> activeStates = TreeConfigCodec.defaultStates();
            require(Boolean.TRUE.equals(activeStates.get("example:redwood")),
                    "Un datapack debe incorporarse al catálogo activo");
            for (int i = 0; i < 100; i++) {
                DataTreeDefinition selected = TreeCatalog.chooseRandom(activeStates, new Random(i));
                require(activeStates.containsKey(selected.id()),
                        "La selección uniforme solo puede devolver opciones habilitadas");
            }

            UUID playerId = UUID.randomUUID();
            require(PlayerTreeSelectionState.select(
                            playerId, "example:redwood", activeStates, false
                    ) == PlayerTreeSelectionState.Result.NOT_IN_SELECTION,
                    "El servidor debe rechazar selecciones fuera del flujo inicial");
            require(PlayerTreeSelectionState.select(
                            playerId, "example:redwood", activeStates, true
                    ) == PlayerTreeSelectionState.Result.ACCEPTED,
                    "El servidor debe aceptar un ID conocido y habilitado");
            require(PlayerTreeSelectionState.get(playerId).orElseThrow().equals("example:redwood"),
                    "La selección temporal debe conservar el ID estable");

            Map<String, Boolean> disabledRedwood = new LinkedHashMap<>(activeStates);
            disabledRedwood.put("example:redwood", false);
            require(PlayerTreeSelectionState.select(
                            playerId, "example:redwood", disabledRedwood, true
                    ) == PlayerTreeSelectionState.Result.DISABLED_TREE,
                    "El servidor debe rechazar árboles deshabilitados");
            require(PlayerTreeSelectionState.select(
                            playerId, "example:missing", activeStates, true
                    ) == PlayerTreeSelectionState.Result.UNKNOWN_TREE,
                    "El servidor debe rechazar árboles inexistentes");

            require(PlayerTreeSelectionState.selectRandom(
                            playerId, activeStates, bound -> 0, true
                    ) == PlayerTreeSelectionState.Result.ACCEPTED,
                    "La selección aleatoria debe resolverse en el servidor");
            require(activeStates.containsKey(PlayerTreeSelectionState.get(playerId).orElseThrow()),
                    "La selección aleatoria debe producir un ID del catálogo activo");
            PlayerTreeSelectionState.clear(playerId);
            require(PlayerTreeSelectionState.get(playerId).isEmpty(),
                    "La selección temporal debe poder limpiarse al desconectar");

            TreeCatalogPayload payload = TreeCatalogPayload.create(activeStates);
            require(payload.entries().stream().anyMatch(entry -> entry.id().equals("example:redwood")),
                    "La sincronización debe incluir definiciones externas válidas");
        } finally {
            TreeCatalog.replaceDataPackDefinitions(Map.of());
        }

        String weightedJson = dataJson.replace(
                "\"schema_version\": 1,",
                "\"schema_version\": 1, \"random_weight\": 5,"
        );
        boolean rejectedWeight = false;
        try {
            TreeDefinitionReloadListener.parse(
                    "example:weighted",
                    JsonParser.parseString(weightedJson).getAsJsonObject()
            );
        } catch (IllegalArgumentException expected) {
            rejectedWeight = true;
        }
        require(rejectedWeight, "El esquema debe rechazar pesos aleatorios");

        BlockPos origin = new BlockPos(10, 65, -20);
        require(TreePlacementService.saplingPositions(
                        origin,
                        DataTreeDefinition.SaplingPlacement.Pattern.SINGLE
                ).equals(java.util.List.of(origin)),
                "El patrón single debe ocupar una posición");
        require(TreePlacementService.saplingPositions(
                        origin,
                        DataTreeDefinition.SaplingPlacement.Pattern.TWO_BY_TWO
                ).equals(java.util.List.of(origin, origin.east(), origin.south(), origin.east().south())),
                "El patrón two_by_two debe ocupar cuatro posiciones contiguas");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
