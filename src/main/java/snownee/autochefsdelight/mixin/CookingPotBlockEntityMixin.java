package snownee.autochefsdelight.mixin;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.nhoryzon.mc.farmersdelight.block.CookingPotBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.inventory.RecipeWrapper;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import snownee.autochefsdelight.AutochefsDelight;
import snownee.autochefsdelight.util.CommonProxy;
import snownee.autochefsdelight.util.DummyRecipeContext;
import snownee.autochefsdelight.util.RecipeMatcher;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntityMixin {

	@Unique
	@Nullable
	private RecipeMatcher<ItemStack> lastRecipeMatch;

	@Inject(method = "getMatchingRecipe", at = @At("HEAD"))
	private void getMatchingRecipe(RecipeWrapper inventory, CallbackInfoReturnable<Optional<CookingPotRecipe>> ci, @Local LocalRef<RecipeWrapper> inventoryRef) {
		inventoryRef.set(new DummyRecipeContext(((RecipeWrapperAccess) inventory).getInventory(), $ -> lastRecipeMatch = $));
	}

	@WrapOperation(method = "getMatchingRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
	private Optional<CookingPotRecipe> getMatchingRecipe(RecipeManager instance, RecipeType<CookingPotRecipe> recipeType, Container ctx, Level level, Operation<Optional<CookingPotRecipe>> original) {
		for (CookingPotRecipe recipe : AutochefsDelight.COOKING_POT_RECIPES) {
			if (recipe.matches(ctx, level)) {
				return Optional.of(recipe);
			}
		}
		return Optional.empty();
	}

	@Inject(method = "processCooking", at = @At(value = "INVOKE", target = "Lcom/nhoryzon/mc/farmersdelight/entity/block/CookingPotBlockEntity;trackRecipeExperience(Lnet/minecraft/world/item/crafting/Recipe;)V", shift = At.Shift.AFTER), cancellable = true)
	private void processCooking(CookingPotRecipe recipe, CallbackInfoReturnable<Boolean> ci) {
		CookingPotBlockEntity self = (CookingPotBlockEntity) (Object) this;
		DummyRecipeContext ctx = new DummyRecipeContext(self, $ -> lastRecipeMatch = $);
		Level level = self.getLevel();
		if (lastRecipeMatch == null && level != null) {
			recipe.matches(ctx, level);
		}
		if (lastRecipeMatch == null) {
			return;
		}
		for (int i = 0; i < lastRecipeMatch.inputUsed.length; i++) {
			ItemStack stack = lastRecipeMatch.inputs.get(i);
			int used = lastRecipeMatch.inputUsed[i];
			ItemStack remainder = CommonProxy.getRecipeRemainder(stack);
			if (level != null && !remainder.isEmpty()) {
				if (ItemStack.isSameItemSameTags(remainder, stack)) {
					continue;
				}
				Direction direction = self.getBlockState().getValue(CookingPotBlock.FACING).getCounterClockWise();
				double dropX = (double) self.getBlockPos().getX() + 0.5 + (double) direction.getStepX() * 0.25;
				double dropY = (double) self.getBlockPos().getY() + 0.7;
				double dropZ = (double) self.getBlockPos().getZ() + 0.5 + (double) direction.getStepZ() * 0.25;
				for (int j = 0; j < used; j++) {
					ItemEntity entity = new ItemEntity(level, dropX, dropY, dropZ, remainder.copy());
					entity.setDeltaMovement((float) direction.getStepX() * 0.08F, 0.25, (float) direction.getStepZ() * 0.08F);
					level.addFreshEntity(entity);
				}
			}
			stack.shrink(used);
		}
		lastRecipeMatch = null;
		self.onContentsChanged(0);
		ci.setReturnValue(true);
	}
}
