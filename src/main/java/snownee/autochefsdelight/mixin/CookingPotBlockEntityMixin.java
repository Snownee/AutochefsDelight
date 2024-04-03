package snownee.autochefsdelight.mixin;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import snownee.autochefsdelight.AutochefsDelight;
import snownee.autochefsdelight.util.CommonProxy;
import snownee.autochefsdelight.util.DummyRecipeContext;
import snownee.autochefsdelight.util.RecipeMatcher;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(CookingPotBlockEntity.class)
public abstract class CookingPotBlockEntityMixin {

	@Shadow
	protected abstract void ejectIngredientRemainder(ItemStack remainderStack);

	@Shadow
	@Final
	public static Map<Item, Item> INGREDIENT_REMAINDER_OVERRIDES;
	@Unique
	@Nullable
	private RecipeMatcher<ItemStack> lastRecipeMatch;

	@Inject(method = "getMatchingRecipe", at = @At("HEAD"), remap = false)
	private void getMatchingRecipe(
			RecipeWrapper inventory,
			CallbackInfoReturnable<Optional<CookingPotRecipe>> ci,
			@Local(argsOnly = true) LocalRef<RecipeWrapper> inventoryRef) {
		inventoryRef.set(new DummyRecipeContext(((RecipeWrapperAccess) inventory).getHandler(), this::setRecipeMatch));
	}

	@WrapOperation(
			method = "getMatchingRecipe", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
			remap = true), remap = false)
	private Optional<CookingPotRecipe> getMatchingRecipe(
			RecipeManager instance,
			RecipeType<CookingPotRecipe> recipeType,
			Container ctx,
			Level level,
			Operation<Optional<CookingPotRecipe>> original,
			@Local(argsOnly = true) RecipeWrapper recipeWrapper) {
		for (CookingPotRecipe recipe : AutochefsDelight.COOKING_POT_RECIPES) {
			if (recipe.matches(recipeWrapper, level)) {
				return Optional.of(recipe);
			}
		}
		return Optional.empty();
	}

	@Inject(
			method = "processCooking", at = @At(
			value = "INVOKE",
			target = "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/Recipe;)V",
			shift = At.Shift.AFTER,
			remap = true), cancellable = true, remap = false)
	private void processCooking(CookingPotRecipe recipe, CookingPotBlockEntity self, CallbackInfoReturnable<Boolean> ci) {
		Level level = Objects.requireNonNull(self.getLevel());
		if (lastRecipeMatch == null) {
			recipe.matches(new DummyRecipeContext(self.getInventory(), this::setRecipeMatch), level);
			if (lastRecipeMatch == null) {
				return;
			}
		}
		for (int i = 0; i < lastRecipeMatch.inputUsed.length; i++) {
			ItemStack stack = lastRecipeMatch.inputs.get(i);
			int used = lastRecipeMatch.inputUsed[i];
			ItemStack remainder = CommonProxy.getRecipeRemainder(stack);
			if (!remainder.isEmpty()) {
				if (ItemStack.isSameItemSameTags(remainder, stack)) {
					continue;
				} else {
					ejectIngredientRemainder(remainder);
				}
			} else {
				Item remainderItem = INGREDIENT_REMAINDER_OVERRIDES.get(stack.getItem());
				if (remainderItem != null) {
					ejectIngredientRemainder(remainderItem.getDefaultInstance());
				}
			}
			stack.shrink(used);
		}
		lastRecipeMatch = null;
		self.getInventory().setChanged();
		ci.setReturnValue(true);
	}

	@Unique
	public void setRecipeMatch(@Nullable RecipeMatcher<ItemStack> match) {
		lastRecipeMatch = match;
	}
}
