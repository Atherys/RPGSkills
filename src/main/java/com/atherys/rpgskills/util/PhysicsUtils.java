package com.atherys.rpgskills.util;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.living.Living;

public final class PhysicsUtils {
    public static Vector3d getUnitDirection(Living entity) {
        Vector3d rotation = entity.getHeadRotation();
        return Quaterniond
                .fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection()
                .normalize();
    }
}
