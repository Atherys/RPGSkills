package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Pulsewave extends RPGSkill implements PartySkill {
    private static final String DEFAULT_RADIUS = "5.0";
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_OTHER_TEXT = "";

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.FLAME)
            .quantity(5)
            .build();

    public Pulsewave() {
        super(
                SkillSpec.create()
                        .id("immolate")
                        .name("Immolate")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Send out a burst of fire, dealing ", arg(DAMAGE), " magical damage to all enemies in a ",
                                arg(RADIUS), " block radius from you. ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(RADIUS, ofProperty(this, AMPLIFIER, DEFAULT_RADIUS)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double radius = asDouble(user, getProperty(RADIUS, String.class, DEFAULT_RADIUS));
        Collection<Entity> inRadius = user.getNearbyEntities(radius);

        if (inRadius.size() > 0) {
            double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
            DamageSource damageSource = DamageUtils.directMagical(user);
            Vector3d userPosition = user.getLocation().getPosition();

            inRadius.forEach(entity -> {
                if (entity instanceof Living && !entity.equals(user) && !arePlayersInParty(user, (Living) entity)) {
                    Living target = (Living) entity;
                    Vector3d between = target.getLocation().getPosition().sub(userPosition).normalize();

                    target.setVelocity(Vector3d.from(between.getX() * 0.5, 0.4, between.getZ() * 0.5));
                    target.damage(damage, damageSource);
                }
            });
        }

        spawnParticles(user.getLocation(), radius / 3);

        Task.builder()
                .delayTicks(4)
                .execute(() -> spawnParticles(user.getLocation(), radius * 2/3))
                .submit(AtherysRPG.getInstance());

        Task.builder()
                .delayTicks(8)
                .execute(() -> spawnParticles(user.getLocation(), radius))
                .submit(AtherysRPG.getInstance());

        return CastResult.success();
    }

    private void spawnParticles(Location<World> location, double radius) {
        Vector3d position = location.getPosition();
        double y = position.getY() + 1;

        for (double i = 0; i < Math.PI * 2; i += 0.1) {
            double x = position.getX() + radius * Math.cos(i);
            double z = position.getZ() + radius * Math.sin(i);

            location.getExtent().spawnParticles(particleEffect, Vector3d.from(x, y, z));
        }
    }
}
