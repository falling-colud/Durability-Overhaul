package net.cloud.improved_damage.init;

import dev.architectury.registry.menu.MenuRegistry;
import net.cloud.improved_damage.blocks.EnchanterScreen;

public class ImprovedDamageModScreens {
    public static void registerScreens() {
        MenuRegistry.registerScreenFactory(ImprovedDamageModMenus.ENCHANTER.get(), EnchanterScreen::new);
    }
}
