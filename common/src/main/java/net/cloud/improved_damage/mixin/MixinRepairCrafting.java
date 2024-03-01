package net.cloud.improved_damage.mixin;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RepairItemRecipe.class)
public class MixinRepairCrafting {

    /**
     * @author Cloud
     * @reason no repairing :)
     */
    //@Overwrite
    public ItemStack assemble(CraftingContainer p_44136_) {
        return ItemStack.EMPTY;
    }

    /**
     * @author Cloud
     * @reason no repairing :)
     */
    @Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"), cancellable = true)
    public void matches(CraftingContainer p_44138_, Level p_44139_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}