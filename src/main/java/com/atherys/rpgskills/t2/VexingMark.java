package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.effect.TemporaryAttributesEffect;
import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.rpg.api.stat.AttributeTypes;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import java.util.Collections;
import java.util.Map;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class VexingMark extends TargetedRPGSkill implements PartySkill {
    public static final String VEXING_MARK_EFFECT = "vexing-mark-effect";

    private static final String DEFAULT_DECREASE = "0.5";
    private static final String DEFAULT_TIME = "10000";
    private static final String DEFAULT_OTHER_TEXT = "";
    private static final String DEFAULT_ATTRIBUTE = AttributeTypes.CONSTITUTION.getId();

    private final AttributeType attributeType;

    public VexingMark() {
        super(
                SkillSpec.create()
                        .id("vexing-mark")
                        .name("Vexing Mark")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Mark your target, making them standout for ", arg(TIME), ". All healing they receive is reduced by ",
                                arg(AMPLIFIER), "% for the duration. ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_DECREASE)),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );

        this.attributeType = Sponge.getRegistry().getType(AttributeType.class, getProperty(ATTRIBUTE, String.class, DEFAULT_ATTRIBUTE)).get();
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target)) throw isInParty();
        double decrease = asDouble(user, target, getProperty(AMPLIFIER, String.class, DEFAULT_DECREASE));
        Map<AttributeType, Double> decreasedAttributes = Collections.singletonMap(attributeType, -decrease);

        AtherysSkills.getInstance().getEffectService().applyEffect(
                target,
                new VexingMarkEffect(
                        (long) asDouble(user, target, getProperty(TIME, String.class, DEFAULT_TIME)),
                        decreasedAttributes
                )
        );

        return CastResult.success();
    }

    private static class VexingMarkEffect extends TemporaryAttributesEffect {
        private VexingMarkEffect(long duration, Map<AttributeType, Double> attributes) {
            super(VEXING_MARK_EFFECT, "Vexing Mark", duration, attributes, false);
        }

        @Override
        public boolean apply(long timestamp, ApplyableCarrier<?> character) {
            character.getLiving().ifPresent(living -> {
                PhysicsUtils.playSoundForLiving(living, SoundTypes.ENTITY_ELDER_GUARDIAN_CURSE, 1, 1.2);
            });
            return super.apply(timestamp, character);
        }
    }
}
