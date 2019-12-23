package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.text.TextTemplate;

import java.util.Collection;
import java.util.List;

import static com.atherys.rpgskills.util.CommonProperties.AMPLIFIER;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;

public class Pulsewave extends RPGSkill {
    private static final String DEFAULT_RADIUS = "5.0";
    private static final String DEFAULT_DAMAGE = "5.0";
    public Pulsewave() {
        super(
                SkillSpec.create()
                        .id("pulsewave")
                        .name("Pulsewave")
                        .descriptionTemplate(TextTemplate.of(
                                ""
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Collection<Entity> inRadius = user.getNearbyEntities(asDouble(user, getProperty(AMPLIFIER, String.class, DEFAULT_RADIUS)));
        String damageExpression = getProperty(DAMAGE, String.class, DEFAULT_DAMAGE);
        DamageSource damageSource = DamageUtils.directPhysical(user);
        Vector3d userPosition = user.getLocation().getPosition();

        inRadius.forEach(entity -> {
            if (entity instanceof Living && !entity.equals(user)) {
                Living target = (Living) entity;
                double damage = asDouble(user, target, damageExpression);
                target.damage(damage, damageSource);
                Vector3d between = target.getLocation().getPosition().sub(userPosition).normalize();
                target.setVelocity(Vector3d.from(between.getX() * 0.5, 0.2, between.getZ() * 0.5));
            }
        });
        return CastResult.success();
    }
}
