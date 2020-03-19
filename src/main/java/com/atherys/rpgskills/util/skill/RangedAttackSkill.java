package com.atherys.rpgskills.util.skill;

import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

public interface  RangedAttackSkill {
    default void onRangedAttack(DamageEntityEvent event, @Root IndirectEntityDamageSource source) {
        if (source.getType() == DamageTypes.CUSTOM
                || source.getType() == DamageTypes.MAGIC
                || source.getType() == DamageTypes.VOID) return;

        if (event.getTargetEntity() instanceof Living && source.getIndirectSource() instanceof Living) {
            event.setCancelled(rangedAttack((Living) source.getIndirectSource(), (Living) event.getTargetEntity()));
        }
    }

    boolean rangedAttack(Living user, Living target);
}
