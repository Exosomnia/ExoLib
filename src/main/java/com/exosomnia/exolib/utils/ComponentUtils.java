package com.exosomnia.exolib.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentUtils {

    private static Pattern FORMAT_PATTERN = Pattern.compile("\\d\\{.*?}");

    public enum Styles {
        BLANK(Style.EMPTY.withColor(ChatFormatting.WHITE).withUnderlined(false).withBold(false).withItalic(false).withStrikethrough(false).withObfuscated(false)),
        NO_FORMATTING(Style.EMPTY.withUnderlined(false).withBold(false).withItalic(false).withStrikethrough(false).withObfuscated(false)),
        NO_COLOR(Style.EMPTY.withColor(ChatFormatting.WHITE)),
        INFO_HEADER(Style.EMPTY.withColor(TextColor.fromRgb(0x9c679f)).withUnderlined(true).withBold(false).withItalic(true).withStrikethrough(false).withObfuscated(false)),
        DEFAULT_DESC(Style.EMPTY.withColor(TextColor.fromRgb(0x909090)).withUnderlined(false).withBold(false).withItalic(true).withStrikethrough(false).withObfuscated(false)),
        HIGHLIGHT_DESC(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF80)).withUnderlined(true).withBold(false).withItalic(true).withStrikethrough(false).withObfuscated(false)),
        HIGHLIGHT_STAT(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFF80)).withUnderlined(false).withBold(false).withItalic(true).withStrikethrough(false).withObfuscated(false));

        private final Style style;

        Styles(Style style) {
            this.style = style;
        }

        public Style getStyle() {
            return style;
        }
    }

    public enum DetailLevel {
        BASIC,
        DESCRIPTION,
        STATISTICS
    }

    public static MutableComponent formatLine(String translation, Style defaultStyle, Style... styles) {
        Matcher matcher = FORMAT_PATTERN.matcher(translation);
        ArrayList<String> pieces = new ArrayList<>();
        int prevIndex = 0;

        while(matcher.find()) {
            //If the previous index is not equal to the start of the found group, add the text before the group
            if (prevIndex < matcher.start()) {
                pieces.add(translation.substring(prevIndex, matcher.start()));
            }
            //Add the text of the found group and update the previous index
            pieces.add(matcher.group());
            prevIndex = matcher.end();
        }
        //If there is text left after finding all the groups, add that
        if (prevIndex < translation.length()) {
            pieces.add(translation.substring(prevIndex));
        }

        MutableComponent line = Component.empty();
        for (String piece : pieces) {
            boolean formatted = piece.length() > 2 && (piece.charAt(1) == '{' && piece.endsWith("}"));
            Style style = formatted ? styles[Character.getNumericValue(piece.charAt(0))] : defaultStyle;
            piece = formatted ? piece.substring(2, piece.length() - 1) : piece;

            line.append(Component.literal(piece).withStyle(style));
        }

        return line;
    }
}
