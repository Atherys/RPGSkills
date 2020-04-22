package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.MeleeAttackSkill;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpg.api.skill.DescriptionArguments.ofSource;
import static com.atherys.rpg.api.skill.TargetedRPGSkill.MAX_RANGE_PROPERTY;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;
import static org.spongepowered.api.text.format.TextColors.GOLD;

public class Hamstring extends RPGSkill implements MeleeAttackSkill, PartySkill {
    public static final String HAMSTRING_EFFECT = "hamstring-user-effect";

    private static final String DEFAULT_TIME = "5000";
    private static final String DEFAULT_AMPLIFIER = "1";
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_OTHER_TEXT = "";

    public Hamstring() {
        super(
                SkillSpec.create()
                .id("hamstring")
                .name("Hamstring")
                .descriptionTemplate(DescriptionUtils.buildTemplate(
                        "Your next melee attack to hit an enemy will cripple them, dealing ", arg(DAMAGE),
                        " physical damage and reducing their movement speed by ", arg(AMPLIFIER),
                        GOLD, "%", " for ", arg(TIME),
                        ". ", arg(OTHER_TEXT)
                ))
                .cooldown("0")
                .resourceCost("0")
                .properties(ImmutableMap.of(MAX_RANGE_PROPERTY, "5.0"))
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(AMPLIFIER, ofSource(getProperty(AMPLIFIER, String.class, DEFAULT_AMPLIFIER) + "*15")),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, HAMSTRING_EFFECT);

        return CastResult.success();
    }

    @Override
    public boolean meleeAttack(Living user, Living target) {
        if (arePlayersInParty(user, target)) return true;

        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, HAMSTRING_EFFECT)) {
            AtherysSkills.getInstance().getEffectService().removeEffect(user, HAMSTRING_EFFECT);

            int slowTime = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));
            int slowAmplifier = asInt(user, getProperty(AMPLIFIER, String.class, DEFAULT_AMPLIFIER));
            double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));

            target.damage(damage, DamageUtils.directPhysical(user));
            Applyable hamstring = Effects.ofSlowness("hamstring", "Hamstring", slowTime, slowAmplifier);
            AtherysSkills.getInstance().getEffectService().applyEffect(target, hamstring);
        }

        return false;
    }

    @Listener(order = Order.LAST)
    public void onMeleeAttack(DamageEntityEvent event, @Root EntityDamageSource source, @Getter("getTargetEntity") Living target) {
        onDamage(event, source, target);
    }
}
