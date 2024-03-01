package net.cloud.improved_damage.blocks;

import com.mojang.logging.LogUtils;
import net.cloud.improved_damage.init.ImprovedDamageModBlocks;
import net.cloud.improved_damage.init.ImprovedDamageModMenus;
import net.cloud.improved_damage.mixin.ItemCombinerMenuAccessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.Map;

public class EnchanterMenu extends ItemCombinerMenu {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEBUG_COST = false;
    public static final int MAX_NAME_LENGTH = 50;
    public int repairItemCountCost;
    private final DataSlot cost = DataSlot.standalone();
    private static final int COST_FAIL = 0;
    private static final int COST_BASE = 1;
    private static final int COST_ADDED_BASE = 1;
    private static final int COST_REPAIR_MATERIAL = 1;
    private static final int COST_REPAIR_SACRIFICE = 2;
    private static final int COST_INCOMPATIBLE_PENALTY = 1;
    private static final int COST_RENAME = 1;

    public EnchanterMenu(int i, Inventory inventory) {
        this(i, inventory, ContainerLevelAccess.NULL);
    }

    public EnchanterMenu(int i, Inventory inventory, ContainerLevelAccess access) {
        super(ImprovedDamageModMenus.ENCHANTER.get(), i, inventory, access);
        this.addDataSlot(this.cost);
    }

    public EnchanterMenu(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(i, inventory, ContainerLevelAccess.NULL);
    }

    protected boolean isValidBlock(BlockState p_39019_) {
        return p_39019_.is(ImprovedDamageModBlocks.ENCHANTER.get()) || p_39019_.is(ImprovedDamageModBlocks.CHIPPED_ENCHANTER.get()) || p_39019_.is(ImprovedDamageModBlocks.DAMAGED_ENCHANTER.get());
    }

    protected boolean mayPickup(Player p_39023_, boolean p_39024_) {
        return (p_39023_.getAbilities().instabuild || p_39023_.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    protected void onTake(Player p_150474_, ItemStack p_150475_) {
        if (!p_150474_.getAbilities().instabuild) {
            p_150474_.giveExperienceLevels(-this.cost.get());
        }

        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack itemstack = this.inputSlots.getItem(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.repairItemCountCost) {
                itemstack.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, itemstack);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }

        this.cost.set(0);
        float breakChance = 0.12f;

        ((ItemCombinerMenuAccessor) this).getAccess().execute((p_150479_, p_150480_) -> {
            BlockState blockstate = p_150479_.getBlockState(p_150480_);

            if (!p_150474_.getAbilities().instabuild && blockstate.getBlock() instanceof EnchanterBlock && p_150474_.getRandom().nextFloat() < breakChance) {
                BlockState blockstate1 = EnchanterBlock.damage(blockstate);
                p_150479_.setBlock(p_150480_, blockstate1, 2);
                if (blockstate1.is(ImprovedDamageModBlocks.BROKEN_ENCHANTER.get())) {
                    p_150479_.levelEvent(1029, p_150480_, 0);
                } else {
                    p_150479_.levelEvent(1030, p_150480_, 0);
                }
            } else {
                p_150479_.levelEvent(1030, p_150480_, 0);
            }

        });
    }

    public void createResult() {
        ItemStack itemstack = this.inputSlots.getItem(0);
        this.cost.set(1);
        int i = 0;
        int j = 0;
        int k = 0;
        if (itemstack.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        } else {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.inputSlots.getItem(1);
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
            j += itemstack.getBaseRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getBaseRepairCost());
            this.repairItemCountCost = 0;
            boolean flag = false;

            if (!itemstack2.isEmpty()) {
                flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
                if (!flag && (!itemstack1.is(itemstack2.getItem()) || !itemstack1.isDamageableItem())) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }

                Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
                boolean flag2 = false;
                boolean flag3 = false;

                for(Enchantment enchantment1 : map1.keySet()) {
                    if (enchantment1 != null) {
                        int i2 = map.getOrDefault(enchantment1, 0);
                        int j2 = map1.get(enchantment1);
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        boolean flag1 = enchantment1.canEnchant(itemstack);
                        if (this.player.getAbilities().instabuild || itemstack.is(Items.ENCHANTED_BOOK)) {
                            flag1 = true;
                        }

                        for(Enchantment enchantment : map.keySet()) {
                            if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                                flag1 = false;
                                ++i;
                            }
                        }

                        if (!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (j2 > enchantment1.getMaxLevel()) {
                                j2 = enchantment1.getMaxLevel();
                            }

                            map.put(enchantment1, j2);
                            int k3 = 0;
                            switch(enchantment1.getRarity()) {
                                case COMMON:
                                    k3 = 1;
                                    break;
                                case UNCOMMON:
                                    k3 = 2;
                                    break;
                                case RARE:
                                    k3 = 4;
                                    break;
                                case VERY_RARE:
                                    k3 = 8;
                            }

                            if (flag) {
                                k3 = Math.max(1, k3 / 2);
                            }

                            i += k3 * j2;
                            if (itemstack.getCount() > 1) {
                                i = 40;
                            }
                        }
                    }
                }

                if (flag3 && !flag2) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
            }

            if (flag && !itemstack1.getItem().isEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

            this.cost.set(j + i);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int k2 = itemstack1.getBaseRepairCost();
                if (!itemstack2.isEmpty() && k2 < itemstack2.getBaseRepairCost()) {
                    k2 = itemstack2.getBaseRepairCost();
                }

                k2 = calculateIncreasedRepairCost(k2);

                itemstack1.setRepairCost(k2);
                EnchantmentHelper.setEnchantments(map, itemstack1);
            }

            itemstack1.setDamageValue(itemstack1.getDamageValue());
            this.resultSlots.setItem(0, itemstack1);
            this.broadcastChanges();
        }
    }

    public static int calculateIncreasedRepairCost(int p_39026_) {
        return p_39026_ * 2 + 1;
    }

    public int getCost() {
        return this.cost.get();
    }

    public void setMaximumCost(int value) {
        this.cost.set(value);
    }
}
