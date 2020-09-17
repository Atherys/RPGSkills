package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.AttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.TemporaryEffect;
import com.atherys.skills.api.effect.TemporaryPotionEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;

public class Vanish extends RPGSkill implements AttackSkill {
    private static ParticleEffect effect = ParticleEffect.builder()
            .type(ParticleTypes.LARGE_SMOKE)
            .quantity(2)
            .build();

    private static Sound sound = Sound.builder(SoundTypes.BLOCK_FIRE_EXTINGUISH, 1).pitch(0.5).build();

    public Vanish() {
        super(
                SkillSpec.create()
                        .id("vanish")
                        .name("Vanish")
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        int duration = asInt(user, getProperty(CommonProperties.TIME, String.class, "10000"));
        PhysicsUtils.spawnParticleCloud(effect, user.getLocation().add(0, -3, 0));
        PhysicsUtils.spawnParticleCloud(effect, user.getLocation().add(0, -2, 0));
        PhysicsUtils.spawnParticleCloud(effect, user.getLocation().add(0, -1, 0));
        Sound.playSound(sound, user.getWorld(), user.getLocation().getPosition());
        AtherysSkills.getInstance().getEffectService().applyEffect(user, new VanishEffect(duration));
        return CastResult.success();
    }

    @Override
    public boolean attack(Living user, Living target) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, "vanish")) {
            AtherysSkills.getInstance().getEffectService().removeEffect(user, "vanish");
        }

        if (AtherysSkills.getInstance().getEffectService().hasEffect(target, "vanish")) {
            AtherysSkills.getInstance().getEffectService().removeEffect(target, "vanish");
        }

        return false;
    }

    private static class VanishEffect extends TemporaryPotionEffect {
        private static PotionEffect.Builder builder = PotionEffect.builder()
                .particles(false)
                .potionType(PotionEffectTypes.INVISIBILITY);

        protected VanishEffect(int duration) {
            super("vanish", "Vanish", builder.duration(duration / 50).build(), true);
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            super.apply(character);
            character.getLiving().ifPresent(living -> {
                living.offer(Keys.VANISH, true);
            });
            return false;
        }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) {
            super.remove(character);
            character.getLiving().ifPresent(living -> {
                living.offer(Keys.VANISH, false);
            });
            return false;
        }
    }
}
