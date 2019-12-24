package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.stat.AttributeType;
import com.atherys.rpg.api.stat.AttributeTypes;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PartySkill;
import com.atherys.rpgskills.util.TargetedAtherysSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;

import java.util.Collections;
import java.util.Map;

import static com.atherys.rpgskills.util.CommonProperties.AMPLIFIER;
import static org.spongepowered.api.text.TextTemplate.arg;

public class VexingMark extends TargetedAtherysSkill implements PartySkill {
    public static final String VEXING_MARK_EFFECT = "vexing-mark-effect";

    private static final String DEFAULT_DECREASE = "TARGET_CON * 0.5";
    private static final String DEFAULT_TIME = "10.0";

    public VexingMark() {
        super(
                SkillSpec.create()
                        .id("vexing-mark")
                        .name("Vexing Mark")
                        .descriptionTemplate(TextTemplate.of(
                                "Mark your target, making them standout for ", arg(AMPLIFIER), " seconds. All healing they receive is reduced by ",
                                arg(AMPLIFIER), "% for the duration."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (arePlayersInParty(user, target)) throw isInParty();

        double decrease = asDouble(user, target, getAmplifier(DEFAULT_DECREASE));
        Map<AttributeType, Double> decreasedAttributes = Collections.singletonMap(AttributeTypes.CONSTITUTION, -decrease);

        AtherysSkills.getInstance().getEffectService().applyEffect(
                target,
                Effects.ofAttributes(
                        VEXING_MARK_EFFECT,
                        "Vexing Mark",
                        (long) asDouble(user, target, getTime(DEFAULT_TIME)),
                        decreasedAttributes,
                        false
                )
        );

        return CastResult.success();
    }
}
