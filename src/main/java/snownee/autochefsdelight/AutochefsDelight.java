package snownee.autochefsdelight;


import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

public final class AutochefsDelight {
	public static final String ID = "autochefsdelight";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static List<RecipeHolder<CookingPotRecipe>> COOKING_POT_RECIPES = List.of();

	public static void buildRecipeCache(RecipeManager recipeManager) {
		List<RecipeHolder<CookingPotRecipe>> recipes = recipeManager.getAllRecipesFor(ModRecipeTypes.COOKING.get());
		COOKING_POT_RECIPES = recipes.stream()
				.filter($ -> !$.value().isIncomplete())
				.sorted(Comparator.comparingInt((RecipeHolder<CookingPotRecipe> $) -> $.value().getIngredients().size())
						.thenComparingInt(AutochefsDelight::countSimpleIngredients)
						.reversed())
				.toList();
	}

	private static int countSimpleIngredients(RecipeHolder<CookingPotRecipe> recipeHolder) {
		return (int) recipeHolder.value().getIngredients().stream().filter($ -> !$.isEmpty() && !$.requiresTesting()).count();
	}
}
