package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Disarm extends TargetedRPGSkill implements PartySkill {
    private static final String DEFAULT_TIME = "10000";
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_OTHER_TEXT = "";

    public Disarm() {
        super(
                SkillSpec.create()
                        .id("disarm")
                        .name("Disarm")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Strike a target enemy’s weapon, dealing ",
                                arg(DAMAGE), " physical damage and disarming them for ", arg(TIME), ". ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
            Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
            Tuple.of(TIME, DescriptionArguments.time(getProperty(TIME, String.class, DEFAULT_TIME))),
            Tuple.of(OTHER_TEXT, TextSerializers.FORMATTING_CODE.deserialize(this.getProperty(OTHER_TEXT, String.class, DEFAULT_OTHER_TEXT)))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target)) throw isInParty();

        int disarmTime = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));
        double disarmDamage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));

        AtherysSkills.getInstance().getEffectService().applyEffect(target, Effects.disarm(disarmTime));
        target.damage(disarmDamage, DamageUtils.directPhysical(user));

        return CastResult.success();
    }
}
