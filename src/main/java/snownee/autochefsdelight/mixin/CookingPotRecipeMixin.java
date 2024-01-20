package snownee.autochefsdelight.mixin;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import snownee.autochefsdelight.util.DummyRecipeContext;
import snownee.autochefsdelight.util.RecipeMatcher;

@Mixin(value = CookingPotRecipe.class, remap = false)
public abstract class CookingPotRecipeMixin {

	@Shadow
	@Final
	private NonNullList<Ingredient> ingredientList;

	@Inject(method = "matches", at = @At("HEAD"), cancellable = true)
	private void matches(Container inv, Level world, CallbackInfoReturnable<Boolean> ci) {
		List<ItemStack> inputs;
		int[] amount;
		if (inv instanceof DummyRecipeContext ctx) {
			if (ctx.itemCount < ingredientList.size()) {
				ci.setReturnValue(false);
				return;
			}
			inputs = ctx.filteredInputs;
			amount = ctx.amount;
		} else {
			inputs = Lists.newArrayListWithExpectedSize(CookingPotRecipe.INPUT_SLOTS);
			int itemCount = 0;
			for (int slotOffset = 0; slotOffset < CookingPotRecipe.INPUT_SLOTS; ++slotOffset) {
				ItemStack itemStack = inv.getItem(slotOffset);
				if (!itemStack.isEmpty()) {
					inputs.add(itemStack);
					itemCount += itemStack.getCount();
				}
			}
			if (itemCount < ingredientList.size()) {
				ci.setReturnValue(false);
				return;
			}
			amount = new int[inputs.size()];
			for (int i = 0; i < amount.length; i++) {
				amount[i] = inputs.get(i).getCount();
			}
		}
		Optional<RecipeMatcher<ItemStack>> match = RecipeMatcher.findMatches(inputs, ingredientList, amount);
		if (inv instanceof DummyRecipeContext ctx) {
			ctx.matchSetter.accept(match.orElse(null));
		}
		ci.setReturnValue(match.isPresent());
	}
}
