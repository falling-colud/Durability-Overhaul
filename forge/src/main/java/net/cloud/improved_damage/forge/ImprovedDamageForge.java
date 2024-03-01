package net.cloud.improved_damage.forge;

import dev.architectury.platform.forge.EventBuses;
import net.cloud.improved_damage.ImprovedDamage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ImprovedDamage.MODID)
public class ImprovedDamageForge {

    public ImprovedDamageForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ImprovedDamage.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        {
            ImprovedDamage.init();
        }
    }
}