package net.cloud.improved_damage.fabric;

import net.cloud.improved_damage.ImprovedDamageClient;
import net.cloud.improved_damage.fabric.client.RenderLayersRegistry;
import net.fabricmc.api.ClientModInitializer;

public class ImprovedDamageFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ImprovedDamageClient.clientInit();
        RenderLayersRegistry.registerRenderLayer();
    }
}