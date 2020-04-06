package com.atherys.rpgskills.t1;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.data.DamageExpressionData;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static org.spongepowered.api.text.TextTemplate.arg;

public class FireballSkill extends RPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_WIS * 1.5, 0.5, 10.0)";
    private static final String DEFAULT_OTHER_TEXT = "";

    private Map<UUID, Living> fireballs = new WeakHashMap<>();

    public FireballSkill() {
        super(
                SkillSpec.create()
                .id("fireball")
                .name("Fireball")
                .descriptionTemplate(DescriptionUtils.buildTemplate(
                        "Launch a fireball in the direction you’re facing, dealing ", arg(DAMAGE),
                        " magical damage to any enemy hit. ", arg(OTHER_TEXT)
                ))
                .resourceCost("0")
                .cooldown("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE_EXPRESSION)),
                Tuple.of(OTHER_TEXT, TextSerializers.FORMATTING_CODE.deserialize(this.getProperty(OTHER_TEXT, String.class, DEFAULT_OTHER_TEXT)))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d spawnPosition = user.getLocation().getPosition().add(0, 1.5, 0);
        Snowball fireball = (Snowball) user.getWorld().createEntity(EntityTypes.SNOWBALL, spawnPosition);

        AtherysRPG.getInstance().getLogger().info(fireball.getValues().toString());

        fireball.setShooter(user);
        fireball.offer(Keys.FIRE_TICKS, Integer.MAX_VALUE);
        fireball.offer(new DamageExpressionData(getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION)));
        Vector3d velocity = PhysicsUtils.getUnitDirection(user).mul(2.5);
        fireball.setVelocity(velocity);
        fireball.offer(Keys.ACCELERATION, velocity.mul(0.05));

        user.getWorld().spawnEntity(fireball);

        return CastResult.success();
    }

    @Listener
    public void onFireballCollide(CollideEntityEvent event, @Getter("getSource") Snowball fireball) {
        Living user = fireballs.get(fireball.getUniqueId());

        if (user != null && event.getEntities().get(0) instanceof Living) {
            fireballs.remove(fireball.getUniqueId());
            Living target = (Living) event.getEntities().get(0);

            // If the entity is not a player, we don't want a double hit
            if (!(target instanceof Player)) return;

            if (arePlayersInParty(user, target)) return;

            if (!target.equals(user)) {
                DamageUtils.indirectSource(user, fireball);
            }
        }
    }
}
