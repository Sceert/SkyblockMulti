package cl.treecs.skyblockmulti;

import cl.treecs.skyblockmulti.compatibility.openpac.OpenPacCompat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;

/**
 * Overlay de diagnóstico activable por jugador.
 *
 * Comandos:
 *   /skyblockmulti debug       -> alterna ON/OFF
 *   /skyblockmulti debug on    -> activa
 *   /skyblockmulti debug off   -> desactiva
 *
 * Formato:
 * SB | State: 2 | Slot: 4 | OWN X: -2048 Z: 0 | ACTIVE X: 2048 Z: 0 | Party: Yes | Leader: No
 */
public final class SkyblockDebugOverlay implements ModInitializer {

    private static final int UPDATE_INTERVAL_TICKS = 20;
    private static final Set<UUID> DEBUG_PLAYERS = new HashSet<>();
    private static int updateTicks = 0;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        literal("skyblockmulti")
                                .then(
                                        literal("debug")
                                                .executes(context -> {
                                                    if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                                                        return 0;
                                                    }

                                                    if (DEBUG_PLAYERS.contains(player.getUUID())) {
                                                        disableDebug(context.getSource().getServer(), player);
                                                    } else {
                                                        enableDebug(context.getSource().getServer(), player);
                                                    }
                                                    return 1;
                                                })
                                                .then(
                                                        literal("on")
                                                                .executes(context -> {
                                                                    if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                                                                        return 0;
                                                                    }
                                                                    enableDebug(context.getSource().getServer(), player);
                                                                    return 1;
                                                                })
                                                )
                                                .then(
                                                        literal("off")
                                                                .executes(context -> {
                                                                    if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                                                                        return 0;
                                                                    }
                                                                    disableDebug(context.getSource().getServer(), player);
                                                                    return 1;
                                                                })
                                                )
                                )
                )
        );

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                DEBUG_PLAYERS.remove(handler.player.getUUID())
        );

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (DEBUG_PLAYERS.isEmpty()) {
                updateTicks = 0;
                return;
            }

            updateTicks++;
            if (updateTicks < UPDATE_INTERVAL_TICKS) {
                return;
            }
            updateTicks = 0;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (DEBUG_PLAYERS.contains(player.getUUID())) {
                    showOverlay(server, player);
                }
            }
        });

        System.out.println("[SkyblockMulti] Debug overlay disponible con /skyblockmulti debug");
    }

    private static void enableDebug(MinecraftServer server, ServerPlayer player) {
        DEBUG_PLAYERS.add(player.getUUID());
        player.sendSystemMessage(Component.literal("[SkyblockMulti] Debug overlay: ON"));
        showOverlay(server, player);
    }

    private static void disableDebug(MinecraftServer server, ServerPlayer player) {
        DEBUG_PLAYERS.remove(player.getUUID());
        clearOverlay(server, player);
        player.sendSystemMessage(Component.literal("[SkyblockMulti] Debug overlay: OFF"));
    }

    private static void showOverlay(MinecraftServer server, ServerPlayer player) {
        OpenPacCompat.PartyInfo partyInfo = OpenPacCompat.isInstalled()
                ? OpenPacCompat.getPartyInfo(player)
                : OpenPacCompat.PartyInfo.noParty();

        boolean inParty = partyInfo.inParty();
        boolean leader = inParty && partyInfo.owner();
        String playerName = player.getGameProfile().name();

        String partyText = inParty ? "Yes" : "No";
        String partyColor = inParty ? "green" : "red";
        String leaderText = leader ? "Yes" : "No";
        String leaderColor = leader ? "gold" : "gray";

        String json = "["
                + "{\"text\":\"SB\",\"color\":\"aqua\",\"bold\":true},"
                + "{\"text\":\" | State: \",\"color\":\"gray\"},"
                + score(playerName, "sb3_state", "white") + ","
                + "{\"text\":\" | Slot: \",\"color\":\"gray\"},"
                + score(playerName, "sb3_slot", "yellow") + ","
                + "{\"text\":\" | OWN X: \",\"color\":\"gray\"},"
                + score(playerName, "sb3_x", "white") + ","
                + "{\"text\":\" Z: \",\"color\":\"gray\"},"
                + score(playerName, "sb3_z", "white") + ","
                + "{\"text\":\" | ACTIVE X: \",\"color\":\"gray\"},"
                + score(playerName, "sb_active_x", "green") + ","
                + "{\"text\":\" Z: \",\"color\":\"gray\"},"
                + score(playerName, "sb_active_z", "green") + ","
                + "{\"text\":\" | Party: \",\"color\":\"gray\"},"
                + jsonLiteral(partyText, partyColor) + ","
                + "{\"text\":\" | Leader: \",\"color\":\"gray\"},"
                + jsonLiteral(leaderText, leaderColor)
                + "]";

        try {
            new ServerCommandExecutor(server).run(
                    "title " + playerName + " actionbar " + json
            );
        } catch (Exception e) {
            System.err.println(
                    "[SkyblockMulti] No fue posible mostrar el debug overlay para "
                            + playerName + ": " + e
            );
        }
    }

    private static void clearOverlay(MinecraftServer server, ServerPlayer player) {
        try {
            new ServerCommandExecutor(server).run(
                    "title " + player.getGameProfile().name() + " actionbar {\"text\":\"\"}"
            );
        } catch (Exception e) {
            System.err.println(
                    "[SkyblockMulti] No fue posible limpiar el debug overlay para "
                            + player.getGameProfile().name() + ": " + e
            );
        }
    }

    private static String score(String playerName, String objective, String color) {
        return "{\"score\":{\"name\":\"" + playerName
                + "\",\"objective\":\"" + objective
                + "\"},\"color\":\"" + color + "\"}";
    }

    private static String jsonLiteral(String text, String color) {
        return "{\"text\":\"" + text + "\",\"color\":\"" + color + "\"}";
    }

    /**
     * Ejecutor compatible con los mismos entornos named/intermediary que usa SkyblockMultiMod.
     * Se mantiene local para no modificar ni exponer la clase privada existente.
     */
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
                if (method.getParameterCount() == 0
                        && method.getReturnType().getName().equals("com.mojang.brigadier.CommandDispatcher")) {
                    return method.invoke(commands);
                }
            }
            throw new NoSuchMethodException("Commands -> CommandDispatcher");
        }

        private static Method findExecuteMethod(Class<?> dispatcherClass) throws NoSuchMethodException {
            for (Method method : dispatcherClass.getMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (method.getName().equals("execute")
                        && params.length == 2
                        && params[0] == String.class) {
                    return method;
                }
            }
            throw new NoSuchMethodException("CommandDispatcher.execute(String, source)");
        }

        private static Object invokeNoArgReturning(Object owner, Class<?> returnType)
                throws ReflectiveOperationException {
            for (Method method : owner.getClass().getMethods()) {
                if (method.getParameterCount() == 0
                        && returnType.isAssignableFrom(method.getReturnType())) {
                    return method.invoke(owner);
                }
            }
            throw new NoSuchMethodException(
                    owner.getClass().getName() + " -> " + returnType.getName()
            );
        }

        private static Class<?> runtimeClass(String namedClass, String intermediaryClass)
                throws ClassNotFoundException {
            MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
            Collection<String> namespaces = resolver.getNamespaces();

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

            try {
                return Class.forName(intermediaryClass);
            } catch (ClassNotFoundException ignored) {
                return Class.forName(namedClass);
            }
        }
    }
}
