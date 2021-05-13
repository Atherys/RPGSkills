package com.atherys.rpgskills.util.skill;

import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;

/**
 * Provides a generic way to implement a listener for melee attacks.
 */
public interface MeleeAttackSkill {

    default void onDamage(DamageEntityEvent event, EntityDamageSource source, Living target) {
        if (event instanceof IndirectEntityDamageSource) return;
        if (target.health().get() <= 0) return;

        if (source.getType() == DamageTypes.CUSTOM
                || source.getType() == DamageTypes.MAGIC
                || source.getType() == DamageTypes.VOID) return;

        if (source.getSource() instanceof Living) {
            Living user = (Living) source.getSource();
            event.setCancelled(meleeAttack(user, target));
        }
    }

    boolean meleeAttack(Living user, Living target);
}
