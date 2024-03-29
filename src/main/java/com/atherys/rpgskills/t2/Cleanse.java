package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;

public class Cleanse extends TargetedRPGSkill implements PartySkill {

    public Cleanse() {
        super(
                SkillSpec.create()
                        .id("cleanse")
                        .name("Cleanse")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Cleanse target ally of any negative effects. If you have no target, cleanse yourself."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        try {
            return super.cast(user, timestamp, args);
        } catch (CastException e) {
            return cast(user, user, timestamp, args);
        }
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target) || user == target) {
            AtherysSkills.getInstance().getEffectService().clearNegativeEffects(target);
            return CastResult.success();
        }

        throw notInParty();
    }
}
