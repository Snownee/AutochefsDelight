package snownee.autochefsdelight;


import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

public final class AutochefsDelight {
	public static final String ID = "autochefsdelight";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static List<CookingPotRecipe> COOKING_POT_RECIPES = List.of();

	public static void buildRecipeCache(RecipeManager recipeManager) {
		List<CookingPotRecipe> recipes = recipeManager.getAllRecipesFor(ModRecipeTypes.COOKING.get());
		COOKING_POT_RECIPES = recipes.stream()
				.filter(Predicate.not(Recipe::isIncomplete))
				.sorted(Comparator.comparingInt((CookingPotRecipe $) -> $.getIngredients().size())
						.thenComparingInt(AutochefsDelight::countSimpleIngredients)
						.reversed())
				.toList();
	}

	private static int countSimpleIngredients(Recipe<?> recipe) {
		return (int) recipe.getIngredients().stream().filter($ -> !$.isEmpty() && $.isSimple()).count();
	}
}
