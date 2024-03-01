package net.cloud.improved_damage.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {
    @Accessor
    ResultContainer getResultSlots();
    @Accessor
    Container getInputSlots();
    @Accessor
    Player getPlayer();

    @Accessor
    ContainerLevelAccess getAccess();
}
