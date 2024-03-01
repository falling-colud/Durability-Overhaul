package net.cloud.improved_damage.mixin;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public abstract class MixinEnchanting {

    @Shadow
    public static List<EnchantmentInstance> getAvailableEnchantmentResults(int p_44818_, ItemStack p_44819_, boolean p_44820_) {
        return null;
    }

    @Shadow
    public static void filterCompatibleEnchantments(List<EnchantmentInstance> p_44863_, EnchantmentInstance p_44864_) {
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static int getEnchantmentCost(RandomSource p_44873_, int p_44874_, int p_44875_, ItemStack p_44876_) {
        Item item = p_44876_.getItem();
        int i = p_44876_.getItem().getEnchantmentValue();
        if (i <= 0) {
            return 0;
        } else {
            if (p_44875_ > 25) {
                p_44875_ = 25;
            }

            p_44875_ = (int) (p_44875_ * 20f/25f);

            int j = p_44873_.nextInt(8) + 1 + (p_44875_ >> 1) + p_44873_.nextInt(p_44875_ + 1);
            if (p_44874_ == 0) {
                return Math.max(j / 3, 1);
            } else {
                return p_44874_ == 1 ? j * 2 / 3 + 1 : Math.max(j, p_44875_ * 2);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static List<EnchantmentInstance> selectEnchantment(RandomSource p_44910_, ItemStack p_44911_, int p_44912_, boolean p_44913_) {
        p_44912_ = (int) (p_44912_ * 0.69);
        List<EnchantmentInstance> list = Lists.newArrayList();
        Item item = p_44911_.getItem();
        int i = p_44911_.getItem().getEnchantmentValue();
        if (i <= 0) {
            return list;
        } else {
            p_44912_ += 1 + p_44910_.nextInt(i / 4 + 1) + p_44910_.nextInt(i / 4 + 1);
            float f = (p_44910_.nextFloat() + p_44910_.nextFloat() - 1.0F) * 0.15F;
            p_44912_ = Mth.clamp(Math.round((float)p_44912_ + (float)p_44912_ * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list1 = getAvailableEnchantmentResults(p_44912_, p_44911_, p_44913_);
            if (!list1.isEmpty()) {
                WeightedRandom.getRandomItem(p_44910_, list1).ifPresent(list::add);

                while(p_44910_.nextInt(50) <= p_44912_) {
                    if (!list.isEmpty()) {
                        filterCompatibleEnchantments(list1, Util.lastOf(list));
                    }

                    if (list1.isEmpty()) {
                        break;
                    }

                    WeightedRandom.getRandomItem(p_44910_, list1).ifPresent(list::add);
                    p_44912_ /= 2;
                }
            }

            return list;
        }
    }

}