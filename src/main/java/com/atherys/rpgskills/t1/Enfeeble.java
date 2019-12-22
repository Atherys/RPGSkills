package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
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
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Enfeeble extends TargetedRPGSkill {
    public static final String ENFEEBLE_RESISTANCE_EFFECT = "enfeeble-effect";
    public static final String ENFEEBLE_DOT_EFFECT = "enfeeble-dot-effect";

    private static final String DEFAULT_DURATION = "5000";
    private static final String DEFAULT_RESISTANCE_LOSS = "5";
    private static final String DEFAULT_DAMAGE = "5";

    public Enfeeble() {
        super(
                SkillSpec.create()
                        .id("enfeeble")
                        .name("Enfeeble")
                        .descriptionTemplate(TextTemplate.of(
                                "Weaken your target, dealing ", arg(DAMAGE), " magical damage over ",
                                arg(TIME), " seconds and reducing their physical and magic resistances by ",
                                arg(AMPLIFIER), " for the duration."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, "5.0")),
                Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_RESISTANCE_LOSS)),
                Tuple.of(TIME, ofProperty(this, TIME, DEFAULT_DURATION))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        double resistancesLost = -1 * asDouble(user, target, getProperty(AMPLIFIER, String.class, DEFAULT_RESISTANCE_LOSS));
        long duration = (long) asDouble(user, getProperty(TIME, String.class, DEFAULT_DURATION));
        double damage = asDouble(user, target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));

        Map<AttributeType, Double> attributes = new HashMap<>(2);
        attributes.put(AttributeTypes.MAGICAL_RESISTANCE, resistancesLost);
        attributes.put(AttributeTypes.PHYSICAL_RESISTANCE, resistancesLost);
        Applyable resistanceEffect = Effects.ofAttributes(ENFEEBLE_RESISTANCE_EFFECT, "Enfeeble", duration, attributes, false);

        Applyable damageEffect = Effects.damageOverTime(ENFEEBLE_DOT_EFFECT, "Enfeeble", duration, damage);

        AtherysSkills.getInstance().getEffectService().applyEffect(target, resistanceEffect);
        AtherysSkills.getInstance().getEffectService().applyEffect(target, damageEffect);

        return CastResult.success();
    }
}
