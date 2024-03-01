
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.cloud.improved_damage.init;


import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static net.cloud.improved_damage.ImprovedDamage.MODID;

public class ImprovedDamageModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(MODID, Registry.ITEM_REGISTRY);
	public static final RegistrySupplier<Item> BROKEN_ANVIL = block(ImprovedDamageModBlocks.BROKEN_ANVIL, CreativeModeTab.TAB_MISC);
	public static final RegistrySupplier<Item> ENCHANTER = block(ImprovedDamageModBlocks.ENCHANTER, CreativeModeTab.TAB_MISC);
	public static final RegistrySupplier<Item> CHIPPED_ENCHANTER = block(ImprovedDamageModBlocks.CHIPPED_ENCHANTER, CreativeModeTab.TAB_MISC);
	public static final RegistrySupplier<Item> DAMAGED_ENCHANTER = block(ImprovedDamageModBlocks.DAMAGED_ENCHANTER, CreativeModeTab.TAB_MISC);
	public static final RegistrySupplier<Item> BROKEN_ENCHANTER = block(ImprovedDamageModBlocks.BROKEN_ENCHANTER, CreativeModeTab.TAB_MISC);

	private static RegistrySupplier<Item> block(RegistrySupplier<Block> block, CreativeModeTab tab) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
	}
}
