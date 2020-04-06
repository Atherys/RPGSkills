package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;

import java.util.Collection;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Pulsewave extends RPGSkill implements PartySkill {
    private static final String DEFAULT_RADIUS = "5.0";
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_OTHER_TEXT = "";

    public Pulsewave() {
        super(
                SkillSpec.create()
                        .id("pulsewave")
                        .name("Pulsewave")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Send out a burst of energy, dealing ", arg(DAMAGE), " magical damage to all enemies in a ",
                                arg(AMPLIFIER), " block radius from you. ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_RADIUS)),
                Tuple.of(OTHER_TEXT, TextSerializers.FORMATTING_CODE.deserialize(this.getProperty(OTHER_TEXT, String.class, DEFAULT_OTHER_TEXT)))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Collection<Entity> inRadius = user.getNearbyEntities(asDouble(user, getProperty(AMPLIFIER, String.class, DEFAULT_RADIUS)));
        String damageExpression = getProperty(DAMAGE, String.class, DEFAULT_DAMAGE);
        DamageSource damageSource = DamageUtils.directMagical(user);
        Vector3d userPosition = user.getLocation().getPosition();

        inRadius.forEach(entity -> {
            if (entity instanceof Living && !entity.equals(user) && !arePlayersInParty(user, (Living) entity)) {
                Living target = (Living) entity;
                double damage = asDouble(user, target, damageExpression);
                Vector3d between = target.getLocation().getPosition().sub(userPosition).normalize();
                target.setVelocity(Vector3d.from(between.getX() * 0.5, 0.4, between.getZ() * 0.5));

                target.damage(damage, damageSource);
            }
        });
        return CastResult.success();
    }
}
