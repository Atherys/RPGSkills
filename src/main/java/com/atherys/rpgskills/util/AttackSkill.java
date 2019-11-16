package com.atherys.rpgskills.util;

import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

/**
 * Provides a generic way to implement an attack (any kind of attack) in a skill.
 */
public interface AttackSkill {

    @Listener
    default void onDamage(DamageEntityEvent event, @Root EntityDamageSource source) {
        if (!(event.getTargetEntity() instanceof Living)) return;

        Living target = (Living) event.getTargetEntity();

        if (source.getSource() instanceof Living) {
            attack((Living) source.getSource(), target);
            return;
        }

        if (source instanceof IndirectEntityDamageSource) {
            IndirectEntityDamageSource indirectSource = (IndirectEntityDamageSource)  source;
            if (indirectSource.getIndirectSource() instanceof Living)  {
                attack((Living) indirectSource.getIndirectSource(), target);
            }
        }
    }

    void attack(Living user, Living target);

}