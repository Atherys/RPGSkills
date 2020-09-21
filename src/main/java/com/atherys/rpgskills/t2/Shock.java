package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;

import java.util.Collection;

public class Shock extends RPGSkill implements PartySkill {
    private static ParticleEffect particle = ParticleEffect.builder()
            .type(ParticleTypes.MAGIC_CRITICAL_HIT)
            .quantity(2)
            .build();

    private static Sound sound = Sound.builder(SoundTypes.ENTITY_FIREWORK_TWINKLE, 1)
            .pitch(2)
            .build();

    public Shock() {
        super(
                SkillSpec.create()
                        .id("shock")
                        .name("Shock")
                        .resourceCost("0")
                        .cooldown("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Collection<Entity> nearby = user.getNearbyEntities(asDouble(user, getProperty(CommonProperties.RANGE, String.class, "20")));
        Vector3d position = user.getLocation().getPosition();

        Living closest = null;
        double distance = Double.MAX_VALUE;

        for (Entity e : nearby) {
            if (e instanceof Living && e != user && !arePlayersInParty((Living) e, user)) {
                double d = e.getLocation().getPosition().distanceSquared(position);
                if (d < distance) {
                    closest = (Living) e;
                    distance = d;
                }
            }
        }

        if (closest != null) {
            double damage = asDouble(user, getProperty(CommonProperties.DAMAGE, String.class, "50"));
            PhysicsUtils.spawnParticleBeam(particle, user.getLocation(), closest.getLocation());
            PhysicsUtils.spawnParticleCloud(particle, closest.getLocation().add(0, -1, 0));
            PhysicsUtils.spawnParticleCloud(particle, closest.getLocation().add(0, -2, 0));
            Sound.playSound(sound, closest.getWorld(), closest.getLocation().getPosition());
            closest.damage(damage, DamageUtils.directMagical(user));
        }

        return CastResult.success();
    }
}
