package com.atherys.rpgskills;

import com.atherys.skills.api.effect.TemporaryPotionEffect;
import com.atherys.skills.event.EffectRegistrationEvent;
import com.atherys.skills.event.SkillRegistrationEvent;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "rpgskills",
        name = "RPG Skills",
        description = "Proprietary skills for A'therys Horizons",
        dependencies = {
                @Dependency(id = "atheryscore"),
                @Dependency(id = "atherysrpg")
        }
)
public class RpgSkills {

    private static RpgSkills instance;

    public static RpgSkills getInstance() {
        return instance;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        instance = this;
    }

    @Listener
    public void onRegisterSkills(SkillRegistrationEvent event) {
        event.registerSkills(
                new RagingBlow(),
                new MysticMissile(),
                new Hamstring()
        );
    }

    @Listener
    public void onRegisterEffects(EffectRegistrationEvent event) {
        event.registerEffects(
                new TemporaryPotionEffect(
                        "slow",
                        "Slow",
                        PotionEffect.builder()
                                .potionType(PotionEffectTypes.SLOWNESS)
                                .amplifier(1)
                                .particles(true)
                                .duration(100)
                                .build()
                )
        );
    }
}
