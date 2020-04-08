package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.t2.VexingMark;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import java.util.List;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.HEALING;

public class Recover extends RPGSkill {
    private static final String DEFAULT_HEAL_EXPRESSION = "5.0";
    private static final List<ParticleEffect> RECOVER_EFFECTS = ImmutableList.of(
            particle(Vector3d.FORWARD.mul(0.5)),
            particle(Vector3d.RIGHT.mul(0.5)),
            particle(Vector3d.ZERO.mul(0.5)),
            particle(Vector3d.UNIT_Z.mul(0.5)),
            particle(Vector3d.UNIT_X.mul(0.5))
    );

    private static ParticleEffect particle(Vector3d offset) {
        return ParticleEffect.builder()
                .type(ParticleTypes.HAPPY_VILLAGER)
                .quantity(2)
                .offset(offset)
                .build();
    }

    public Recover() {
        super(
                SkillSpec.create()
                .id("recovery")
                .name("Recovery")
                .descriptionTemplate(DescriptionUtils.buildTemplate(
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
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, VexingMark.VEXING_MARK_EFFECT)) {
            healAmount *= 0.5;
        }

        RECOVER_EFFECTS.forEach(particleEffect -> {
            user.getWorld().spawnParticles(particleEffect, user.getLocation().getPosition().add(Vector3d.from(0, 2, 0)));
        });

        LivingUtils.healLiving(user, healAmount);
        return CastResult.success();
    }
}
