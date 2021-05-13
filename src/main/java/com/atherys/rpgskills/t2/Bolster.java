package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import java.util.HashMap;
import java.util.Map;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Bolster extends RPGSkill {
    private static final String DEFAULT_TIME = "10000";
    private static final String DEFAULT_PHYS = "5.0";
    private static final String DEFAULT_MAG = "5.0";

    private final AttributeType physAttributeType;
    private final AttributeType magicAttributeType;

    public Bolster() {
        super(
                SkillSpec.create()
                        .id("fortify")
                        .name("Fortify")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Bolster your defenses, gaining ", arg(PHYSICAL), " physical resistance and ", arg(MAGICAL),
                                " magical resistance for ", arg(TIME), ". ", arg(OTHER_TEXT)
                        ))
        );

        setDescriptionArguments(
                Tuple.of(PHYSICAL, ofProperty(this, PHYSICAL, DEFAULT_PHYS)),
                Tuple.of(MAGICAL, ofProperty(this, MAGICAL, DEFAULT_MAG)),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );

        this.physAttributeType = Sponge.getRegistry().getType(AttributeType.class, "atherys:physres_multiplier").get();
        this.magicAttributeType = Sponge.getRegistry().getType(AttributeType.class, "atherys:magicres_multiplier").get();
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));
        double physicalAmount = asDouble(user, getProperty(PHYSICAL, String.class, DEFAULT_PHYS));
        double magicAmount = asDouble(user, getProperty(MAGICAL, String.class, DEFAULT_MAG));

        Map<AttributeType, Double> attributes = new HashMap<>(2);
        attributes.put(physAttributeType, physicalAmount);
        attributes.put(magicAttributeType, magicAmount);
        Applyable resistanceEffect = Effects.ofAttributes(getId(), getName(), duration, attributes, true);

        AtherysSkills.getInstance().getEffectService().applyEffect(user, resistanceEffect);

        return CastResult.success();
    }
}
