package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.*;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.PeriodicEffect;
import com.atherys.skills.api.effect.TemporaryEffect;
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
import org.spongepowered.api.util.Tuple;

import java.util.Collection;
import java.util.List;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Tackle extends RPGSkill {

    public Tackle() {
        super(
                SkillSpec.create()
                        .id("tackle")
                        .name("Tackle")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Dash forward in the direction you are facing, dealing ",
                                arg(DAMAGE), " physical damage to any enemy you collide with slowing both of you 2+(0.1*STR) seconds."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, "50"))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double horizontal = asDouble(user, getProperty(CommonProperties.HORIZONTAL, String.class, "2"));
        double vertical = asDouble(user, getProperty(CommonProperties.VERTICAL, String.class, "0.5"));
        Vector3d direction = PhysicsUtils.getUnitDirection(user);

        Applyable tackleEffect = Effects.aura(
                getId(),
                getName(),
                Integer.MAX_VALUE,
                true,
                1.5,
                false,
                this::applyTackle
        );

        AtherysSkills.getInstance().getEffectService().applyEffect(user, tackleEffect);
        user.setVelocity(Vector3d.from(direction.getX() * horizontal, vertical, direction.getZ() * horizontal));
        return CastResult.success();
    }

    @Listener
    public void onLand(CollideBlockEvent event, @Root Living entity) {
        AtherysSkills.getInstance().getEffectService().removeEffect(entity, getId());
    }

    private void applyTackle(Living user, List<Living> nearby) {
        AtherysSkills.getInstance().getEffectService().removeEffect(user, getId());
        user.setVelocity(Vector3d.ZERO);

        double damage = asDouble(user, getProperty(DAMAGE, String.class, "50"));
        DamageSource source = DamageUtils.directPhysical(user);

        Living target = nearby.get(0);

        target.damage(damage, source);

        int duration = asInt(user, target, getProperty(CommonProperties.TIME, String.class, "3000"));
        Applyable targetSlowness = Effects.ofSlowness("Slowness", "slowness", duration, 2);
        Applyable userSlowness = Effects.ofSlowness("Slowness", "slowness", duration, 2);

        AtherysSkills.getInstance().getEffectService().applyEffect(user, userSlowness);
        AtherysSkills.getInstance().getEffectService().applyEffect(target, targetSlowness);
    }
}
