package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.explosive.fireball.SmallFireball;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;

public class FireballSkill extends RPGSkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_WIS * 1.5, 0.5, 10.0)";

    public FireballSkill() {
        super(
                SkillSpec.create()
                .id("fireball")
                .name("Fireball")
                .descriptionTemplate(TextTemplate.of(
                        "Launch a fireball in the direction you’re facing, dealing ", TextTemplate.arg(DAMAGE),
                        " magical damage to any enemy hit."
                ))
                .resourceCost("0")
                .cooldown("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE_EXPRESSION))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d spawnPosition = user.getLocation().getPosition().add(0, 1.5, 0);
        SmallFireball fireball = (SmallFireball) user.getWorld().createEntity(EntityTypes.SMALL_FIREBALL, spawnPosition);

        fireball.setShooter(user);
        fireball.offer(Keys.ATTACK_DAMAGE, asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION)));
        Vector3d velocity = PhysicsUtils.getUnitDirection(user);
        fireball.setVelocity(velocity);
        fireball.offer(Keys.ACCELERATION, velocity.mul(0.05));

        user.getWorld().spawnEntity(fireball);

        return CastResult.success();
    }
}
