package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class Tackle extends RPGSkill {
    public static final String TACKLE_EFFECT = "tackle-user-effect";

    public Tackle() {
        super(
                SkillSpec.create()
                        .id("tackle")
                        .name("Tackle")
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double horizontal = asDouble(user, getProperty(CommonProperties.HORIZONTAL, String.class, "2"));
        double vertical = asDouble(user, getProperty(CommonProperties.VERTICAL, String.class, "0.5"));
        Vector3d direction = PhysicsUtils.getUnitDirection(user);

        AtherysSkills.getInstance().getEffectService().applyEffect(user, TACKLE_EFFECT);
        user.setVelocity(Vector3d.from(direction.getX() * horizontal, vertical, direction.getZ() * horizontal));
        return CastResult.success();
    }

    @Listener
    public void onLand(CollideBlockEvent event, @Root Living entity) {
        AtherysSkills.getInstance().getEffectService().removeEffect(entity, TACKLE_EFFECT);
    }

    @Listener
    public void onCollide(CollideEntityEvent event, @Root Living collider) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(collider, TACKLE_EFFECT)) {
            Entity target = event.getEntities().get(0);

            if (target instanceof Living) {
                AtherysSkills.getInstance().getEffectService().removeEffect(collider, TACKLE_EFFECT);
                collider.setVelocity(Vector3d.ZERO);

                double damage = asDouble(collider, getProperty(CommonProperties.DAMAGE, String.class, "50"));
                DamageSource source = DamageUtils.directPhysical(collider);

                target.damage(damage, source);

                int duration = asInt(collider, getProperty(CommonProperties.TIME, String.class, "3000"));
                Applyable slowness = Effects.ofSlowness("Slowness", "slowness", duration, 2);

                AtherysSkills.getInstance().getEffectService().applyEffect(collider, slowness);
                AtherysSkills.getInstance().getEffectService().applyEffect((Living) target, slowness);
            }
        }
    }
}