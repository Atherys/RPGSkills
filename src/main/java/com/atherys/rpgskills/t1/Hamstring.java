package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.CommonProperties;
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
    public static final String HAMSTRING_EFFECT = "hamstring-user-effect";

    private static final String DEFAULT_SLOW_TIME = "60";
    private static final String DEFAULT_SLOW_AMPLIFIER= "1";

    public Hamstring() {
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
        AtherysSkills.getInstance().getEffectService().applyEffect(user, HAMSTRING_EFFECT);

        return CastResult.success();
    }

    public void meleeAttack(Living user, Living target) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, HAMSTRING_EFFECT)) {
            int slowTime = (int) Math.round(asDouble(user, target, getProperty(CommonProperties.TIME, String.class, DEFAULT_SLOW_TIME)));
            int slowAmplifier = (int) Math.round(asDouble(user, target, getProperty(CommonProperties.AMPLIFIER, String.class, DEFAULT_SLOW_AMPLIFIER)));

            AtherysSkills.getInstance().getEffectService().applyEffect(target, new HamstringEffect(slowTime, slowAmplifier));
            AtherysSkills.getInstance().getEffectService().removeEffect(user, HAMSTRING_EFFECT);
        }
    }

    private static class HamstringEffect extends TemporaryPotionEffect {
        private HamstringEffect(int duration, int amplifier) {
            super(
                    "hamstring-effect",
                    "Hamstring",
                    PotionEffect.builder()
                            .potionType(PotionEffectTypes.SLOWNESS)
                            .amplifier(amplifier)
                            .duration(duration)
                            .particles(true)
                            .build()
            );
        }
    }
}
