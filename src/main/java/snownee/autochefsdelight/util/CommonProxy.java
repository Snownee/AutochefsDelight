package snownee.autochefsdelight.util;

import net.minecraft.world.item.ItemStack;

public class CommonProxy {
	public static ItemStack getRecipeRemainder(ItemStack stack) {
		return stack.getRecipeRemainder();
	}
}
