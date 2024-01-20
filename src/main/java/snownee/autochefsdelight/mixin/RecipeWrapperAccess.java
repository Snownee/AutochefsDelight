package snownee.autochefsdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackInventory;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;

@Mixin(value = RecipeWrapper.class)
public interface RecipeWrapperAccess {
	@Accessor(remap = false)
	ItemStackInventory getInventory();
}
