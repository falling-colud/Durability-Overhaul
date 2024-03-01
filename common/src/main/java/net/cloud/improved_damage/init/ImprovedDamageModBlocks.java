
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.cloud.improved_damage.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.cloud.improved_damage.blocks.EnchanterBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static net.cloud.improved_damage.ImprovedDamage.MODID;

public class ImprovedDamageModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(MODID, Registry.BLOCK_REGISTRY);
	public static final RegistrySupplier<Block> BROKEN_ANVIL = REGISTRY.register("broken_anvil", () -> new AnvilBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.ANVIL)));
	public static final RegistrySupplier<Block> ENCHANTER = REGISTRY.register("enchanter", () -> new EnchanterBlock());
	public static final RegistrySupplier<Block> CHIPPED_ENCHANTER = REGISTRY.register("chipped_enchanter", () -> new EnchanterBlock());
	public static final RegistrySupplier<Block> DAMAGED_ENCHANTER = REGISTRY.register("damaged_enchanter", () -> new EnchanterBlock());
	public static final RegistrySupplier<Block> BROKEN_ENCHANTER = REGISTRY.register("broken_enchanter", () -> new EnchanterBlock());


	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		EnchanterBlock.registerRenderLayer();

	}

}
