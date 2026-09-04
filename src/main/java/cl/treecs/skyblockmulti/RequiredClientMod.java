package cl.treecs.skyblockmulti;

import cl.treecs.skyblockmulti.network.RequiredClientPayload;
import cl.treecs.skyblockmulti.network.TreeCatalogPayload;
import cl.treecs.skyblockmulti.network.TreeSelectPayload;
import cl.treecs.skyblockmulti.network.OpenTreeSelectionPayload;
import cl.treecs.skyblockmulti.network.OpenDifficultySelectionPayload;
import cl.treecs.skyblockmulti.network.DifficultySelectPayload;
import cl.treecs.skyblockmulti.network.BackToTreeSelectionPayload;
import cl.treecs.skyblockmulti.tree.PlayerTreeSelectionState;
import cl.treecs.skyblockmulti.tree.TreeCatalog;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.ScoreHolder;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;

public final class RequiredClientMod implements ModInitializer {
    private static final Set<UUID> DIFFICULTY_SCREEN_SENT = new HashSet<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundConfiguration().register(
                RequiredClientPayload.TYPE,
                RequiredClientPayload.CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                TreeCatalogPayload.TYPE,
                TreeCatalogPayload.CODEC
        );
        PayloadTypeRegistry.serverboundPlay().register(
                TreeSelectPayload.TYPE,
                TreeSelectPayload.CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                OpenTreeSelectionPayload.TYPE,
                OpenTreeSelectionPayload.CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                OpenDifficultySelectionPayload.TYPE,
                OpenDifficultySelectionPayload.CODEC
        );
        PayloadTypeRegistry.serverboundPlay().register(
                DifficultySelectPayload.TYPE,
                DifficultySelectPayload.CODEC
        );
        PayloadTypeRegistry.serverboundPlay().register(
                BackToTreeSelectionPayload.TYPE,
                BackToTreeSelectionPayload.CODEC
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("skyblockmulti").then(
                        literal("trees").executes(command -> {
                            if (!(command.getSource().getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) {
                                return 0;
                            }
                            if (isAwaitingTreeSelection(player) && areIslandAnchorsReady(player)
                                    && ServerPlayNetworking.canSend(player, OpenTreeSelectionPayload.TYPE)) {
                                ServerPlayNetworking.send(player, OpenTreeSelectionPayload.INSTANCE);
                                return 1;
                            }
                            if (isAwaitingDifficultySelection(player)
                                    && ServerPlayNetworking.canSend(player, OpenDifficultySelectionPayload.TYPE)) {
                                ServerPlayNetworking.send(player, OpenDifficultySelectionPayload.INSTANCE);
                                return 1;
                            }
                            return 0;
                        })
                ))
        );

