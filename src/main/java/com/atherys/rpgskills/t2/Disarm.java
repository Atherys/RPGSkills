package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Disarm extends TargetedRPGSkill implements PartySkill {
    private static final String DEFAULT_DISARM_TIME = "5000";
    private static final String DEFAULT_DISARM_DAMAGE = "5000";

    public Disarm() {
        super(
                SkillSpec.create()
                        .id("disarm")
                        .name("Disarm")
                        .descriptionTemplate(TextTemplate.of(
                                "Strike a target enemy’s weapon, dealing ",
                                arg(DAMAGE), " physical damage and disarming them for ", arg(TIME), " seconds."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
            Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DISARM_DAMAGE)),
            Tuple.of(TIME, ofProperty(this, AMPLIFIER, DEFAULT_DISARM_TIME))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (!arePlayersInParty(user, target)) {
            int disarmTime = (int) asDouble(user, getProperty(TIME, String.class, DEFAULT_DISARM_TIME));
            AtherysSkills.getInstance().getEffectService().applyEffect(target, Effects.disarm(disarmTime));
            return CastResult.success();
        }

        return CastResult.empty();
    }
}
