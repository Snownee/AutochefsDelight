package snownee.autochefsdelight.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.refabricated.inventory.ItemHandler;
import vectorwing.farmersdelight.refabricated.inventory.RecipeWrapper;

public class DummyRecipeInput extends RecipeWrapper {
	public final List<ItemStack> filteredInputs;
	public final int itemCount;
	public final int[] amount;
	public final Consumer<RecipeMatcher<ItemStack>> matchSetter;

	public DummyRecipeInput(ItemHandler inventory, Consumer<RecipeMatcher<ItemStack>> matchSetter) {
		super(inventory);
		filteredInputs = IntStream.range(0, CookingPotRecipe.INPUT_SLOTS)
				.mapToObj(inventory::getStackInSlot)
				.filter(Predicate.not(ItemStack::isEmpty))
				.toList();
		itemCount = filteredInputs.stream().mapToInt(ItemStack::getCount).sum();
		amount = filteredInputs.stream().mapToInt(ItemStack::getCount).toArray();
		this.matchSetter = matchSetter;
	}
}
