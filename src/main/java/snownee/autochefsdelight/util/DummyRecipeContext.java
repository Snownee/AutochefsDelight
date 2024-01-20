package snownee.autochefsdelight.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.nhoryzon.mc.farmersdelight.entity.block.inventory.ItemStackInventory;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;

import net.minecraft.world.item.ItemStack;

public class DummyRecipeContext extends RecipeWrapper {
	public final List<ItemStack> filteredInputs;
	public final int itemCount;
	public final int[] amount;
	public final Consumer<RecipeMatcher<ItemStack>> matchSetter;

	public DummyRecipeContext(ItemStackInventory inventory, Consumer<RecipeMatcher<ItemStack>> matchSetter) {
		super(inventory);
		filteredInputs = inventory.getItems().stream().limit(CookingPotRecipe.INPUT_SLOTS).filter(Predicate.not(ItemStack::isEmpty)).toList();
		itemCount = filteredInputs.stream().mapToInt(ItemStack::getCount).sum();
		amount = filteredInputs.stream().mapToInt(ItemStack::getCount).toArray();
		this.matchSetter = matchSetter;
	}
}
