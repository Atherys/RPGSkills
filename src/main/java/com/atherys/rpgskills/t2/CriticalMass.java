package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.rpgskills.util.skill.RadiusSkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import com.udojava.evalex.Expression;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import java.math.BigDecimal;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class CriticalMass extends RPGSkill implements RadiusSkill, PartySkill {
    private static final String DEFAULT_RADIUS = "10";
    private static final String DEFAULT_HORIZONTAL = "1";

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

        applyToRadius(user.getLocation(), radius, living -> {
            if (arePlayersInParty(living, user) || living == user) return;
            double distance = userPosition.distanceSquared(living.getLocation().getPosition());

            Expression h = AtherysRPG.getInstance().getExpressionService().getExpression(getProperty(HORIZONTAL, String.class, DEFAULT_HORIZONTAL));
            h.setVariable("DISTANCE", BigDecimal.valueOf(distance));
            double horizontal = AtherysRPG.getInstance().getExpressionService().evalExpression(user, h).doubleValue();

            Expression v = AtherysRPG.getInstance().getExpressionService().getExpression(getProperty(VERTICAL, String.class, DEFAULT_HORIZONTAL));
            v.setVariable("DISTANCE", BigDecimal.valueOf(distance));
            double vertical = AtherysRPG.getInstance().getExpressionService().evalExpression(user, v).doubleValue();

            Vector3d between = userPosition.sub(living.getLocation().getPosition()).normalize();
            living.setVelocity(between.mul(horizontal, vertical, horizontal));
        });

        return CastResult.success();
    }
}
