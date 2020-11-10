package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.world.World;

public class SplitShot extends RPGSkill {
    public static final String SPLITSHOT_EFFECT = "split-shot";
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

            DataContainer arrowContainer = event.getTargetEntity().toContainer();
            World world = living.getWorld();

            Projectile arrow1 = (Projectile) world.createEntity(arrowContainer).get();
            arrow1.setVelocity(arrow1.getVelocity().add(0.5, 0, 0));
            world.spawnEntity(arrow1);

            Projectile arrow2 = (Projectile) world.createEntity(arrowContainer).get();
            arrow2.setVelocity(arrow1.getVelocity().add(0.25, 0, 0));
            world.spawnEntity(arrow2);

            Projectile arrow3 = (Projectile) world.createEntity(arrowContainer).get();
            arrow3.setVelocity(arrow1.getVelocity().add(0, 0, 0.5));
            world.spawnEntity(arrow3);

            Projectile arrow4 = (Projectile) world.createEntity(arrowContainer).get();
            arrow4.setVelocity(arrow1.getVelocity().add(0.5, 0, 0.25));
            world.spawnEntity(arrow4);
        }
    }
}
