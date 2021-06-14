package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.*;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Kick extends TargetedRPGSkill {
    private static Map<UUID, Living> kickers = new HashMap<>();

    public Kick() {
        super(
                SkillSpec.create()
                        .id("kick")
                        .name("Kick")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Deliver a heavy kick to a target enemy, knocking them back. If they collide with an enemy, they both take ",
                                arg(DAMAGE), " physical damage."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, "50"))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        Vector3d direction = PhysicsUtils.getUnitDirection(user);
        double horizontal = asDouble(user, getProperty(HORIZONTAL, String.class, "0.8"));
        double vertical = asDouble(user, getProperty(VERTICAL, String.class, "0.5"));

        target.setVelocity(Vector3d.from(direction.getX() * horizontal, vertical, direction.getZ() * horizontal));

        double damage = asDouble(user, getProperty(CommonProperties.DAMAGE, String.class, "50"));
        DamageSource source = DamageUtils.directPhysical(user);
        target.damage(damage, source);

        kickers.put(target.getUniqueId(), user);

        Applyable kickEffect = Effects.aura(
                getId(),
                getName(),
                Integer.MAX_VALUE,
                false,
                1.5,
                false,
                this::applyKick
        );
        AtherysSkills.getInstance().getEffectService().applyEffect(target, kickEffect);

        return CastResult.success();
    }

    @Listener
    public void onLand(CollideBlockEvent event, @Root Living entity) {
        AtherysSkills.getInstance().getEffectService().removeEffect(entity, getId());
        kickers.remove(entity.getUniqueId());
    }

    private void applyKick(Living kicked, List<Living> nearby) {
        Living user = kickers.remove(kicked.getUniqueId());
        AtherysSkills.getInstance().getEffectService().removeEffect(user, getId());

        double damage = asDouble(user, getProperty(CommonProperties.DAMAGE, String.class, "50"));
        DamageSource source = DamageUtils.directPhysical(user);

        kicked.damage(damage, source);
        nearby.get(0).damage(damage, source);
    }
}
