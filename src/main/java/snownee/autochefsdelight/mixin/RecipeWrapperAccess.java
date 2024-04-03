package snownee.autochefsdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;

@Mixin(RecipeWrapper.class)
public interface RecipeWrapperAccess {
	@Accessor(remap = false)
	ItemStackHandler getHandler();
}
