package com.atherys.rpgskills.util;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;

public final class PhysicsUtils {

    private static final List<Vector3d> offsets = Arrays.asList(
            Vector3d.from(0, 3, 0),
            Vector3d.from(0.5, 3, 0),
            Vector3d.from(0, 3, 0.5),
            Vector3d.from(0, 3.2, 0),
            Vector3d.from(0, 3.5, 0),
            Vector3d.from(0.1, 3.3, 0.7),
            Vector3d.from(0, 2.8, 0),
            Vector3d.from(0.5, 3, 0.2),
            Vector3d.from(0.3, 2.7, 0.3),
            Vector3d.from(0.7, 3.3, 0.1),
            Vector3d.from(0, 3.5, 1),
            Vector3d.from(0, 3, 0.5),
            Vector3d.from(-0.5, 3, 0.0),
            Vector3d.from(-0.2, 3.4, -0.2),
            Vector3d.from(-0.7, 3.8, 0.3),
            Vector3d.from(-0.4, 2.6, -0.5),
            Vector3d.from(0.4, 3.1, -0.8),
            Vector3d.from(0.2, 2.9, -0.5)
    );

    public static Vector3d getUnitDirection(Living entity) {
        Vector3d rotation = entity.getHeadRotation();
        return Quaterniond
                .fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection()
                .normalize();
    }

    public static void spawnParticleCloud(ParticleEffect particleEffect, Location<World> location) {
        Vector3d position = location.getPosition();
        for (Vector3d offset : offsets) {
            location.getExtent().spawnParticles(particleEffect, position.add(offset));
        }
    }

    public static void playSoundForLiving(Living living, SoundType sound, double volume, double pitch) {
        if (living instanceof Player) {
            Player player = (Player) living;
            player.playSound(sound, player.getPosition(), volume, pitch);
        }
    }

    public static void spawnParticleBeam(ParticleEffect particleEffect, Location<World> from, Location<World> to) {
        Vector3d difference = to.getPosition().sub(from.getPosition());
        double distance = difference.length();
        difference = difference.normalize();

        for (double i = 0; i < distance; i += 0.1) {
            from.getExtent().spawnParticles(particleEffect, from.getPosition().add(difference.mul(i).add(0, 1, 0)));
        }
    }
}
