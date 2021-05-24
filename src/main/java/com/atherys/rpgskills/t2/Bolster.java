package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
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

import java.util.HashMap;
import java.util.Map;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.PERCENT;
import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Bolster extends RPGSkill {
    private static final String DEFAULT_TIME = "10000";
    private static final String DEFAULT_PERCENT = "30";

    private AttributeType physAttributeType;
    private AttributeType magicAttributeType;

    public Bolster() {
        super(
                SkillSpec.create()
                        .id("fortify")
                        .name("Fortify")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Bolster your defenses, increasing physical and magical resistances by ", arg(PERCENT),
                                "% for ", arg(TIME), "."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(PERCENT, ofProperty(this, PERCENT, DEFAULT_PERCENT)),
                Tuple.of(TIME, DescriptionArguments.ofTimeProperty(this, TIME, DEFAULT_TIME))
        );
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        super.setProperties(properties);

        this.physAttributeType = Sponge.getRegistry().getType(AttributeType.class, "atherys:physical_resistance").get();
        this.magicAttributeType = Sponge.getRegistry().getType(AttributeType.class, "atherys:magical_resistance").get();
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));

        Map<AttributeType, Double> userAttributes = AtherysRPG.getInstance().getAttributeService().getAllAttributes(user);
        double percent = asDouble(user, getProperty(PERCENT, String.class, DEFAULT_PERCENT)) / 100;

        Map<AttributeType, Double> attributes = new HashMap<>(2);
        attributes.put(physAttributeType, percent * userAttributes.get(physAttributeType));
        attributes.put(magicAttributeType, percent * userAttributes.get(magicAttributeType));
        Applyable resistanceEffect = Effects.ofAttributes(getId(), getName(), duration, attributes, true);

        AtherysSkills.getInstance().getEffectService().applyEffect(user, resistanceEffect);

        return CastResult.success();
    }
}
