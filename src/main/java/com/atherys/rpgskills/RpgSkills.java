package com.atherys.rpgskills;

import com.atherys.skills.AtherysSkills;
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

    private static RpgSkills instance;

    public static RpgSkills getInstance() {
        return instance;
    }

    private void registerSkills() {
        AtherysSkills.getInstance().getSkillService().registerSkills(
                new RagingBlow(),
                new MysticMissile()
        );
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        instance = this;
        registerSkills();
    }
}
