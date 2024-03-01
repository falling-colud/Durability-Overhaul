
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.cloud.improved_damage.init;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.cloud.improved_damage.blocks.EnchanterMenu;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;

import static net.cloud.improved_damage.ImprovedDamage.MODID;

public class ImprovedDamageModMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(MODID, Registry.MENU_REGISTRY);

    public static final RegistrySupplier<MenuType<EnchanterMenu>> ENCHANTER = REGISTRY.register("enchanter", () -> MenuRegistry.ofExtended(EnchanterMenu::new));

}
