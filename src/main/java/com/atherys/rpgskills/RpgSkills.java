package com.atherys.rpgskills;

import com.atherys.rpgskills.t2.Bolster;
import com.atherys.rpgskills.other.Recover;
import com.atherys.rpgskills.t1.*;
import com.atherys.rpgskills.t2.*;
import com.atherys.rpgskills.t3.*;
import com.atherys.rpgskills.util.BlankEffect;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.BlankSkill;
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
        version = "%PLUGIN_VERSION%",
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
                new FireballSkill(),
                new Invocation(),
                new Shield(),
                new Slash(),
                new Sprint(),

                // Tier 2
                new ArcaneWard(),
                new Backstab(),
                new Bolster(),
                new Cleanse(),
                new Cleave(),
                new Endurance(),
                new Enfeeble(),
                new Hamstring(),
                new IceBolt(),
                new Intimidate(),
                new Invigorate(),
                new Kick(),
                new Leap(),
                new Pulsewave(),
                new Shock(),
                new SplitShot(),
                new Strengthen(),
                new Tackle(),
                new Vanish(),
                new VexingMark(),

                // Tier 3
                new BoulderToss(),
                new Blindbolt(),
                new Disarm(),
                new Siphon(),
                new ShieldSpikes(),
                new Envenom(),

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
