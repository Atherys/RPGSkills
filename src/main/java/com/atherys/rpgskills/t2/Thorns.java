package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.AttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.TemporaryEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.AMPLIFIER;
import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Thorns extends RPGSkill implements AttackSkill {
    private static final String DEFAULT_DURATION = "5000";
    private static final String DEFAULT_AMPLIFIER = "0.5";

    private static final Sound[] sounds = {
            Sound.builder(SoundTypes.ENTITY_BLAZE_HURT, 1).pitch(1.2)
                    .soundCategory(SoundCategories.PLAYER).build(),
            Sound.builder(SoundTypes.ENCHANT_THORNS_HIT, 1).pitch(0.8)
                    .soundCategory(SoundCategories.PLAYER).build(),
            Sound.builder(SoundTypes.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1).pitch(2)
                    .soundCategory(SoundCategories.PLAYER).build(),
    };

    public Thorns() {
        super(
                SkillSpec.create()
                        .id("thorns")
                        .name("Thorns")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "For the next ", arg(TIME), " seconds, ", arg(AMPLIFIER),
                                "% of all physical damage you take is reflected back to the attacker."
                        ))
                        .resourceCost("0")
                        .cooldown("0")
        );

        setDescriptionArguments(
                Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_AMPLIFIER)),
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_DURATION))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(TIME, String.class, DEFAULT_DURATION));
        AtherysSkills.getInstance().getEffectService().applyEffect(user, new ThornsEffect(duration));
        return CastResult.success();
    }

    @Listener(order = Order.LATE)
    public void onAttack(DamageEntityEvent event, @Root EntityDamageSource source) {
        onDamage(event, source);
    }

    @Override
    public boolean attack(Living user, Living target, DamageEntityEvent event) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(target, "thorns")) {
            DamageType damageType = ((DamageSource) event.getSource()).getType();
            if (damageType == DamageTypes.CUSTOM || damageType == DamageTypes.ATTACK) {
                double percent = asDouble(target, getProperty(AMPLIFIER, String.class, DEFAULT_AMPLIFIER));
                DamageSource source = DamageUtils.directPhysical(target);
                user.damage(percent * event.getBaseDamage(), source);
                Location<World> location = user.getLocation();
                for (Sound sound : sounds) {
                    Sound.playSound(sound, location.getExtent(), location.getPosition());
                }
            }
        }

        return false;
    }

    private static class ThornsEffect extends TemporaryEffect {

        private ThornsEffect(int duration) {
            super("thorns", "Thorns", duration, true);
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
