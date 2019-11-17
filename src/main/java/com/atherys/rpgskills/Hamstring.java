package com.atherys.rpgskills;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.MeleeAttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.TemporaryPotionEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.Living;

public class Hamstring extends TargetedRPGSkill implements MeleeAttackSkill {
    private static final String DEFAULT_SLOW_TIME = "60";
    private static final String USER_EFFECT = "hamstring-user-effect";

    protected Hamstring() {
        super(
                SkillSpec.create()
                .id("hamstring")
                .name("Hamstring")
                .descriptionTemplate("Your next melee attack will slow the target.")
                .cooldown("0")
                .resourceCost("0")
                .properties(ImmutableMap.of(MAX_RANGE_PROPERTY, "5.0"))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, USER_EFFECT);

        return CastResult.success();
    }

    public void meleeAttack(Living user, Living target) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, USER_EFFECT)) {
            int slowTime = (int) Math.round(asDouble(user, target, getProperty("slow-time", String.class, DEFAULT_SLOW_TIME)));

            AtherysSkills.getInstance().getEffectService().applyEffect(target, new HamstringEffect(slowTime));
            AtherysSkills.getInstance().getEffectService().removeEffect(user, USER_EFFECT);
        }
    }

    private static class HamstringEffect extends TemporaryPotionEffect {
        private HamstringEffect(int duration) {
            super(
                    "hamstring-effect",
                    "Hamstring",
                    PotionEffect.builder()
                            .potionType(PotionEffectTypes.SLOWNESS)
                            .amplifier(1)
                            .duration(duration)
                            .particles(true)
                            .build()
            );
        }
    }
}
