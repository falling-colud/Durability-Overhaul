package net.cloud.improved_damage.forge.client;

import net.cloud.improved_damage.init.ImprovedDamageModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class RenderLayersRegistry {
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(ImprovedDamageModBlocks.ENCHANTER.get(), renderType -> renderType == RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ImprovedDamageModBlocks.CHIPPED_ENCHANTER.get(), renderType -> renderType == RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ImprovedDamageModBlocks.DAMAGED_ENCHANTER.get(), renderType -> renderType == RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ImprovedDamageModBlocks.BROKEN_ENCHANTER.get(), renderType -> renderType == RenderType.translucent());
    }
}
