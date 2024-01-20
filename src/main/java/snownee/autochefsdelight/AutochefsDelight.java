package snownee.autochefsdelight;


import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

public final class AutochefsDelight {
	public static final String ID = "autochefsdelight";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static List<CookingPotRecipe> COOKING_POT_RECIPES = List.of();

	public static void buildRecipeCache(RecipeManager recipeManager) {
		List<CookingPotRecipe> recipes = recipeManager.getAllRecipesFor(RecipeTypesRegistry.COOKING_RECIPE_SERIALIZER.type());
		COOKING_POT_RECIPES = recipes.stream()
				.filter(Predicate.not(Recipe::isIncomplete))
				.sorted(Comparator
						.comparingInt((CookingPotRecipe $) -> $.getIngredients().size())
						.thenComparingInt(AutochefsDelight::countSimpleIngredients))
				.toList();
	}

	private static int countSimpleIngredients(Recipe<?> recipe) {
		return (int) recipe.getIngredients().stream()
				.filter($ -> !$.isEmpty() && !$.requiresTesting())
				.count();
	}
}
