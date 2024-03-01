package net.cloud.improved_damage.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
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

    public boolean keyPressed(int p_97878_, int p_97879_, int p_97880_) {
        if (p_97878_ == 256) {
            this.minecraft.player.closeContainer();
        }

        return super.keyPressed(p_97878_, p_97879_, p_97880_);
    }

    protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
        super.renderLabels(guiGraphics, i, j);
        int k = (this.menu).getCost();
        if (k > 0) {
            int l = 8453920;
            Object component;
            if (!(this.menu).getSlot(2).hasItem()) {
                component = null;
            } else {
                component = Component.translatable("container.improved_damage.enchant.cost", k);
                if (!(this.menu).getSlot(2).mayPickup(this.player)) {
                    l = 16736352;
                }
            }

            if (component != null) {
                int m = this.imageWidth - 8 - this.font.width((FormattedText)component) - 2;
                guiGraphics.fill(m - 2, 67, this.imageWidth - 8, 79, 1325400064);
                guiGraphics.drawString(this.font, (Component)component, m, 69, l);
            }
        }

    }

    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        super.renderBg(guiGraphics, f, i, j);
        guiGraphics.blit(ENCHANTER_LOCATION, this.leftPos + 59, this.topPos + 20, 0, this.imageHeight + ((this.menu).getSlot(0).hasItem() ? 0 : 16), 110, 16);
    }

    protected void renderErrorIcon(GuiGraphics guiGraphics, int i, int j) {
        if (((this.menu).getSlot(0).hasItem() || (this.menu).getSlot(1).hasItem()) && !(this.menu).getSlot((this.menu).getResultSlot()).hasItem()) {
            guiGraphics.blit(ENCHANTER_LOCATION, i + 99, j + 45, this.imageWidth, 0, 28, 21);
        }

    }
}
