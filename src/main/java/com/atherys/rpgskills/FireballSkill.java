package com.atherys.rpgskills;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Snowball;

public class FireballSkill extends RPGSkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_WISDOM * 1.5, 0.5, 10.0)";

    protected FireballSkill() {
        super(
                SkillSpec.create()
                .id("mystic-missile")
                .name("Mystic Missile")
                .descriptionTemplate("Fires a fireball to destroy your foes.")
                .resourceCost("0")
                .cooldown("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Snowball fireball = (Snowball) user.getWorld().createEntity(EntityTypes.FIREBALL, user.getLocation().getBlockPosition());
        fireball.setShooter(user);
        fireball.offer(Keys.ATTACK_DAMAGE, asDouble(user, getProperty("damage", String.class, DEFAULT_DAMAGE_EXPRESSION)));
        fireball.offer(Keys.FIRE_TICKS, Integer.MAX_VALUE);

        double yaw = (user.getHeadRotation().getY() + 90)  % 360;
        double pitch = user.getHeadRotation().getX() * -1;
        double rotYCos = Math.cos(Math.toRadians(pitch));
        double rotYSin = Math.sin(Math.toRadians(pitch));
        double rotXCos = Math.cos(Math.toRadians(yaw));
        double rotXSin = Math.sin(Math.toRadians(yaw));

        Vector3d velocity = Vector3d.from(1 * rotYCos * rotXCos, 1 * rotYSin, 1 * rotYCos * rotXSin);

        // TODO: Set cause stack?
        fireball.offer(Keys.VELOCITY, velocity);
        user.getWorld().spawnEntity(fireball);

        return CastResult.success();
    }
}
