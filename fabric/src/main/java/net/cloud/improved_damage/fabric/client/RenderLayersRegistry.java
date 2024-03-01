package net.cloud.improved_damage.fabric.client;

import net.cloud.improved_damage.init.ImprovedDamageModBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class RenderLayersRegistry {
    public static void registerRenderLayer() {
        BlockRenderLayerMap.INSTANCE.putBlock(ImprovedDamageModBlocks.ENCHANTER.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ImprovedDamageModBlocks.CHIPPED_ENCHANTER.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ImprovedDamageModBlocks.DAMAGED_ENCHANTER.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ImprovedDamageModBlocks.BROKEN_ENCHANTER.get(), RenderType.translucent());
    }
}
