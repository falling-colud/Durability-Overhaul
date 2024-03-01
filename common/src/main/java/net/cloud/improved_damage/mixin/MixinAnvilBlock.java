package net.cloud.improved_damage.mixin;

import net.cloud.improved_damage.init.ImprovedDamageModBlocks;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AnvilBlock.class)
public abstract class MixinAnvilBlock {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static BlockState damage(BlockState p_48825_) {
        if (p_48825_.is(Blocks.ANVIL)) {
            return Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, p_48825_.getValue(AnvilBlock.FACING));
        } else if (p_48825_.is(Blocks.CHIPPED_ANVIL)){
            return Blocks.DAMAGED_ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, p_48825_.getValue(AnvilBlock.FACING));
        } else {
            return ImprovedDamageModBlocks.BROKEN_ANVIL.get().defaultBlockState().setValue(AnvilBlock.FACING, p_48825_.getValue(AnvilBlock.FACING));
        }
    }
}