package net.cloud.improved_damage.forge;

import net.cloud.improved_damage.ImprovedDamageClient;
import net.cloud.improved_damage.forge.client.RenderLayersRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ImprovedDamageClientForge {

    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
		// Submit our event bus to let architectury register our content on the right time
        ImprovedDamageClient.clientInit();
        RenderLayersRegistry.registerRenderLayer();
    }
}