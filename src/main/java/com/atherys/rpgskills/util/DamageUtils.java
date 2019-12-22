package com.atherys.rpgskills.util;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;

public final class DamageUtils {
    public static EntityDamageSource directSource(Entity user) {
        return EntityDamageSource.builder()
                .entity(user)
                .type(DamageTypes.CUSTOM)
                .absolute()
                .bypassesArmor()
                .build();
    }

    public static DamageSource indirectSource(Entity user, Entity source) {
        return IndirectEntityDamageSource.builder()
                .entity(source)
                .proxySource(user)
                .type(DamageTypes.CUSTOM)
                .absolute()
                .bypassesArmor()
                .build();
    }
}
