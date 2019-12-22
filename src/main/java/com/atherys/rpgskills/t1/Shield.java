package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.rpg.api.stat.AttributeTypes;
import com.atherys.rpgskills.util.Effects;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import java.util.HashMap;
import java.util.Map;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Shield extends RPGSkill {
    public static final String SHIELD_EFFECT = "shield-effect";

    private static final String MAG_PROP = "magic";
    private static final String PHYS_PROP = "phys";
    private static final String DEFAULT_SHIELD_TIME = "10000";
    private static final String DEFAULT_PHYS = "5.0";
    private static final String DEFAULT_MAG = "5.0";

    public Shield() {
        super(
                SkillSpec.create()
                        .id("shield")
                        .name("Shield")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(TextTemplate.of(
                                "Bolster your defenses, gaining ", arg(PHYS_PROP), " physical resistance and ", arg(MAG_PROP),
                                " magical resistance for ", arg(TIME), " seconds."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(PHYS_PROP, ofProperty(this, PHYS_PROP, DEFAULT_PHYS)),
                Tuple.of(MAG_PROP, ofProperty(this, MAG_PROP, DEFAULT_MAG)),
                Tuple.of(TIME, ofProperty(this, TIME, DEFAULT_SHIELD_TIME))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = (int) Math.round(asDouble(user, getProperty(TIME, String.class, DEFAULT_SHIELD_TIME)));
        double physicalAmount = asDouble(user, getProperty(PHYS_PROP, String.class, DEFAULT_PHYS));
        double magicAmount = asDouble(user, getProperty(MAG_PROP, String.class, DEFAULT_MAG));

        Map<AttributeType, Double> attributes = new HashMap<>(2);
        attributes.put(AttributeTypes.PHYSICAL_RESISTANCE, physicalAmount);
        attributes.put(AttributeTypes.MAGICAL_RESISTANCE, magicAmount);
        Applyable resistanceEffect = Effects.ofAttributes(SHIELD_EFFECT, "Shield", duration, attributes, true);

        AtherysSkills.getInstance().getEffectService().applyEffect(user, resistanceEffect);

        return CastResult.success();
    }
}
