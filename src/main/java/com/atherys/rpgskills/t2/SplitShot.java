package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;

public class SplitShot extends RPGSkill {
    public static final String SPLITSHOT_EFFECT = "split-shot";

    private static final List<Double> angles = Arrays.asList(0.17, -0.17, 0.34, -0.34);

    public SplitShot() {
        super(
                SkillSpec.create()
                        .id("split-shot")
                        .name("Split Shot")
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, SPLITSHOT_EFFECT);
        return CastResult.success();
    }

    @Listener
    public void onShoot(LaunchProjectileEvent event, @First Living living) {
        if (AtherysSkills.getInstance().getEffectService().hasEffect(living, SPLITSHOT_EFFECT) && event.getTargetEntity().getType() == EntityTypes.TIPPED_ARROW) {
            AtherysSkills.getInstance().getEffectService().removeEffect(living, SPLITSHOT_EFFECT);

            Projectile originalArrow = event.getTargetEntity();
            Vector3d velocity = originalArrow.getVelocity();

            angles.forEach(a -> {
                double x = velocity.getX();
                double z = velocity.getZ();
                Vector3d newVelocity = Vector3d.from((Math.cos(a) * x - Math.sin(a) * z), velocity.getY(), Math.sin(a) * x - Math.cos(a) * z);

                living.launchProjectile(Arrow.class, newVelocity);
            });
        }
    }
}
