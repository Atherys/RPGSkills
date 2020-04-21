package com.atherys.rpgskills.util;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;

public final class DamageUtils {
    private static final EntityDamageSource.Builder common = EntityDamageSource.builder()
            .absolute()
            .bypassesArmor();

    public static EntityDamageSource directPhysical(Entity user) {
        return common
                .entity(user)
                .type(DamageTypes.CUSTOM)
                .build();
    }

    public static EntityDamageSource directMagical(Entity user) {
        return common
                .entity(user)
                .type(DamageTypes.MAGIC)
                .build();
    }

    public static EntityDamageSource directPure(Entity user) {
        return common
                .entity(user)
                .type(DamageTypes.VOID)
                .build();
    }

    public static DamageSource indirectSource(Entity user, Entity source, DamageType damageType) {
        return IndirectEntityDamageSource.builder()
                .entity(source)
                .proxySource(user)
                .type(damageType)
                .absolute()
                .bypassesArmor()
                .build();
    }
}
