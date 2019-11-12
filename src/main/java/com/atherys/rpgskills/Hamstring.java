package com.atherys.rpgskills;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.TemporaryPotionEffect;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

public class Hamstring extends TargetedRPGSkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_STRENGTH * 1.5, 0.5, 10.0)";
    private static final String DEFAULT_SLOW_TIME = "60";
    private static final String USER_EFFECT = "hamstring-user-effect";

    protected Hamstring() {
        super(
                SkillSpec.create()
                .id("hamstring")
                .name("Hamstring")
                .descriptionTemplate("Attacks and slows your target.")
                .cooldown("0")
                .resourceCost("0")
                .properties(ImmutableMap.of(MAX_RANGE_PROPERTY, "5.0"))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        double damage = asDouble(user, target, getProperty("damage", String.class, DEFAULT_DAMAGE_EXPRESSION));

        target.damage(damage, EntityDamageSource.builder().entity(user).type(DamageTypes.ATTACK).build());
        AtherysSkills.getInstance().getEffectService().applyEffect(user, USER_EFFECT);

        return CastResult.success();
    }

    @Listener
    public void onDamage(DamageEntityEvent event, @Root EntityDamageSource source) {
        if (event instanceof IndirectEntityDamageSource) return;

        if (source.getSource() instanceof Living && event.getTargetEntity() instanceof Living) {
            Living user = (Living) source.getSource();

            if (AtherysSkills.getInstance().getEffectService().hasEffect(user, USER_EFFECT)) {
                Living target = (Living) event.getTargetEntity();
                int slowTime = (int) Math.round(asDouble(user, target, getProperty("slow-time", String.class, DEFAULT_SLOW_TIME)));

                AtherysSkills.getInstance().getEffectService().applyEffect(target, new HamstringEffect(slowTime));
                AtherysSkills.getInstance().getEffectService().removeEffect(user, USER_EFFECT);
            }
        }
    }

    private static class HamstringEffect extends TemporaryPotionEffect {
        private HamstringEffect(int duration) {
            super(
                    "hamstring-effect",
                    "Hamstring",
                    PotionEffect.builder()
                            .potionType(PotionEffectTypes.SLOWNESS)
                            .amplifier(1)
                            .duration(duration)
                            .particles(true)
                            .build()
            );
        }
    }
}
