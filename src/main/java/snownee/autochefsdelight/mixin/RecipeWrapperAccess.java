package snownee.autochefsdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackInventory;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;

@Mixin(value = RecipeWrapper.class, remap = false)
public interface RecipeWrapperAccess {
	@Accessor
	ItemStackInventory getInventory();
}
