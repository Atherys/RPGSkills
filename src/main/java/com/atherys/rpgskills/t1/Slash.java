package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Slash extends TargetedRPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_STR * 1.5, 0.5, 10.0)";

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.SWEEP_ATTACK)
            .quantity(1)
            .build();

    public Slash() {
        super(
                SkillSpec.create()
                        .id("bash")
                        .name("Bash")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Strike your target, dealing ", arg("damage"), " physical damage. "
                        ))
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, DescriptionArguments.ofProperty(this, DAMAGE, DEFAULT_DAMAGE_EXPRESSION))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target)) throw isInParty();

        double damage = asDouble(user, target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION));

        target.damage(damage, DamageUtils.directPhysical(user));
        Vector3d inFront = PhysicsUtils.getUnitDirection(user).mul(2);
        user.getWorld().spawnParticles(particleEffect, user.getLocation().getPosition().add(inFront.getX(), 1, inFront.getZ()));
        PhysicsUtils.playSoundForLiving(user, SoundTypes.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

        return CastResult.success();
    }
}
