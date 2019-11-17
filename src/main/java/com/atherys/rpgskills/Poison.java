package com.atherys.rpgskills;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.AttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.TemporaryPotionEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.Living;

public class Poison extends RPGSkill implements AttackSkill {
    private static final String USER_EFFECT = "poison-effect-user";
    private static final String DEFAULT_POISON_TIME = "60";

    protected Poison() {
        super(
                SkillSpec.create()
                .id("poison")
                .name("Poison")
                .descriptionTemplate("Your next attack will deal poison.")
                .cooldown("0")
                .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, USER_EFFECT);
        return CastResult.success();
    }

    @Override
    public void attack(Living user, Living target) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, USER_EFFECT)) {
            int poisonTime = (int) Math.round(asDouble(user, target, getProperty("poison-time", String.class, DEFAULT_POISON_TIME)));

            AtherysSkills.getInstance().getEffectService().applyEffect(target, new PoisonEffect(poisonTime));
            AtherysSkills.getInstance().getEffectService().removeEffect(user, USER_EFFECT);
        }
    }

    private static class PoisonEffect extends TemporaryPotionEffect {
        private PoisonEffect(int duration) {
            super(
                    "poison-effect",
                    "Poison",
                    PotionEffect.builder()
                            .potionType(PotionEffectTypes.POISON)
                            .amplifier(1)
                            .duration(duration)
                            .particles(true)
                            .build()
            );
        }
    }
}
