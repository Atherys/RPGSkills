package com.atherys.rpgskills.util;

import com.atherys.rpg.api.skill.DescriptionArgument;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.function.Supplier;

public class OtherTextDescriptionArgument implements DescriptionArgument {

    private Supplier<String> otherText;

    public OtherTextDescriptionArgument(Supplier<String> otherText) {
        this.otherText = otherText;
    }

    @Override
    public TextRepresentable apply(Living living) {
        return TextSerializers.FORMATTING_CODE.deserialize(otherText.get());
    }
}
