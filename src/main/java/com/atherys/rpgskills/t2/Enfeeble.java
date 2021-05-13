package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.PartySkill;
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

public class Enfeeble extends TargetedRPGSkill implements PartySkill {
    public static final String ENFEEBLE_RESISTANCE_EFFECT = "enfeeble-effect";
    public static final String ENFEEBLE_DOT_EFFECT = "enfeeble-dot-effect";

    private static final String DEFAULT_TIME = "5000";
    private static final String DEFAULT_RESISTANCE_LOSS = "5";
    private static final String DEFAULT_DAMAGE = "5";

    private final AttributeType physAttributeType;
    private final AttributeType magicAttributeType;

    public Enfeeble() {
        super(
                SkillSpec.create()
                        .id("enfeeble")
                        .name("Enfeeble")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Weaken your target, dealing ", arg(DAMAGE), " magical damage over ",
                                arg(TIME), " and reducing their physical and magic resistances by ",
                                arg(PHYSICAL), " and ", arg(MAGICAL), " for the duration.", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, "5.0")),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(PHYSICAL, ofProperty(this, PHYSICAL, DEFAULT_RESISTANCE_LOSS)),
                Tuple.of(MAGICAL, ofProperty(this, MAGICAL, DEFAULT_RESISTANCE_LOSS)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );

        this.physAttributeType = Sponge.getRegistry().getType(AttributeType.class, "atherys:physres_multiplier").get();
        this.magicAttributeType = Sponge.getRegistry().getType(AttributeType.class, "atherys:magicres_multiplier").get();
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target)) throw isInParty();

        long duration = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));

        double physicalAmount = asDouble(user, getProperty(PHYSICAL, String.class, DEFAULT_RESISTANCE_LOSS));
        double magicAmount = asDouble(user, getProperty(MAGICAL, String.class, DEFAULT_RESISTANCE_LOSS));
        Map<AttributeType, Double> attributes = new HashMap<>(2);
        attributes.put(physAttributeType, physicalAmount);
        attributes.put(magicAttributeType, magicAmount);
        Applyable resistanceEffect = Effects.ofAttributes(ENFEEBLE_RESISTANCE_EFFECT, getName(), duration, attributes, false);

        double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
        Applyable damageEffect = Effects.magicalDamageOverTime(ENFEEBLE_DOT_EFFECT, getName(), duration, damage, user);

        AtherysSkills.getInstance().getEffectService().applyEffect(target, resistanceEffect);
        AtherysSkills.getInstance().getEffectService().applyEffect(target, damageEffect);

        return CastResult.success();
    }
}
