package com.atherys.rpgskills;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.TemporaryPotionEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.Living;

public class Shield extends RPGSkill {
    private static final String DEFAULT_SHIELD_TIME = "500";
    private static final String DEFAULT_SHIELD_AMOUNT = "2";

    protected Shield() {
        super(
                SkillSpec.create()
                .id("shield")
                .name("Shield")
                .cooldown("0")
                .resourceCost("0")
                .descriptionTemplate("Shields from oncoming damage.")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int time = (int) Math.round(asDouble(user, getProperty("shield-time", String.class, DEFAULT_SHIELD_TIME)));
        int level = (int) Math.round(asDouble(user, getProperty("shield-time", String.class, DEFAULT_SHIELD_AMOUNT)));

        AtherysSkills.getInstance().getEffectService().applyEffect(user, new ShieldEffect(time, level));

        return CastResult.success();
    }

    private static class ShieldEffect extends TemporaryPotionEffect {
        private ShieldEffect(int time, int amplifier) {
            super("shield-effect",
                    "Shielding",
                    PotionEffect.builder()
                            .particles(true)
                            .potionType(PotionEffectTypes.ABSORPTION)
                            .duration(time)
                            .amplifier(amplifier)
                            .build());
        }
    }
}
