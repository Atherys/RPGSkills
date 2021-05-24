package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Sprint extends RPGSkill {
    public Sprint() {
        super(
                SkillSpec.create()
                        .id("sprint")
                        .name("Sprint")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Hasten your pace, increasing your movement speed for ", arg(TIME), " seconds."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(TIME, DescriptionArguments.ofTimeProperty(this, TIME, "10000"))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(TIME, String.class, "10000"));
        AtherysSkills.getInstance().getEffectService().applyEffect(user, Effects.ofSpeed(getId(), getName(), duration, 1));
        return CastResult.success();
    }
}
