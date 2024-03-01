
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.cloud.improved_damage.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.cloud.improved_damage.ImprovedDamage;
import net.cloud.improved_damage.enchantment.DurableEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;

public class ImprovedDamageModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ImprovedDamage.MODID, Registries.ENCHANTMENT);
	public static final RegistrySupplier<Enchantment> DURABLE = REGISTRY.register("durable", DurableEnchantment::new);
}
