package com.atherys.rpgskills.util;

import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

public interface  RangedAttackSkill {
    @Listener
    default void onRangedAttack(DamageEntityEvent event, @Root IndirectEntityDamageSource source) {
        if (event.getTargetEntity() instanceof Living && source.getIndirectSource() instanceof Living) {
            rangedAttack((Living) source.getIndirectSource(), (Living) event.getTargetEntity());
        }
    }
    void rangedAttack(Living user, Living target);
}
