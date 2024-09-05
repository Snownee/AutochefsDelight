package snownee.autochefsdelight.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;

public interface CookingPotDuck {
	void autochef$setRecipeMatch(@Nullable RecipeMatcher<ItemStack> lastRecipeMatch);

	void autochef$updateRecipe();
}
