package cl.treecs.skyblockmulti.client;

import cl.treecs.skyblockmulti.network.RequiredClientPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;

public final class RequiredClientModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigurationNetworking.registerGlobalReceiver(
                RequiredClientPayload.TYPE,
                (payload, context) -> {
                    // Registrar el canal confirma al servidor que Skyblock Multi está instalado.
                }
        );
    }
}
