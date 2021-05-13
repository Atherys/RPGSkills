package com.atherys.rpgskills.util.skill;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.function.Consumer;

public interface RadiusSkill {
    default void applyToRadius(Location<World> location, double radius, Consumer<Living> apply) {
        Collection<Entity> inRadius = location.getExtent().getNearbyEntities(location.getPosition(), radius);

        if (inRadius.size() > 0) {
            inRadius.forEach(entity -> {
                if (entity instanceof Living) {
                    apply.accept((Living) entity);
                }
            });
        }
    }
}
