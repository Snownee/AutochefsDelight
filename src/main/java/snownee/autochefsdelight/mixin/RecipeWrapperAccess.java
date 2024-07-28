package snownee.autochefsdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

@Mixin(RecipeWrapper.class)
public interface RecipeWrapperAccess {
	@Accessor(remap = false)
	IItemHandler getInv();
}
