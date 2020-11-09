package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.HEALING;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Invigorate extends TargetedRPGSkill implements PartySkill {
    private static final String DEFAULT_HEAL_EXPRESSION = "5.0";
    private static final String DEFAULT_OTHER_TEXT = "";

    private static final ParticleEffect beamEffect = ParticleEffect.builder()
            .type(ParticleTypes.HAPPY_VILLAGER)
            .quantity(3)
            .build();

    private static final ParticleEffect healEffect = ParticleEffect.builder()
            .type(ParticleTypes.HEART)
            .quantity(2)
            .build();

    public Invigorate() {
        super(
                SkillSpec.create()
                        .id("invigorate")
                        .name("Invigorate")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Reinvigorate a target ally, healing them for ", arg(HEALING), ". ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(HEALING, ofProperty(this, HEALING, DEFAULT_HEAL_EXPRESSION)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        try {
            return super.cast(user, timestamp, args);
        } catch (CastException e) {
            return cast(user, user, timestamp, args);
        }
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (!arePlayersInParty(user, target) && user != target) throw notInParty();

        double healAmount = asDouble(user, getProperty(HEALING, String.class, DEFAULT_HEAL_EXPRESSION));
        if (AtherysSkills.getInstance().getEffectService().hasEffect(target, VexingMark.VEXING_MARK_EFFECT)) {
            healAmount *= 0.5;
        }
        LivingUtils.healLiving(target, healAmount);

        PhysicsUtils.spawnParticleBeam(beamEffect, user.getLocation(), target.getLocation());
        if (target != user) {
            PhysicsUtils.spawnParticleBeam(beamEffect, user.getLocation(), target.getLocation());
        }

        Task.builder()
                .delayTicks(10)
                .execute(() -> PhysicsUtils.spawnParticleCloud(healEffect, target.getLocation().sub(0, 1, 0)))
                .submit(AtherysRPG.getInstance());

        return CastResult.success();
    }
}
