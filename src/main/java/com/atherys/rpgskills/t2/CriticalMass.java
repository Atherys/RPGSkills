package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.rpgskills.util.skill.RadiusSkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import com.udojava.evalex.Expression;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.math.BigDecimal;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class CriticalMass extends RPGSkill implements RadiusSkill, PartySkill {
    private static final String DEFAULT_RADIUS = "10";
    private static final String DEFAULT_FORCE = "1";

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.CRITICAL_HIT)
            .quantity(5)
            .build();

    private static final Sound sound = Sound.builder(SoundTypes.ENTITY_HORSE_ARMOR, 1).pitch(0.6)
            .soundCategory(SoundCategories.PLAYER).build();

    public CriticalMass() {
        super(
                SkillSpec.create()
                        .id("critical-mass")
                        .name("Critical Mass")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "All enemies within ", arg(RADIUS),  " blocks are pulled toward you."
                        ))
        );

        setDescriptionArguments(
            Tuple.of(RADIUS, ofProperty(this, RADIUS, DEFAULT_RADIUS))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double radius = asDouble(user, getProperty(RADIUS, String.class, DEFAULT_RADIUS));
        Vector3d userPosition = user.getLocation().getPosition();
        Location<World> location = user.getLocation();

        applyToRadius(location, radius, living -> {
            if (arePlayersInParty(living, user) || living == user) return;
            double distance = userPosition.distanceSquared(living.getLocation().getPosition());

            Expression h = AtherysRPG.getInstance().getExpressionService().getExpression(
                    getProperty(HORIZONTAL, String.class, DEFAULT_FORCE));
            h.setVariable("DISTANCE", BigDecimal.valueOf(distance));
            double horizontal = AtherysRPG.getInstance().getExpressionService().evalExpression(user, h).doubleValue();

            Expression v = AtherysRPG.getInstance().getExpressionService().getExpression(
                    getProperty(VERTICAL, String.class, DEFAULT_FORCE));
            v.setVariable("DISTANCE", BigDecimal.valueOf(distance));
            double vertical = AtherysRPG.getInstance().getExpressionService().evalExpression(user, v).doubleValue();

            Vector3d between = userPosition.sub(living.getLocation().getPosition()).normalize();
            living.setVelocity(between.mul(horizontal, vertical, horizontal));
        });

        Sound.playSound(sound, location.getExtent(), location.getPosition());

        PhysicsUtils.spawnParticleCircle(particleEffect, location, radius);

        Task.builder()
                .delayTicks(4)
                .execute(() -> PhysicsUtils.spawnParticleCircle(particleEffect, location, radius * 2/3))
                .submit(AtherysRPG.getInstance());

        Task.builder()
                .delayTicks(8)
                .execute(() -> PhysicsUtils.spawnParticleCircle(particleEffect, location, radius / 3))
                .submit(AtherysRPG.getInstance());

        return CastResult.success();
    }
}
