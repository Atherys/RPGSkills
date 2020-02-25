package com.atherys.rpgskills.util;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.TextTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.GOLD;

public class DescriptionUtils {

    /**
     * Builds a template for skills. Will apply green to any strings, and gold to any arguments.
     */
    public static TextTemplate buildTemplate(Object...elements) {
        List<TextRepresentable> finalElements = new ArrayList<>();
        for (Object o : elements) {
            if (o instanceof TextTemplate.Arg.Builder) {
                finalElements.add(
                        ((TextTemplate.Arg.Builder) o)
                                .color(GOLD)
                                .build()
                );
            } else {
                finalElements.add(Text.of(DARK_GREEN, o));
            }
        }
        return TextTemplate.of(finalElements.toArray());
    }
}
