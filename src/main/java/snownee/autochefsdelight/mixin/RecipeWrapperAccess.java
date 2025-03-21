package snownee.autochefsdelight.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import vectorwing.farmersdelight.refabricated.inventory.ItemHandler;
import vectorwing.farmersdelight.refabricated.inventory.RecipeWrapper;

@Mixin(value = RecipeWrapper.class, remap = false)
public interface RecipeWrapperAccess {
	@Accessor
	@Final
	ItemHandler getHandler();
}
