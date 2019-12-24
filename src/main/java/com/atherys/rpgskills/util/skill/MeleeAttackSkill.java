package com.atherys.rpgskills.util.skill;

import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

/**
 * Provides a generic way to implement a listener for melee attacks.
 */
public interface MeleeAttackSkill {

    default void onDamage(DamageEntityEvent event, @Root EntityDamageSource source) {
        if (event instanceof IndirectEntityDamageSource) return;

        if (source.getSource() instanceof Living && event.getTargetEntity() instanceof Living) {
            Living user = (Living) source.getSource();
            event.setCancelled(meleeAttack(user, (Living) event.getTargetEntity()));
        }
    }

    boolean meleeAttack(Living user, Living target);
}
