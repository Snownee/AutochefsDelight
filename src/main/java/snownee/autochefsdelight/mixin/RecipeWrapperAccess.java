package snownee.autochefsdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

@Mixin(RecipeWrapper.class)
public interface RecipeWrapperAccess {
	@Accessor(remap = false)
	IItemHandlerModifiable getInv();
}
