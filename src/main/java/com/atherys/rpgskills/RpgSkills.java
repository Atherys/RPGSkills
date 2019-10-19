package com.atherys.rpgskills;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
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

    private void registerSkills() {

    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        registerSkills();
    }
}
