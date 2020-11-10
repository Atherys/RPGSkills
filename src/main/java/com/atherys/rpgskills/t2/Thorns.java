package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.skill.AttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.TemporaryEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;

import static com.atherys.rpgskills.util.CommonProperties.AMPLIFIER;
import static com.atherys.rpgskills.util.CommonProperties.TIME;

public class Thorns extends RPGSkill implements AttackSkill {
    private static final String DEFAULT_DURATION = "5000";

    public Thorns() {
        super(
                SkillSpec.create()
                        .id("thorns")
                        .name("Thorns")
                        .resourceCost("0")
                        .cooldown("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(TIME, String.class, DEFAULT_DURATION));
        AtherysSkills.getInstance().getEffectService().applyEffect(user, new ThornsEffect(duration));
        return CastResult.success();
    }

    @Listener(order = Order.LATE)
    public void onAttack(DamageEntityEvent event, EntityDamageSource source) {
        onDamage(event, source);
    }

    @Override
    public boolean attack(Living user, Living target, DamageEntityEvent event) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(target, "thorns")) {
            DamageType damageType = ((DamageSource) event.getSource()).getType();

            if (damageType == DamageTypes.CUSTOM) {
                double percent = asDouble(target, getProperty(AMPLIFIER, String.class, "0.5"));
                DamageSource source = DamageUtils.directPhysical(target);
                user.damage(percent * event.getBaseDamage(), source);
            }
        }

        return false;
    }

    private static class ThornsEffect extends TemporaryEffect {

        private ThornsEffect(int duration) {
            super("thorns", "Shield Spikes", duration, true);
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            return true;
        }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) {
            return true;
        }
    }
}
