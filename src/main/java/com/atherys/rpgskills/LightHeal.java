package com.atherys.rpgskills;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import org.spongepowered.api.entity.living.Living;

public class LightHeal extends RPGSkill {
    private static String DEFAULT_HEAL_EXPRESSION = "5.0";

    protected LightHeal() {
        super(
                SkillSpec.create()
                .id("light-heal")
                .name("Recover")
                .descriptionTemplate("Heal for a small amount.")
                .resourceCost("0")
                .cooldown("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double healAmount = asDouble(user, getProperty("healing", String.class, DEFAULT_HEAL_EXPRESSION));
        LivingUtils.healLiving(user, healAmount);
        return CastResult.success();
    }
}
