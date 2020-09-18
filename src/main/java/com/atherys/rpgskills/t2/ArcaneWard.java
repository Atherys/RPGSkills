package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.CommonProperties;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.skill.AttackSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class ArcaneWard extends RPGSkill implements AttackSkill {
    public static final String WARD_EFFECT = "arcane-ward-effect";
    public ArcaneWard() {
        super(
                SkillSpec.create()
                        .id("arcane-ward")
                        .name("Arcane Ward")
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, WARD_EFFECT);
        return CastResult.success();
    }

    @Override
    public boolean attack(Living user, Living target, DamageEntityEvent event) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(user, WARD_EFFECT) && ((DamageSource) event.getSource()).getType() == DamageTypes.MAGIC) {
            int duration = asInt(user, getProperty(CommonProperties.TIME, String.class, "3000"));
            AtherysSkills.getInstance().getEffectService().applyEffect(target, Effects.ofSlowness("arcane-ward", "Root", duration, 50));
        }
        return false;
    }
}
