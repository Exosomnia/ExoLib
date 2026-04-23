package com.exosomnia.exolib.recipes.brewing;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

import java.util.Optional;

public class SimpleBrewingRecipe implements IBrewingRecipe {

    private final Potion potion;
    private final Item ingredient;
    private final Potion output;

    public SimpleBrewingRecipe(Potion potion, Item ingredient, Potion outputs) {
        super();
        this.potion = potion;
        this.ingredient = ingredient;
        this.output = outputs;
    }

    @Override
    public boolean isInput(ItemStack input) {
        PotionContents potionContents = input.get(DataComponents.POTION_CONTENTS);
        if (potionContents == null) return false;

        Optional<Holder<Potion>> inputPotion = potionContents.potion();
        if (inputPotion.isEmpty()) return false;

        return input.getItem() == Items.POTION && inputPotion.get().value() == potion;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.getItem() == this.ingredient;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (isInput(input) && isIngredient(ingredient)) {
            ItemStack output = new ItemStack(Items.POTION);
            output.set(DataComponents.POTION_CONTENTS, new PotionContents(Holder.direct(this.output)));
            return output;
        }
        return ItemStack.EMPTY;
    }
}