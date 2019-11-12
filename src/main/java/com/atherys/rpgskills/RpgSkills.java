package com.atherys.rpgskills;

import com.atherys.skills.event.EffectRegistrationEvent;
import com.atherys.skills.event.SkillRegistrationEvent;
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
                new FireballSkill(),
                new Hamstring(),
                new LightHeal(),
                new Shield()
        );
    }

    @Listener
    public void onRegisterEffects(EffectRegistrationEvent event) {
        event.registerEffects(
                new BlankEffect("hamstring-user-effect", "Hamstring User")
        );
    }
}
