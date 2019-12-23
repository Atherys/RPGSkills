package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.HEALING;

public class Recover extends RPGSkill {
    private static String DEFAULT_HEAL_EXPRESSION = "5.0";

    public Recover() {
        super(
                SkillSpec.create()
                .id("recover")
                .name("Recover")
                .descriptionTemplate(TextTemplate.of(
                        "Recover some strength, healing yourself for ", TextTemplate.arg(HEALING), "."
                ))
                .resourceCost("0")
                .cooldown("0")
        );

        setDescriptionArguments(
                Tuple.of(HEALING, ofProperty(this, HEALING, DEFAULT_HEAL_EXPRESSION))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double healAmount = asDouble(user, getProperty(HEALING, String.class, DEFAULT_HEAL_EXPRESSION));
        LivingUtils.healLiving(user, healAmount);
        return CastResult.success();
    }
}
