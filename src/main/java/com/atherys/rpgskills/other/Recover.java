package com.atherys.rpgskills.other;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.t2.VexingMark;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.HEALING;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Recover extends RPGSkill {
    private final static String DEFAULT_HEAL_EXPRESSION = "5.0";

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.HAPPY_VILLAGER)
            .quantity(2)
            .build();

    public Recover() {
        super(
                SkillSpec.create()
                .id("recovery")
                .name("Recovery")
                .descriptionTemplate(DescriptionUtils.buildTemplate(
                        "Recover some strength, healing yourself for ", arg(HEALING), "."
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
        LivingUtils.healLiving(user, healAmount);
        PhysicsUtils.spawnParticleCloud(particleEffect, user.getLocation());
        return CastResult.success();
    }
}
