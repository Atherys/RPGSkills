package com.atherys.rpgskills;

import com.atherys.rpgskills.t1.*;
import com.atherys.rpgskills.t2.*;
import com.atherys.rpgskills.util.BlankEffect;
import com.atherys.rpgskills.util.BlankSkill;
import com.atherys.rpgskills.util.Effects;
import com.atherys.skills.event.EffectRegistrationEvent;
import com.atherys.skills.event.SkillRegistrationEvent;
import org.spongepowered.api.Sponge;
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
        Sponge.getEventManager().registerListeners(this, new Effects());
    }

    @Listener
    public void onRegisterSkills(SkillRegistrationEvent event) {
        event.registerSkills(
                // Tier 1
                new Enfeeble(),
                new FireballSkill(),
                new Hamstring(),
                new LightHeal(),
                new Slash(),
                new Shield(),

                // Tier 2
                new BoulderToss(),
                new Cleanse(),
                new Counterattack(),
                new Disarm(),
                new Envenom(),
                new Invigorate(),
                new Leap(),
                new Blindbolt(),
                new Pulsewave(),
                new Siphon(),
                new Sweep(),

                // For tree root
                new BlankSkill("root-skill", "RootSkill")
        );
    }

    @Listener
    public void onRegisterEffects(EffectRegistrationEvent event) {
        event.registerEffects(
                new BlankEffect(Hamstring.HAMSTRING_EFFECT, "Hamstring User", true),
                new BlankEffect(Envenom.POISON_EFFECT_USER, "Poison User", true)
        );
    }
}
