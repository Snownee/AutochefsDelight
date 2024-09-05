package snownee.autochefsdelight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import snownee.autochefsdelight.util.CookingPotDuck;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

@Mixin(value = SyncedBlockEntity.class, remap = false)
public class SyncedBlockEntityMixin {
	@Inject(method = "inventoryChanged", at = @At("HEAD"))
	private void inventoryChanged(CallbackInfo ci) {
		if (this instanceof CookingPotDuck pot) {
			pot.autochef$updateRecipe();
		}
	}
}
