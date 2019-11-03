package com.atherys.rpgskills;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.text.channel.MessageReceiver;

public class Hamstring extends TargetedRPGSkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_STRENGTH * 1.5, 0.5, 10.0)";
    private static final double DEFAULT_SLOW_TIME = 5.0;

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

        try {
            AtherysSkills.getInstance().getEffectFacade().applyEffect(target, "slow");
        } catch (CommandException e) {
            if (user instanceof MessageReceiver) {
                ((MessageReceiver) user).sendMessage(e.getText());
            }
        }
        return CastResult.success();
    }
}
