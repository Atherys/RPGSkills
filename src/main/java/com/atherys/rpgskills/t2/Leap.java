package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Leap extends RPGSkill {
    private static final String HORIZONTAL = "horizontal";
    private static final String VERTICAL = "vertical";

    private static final String DEFAULT_HORIZONTAL = "1";
    private static final String DEFAULT_OTHER_TEXT = "";

    public Leap() {
        super(
                SkillSpec.create()
                        .id("leap")
                        .name("Leap")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Jump forward in the direction you're facing. ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(OTHER_TEXT, TextSerializers.FORMATTING_CODE.deserialize(this.getProperty(OTHER_TEXT, String.class, DEFAULT_OTHER_TEXT)))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d direction = PhysicsUtils.getUnitDirection(user);
        double horizontal = asDouble(user, getProperty(HORIZONTAL, String.class, DEFAULT_HORIZONTAL));
        double vertical = asDouble(user, getProperty(VERTICAL, String.class, DEFAULT_HORIZONTAL));

        user.setVelocity(Vector3d.from(direction.getX() * horizontal, vertical, direction.getZ() * horizontal));

        return CastResult.success();
    }
}
