package com.atherys.rpgskills.t3;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.action.CollideEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.security.spec.ECField;
import java.util.Collection;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class ExplosiveShot extends RPGSkill {
    public static final String EXPLOSIVE_SHOT_EFFECT = "explosive-shot-user-effect";

    public ExplosiveShot() {
        super(
                SkillSpec.create()
                        .id("explosive-shot")
                        .name("Explosive Shot")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Your next arrow shot within ", arg(TIME), " seconds will explode on impact, dealing ",
                                arg(DAMAGE), " physical damage to all enemies within ", arg(RADIUS), " blocks."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(RADIUS, ofProperty(this, RADIUS, "3")),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, "10000")),
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, "100"))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, EXPLOSIVE_SHOT_EFFECT);
        return CastResult.success();
    }

    public void onCollide(CollideEvent event, @Getter("getSource") Arrow arrow) {
        Location<World> location = arrow.getLocation();
    }
}
