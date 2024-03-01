package net.cloud.improved_damage.fabric;

import net.cloud.improved_damage.ImprovedDamage;
import net.fabricmc.api.ModInitializer;

public class ImprovedDamageFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ImprovedDamage.init();
    }
}