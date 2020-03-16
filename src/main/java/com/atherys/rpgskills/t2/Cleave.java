package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.util.Tuple;

import java.util.Collection;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Cleave extends RPGSkill implements PartySkill {
    private static String DEFAULT_DAMAGE = "5.0";
    public Cleave() {
        super(
                SkillSpec.create()
                        .id("cleave")
                        .name("Cleave")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Make a wide sweeping melee attack in front of you, dealing ",
                                arg(DAMAGE), " physical damage to all enemies in the area."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d direction = PhysicsUtils.getUnitDirection(user);
        /*
           ###
         P####
           ###
         */
        Vector3d centre = user.getLocation().getPosition().add(direction.getX() * 3, 0, direction.getZ() * 3);
        Vector3d inFront = user.getLocation().getPosition().add(direction);

        AtherysRPG.getInstance().getLogger().info(centre.toString());
        Collection<Entity> inRadius = user.getWorld().getNearbyEntities(centre, 1.75);
        inRadius.addAll(user.getWorld().getNearbyEntities(inFront, 0.75));

        String damageExpression = getProperty(DAMAGE, String.class, DEFAULT_DAMAGE);
        DamageSource damageSource = DamageUtils.directPhysical(user);

        inRadius.forEach(entity -> {
            if (entity instanceof Living && !entity.equals(user) && !arePlayersInParty(user, (Living) entity)) {
                Living target = (Living) entity;
                double damage = asDouble(user, target, damageExpression);
                target.damage(damage, damageSource);
            }
        });

        return CastResult.success();
    }
}
