package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Sprint extends RPGSkill {
    private static final String DEFAULT_AMPLIFIER = "0.4";
    private static final String DEFAULT_TIME = "10000";
    private static final String DEFAULT_ATTRIBUTE = "atherys:speed_multiplier";

    private AttributeType attributeType;

    public Sprint() {
        super(
                SkillSpec.create()
                        .id("sprint")
                        .name("Sprint")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Hasten your pace, increasing your movement speed by ",
                                arg(AMPLIFIER), "% for ", arg(TIME), " seconds."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_AMPLIFIER)),
                Tuple.of(TIME, DescriptionArguments.ofTimeProperty(this, TIME, "10000"))
        );
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        super.setProperties(properties);

        String attributeId = getProperty(ATTRIBUTE, String.class, DEFAULT_ATTRIBUTE);
        this.attributeType = Sponge.getRegistry().getType(AttributeType.class, attributeId).get();
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double speedMultiplier = asDouble(user, getProperty(AMPLIFIER, String.class, DEFAULT_AMPLIFIER));
        long duration = (long) asDouble(user, getProperty(TIME, String.class, DEFAULT_TIME));

        Map<AttributeType, Double> attributes = new HashMap<>(1);
        attributes.put(this.attributeType, speedMultiplier);

        Applyable speedEffect = Effects.ofAttributes(getId(), getName(), duration, attributes, true);
        AtherysSkills.getInstance().getEffectService().applyEffect(user, speedEffect);

        return CastResult.success();
    }
}
