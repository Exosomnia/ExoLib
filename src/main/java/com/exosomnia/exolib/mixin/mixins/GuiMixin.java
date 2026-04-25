package com.exosomnia.exolib.mixin.mixins;

import com.exosomnia.exolib.mixin.interfaces.IGuiMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin implements IGuiMixin {

    private int lastItemNameYShift;

    @Override
    public int getLastItemNameYShift() {
        return lastItemNameYShift;
    }

    @Override
    public void setLastItemNameYShift(int yShift) {
        lastItemNameYShift = yShift;
    }

    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At("HEAD"))
    private void injectYShift(GuiGraphics guiGraphics, int yShift, CallbackInfo ci) {
        MultiPlayerGameMode gm = Minecraft.getInstance().gameMode;
        if (gm == null) return;

        int amount = Math.max(yShift, 59);
        if (!gm.canHurtPlayer()) {
            amount -= 14;
        }

        setLastItemNameYShift(amount);
    }
}
