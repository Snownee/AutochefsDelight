package snownee.autochefsdelight.mixin;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import snownee.autochefsdelight.AutochefsDelight;
import snownee.autochefsdelight.util.CommonProxy;
import snownee.autochefsdelight.util.CookingPotDuck;
import snownee.autochefsdelight.util.DummyRecipeInput;
import snownee.autochefsdelight.util.RecipeMatcher;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(CookingPotBlockEntity.class)
public abstract class CookingPotBlockEntityMixin implements CookingPotDuck {

	@Shadow(remap = false)
	protected abstract void ejectIngredientRemainder(ItemStack remainderStack);

	@Shadow(remap = false)
	@Final
	public static Map<Item, Item> INGREDIENT_REMAINDER_OVERRIDES;
	@Shadow
	private int cookTime;
	@Shadow
	private int cookTimeTotal;
	@Unique
	@Nullable
	private RecipeMatcher<ItemStack> autochef$lastRecipeMatch;
	@Unique
	private boolean autochef$updateRecipe = true;
	@Unique
	@Nullable
	private ResourceLocation autochef$processingRecipeID;

	@WrapOperation(
			method = "getMatchingRecipe", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
			remap = true), remap = false)
	private Optional<RecipeHolder<CookingPotRecipe>> getMatchingRecipe(
			RecipeManager.CachedCheck<RecipeWrapper, CookingPotRecipe> instance,
			RecipeInput recipeWrapper,
			Level level,
			Operation<Optional<RecipeHolder<CookingPotRecipe>>> original) {
//		AutochefsDelight.LOGGER.info("getMatchingRecipe");
		recipeWrapper = new DummyRecipeInput(((RecipeWrapperAccess) recipeWrapper).getInv(), this::autochef$setRecipeMatch);
		for (RecipeHolder<CookingPotRecipe> recipe : AutochefsDelight.COOKING_POT_RECIPES) {
			if (recipe.value().matches((RecipeWrapper) recipeWrapper, level)) {
				return Optional.of(recipe);
			}
		}
		return Optional.empty();
	}

	@WrapOperation(
			method = "cookingTick", at = @At(
			value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;hasInput()Z"), remap = false)
	private static boolean doNotFindRecipeIfInventoryUnchanged(@NotNull CookingPotBlockEntity cookingPot, Operation<Boolean> original) {
		CookingPotBlockEntityMixin pot = (CookingPotBlockEntityMixin) (Object) cookingPot;
		boolean bl = (pot.autochef$updateRecipe || pot.autochef$processingRecipeID != null) && original.call(cookingPot);
		pot.autochef$updateRecipe = false;
		return bl;
	}

	@WrapOperation(
			method = "cookingTick",
			at = @At(
					value = "INVOKE",
					target = "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;getMatchingRecipe(Lnet/neoforged/neoforge/items/wrapper/RecipeWrapper;)Ljava/util/Optional;"),
			remap = false)
	private static Optional<RecipeHolder<CookingPotRecipe>> getProcessingRecipe(
			@NotNull CookingPotBlockEntity cookingPot,
			RecipeWrapper recipeWrapper,
			Operation<Optional<RecipeHolder<CookingPotRecipe>>> original) {
		CookingPotBlockEntityMixin pot = (CookingPotBlockEntityMixin) (Object) cookingPot;
		ResourceLocation lastRecipeID = pot.autochef$processingRecipeID;
		if (lastRecipeID == null || pot.cookTime + 1 >= pot.cookTimeTotal) {
			return original.call(cookingPot, recipeWrapper);
		}
		//noinspection unchecked
		return (Optional<RecipeHolder<CookingPotRecipe>>) (Object) Objects.requireNonNull(cookingPot.getLevel()).getRecipeManager().byKey(
				lastRecipeID);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Inject(
			method = "cookingTick",
			at = @At(
					value = "INVOKE",
					target = "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;processCooking(Lnet/minecraft/world/item/crafting/RecipeHolder;Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;)Z"),
			remap = false)
	private static void setProcessingRecipe(
			Level level,
			BlockPos pos,
			BlockState state,
			@NotNull CookingPotBlockEntity cookingPot,
			CallbackInfo ci,
			@Local Optional<RecipeHolder<CookingPotRecipe>> recipe) {
		CookingPotBlockEntityMixin pot = (CookingPotBlockEntityMixin) (Object) cookingPot;
		pot.autochef$processingRecipeID = recipe.orElseThrow().id();
		pot.autochef$updateRecipe = false;
	}

	@Inject(
			method = "processCooking", at = @At(
			value = "INVOKE",
			target = "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/RecipeHolder;)V",
			shift = At.Shift.AFTER,
			remap = true), cancellable = true, remap = false)
	private void processCooking(
			RecipeHolder<CookingPotRecipe> recipe, CookingPotBlockEntity self, CallbackInfoReturnable<Boolean> ci) {
		Level level = Objects.requireNonNull(self.getLevel());
		if (autochef$lastRecipeMatch == null) {
			recipe.value().matches(new DummyRecipeInput(self.getInventory(), this::autochef$setRecipeMatch), level);
			if (autochef$lastRecipeMatch == null) {
				return;
			}
		}
		for (int i = 0; i < autochef$lastRecipeMatch.inputUsed.length; i++) {
			ItemStack stack = autochef$lastRecipeMatch.inputs.get(i);
			int used = autochef$lastRecipeMatch.inputUsed[i];
			ItemStack remainder = CommonProxy.getRecipeRemainder(stack);
			if (!remainder.isEmpty()) {
				if (ItemStack.isSameItemSameComponents(remainder, stack)) {
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
		autochef$lastRecipeMatch = null;
		ci.setReturnValue(true);
	}

	@Override
	public void autochef$setRecipeMatch(@Nullable RecipeMatcher<ItemStack> match) {
		autochef$lastRecipeMatch = match;
	}

	@Override
	public void autochef$updateRecipe() {
		autochef$updateRecipe = true;
		autochef$processingRecipeID = null;
	}
}