        ServerPlayNetworking.registerGlobalReceiver(
                TreeSelectPayload.TYPE,
                (payload, context) -> context.server().execute(() -> {
                    boolean selectionAllowed = isAwaitingTreeSelection(context)
                            && areIslandAnchorsReady(context.player());
                    Map<String, Boolean> selectableStates = selectableTreeStates();
                    PlayerTreeSelectionState.Result result;
                    if (TreeSelectPayload.RANDOM_SELECTION.equals(payload.treeId())) {
                        result = PlayerTreeSelectionState.selectRandom(
                                context.player().getUUID(),
                                selectableStates,
                                bound -> context.player().getRandom().nextInt(bound),
                                selectionAllowed
                        );
                    } else {
                        result = PlayerTreeSelectionState.select(
                                context.player().getUUID(),
                                payload.treeId(),
                                selectableStates,
                                selectionAllowed
                        );
                    }
                    if (result == PlayerTreeSelectionState.Result.ACCEPTED) {
                        PlayerTreeSelectionState.get(context.player().getUUID())
                                .flatMap(TreeCatalog::find)
                                .ifPresent(tree -> applyLegacyTrigger(context, tree.legacyTriggerValue()));
                    }
                })
        );
        ServerPlayNetworking.registerGlobalReceiver(
                DifficultySelectPayload.TYPE,
                (payload, context) -> context.server().execute(() -> {
                    if (!isAwaitingDifficultySelection(context.player())
                            || payload.value() < 1 || payload.value() > 4) {
                        return;
                    }
                    var objective = context.server().getScoreboard().getObjective("sb_difficulty");
                    if (objective != null) {
                        DIFFICULTY_SCREEN_SENT.remove(context.player().getUUID());
                        context.server().getScoreboard()
                                .getOrCreatePlayerScore(context.player(), objective)
                                .set(payload.value());
                    }
                })
        );
        ServerPlayNetworking.registerGlobalReceiver(
                BackToTreeSelectionPayload.TYPE,
                (payload, context) -> context.server().execute(() -> {
                    if (!isAwaitingDifficultySelection(context.player())) return;

                    var scoreboard = context.server().getScoreboard();
                    var stateObjective = scoreboard.getObjective("sb3_state");
                    var treeObjective = scoreboard.getObjective("sb_tree");
                    var difficultyObjective = scoreboard.getObjective("sb_difficulty");
                    if (stateObjective == null || treeObjective == null || difficultyObjective == null) return;

                    scoreboard.getOrCreatePlayerScore(context.player(), stateObjective).set(1);
                    scoreboard.getOrCreatePlayerScore(context.player(), treeObjective).set(0);
                    scoreboard.getOrCreatePlayerScore(context.player(), difficultyObjective).set(0);
                    PlayerTreeSelectionState.clear(context.player().getUUID());
                    DIFFICULTY_SCREEN_SENT.remove(context.player().getUUID());

                    if (ServerPlayNetworking.canSend(context.player(), OpenTreeSelectionPayload.TYPE)) {
                        ServerPlayNetworking.send(context.player(), OpenTreeSelectionPayload.INSTANCE);
                    }
                })
        );

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (ServerPlayNetworking.canSend(player, TreeCatalogPayload.TYPE)) {
                ServerPlayNetworking.send(
                        player,
                        TreeCatalogPayload.create(SkyblockMultiMod.getConfiguredTreeStatesById())
                );
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            var scoreboard = server.getScoreboard();
            var objective = scoreboard.getObjective("sb3_state");
            if (objective == null) return;
            for (var player : server.getPlayerList().getPlayers()) {
                var score = scoreboard.getPlayerScoreInfo(player, objective);
                boolean selectingDifficulty = score != null && score.value() == 4;
                if (!selectingDifficulty) {
                    DIFFICULTY_SCREEN_SENT.remove(player.getUUID());
                } else if (DIFFICULTY_SCREEN_SENT.add(player.getUUID())
                        && ServerPlayNetworking.canSend(player, OpenDifficultySelectionPayload.TYPE)) {
                    ServerPlayNetworking.send(player, OpenDifficultySelectionPayload.INSTANCE);
                }
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                DIFFICULTY_SCREEN_SENT.remove(handler.getPlayer().getUUID())
        );
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            DIFFICULTY_SCREEN_SENT.clear();
        });

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (!ServerConfigurationNetworking.canSend(handler, RequiredClientPayload.TYPE)) {
                handler.disconnect(Component.literal(
                        "Skyblock Multi debe estar instalado en tu cliente. "
                                + "Instala la misma versión del mod, Fabric Loader y Fabric API. / "
                                + "Skyblock Multi must be installed on your client. "
                                + "Install the same mod version, Fabric Loader, and Fabric API."
                ));
            }
        });
    }

    private static boolean isAwaitingTreeSelection(ServerPlayNetworking.Context context) {
        return isAwaitingTreeSelection(context.player());
    }

    private static boolean isAwaitingTreeSelection(net.minecraft.server.level.ServerPlayer player) {
        return selectionState(player) == 1;
    }

    private static boolean isAwaitingDifficultySelection(net.minecraft.server.level.ServerPlayer player) {
        return selectionState(player) == 4;
    }

    private static boolean areIslandAnchorsReady(net.minecraft.server.level.ServerPlayer player) {
        var scoreboard = player.level().getScoreboard();
        var objective = scoreboard.getObjective("sb3_const");
        if (objective == null) return false;
        var score = scoreboard.getPlayerScoreInfo(ScoreHolder.forNameOnly("#slots_built"), objective);
        return score != null && score.value() == 1;
    }

    private static int selectionState(net.minecraft.server.level.ServerPlayer player) {
        var scoreboard = player.level().getScoreboard();
        var objective = scoreboard.getObjective("sb3_state");
        if (objective == null) return Integer.MIN_VALUE;
        var score = scoreboard.getPlayerScoreInfo(player, objective);
        return score == null ? Integer.MIN_VALUE : score.value();
    }

    private static Map<String, Boolean> selectableTreeStates() {
        Map<String, Boolean> states = new LinkedHashMap<>(SkyblockMultiMod.getConfiguredTreeStatesById());
        states.replaceAll((id, enabled) -> enabled && TreeCatalog.find(id).isPresent());
        return states;
    }

    private static void applyLegacyTrigger(ServerPlayNetworking.Context context, int triggerValue) {
        var scoreboard = context.server().getScoreboard();
        var objective = scoreboard.getObjective("sb_tree");
        if (objective != null) {
            scoreboard.getOrCreatePlayerScore(context.player(), objective).set(triggerValue);
        }
    }
}
