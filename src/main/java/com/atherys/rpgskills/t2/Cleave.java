package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.util.Tuple;

import java.util.HashSet;
import java.util.Set;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Cleave extends RPGSkill implements PartySkill {
    private static String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_OTHER_TEXT = "";

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.SWEEP_ATTACK)
            .quantity(1)
            .build();

    public Cleave() {
        super(
                SkillSpec.create()
                        .id("cleave")
                        .name("Cleave")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Make a wide sweeping melee attack in front of you, dealing ",
                                arg(DAMAGE), " physical damage to all enemies in the area. ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d direction = PhysicsUtils.getUnitDirection(user);
        /*
        The area the AoE should cover, where P is the caster.
           ###
         P####
           ###
         */
        Vector3d centre = user.getLocation().getPosition().add(direction.getX() * 3, 1, direction.getZ() * 3);
        Vector3d inFront = user.getLocation().getPosition().add(direction.getX(), 1, direction.getZ());

        Set<Entity> inRadius = new HashSet<>(user.getWorld().getNearbyEntities(centre, 1.75));
        inRadius.addAll(user.getWorld().getNearbyEntities(inFront, 2));

        String damageExpression = getProperty(DAMAGE, String.class, DEFAULT_DAMAGE);
        DamageSource damageSource = DamageUtils.directPhysical(user);

        inRadius.forEach(entity -> {
            if (entity instanceof Living && !entity.equals(user) && !arePlayersInParty(user, (Living) entity)) {
                Living target = (Living) entity;
                double damage = asDouble(user, target, damageExpression);
                target.damage(damage, damageSource);
            }
        });

        user.getWorld().spawnParticles(particleEffect, inFront);
        user.getWorld().spawnParticles(particleEffect, centre);
        user.getWorld().spawnParticles(particleEffect, centre.add(Vector3d.UNIT_X));
        user.getWorld().spawnParticles(particleEffect, centre.add(Vector3d.UNIT_Z));

        return CastResult.success();
    }
}
