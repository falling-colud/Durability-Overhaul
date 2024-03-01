package net.cloud.improved_damage.init;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import net.cloud.improved_damage.blocks.EnchanterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.AnvilBlock.FACING;

public class ImprovedDamageEvents {
    public static void load() {
        onBlockBroken();
        onRightClick();
    }

    public static void onBlockBroken() {
        BlockEvent.BREAK.register((level, blockPos, blockState, player, value) -> {
            ItemStack stack = player.getMainHandItem();
            if (stack.getOrCreateTag().getBoolean("broken") && stack.isCorrectToolForDrops(blockState)) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }

    public static void onRightClick() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, blockPos, direction) -> {
            Level level = player.level();
            BlockState blockState = level.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if (!(block instanceof AnvilBlock || block instanceof EnchanterBlock)) {
                return EventResult.pass();
            }

            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() == Items.IRON_BLOCK) {
                BlockState setBlock;
                if (blockState.is(Blocks.CHIPPED_ANVIL)) {
                    setBlock = Blocks.ANVIL.defaultBlockState().setValue(FACING, blockState.getValue(FACING));
                } else if (blockState.is(Blocks.DAMAGED_ANVIL)) {
                    setBlock = Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(FACING, blockState.getValue(FACING));
                } else if (blockState.is(ImprovedDamageModBlocks.BROKEN_ANVIL.get())) {
                    setBlock = Blocks.DAMAGED_ANVIL.defaultBlockState().setValue(FACING, blockState.getValue(FACING));
                } else {
                    return EventResult.pass();
                }

                @Nullable SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation("block.anvil.use"));
                double x = blockPos.getX();
                double y = blockPos.getY();
                double z = blockPos.getZ();
                if (!level.isClientSide()) {
                    level.playSound(null, new BlockPos((int) x, (int) y, (int) z), sound, SoundSource.BLOCKS, 1, 1);
                } else {
                    level.playLocalSound(x, y, z, sound, SoundSource.BLOCKS, 1, 1, false);
                }

                player.interact(player, hand);

                level.setBlock(blockPos, setBlock, 2);

                player.closeContainer();

                return EventResult.interruptTrue();


            } else if (stack.getItem() == Items.AMETHYST_BLOCK) {
                BlockState setBlock;
                if (blockState.is(ImprovedDamageModBlocks.CHIPPED_ENCHANTER.get())) {
                    setBlock = ImprovedDamageModBlocks.ENCHANTER.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING));
                } else if (blockState.is(ImprovedDamageModBlocks.DAMAGED_ENCHANTER.get())) {
                    setBlock = ImprovedDamageModBlocks.CHIPPED_ENCHANTER.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING));
                } else if (blockState.is(ImprovedDamageModBlocks.BROKEN_ENCHANTER.get())) {
                    setBlock = ImprovedDamageModBlocks.DAMAGED_ENCHANTER.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING));
                } else {
                    return EventResult.pass();
                }

                @Nullable SoundEvent sound = SoundEvent.createFixedRangeEvent(new ResourceLocation("block.amethyst_block.place"), 12);
                double x = blockPos.getX();
                double y = blockPos.getY();
                double z = blockPos.getZ();
                if (!level.isClientSide()) {
                    level.playSound(null, new BlockPos((int) x, (int) y, (int) z), sound, SoundSource.BLOCKS, 1, 1);
                } else {
                    level.playLocalSound(x, y, z, sound, SoundSource.BLOCKS, 1, 1, false);
                }

                player.interact(player, hand);


                level.setBlock(blockPos, setBlock, 2);

                player.closeContainer();

                return EventResult.interruptTrue();
            } else if (blockState.is(ImprovedDamageModBlocks.BROKEN_ANVIL.get())) {
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }
}
