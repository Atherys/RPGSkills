package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.AttackSkill;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Envenom extends RPGSkill implements AttackSkill, PartySkill {
    public static final String POISON_EFFECT_USER = "poison-effect-user";

    private static final String DEFAULT_POISON_TIME = "10000";
    private static final String DEFAULT_POISON_DAMAGE = "10";

    public Envenom() {
        super(
                SkillSpec.create()
                        .id("envenom")
                        .name("Envenom")
                        .descriptionTemplate(TextTemplate.of(
                                "Coat your weapon in a deadly venom, causing your next weapon attack to deal an additional ", arg(DAMAGE),
                                "pure damage over ", arg(TIME), " seconds."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, DescriptionArguments.ofProperty(this, DAMAGE, DEFAULT_POISON_DAMAGE)),
                Tuple.of(TIME, DescriptionArguments.ofProperty(this, TIME, DEFAULT_POISON_TIME))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, POISON_EFFECT_USER);
        return CastResult.success();
    }

    @Override
    public void attack(Living user, Living target) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, POISON_EFFECT_USER)) {
            if (arePlayersInParty(user, target)) return;

            int poisonDamage = (int) Math.round(asDouble(user, target, getProperty(CommonProperties.AMPLIFIER, String.class, DEFAULT_POISON_DAMAGE)));
            int poisonTime = (int) Math.round(asDouble(user, target, getProperty(TIME, String.class, DEFAULT_POISON_TIME)));

            Applyable poisonEffect = Effects.damageOverTime(
                    "poison",
                    "Poison",
                    poisonTime,
                    poisonDamage
            );

            AtherysSkills.getInstance().getEffectService().applyEffect(target, poisonEffect);
            AtherysSkills.getInstance().getEffectService().removeEffect(user, POISON_EFFECT_USER);
        }
    }

    @Listener
    public void onAttack(DamageEntityEvent event, @Root EntityDamageSource source) {
        onDamage(event, source);
    }
}
