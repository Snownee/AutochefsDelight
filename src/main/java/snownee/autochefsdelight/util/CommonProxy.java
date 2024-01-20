package snownee.autochefsdelight.util;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.ItemStack;

public class CommonProxy implements ModInitializer {
	@Override
	public void onInitialize() {
	}

	public static ItemStack getRecipeRemainder(ItemStack stack) {
		return stack.getRecipeRemainder();
	}
}
