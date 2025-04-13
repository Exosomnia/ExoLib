package com.exosomnia.exolib.recipes.brewing;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.crafting.PartialNBTIngredient;

public class BrewingRecipeHelper {

    public static void addSimplePotionRecipe(Potion potion, Item ingredient, Potion output) {
        ItemStack inputPotion = PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
        BrewingRecipeRegistry.addRecipe(PartialNBTIngredient.of(Items.POTION, inputPotion.getTag()), Ingredient.of(ingredient), PotionUtils.setPotion(new ItemStack(Items.POTION), output));
    }
}
