package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Cleanse  extends TargetedRPGSkill implements PartySkill {

    private static final String DEFAULT_OTHER_TEXT = "";

    public Cleanse() {
        super(
                SkillSpec.create()
                        .id("cleanse")
                        .name("Cleanse")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Cleanse target ally of any negative effects.", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(OTHER_TEXT, TextSerializers.FORMATTING_CODE.deserialize(this.getProperty(OTHER_TEXT, String.class, DEFAULT_OTHER_TEXT)))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (!arePlayersInParty(user, target)) throw notInParty();

        AtherysSkills.getInstance().getEffectService().clearNegativeEffects(target);
        return CastResult.success();
    }
}
