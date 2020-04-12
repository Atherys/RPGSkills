package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Siphon extends TargetedRPGSkill {
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_HEALING = "5.0";
    private static final String DEFAULT_TIME = "10000";
    private static final String DEFAULT_OTHER_TEXT = "";

    public Siphon() {
        super(
                SkillSpec.create()
                        .id("siphon")
                        .name("Siphon")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Siphon life from target enemy, dealing ", arg(DAMAGE), " magical damage to them and healing yourself for ",
                                arg(HEALING), " over ", arg(TIME), ". ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(HEALING, ofProperty(this, HEALING, DEFAULT_HEALING)),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        int duration = asInt(user, target, getProperty(TIME, String.class, DEFAULT_TIME));
        double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
        double healing = asDouble(user, getProperty(HEALING, String.class, DEFAULT_HEALING));

        AtherysSkills.getInstance().getEffectService().applyEffect(target, new SiphonEffect(duration, damage, healing, user));
        return CastResult.success();
    }

    private static class SiphonEffect extends Effects.DamageOverTimeEffect {
        private Living caster;
        private double healingPerTick;

        protected SiphonEffect(long duration, double damage, double healing, Living caster) {
            super("siphon", "Siphon", duration, damage, DamageUtils.directMagical(caster));
            this.caster = caster;
            this.healingPerTick = healing / duration * 1000;
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            if (caster.isRemoved()) {
                this.setRemoved();
                return true;
            }

            super.apply(character);
            LivingUtils.healLiving(caster, healingPerTick);
            return true;
        }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) {
            return true;
        }

    }
}
