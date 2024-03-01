package net.cloud.improved_damage.blocks;

import net.cloud.improved_damage.init.ImprovedDamageModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class EnchanterBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape LEG_1 = Block.box(1D, 0D, 1D, 4D, 13D, 4D);
    private static final VoxelShape LEG_2 = Block.box(1D, 0D, 12D, 4D, 13D, 15D);
    private static final VoxelShape LEG_3 = Block.box(12D, 0D, 1D, 15D, 13D, 4D);
    private static final VoxelShape LEG_4 = Block.box(12D, 0D, 12D, 15D, 13D, 15D);
    private static final VoxelShape LEGS = Shapes.or(LEG_1, LEG_2, LEG_3, LEG_4);
    private static final VoxelShape TABLE = Block.box(0D, 13D, 0D, 16D, 16D, 16D);
    private static final VoxelShape AABB_1 = Shapes.or(TABLE, LEGS);

    public EnchanterBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(0.3F).sound(SoundType.WOOD).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return AABB_1;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDrops(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }

    public static BlockState damage(BlockState p_48825_) {
        if (p_48825_.is(ImprovedDamageModBlocks.ENCHANTER.get())) {
            return ImprovedDamageModBlocks.CHIPPED_ENCHANTER.get().defaultBlockState().setValue(FACING, p_48825_.getValue(FACING));
        } else if (p_48825_.is(ImprovedDamageModBlocks.CHIPPED_ENCHANTER.get())){
            return ImprovedDamageModBlocks.DAMAGED_ENCHANTER.get().defaultBlockState().setValue(FACING, p_48825_.getValue(FACING));
        } else {
            return ImprovedDamageModBlocks.BROKEN_ENCHANTER.get().defaultBlockState().setValue(FACING, p_48825_.getValue(FACING));
        }
    }

    private static final Component CONTAINER_TITLE = Component.translatable("container.improved_damage.enchant");

    public InteractionResult use(BlockState p_48804_, Level p_48805_, BlockPos p_48806_, Player p_48807_, InteractionHand p_48808_, BlockHitResult p_48809_) {
        if (this == ImprovedDamageModBlocks.BROKEN_ENCHANTER.get()) {
            return InteractionResult.FAIL;
        }

        if (p_48805_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            p_48807_.openMenu(p_48804_.getMenuProvider(p_48805_, p_48806_));
            return InteractionResult.CONSUME;
        }
    }
    

    @Nullable
    public MenuProvider getMenuProvider(BlockState p_48821_, Level p_48822_, BlockPos p_48823_) {
        return new SimpleMenuProvider((p_48785_, p_48786_, p_48787_) -> {
            return new EnchanterMenu(p_48785_, p_48786_, ContainerLevelAccess.create(p_48822_, p_48823_));
        }, CONTAINER_TITLE);
    }

    public static void registerRenderLayer() {

    }
}
