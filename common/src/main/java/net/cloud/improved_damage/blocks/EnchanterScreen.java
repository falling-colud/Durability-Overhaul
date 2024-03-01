package net.cloud.improved_damage.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class EnchanterScreen extends ItemCombinerScreen<EnchanterMenu> {
    private static final ResourceLocation ENCHANTER_LOCATION = new ResourceLocation("improved_damage", "textures/gui/container/enchanter.png");
    private final Player player;

    public EnchanterScreen(EnchanterMenu p_97874_, Inventory p_97875_, Component p_97876_) {
        super(p_97874_, p_97875_, p_97876_, ENCHANTER_LOCATION);
        this.player = p_97875_.player;
        this.titleLabelX = 60;
    }

    public void containerTick() {
        super.containerTick();
    }


    public void resize(Minecraft p_97886_, int p_97887_, int p_97888_) {
        this.init(p_97886_, p_97887_, p_97888_);
    }

    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    public boolean keyPressed(int p_97878_, int p_97879_, int p_97880_) {
        if (p_97878_ == 256) {
            this.minecraft.player.closeContainer();
        }

        return super.keyPressed(p_97878_, p_97879_, p_97880_);
    }

    protected void renderLabels(PoseStack p_97890_, int p_97891_, int p_97892_) {
        RenderSystem.disableBlend();
        super.renderLabels(p_97890_, p_97891_, p_97892_);
        int i = this.menu.getCost();
        if (i > 0) {
            int j = 8453920;
            Component component;
            if (!this.menu.getSlot(2).hasItem()) {
                component = null;
            } else {
                component = new TranslatableComponent("container.improved_damage.enchant.cost", i);
                if (!this.menu.getSlot(2).mayPickup(this.player)) {
                    j = 16736352;
                }
            }

            if (component != null) {
                int k = this.imageWidth - 8 - this.font.width(component) - 2;
                int l = 69;
                fill(p_97890_, k - 2, 67, this.imageWidth - 8, 79, 1325400064);
                this.font.drawShadow(p_97890_, component, (float)k, 69.0F, j);
            }
        }

    }
}
