package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.AttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.TemporaryEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofTimeProperty;
import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Vanish extends RPGSkill implements AttackSkill {
    private static final ParticleEffect effect = ParticleEffect.builder()
            .type(ParticleTypes.LARGE_SMOKE)
            .quantity(2)
            .build();

    private static final Sound sound = Sound.builder(SoundTypes.BLOCK_FIRE_EXTINGUISH, 1).pitch(0.5).build();

    public Vanish() {
        super(
                SkillSpec.create()
                        .id("vanish")
                        .name("Vanish")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Vanish in a cloud of smoke, becoming invisible to enemies for ", arg(TIME),
                                " seconds. Taking damage or using skills reveals you."
                        ))
        );

        setDescriptionArguments(
            Tuple.of(TIME, ofTimeProperty(this, TIME, "10000"))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(TIME, String.class, "10000"));
        playParticles(user);
        AtherysSkills.getInstance().getEffectService().applyEffect(user, new VanishEffect(duration));
        return CastResult.success();
    }

    private static void playParticles(Living user) {
        PhysicsUtils.spawnParticleCloud(effect, user.getLocation().add(0, -3, 0));
        PhysicsUtils.spawnParticleCloud(effect, user.getLocation().add(0, -2, 0));
        PhysicsUtils.spawnParticleCloud(effect, user.getLocation().add(0, -1, 0));
        Sound.playSound(sound, user.getWorld(), user.getLocation().getPosition());
    }

    @Override
    public boolean attack(Living user, Living target, DamageEntityEvent event) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, "vanish")) {
            AtherysSkills.getInstance().getEffectService().removeEffect(user, "vanish");
        }

        if (AtherysSkills.getInstance().getEffectService().hasEffect(target, "vanish")) {
            AtherysSkills.getInstance().getEffectService().removeEffect(target, "vanish");
        }

        return false;
    }

    private static class VanishEffect extends TemporaryEffect {

        protected VanishEffect(int duration) {
            super("vanish", "Vanish", duration, true);
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            character.getLiving().ifPresent(living -> {
                living.offer(Keys.VANISH, true);
            });

            return false;
        }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) {
            character.getLiving().ifPresent(living -> {
                living.offer(Keys.VANISH, false);
                Vanish.playParticles(living);
            });
            return false;
        }
    }
}
