package net.cloud.improved_damage.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Random;

@Mixin(EnchantmentTableBlock.class)
public abstract class MixinEnchantingTable {

    @Shadow
    public static boolean isValidBookShelf(Level p_207910_, BlockPos p_207911_, BlockPos p_207912_) {
        return false;
    }

    @Unique
    private static final List<BlockPos> BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-2, 0, -2, 2, 2, 2).filter((p_207914_) -> Math.abs(p_207914_.getX()) == 2 || Math.abs(p_207914_.getZ()) == 2).map(BlockPos::immutable).toList();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void animateTick(BlockState p_52981_, Level p_52982_, BlockPos p_52983_, Random p_52984_) {
        for(BlockPos blockpos : BOOKSHELF_OFFSETS) {
            if (p_52984_.nextInt(16) == 0 && isValidBookShelf(p_52982_, p_52983_, blockpos)) {
                p_52982_.addParticle(ParticleTypes.ENCHANT, (double)p_52983_.getX() + 0.5D, (double)p_52983_.getY() + 2.0D, (double)p_52983_.getZ() + 0.5D, (double)((float)blockpos.getX() + p_52984_.nextFloat()) - 0.5D, (double)((float)blockpos.getY() - p_52984_.nextFloat() - 1.0F), (double)((float)blockpos.getZ() + p_52984_.nextFloat()) - 0.5D);
            }
        }

    }

}