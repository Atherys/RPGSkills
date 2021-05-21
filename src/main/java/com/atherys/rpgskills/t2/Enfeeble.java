package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
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
    private static final String DEFAULT_PERCENT = "30";
    private static final String DEFAULT_DAMAGE = "5";

    private AttributeType physAttributeType;
    private AttributeType magicAttributeType;

    public Enfeeble() {
        super(
                SkillSpec.create()
                        .id("enfeeble")
                        .name("Enfeeble")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Weaken your target, dealing ", arg(DAMAGE), " magical damage over ",
                                arg(TIME), " and reducing their physical and magic resistances by ",
                                arg(PERCENT), "% for the duration.", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(PERCENT, ofProperty(this, PERCENT, DEFAULT_PERCENT)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        super.setProperties(properties);

        this.physAttributeType = Sponge.getRegistry().getType(AttributeType.class, "atherys:physical_resistance").get();
        this.magicAttributeType = Sponge.getRegistry().getType(AttributeType.class, "atherys:magical_resistance").get();
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target)) throw isInParty();

        long duration = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));

        double percent = asDouble(user, getProperty(PERCENT, String.class, DEFAULT_PERCENT)) / -100;
        Map<AttributeType, Double> targetAttributes = AtherysRPG.getInstance().getAttributeService().getAllAttributes(target);

        Map<AttributeType, Double> attributes = new HashMap<>(2);
        attributes.put(physAttributeType, percent * targetAttributes.get(physAttributeType));
        attributes.put(magicAttributeType, percent * targetAttributes.get(magicAttributeType));

        Applyable resistanceEffect = Effects.ofAttributes(
                ENFEEBLE_RESISTANCE_EFFECT,
                getName(),
                duration,
                attributes,
                false
        );

        double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
        Applyable damageEffect = Effects.magicalDamageOverTime(ENFEEBLE_DOT_EFFECT, getName(), duration, damage, user);

        AtherysSkills.getInstance().getEffectService().applyEffect(target, resistanceEffect);
        AtherysSkills.getInstance().getEffectService().applyEffect(target, damageEffect);

        return CastResult.success();
    }
}
