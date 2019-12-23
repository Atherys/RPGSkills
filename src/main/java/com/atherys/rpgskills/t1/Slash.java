package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;

public class Slash extends TargetedRPGSkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_STR * 1.5, 0.5, 10.0)";

    public Slash() {
        super(
                SkillSpec.create()
                        .id("slash")
                        .name("Slash")
                        .cooldown("0")
                        .descriptionTemplate("Strikes the target with a powerful blow.")
                        .resourceCost("0")
                        .descriptionTemplate(TextTemplate.of("Slash at your target, dealing ", TextTemplate.arg("damage"), " physical damage."))
                        .properties(ImmutableMap.of(MAX_RANGE_PROPERTY, "5.0"))
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, DescriptionArguments.ofProperty(this, DAMAGE, DEFAULT_DAMAGE_EXPRESSION))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        double damage = asDouble(user, target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION));

        target.damage(damage, DamageUtils.directPhysical(user));

        return CastResult.success();
    }
}
