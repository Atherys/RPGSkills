package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.HEALING;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Invigorate extends TargetedRPGSkill implements PartySkill {
    private static final String DEFAULT_HEAL_EXPRESSION = "5.0";
    public Invigorate() {
        super(
                SkillSpec.create()
                        .id("invigorate")
                        .name("Invigorate")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Reinvigorate a target ally, healing them for ", arg(HEALING), "."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(HEALING, ofProperty(this, HEALING, DEFAULT_HEAL_EXPRESSION))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (!arePlayersInParty(user, target)) throw notInParty();

        double healAmount = asDouble(user, getProperty(HEALING, String.class, DEFAULT_HEAL_EXPRESSION));
        if (AtherysSkills.getInstance().getEffectService().hasEffect(target, VexingMark.VEXING_MARK_EFFECT)) {
            healAmount *= 0.5;
        }
        LivingUtils.healLiving(target, healAmount);
        return CastResult.success();
    }
}
