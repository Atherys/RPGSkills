package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;

public class Cleanse  extends TargetedRPGSkill implements PartySkill {
    public Cleanse() {
        super(
                SkillSpec.create()
                        .id("cleanse")
                        .name("Cleanse")
                        .descriptionTemplate(TextTemplate.of(
                                "Cleanse target ally of any negative effects."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (!arePlayersInParty(user, target)) throw notInParty();

        AtherysSkills.getInstance().getEffectService().clearNegativeEffects(target);
        return CastResult.success();
    }
}
