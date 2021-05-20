package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
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
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Empower extends RPGSkill {
    private static final String DEFAULT_AMPLIFIER = "0.5";
    private static final String DEFAULT_TIME = "5000";
    private static final String DEFAULT_ATTRIBUTE = "atherys:strength";

    private static Sound sound1 = Sound.builder(SoundTypes.BLOCK_ENDERCHEST_OPEN, 1).pitch(1.5).build();
    private static Sound sound2 = Sound.builder(SoundTypes.ENTITY_ELDER_GUARDIAN_HURT, 0.6).pitch(1.5).build();

    private AttributeType attributeType;

    public Empower() {
        super(
                SkillSpec.create()
                        .id("empower")
                        .name("Empower")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Empower your strikes, increasing your melee physical damage by ",
                                arg(AMPLIFIER), "% for ", arg(TIME), " seconds."
                        ))
                        .resourceCost("0")
                        .cooldown("0")
        );

        setDescriptionArguments(
                Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_AMPLIFIER)),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_TIME))
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
        double damageMultiplier = asDouble(user, getProperty(AMPLIFIER, String.class, DEFAULT_AMPLIFIER));
        long duration = (long) asDouble(user, getProperty(TIME, String.class, DEFAULT_TIME));

        Map<AttributeType, Double> attributes = new HashMap<>(1);
        attributes.put(this.attributeType, damageMultiplier);

        Applyable damageEffect = Effects.ofAttributes(getId(), getName(), duration, attributes, true);
        AtherysSkills.getInstance().getEffectService().applyEffect(user, damageEffect);

        Location<World> location = user.getLocation();
        Sound.playSound(sound1, location.getExtent(), location.getPosition());
        Sound.playSound(sound2, location.getExtent(), location.getPosition());

        return CastResult.success();
    }
}
