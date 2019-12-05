package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.MeleeAttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpg.api.skill.TargetedRPGSkill.MAX_RANGE_PROPERTY;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Hamstring extends RPGSkill implements MeleeAttackSkill {
    public static final String HAMSTRING_EFFECT = "hamstring-user-effect";

    private static final String DEFAULT_SLOW_TIME = "60";
    private static final String DEFAULT_SLOW_AMPLIFIER= "50";

    public Hamstring() {
        super(
                SkillSpec.create()
                .id("hamstring")
                .name("Hamstring")
                .descriptionTemplate(TextTemplate.of(
                        "Your next melee attack to hit an enemy will cripple them, dealing ", arg(DAMAGE),
                        " physical damage and reducing their movement speed by ", arg(AMPLIFIER), "% for ", arg(TIME), " seconds."
                ))
                .cooldown("0")
                .resourceCost("0")
                .properties(ImmutableMap.of(MAX_RANGE_PROPERTY, "5.0"))
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, "5.0")),
                Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_SLOW_AMPLIFIER)),
                Tuple.of(TIME, ofProperty(this, AMPLIFIER, DEFAULT_SLOW_TIME))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, HAMSTRING_EFFECT);

        return CastResult.success();
    }

    @Override
    public void meleeAttack(Living user, Living target) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, HAMSTRING_EFFECT)) {
            int slowTime = (int) Math.round(asDouble(user, target, getProperty(CommonProperties.TIME, String.class, DEFAULT_SLOW_TIME)));
            int slowAmplifier = (int) Math.round(asDouble(user, target, getProperty(CommonProperties.AMPLIFIER, String.class, DEFAULT_SLOW_AMPLIFIER)));

            Applyable hamstring = Effects.ofSlowness("hamstring", "Hamstring", slowTime, slowAmplifier);
            AtherysSkills.getInstance().getEffectService().applyEffect(target, hamstring);

            AtherysSkills.getInstance().getEffectService().removeEffect(user, HAMSTRING_EFFECT);
        }
    }

    @Listener
    public void onMeleeAttack(DamageEntityEvent event, @Root EntityDamageSource source) {
        onDamage(event, source);
    }
}
