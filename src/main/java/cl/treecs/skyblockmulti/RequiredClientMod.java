package cl.treecs.skyblockmulti;

import cl.treecs.skyblockmulti.network.RequiredClientPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.chat.Component;

public final class RequiredClientMod implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundConfiguration().register(
                RequiredClientPayload.TYPE,
                RequiredClientPayload.CODEC
        );

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
}
