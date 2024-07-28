package snownee.autochefsdelight.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public class DummyRecipeInput extends RecipeWrapper {
	public final List<ItemStack> filteredInputs;
	public final int itemCount;
	public final int[] amount;
	public final Consumer<RecipeMatcher<ItemStack>> matchSetter;

	public DummyRecipeInput(IItemHandler inventory, Consumer<RecipeMatcher<ItemStack>> matchSetter) {
		super(inventory);
		filteredInputs = IntStream.range(0, inventory.getSlots())
				.mapToObj(inventory::getStackInSlot)
				.limit(CookingPotRecipe.INPUT_SLOTS)
				.filter(Predicate.not(ItemStack::isEmpty))
				.toList();
		itemCount = filteredInputs.stream().mapToInt(ItemStack::getCount).sum();
		amount = filteredInputs.stream().mapToInt(ItemStack::getCount).toArray();
		this.matchSetter = matchSetter;
	}
}
