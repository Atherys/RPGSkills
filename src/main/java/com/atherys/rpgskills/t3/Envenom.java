package com.atherys.rpgskills.t3;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.AttackSkill;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.*;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Envenom extends RPGSkill implements AttackSkill, PartySkill {
    public static final String POISON_EFFECT_USER = "poison-effect-user";

    private static final String DEFAULT_TIME = "10000";
    private static final String DEFAULT_PREPARED_DURATION = "10000";
    private static final String DEFAULT_DAMAGE = "10";

    public Envenom() {
        super(
                SkillSpec.create()
                        .id("envenom")
                        .name("Envenom")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Coat your weapon in a deadly venom, causing your next weapon attack within ",
                                arg(PREPARED_DURATION), " to deal an additional ", arg(DAMAGE),
                                "pure damage over ", arg(TIME), "."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, DescriptionArguments.ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(TIME, DescriptionArguments.ofTimeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(PREPARED_DURATION, DescriptionArguments.ofTimeProperty(this, PREPARED_DURATION, DEFAULT_PREPARED_DURATION))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user,
                Effects.blankTemporary(
                        POISON_EFFECT_USER, "Poison User",
                        asInt(user, getProperty(PREPARED_DURATION, String.class, DEFAULT_PREPARED_DURATION)),
                        true));
        return CastResult.success();
    }

    @Override
    public boolean attack(Living user, Living target, DamageEntityEvent event) {
        if (arePlayersInParty(user, target)) return true;

        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, POISON_EFFECT_USER)) {
            int poisonDamage = asInt(user, target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
            int poisonTime = asInt(user, target, getProperty(TIME, String.class, DEFAULT_TIME));

            Applyable poisonEffect = Effects.pureDamageOverTime(
                    "poison",
                    "Poison",
                    poisonTime,
                    poisonDamage,
                    user
            );

            AtherysSkills.getInstance().getEffectService().applyEffect(target, poisonEffect);
            AtherysSkills.getInstance().getEffectService().removeEffect(user, POISON_EFFECT_USER);
        }

        return false;
    }

    @Listener
    public void onAttack(DamageEntityEvent event, @Root EntityDamageSource source) {
        onDamage(event, source);
    }
}

