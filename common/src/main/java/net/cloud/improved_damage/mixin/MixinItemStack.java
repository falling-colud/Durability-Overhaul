package net.cloud.improved_damage.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.cloud.improved_damage.configuration.ImprovedDamageConfiguration;
import net.cloud.improved_damage.init.ImprovedDamageModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static net.cloud.improved_damage.ImprovedDamage.damageUUID;
import static net.cloud.improved_damage.ImprovedDamage.speedUUID;


@Mixin(value = ItemStack.class, priority = 1200)
public abstract class MixinItemStack {
    //TODO add comments

    @Shadow
    @Nullable
    private CompoundTag tag;

    @Shadow protected abstract int getHideFlags();

    @Shadow
    protected static Collection<Component> expandBlockState(String string) {
        return null;
    }

    @Inject(method = "getBarWidth", at = @At("RETURN"), cancellable = true)
    public void getBarWidth(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = ((ItemStack) (Object) this);

        cir.setReturnValue(stack.getOrCreateTag().getBoolean("broken")
                ? 13
                : Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) stack.getMaxDamage()));
    }

    @Inject(method = "getBarColor", at = @At("RETURN"), cancellable = true)
    public void getBarColor(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = ((ItemStack) (Object) this);
        if (stack.getOrCreateTag().getBoolean("broken")) cir.setReturnValue(Mth.hsvToRgb(0.001f, 1, 1));
        else {
            float stackMaxDamage = stack.getMaxDamage();
            float f = Math.max(0.0F, (stackMaxDamage - (float)stack.getDamageValue()) / stackMaxDamage);
            cir.setReturnValue(Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F));
        }
    }

    //on item damaged, set attack speed, mining speed, mining level, attack damage

    /**
     * @author Cloud
     * @reason Destroy speed is nerfed with durability
     */
    @Overwrite
    public float getDestroySpeed(BlockState p_41692_) {
        ItemStack stack = ((ItemStack) (Object) this);
        float realSpeed = stack.getItem().getDestroySpeed(stack, p_41692_);

        if (!stack.isDamageableItem()) return realSpeed;

        if (stack.getOrCreateTag().getBoolean("broken")) {
            return 0.5f;
        }

        double start = ImprovedDamageConfiguration.PENALTY_START;
        double progress = (double) stack.getDamageValue() / (double) stack.getMaxDamage();
        double flatPenalty = 0;

        int durable = EnchantmentHelper.getItemEnchantmentLevel(ImprovedDamageModEnchantments.DURABLE.get(), stack);
        if (durable == 3) {
            progress *= ImprovedDamageConfiguration.DURABLE_3_MULT;
        } else if (durable == 2) {
            progress *= ImprovedDamageConfiguration.DURABLE_2_MULT;
        } else if (durable == 1) {
            progress *=ImprovedDamageConfiguration.DURABLE_1_MULT;
        }
        if (progress > ImprovedDamageConfiguration.FLAT_REDUCTION_START && durable < ImprovedDamageConfiguration.DURABLE_NO_FLAT_LEVEL) {
            flatPenalty = ImprovedDamageConfiguration.FLAT_REDUCTION;
        }

        if (progress < start) return stack.getItem().getDestroySpeed(stack, p_41692_);
        double penalty = 1 - ImprovedDamageConfiguration.PENALTY_MULTIPLIER * progress;

        float finalSpeed = (float) (realSpeed * penalty - flatPenalty);
        float airSpeed = Items.AIR.getDestroySpeed(ItemStack.EMPTY, p_41692_);

        return Math.max(finalSpeed, airSpeed);
    }

    @Inject(method = "hurt", at = @At(value = "RETURN"), cancellable = true)
    public void hurt(int p_41630_, RandomSource p_41631_, ServerPlayer p_41632_, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = ((ItemStack) (Object) this);
        if (cir.getReturnValue()) {
            if (!stack.getOrCreateTag().getBoolean("broken")) {
                stack.getOrCreateTag().putBoolean("broken", true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Inject(method = "hurtAndBreak", at = @At(value = "HEAD"), cancellable = true)
    public <T extends LivingEntity> void hurtAndBreak(int i, T livingEntity, Consumer<T> consumer, CallbackInfo ci) {
        ci.cancel();
        
        ItemStack stack = ((ItemStack) (Object) this);
        
        if (!livingEntity.level.isClientSide && (!(livingEntity instanceof Player) || !((Player)livingEntity).getAbilities().instabuild)) {
            if (stack.isDamageableItem()) {
                if (stack.hurt(i, livingEntity.getRandom(), livingEntity instanceof ServerPlayer ? (ServerPlayer)livingEntity : null)) {
                    consumer.accept(livingEntity);
                    Item item = stack.getItem();

                    if (livingEntity instanceof Player) {
                        ((Player)livingEntity).awardStat(Stats.ITEM_BROKEN.get(item));
                    }
                }

            }
        }
    }

    @Inject(method = "setDamageValue", at = @At(value = "HEAD"), cancellable = true)
    public void setDamageValue(int damage, CallbackInfo ci) {
        ItemStack stack = ((ItemStack) (Object) this);

        boolean broken = stack.getOrCreateTag().getBoolean("broken");

        if (broken) {
            if (damage >= stack.getMaxDamage()) {
                ci.cancel();
                ((ItemStack) (Object) this).getOrCreateTag().putInt("Damage", stack.getMaxDamage());
                return;
            }
        }

        EquipmentSlot slot = EquipmentSlot.MAINHAND;

        double start = ImprovedDamageConfiguration.PENALTY_START;
        double progress = damage / (double) stack.getMaxDamage();
        double flatDamagePenalty = 0;
        double flatSpeedPenalty = 0;

        int durable;
        try {
            durable = EnchantmentHelper.getItemEnchantmentLevel(ImprovedDamageModEnchantments.DURABLE.get(), stack);
        } catch (Exception e) {
            durable = 1;
        }

        if (durable == 3) {
            progress *= 0.6;
        } else if (durable == 2) {
            progress *= 0.75;
        } else {
            if (durable == 1)
                progress *=0.75;

            if (progress > ImprovedDamageConfiguration.FLAT_REDUCTION_START) {
                flatDamagePenalty = ImprovedDamageConfiguration.FLAT_REDUCTION * 1;
                flatSpeedPenalty = ImprovedDamageConfiguration.FLAT_REDUCTION * 0.25;
            }
        }

        if (progress < start) return;
        double penalty = 1 - ImprovedDamageConfiguration.PENALTY_MULTIPLIER * progress;

        AttributeModifier dmgAttribute = null;
        AttributeModifier speedAttribute = null;

        for (AttributeModifier attributeModifier : stack.getItem().getDefaultInstance().getAttributeModifiers(slot).values()) {
            if (attributeModifier.getId().equals(damageUUID)) {
                // attack damage is default if broken
                double finalDmg = 0;
                if (!broken) {
                    finalDmg = penalty * attributeModifier.getAmount() - flatDamagePenalty;
                }
                dmgAttribute = new AttributeModifier(attributeModifier.getId(), "Durability Modifier", Math.max(finalDmg, 0), AttributeModifier.Operation.ADDITION);
            } else if (attributeModifier.getId().equals(speedUUID)) {
                //attack speed is default if broken
                double finalSpeed = 0;
                if (!broken) {
                    finalSpeed = (4 + attributeModifier.getAmount()) * penalty - 4 - flatSpeedPenalty;
                }
                speedAttribute = new AttributeModifier(attributeModifier.getId(), "Durability Modifier", Math.max(finalSpeed, -3.5), AttributeModifier.Operation.ADDITION);
            }
        }

        if (dmgAttribute == null || speedAttribute == null) return;

        if (!stack.getOrCreateTag().getBoolean("notFirst")) {
            stack.getOrCreateTag().putBoolean("notFirst", true);
            stack.addAttributeModifier(Attributes.ATTACK_DAMAGE, dmgAttribute, slot);
            stack.addAttributeModifier(Attributes.ATTACK_SPEED, speedAttribute, slot);
        }


        if (stack.hasTag() && stack.getTag().contains("AttributeModifiers", 9)) {
            ListTag listtag = stack.getTag().getList("AttributeModifiers", 10);

            for (int i = 0; i < listtag.size(); ++i) {
                CompoundTag compoundtag = listtag.getCompound(i);
                if (!compoundtag.contains("Slot", 8) || compoundtag.getString("Slot").equals(slot.getName())) {
                    Optional<Attribute> optional = Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundtag.getString("AttributeName")));
                    if (optional.isPresent()) {
                        AttributeModifier attributemodifier = AttributeModifier.load(compoundtag);
                        if (attributemodifier != null && attributemodifier.getId().getLeastSignificantBits() != 0L && attributemodifier.getId().getMostSignificantBits() != 0L) {
                            if (attributemodifier.getId().equals(damageUUID)) {
                                compoundtag = dmgAttribute.save();
                                compoundtag.putString("AttributeName", Registry.ATTRIBUTE.getKey(Attributes.ATTACK_DAMAGE).toString());
                                compoundtag.putString("Slot", slot.getName());
                                stack.getTag().getList("AttributeModifiers", 10).set(i, compoundtag);
                            } else if (attributemodifier.getId().equals(speedUUID)) {
                                compoundtag = speedAttribute.save();
                                compoundtag.putString("AttributeName", Registry.ATTRIBUTE.getKey(Attributes.ATTACK_SPEED).toString());
                                compoundtag.putString("Slot", slot.getName());
                                stack.getTag().getList("AttributeModifiers", 10).set(i, compoundtag);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean shouldShowInTooltip(int p_41627_, ItemStack.TooltipPart p_41628_) {
        return (p_41627_ & p_41628_.getMask()) == 0;
    }

    /**
     * @author Cloud
     * @reason Display name red if broken
     */
    @Overwrite
    public List<Component> getTooltipLines(@Nullable Player p_41652_, TooltipFlag p_41653_) {
        ItemStack stack = ((ItemStack) (Object) this);

        List<Component> list = Lists.newArrayList();
        MutableComponent mutablecomponent;

        if (stack.getOrCreateTag().getBoolean("broken")) {
            mutablecomponent = (Component.literal("Broken ")).append(stack.getHoverName()).withStyle(ChatFormatting.RED);
        } else {
            mutablecomponent = (Component.literal("")).append(stack.getHoverName()).withStyle(stack.getRarity().color);
        }

        if (stack.hasCustomHoverName()) {
            mutablecomponent.withStyle(ChatFormatting.ITALIC);
        }

        list.add(mutablecomponent);
        if (!p_41653_.isAdvanced() && !stack.hasCustomHoverName() && stack.is(Items.FILLED_MAP)) {
            Integer integer = MapItem.getMapId(stack);
            if (integer != null) {
                list.add((Component.literal("#" + integer)).withStyle(ChatFormatting.GRAY));
            }
        }

        int j = this.getHideFlags();
        if (shouldShowInTooltip(j, ItemStack.TooltipPart.ADDITIONAL)) {
            stack.getItem().appendHoverText(stack, p_41652_ == null ? null : p_41652_.level, list, p_41653_);
        }

        if (stack.hasTag()) {
            if (shouldShowInTooltip(j, ItemStack.TooltipPart.ENCHANTMENTS)) {
                ItemStack.appendEnchantmentNames(list, stack.getEnchantmentTags());
            }

            if (this.tag.contains("display", 10)) {
                CompoundTag compoundtag = this.tag.getCompound("display");
                if (shouldShowInTooltip(j, ItemStack.TooltipPart.DYE) && compoundtag.contains("color", 99)) {
                    if (p_41653_.isAdvanced()) {
                        list.add((Component.translatable("item.color", String.format("#%06X", compoundtag.getInt("color")))).withStyle(ChatFormatting.GRAY));
                    } else {
                        list.add((Component.translatable("item.dyed")).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    }
                }

                if (compoundtag.getTagType("Lore") == 9) {
                    ListTag listtag = compoundtag.getList("Lore", 8);

                    for (int i = 0; i < listtag.size(); ++i) {
                        String s = listtag.getString(i);

                        try {
                            MutableComponent mutablecomponent1 = Component.Serializer.fromJson(s);
                            if (mutablecomponent1 != null) {
                                list.add(ComponentUtils.mergeStyles(mutablecomponent1, Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true)));
                            }
                        } catch (Exception exception) {
                            compoundtag.remove("Lore");
                        }
                    }
                }
            }
        }

        if (shouldShowInTooltip(j, ItemStack.TooltipPart.MODIFIERS)) {
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                Multimap<Attribute, AttributeModifier> multimap = stack.getAttributeModifiers(equipmentslot);
                if (!multimap.isEmpty()) {
                    list.add(Component.empty());
                    list.add((Component.translatable("item.modifiers." + equipmentslot.getName())).withStyle(ChatFormatting.GRAY));

                    for (Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        double d0 = attributemodifier.getAmount();
                        boolean flag = false;
                        if (p_41652_ != null) {
                            if (attributemodifier.getId().equals(damageUUID)) {
                                d0 += p_41652_.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                                d0 += (double) EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
                                flag = true;
                            } else if (attributemodifier.getId().equals(speedUUID)) {
                                d0 += p_41652_.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                                flag = true;
                            }
                        }

                        double d1;
                        if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                            if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                                d1 = d0 * 10.0D;
                            } else {
                                d1 = d0;
                            }
                        } else {
                            d1 = d0 * 100.0D;
                        }

                        if (flag) {
                            list.add((Component.literal(" ")).append(Component.translatable("attribute.modifier.equals." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
                        } else if (d0 > 0.0D) {
                            list.add((Component.translatable("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.BLUE));
                        } else if (d0 < 0.0D) {
                            d1 *= -1.0D;
                            list.add((Component.translatable("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(entry.getKey().getDescriptionId()))).withStyle(ChatFormatting.RED));
                        }
                    }
                }
            }

            if (stack.hasTag()) {
                if (shouldShowInTooltip(j, ItemStack.TooltipPart.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                    list.add((Component.translatable("item.unbreakable")).withStyle(ChatFormatting.BLUE));
                }

                if (shouldShowInTooltip(j, ItemStack.TooltipPart.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
                    ListTag listtag1 = this.tag.getList("CanDestroy", 8);
                    if (!listtag1.isEmpty()) {
                        list.add(Component.empty());
                        list.add((Component.translatable("item.canBreak")).withStyle(ChatFormatting.GRAY));

                        for(int k = 0; k < listtag1.size(); ++k) {
                            list.addAll(expandBlockState(listtag1.getString(k)));
                        }
                    }
                }

                if (shouldShowInTooltip(j, ItemStack.TooltipPart.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
                    ListTag listtag2 = this.tag.getList("CanPlaceOn", 8);
                    if (!listtag2.isEmpty()) {
                        list.add(Component.empty());
                        list.add((Component.translatable("item.canPlace")).withStyle(ChatFormatting.GRAY));

                        for(int l = 0; l < listtag2.size(); ++l) {
                            list.addAll(expandBlockState(listtag2.getString(l)));
                        }
                    }
                }
            }

            if (p_41653_.isAdvanced()) {
                if (stack.isDamaged()) {
                    list.add(Component.translatable("item.durability", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
                }

                list.add((Component.literal(Registry.ITEM.getKey(stack.getItem()).toString())).withStyle(ChatFormatting.DARK_GRAY));
                if (stack.hasTag()) {
                    list.add((Component.translatable("item.nbt_tags", this.tag.getAllKeys().size())).withStyle(ChatFormatting.DARK_GRAY));
                }
            }

            //net.minecraftforge.event.ForgeEventFactory.onItemTooltip(stack, p_41652_, list, p_41653_);
            return list;
        }

        //net.minecraftforge.event.ForgeEventFactory.onItemTooltip(stack, p_41652_, list, p_41653_);
        return list;
    }

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = ((ItemStack) (Object) this);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, stack) != 0)
            cir.setReturnValue(cir.getReturnValue() * ImprovedDamageConfiguration.MENDING_DURABILITY_MULT);
    }


}