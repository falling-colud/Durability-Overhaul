package net.cloud.improved_damage.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ExperienceOrb.class)
public abstract class MixinExperience {
    //TODO add comments

    /**
     * @author Cloud
     * @reason mending doesnt repair
     */
    @Overwrite
    private int repairPlayerItems(Player p_147093_, int p_147094_) {
        return p_147094_;
    }
}