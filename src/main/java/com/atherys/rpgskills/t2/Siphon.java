package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.effect.ApplyableCarrier;
import com.atherys.skills.api.effect.PeriodicEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.TextTemplate;

import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Siphon extends RPGSkill {
    public Siphon() {
        super(
                SkillSpec.create()
                        .id("siphon")
                        .name("Siphon")
                        .descriptionTemplate(TextTemplate.of(
                                "Siphon life from target enemy, dealing ", arg(DAMAGE), " magical damage to them and healing yourself for ",
                                arg(DAMAGE), " over ", arg(TIME), " seconds."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        return null;
    }

    private static class SiphonEffect extends PeriodicEffect {

        protected SiphonEffect(long duration) {
            super("siphon", "Siphon", 0, 0, false);
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            return false;
        }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) {
            return false;
        }
    }
}
