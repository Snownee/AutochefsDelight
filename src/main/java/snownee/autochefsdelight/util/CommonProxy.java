package snownee.autochefsdelight.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import snownee.autochefsdelight.AutochefsDelight;

@Mod(AutochefsDelight.ID)
public class CommonProxy {
	public static ItemStack getRecipeRemainder(ItemStack stack) {
		return stack.getCraftingRemainingItem();
	}
}
