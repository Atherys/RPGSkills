package com.atherys.rpgskills.t2;

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

public class Backstab extends TargetedRPGSkill implements PartySkill {
    private static ParticleEffect particle = ParticleEffect.builder()
            .type(ParticleTypes.REDSTONE_DUST)
            .quantity(2)
            .build();

    public Backstab() {
        super(
                SkillSpec.create()
                        .id("backstab")
                        .name("Backstab")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Make a precise strike against target enemy, dealing ", arg(DAMAGE),
                                "physical damage. If you strike from behind, deal double damage."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, DescriptionArguments.ofProperty(this, DAMAGE, "10"))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target)) throw isInParty();

        double damage = asDouble(user, target, getProperty(DAMAGE, String.class, "10"));

        Vector3d facing = PhysicsUtils.getUnitDirection(target.getRotation());
        Vector3d direction = PhysicsUtils.getUnitDirection(user);

        double angle = Math.acos(direction.dot(facing));
        if (angle <= 1 && angle >= 0) {
            damage *= 2;
            PhysicsUtils.spawnParticleCloud(particle, target.getLocation().add(0, -1, 0));
        }

        target.damage(damage, DamageUtils.directPhysical(user));
        PhysicsUtils.playSoundForLiving(user, SoundTypes.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

        return CastResult.success();
    }
}
