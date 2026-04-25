package com.exosomnia.exolib.utils;

import com.exosomnia.exolib.mixin.interfaces.IGuiMixin;
import com.exosomnia.exolib.mixin.mixins.GuiAccessor;
import net.minecraft.client.gui.Gui;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GUIUtils {

    public static int getCurrentYShift(Gui gui) {
        return ((GuiAccessor)gui).getToolHighlightTimer() > 0 ? ((IGuiMixin)gui).getLastItemNameYShift() : Math.max(gui.leftHeight, gui.rightHeight);
    }
}
