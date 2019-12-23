package com.atherys.rpgskills.t2;

import com.atherys.party.AtherysParties;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

public class Cleanse extends TargetedRPGSkill {
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
        if (target instanceof Player && user instanceof Player) {
            if (AtherysParties.getInstance().getPartyFacade().arePlayersInSameParty((Player) user, (Player) target)) {
                return CastResult.custom(Text.of("Target is in your party!"));
            }
        }
        AtherysSkills.getInstance().getEffectService().clearNegativeEffects(target);
        return CastResult.success();
    }
}
