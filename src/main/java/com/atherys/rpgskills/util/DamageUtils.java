package com.atherys.rpgskills.util;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.stat.AttributeType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;

import java.util.Map;

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

    public static DamageSource indirectSource(Entity user, Entity source) {
        return IndirectEntityDamageSource.builder()
                .entity(source)
                .proxySource(user)
                .type(DamageTypes.CUSTOM)
                .absolute()
                .bypassesArmor()
                .build();
    }

    public static double magicDamage(Living target, double incoming) {
        Map<AttributeType, Double> targetAttributes = AtherysRPG.getInstance().getAttributeService().getAllAttributes(target);

        return AtherysRPG.getInstance().getDamageService()
                .getMagicalDamageMitigation(targetAttributes, incoming);
    }
}
