package net.cloud.improved_damage.mixin;

import net.cloud.improved_damage.init.ImprovedDamageModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AnvilMenu.class)
public abstract class MixinRepairAnvil {

    @Shadow
    public abstract int getCost();

    @Shadow
    public int repairItemCountCost;

    @Shadow
    private String itemName;

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected boolean mayPickup(Player player, boolean bl) {
        return true;
    }

    /**
     * @author Cloud
     * @reason Anvil
     */
    //TODO add comments
    @Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"), cancellable = true)
    protected void onTake(Player p_150474_, ItemStack p_150475_, CallbackInfo ci) {
        ci.cancel();
        float breakChance = 0.12f;

        ((ItemCombinerMenuAccessor) this).getAccess().execute((p_150479_, p_150480_) -> {
            BlockState blockstate = p_150479_.getBlockState(p_150480_);

            if (!p_150474_.getAbilities().instabuild && blockstate.is(BlockTags.ANVIL) && p_150474_.getRandom().nextFloat() < breakChance) {
                BlockState blockstate1 = AnvilBlock.damage(blockstate);
                p_150479_.setBlock(p_150480_, blockstate1, 2);
                if (blockstate1.is(ImprovedDamageModBlocks.BROKEN_ANVIL.get())) {
                    p_150479_.levelEvent(1029, p_150480_, 0);
                } else {
                    p_150479_.levelEvent(1030, p_150480_, 0);
                }
            } else {
                p_150479_.levelEvent(1030, p_150480_, 0);
            }

        });

    }


    private void setEmptyResult() {
        ((ItemCombinerMenuAccessor) this).getResultSlots().setItem(0, ItemStack.EMPTY);
    }

    /**
     * @author Cloud
     * @reason Changing the result by taking item type into consideration, to change the amount of material used, xp cost and to remove enchanting
     */
    @Overwrite
    public void createResult() {
        ResultContainer results = ((ItemCombinerMenuAccessor) this).getResultSlots();
        ItemStack itemstack = ((ItemCombinerMenuAccessor) this).getInputSlots().getItem(0);
        int i = 0;
        int k = 0;
        if (itemstack.isEmpty()) {
            setEmptyResult();
        } else {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = ((ItemCombinerMenuAccessor) this).getInputSlots().getItem(1);
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemstack1);
            this.repairItemCountCost = 0;

            if (itemstack1.getDamageValue() <= 0) {
                setEmptyResult();
                return;
            }

            if (!itemstack2.isEmpty()) {
                if (itemstack1.isDamageableItem() && itemstack1.getItem().isValidRepairItem(itemstack, itemstack2)) {
                    int d;
                    Item item = itemstack1.getItem();
                    if (item instanceof ShovelItem)
                        d = 2; // item repairs 1/2
                    else if (item instanceof SwordItem || item instanceof HoeItem)
                        d = 3; // item repairs 2/3
                    else if (item instanceof AxeItem || item instanceof PickaxeItem)
                        d = 4; // item repairs 3/4
                    else
                        d = 4;

                    int l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / d);

                    int i3;
                    for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                        int j3 = itemstack1.getDamageValue() - l2;
                        itemstack1.setDamageValue(j3);
                        ++i;
                        l2 = Math.min(itemstack1.getDamageValue(), itemstack1.getMaxDamage() / d);
                    }

                    this.repairItemCountCost = i3;
                } else {
                    if ((!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                        setEmptyResult();
                        return;
                    }

                    if (itemstack1.isDamageableItem()) {
                        int l1 = getL1(itemstack, itemstack2, itemstack1);

                        if (l1 < itemstack1.getDamageValue()) {
                            itemstack1.setDamageValue(l1);
                            i += 2;
                        }
                    }

                    if (itemstack2.isEnchanted()) {
                        setEmptyResult();
                        return;
                    }
                }
            }

            if (StringUtils.isBlank(this.itemName)) {
                if (itemstack.hasCustomHoverName()) {
                    k = 1;
                    i += k;
                    itemstack1.resetHoverName();
                }
            } else if (!this.itemName.equals(itemstack.getHoverName().getString())) {
                k = 1;
                i += k;
                itemstack1.setHoverName(Component.literal(this.itemName));
            }

            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {

                EnchantmentHelper.setEnchantments(enchantments, itemstack1);

                if (itemstack1.getOrCreateTag().getBoolean("broken")) {
                    if (itemstack1.isDamaged()) {
                        itemstack1.getOrCreateTag().putBoolean("broken", false);
                        itemstack1 = ItemStack.EMPTY;
                    }
                }
            }

            results.setItem(0, itemstack1);
            ((AnvilMenu) (Object) this).broadcastChanges();
        }
    }

    private static int getL1(ItemStack itemstack, ItemStack itemstack2, ItemStack itemstack1) {
        int l = itemstack.getMaxDamage() - itemstack.getDamageValue();
        int i1;
        Item item2 = itemstack2.getItem();
        if (item2 instanceof ShovelItem)
            i1 = itemstack2.getMaxDamage() / 2 - itemstack2.getDamageValue(); // item repairs 1/2
        else if (item2 instanceof SwordItem || item2 instanceof HoeItem)
            i1 = itemstack2.getMaxDamage() * 2 / 3 - itemstack2.getDamageValue(); // item repairs 2/3
        else if (item2 instanceof AxeItem || item2 instanceof PickaxeItem)
            i1 = itemstack2.getMaxDamage() * 3 / 4 - itemstack2.getDamageValue(); // item repairs 3/4
        else
            i1 = itemstack2.getMaxDamage() * 3 / 4 - itemstack2.getDamageValue();

        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
        int k1 = l + j1;
        int l1 = itemstack1.getMaxDamage() - k1;
        if (l1 < 0) {
            l1 = 0;
        }
        return l1;
    }
}