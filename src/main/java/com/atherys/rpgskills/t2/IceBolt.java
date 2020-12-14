package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static com.atherys.rpgskills.util.CommonProperties.TIME;

public class IceBolt extends RPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "10";
    private Map<UUID, Living> iceBolts = new WeakHashMap<>();

    public IceBolt() {
        super(
                SkillSpec.create()
                        .id("ice-bolt")
                        .name("Ice Bolt")
                        .cooldown("0")
                        .resourceCost("0")
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d spawnPosition = user.getLocation().getPosition().add(0, 2, 0);
        FallingBlock boulder = (FallingBlock) user.getWorld().createEntity(EntityTypes.FALLING_BLOCK, spawnPosition);

        boulder.offer(Keys.FALLING_BLOCK_STATE, BlockState.builder().blockType(BlockTypes.ICE).build());
        boulder.offer(Keys.FALL_TIME, 1);
        boulder.offer(Keys.CAN_PLACE_AS_BLOCK, false);

        Vector3d velocity = PhysicsUtils.getUnitDirection(user).mul(1.25, 1.25, 1.25);

        iceBolts.put(boulder.getUniqueId(), user);
        boulder.setVelocity(velocity);
        user.getWorld().spawnEntity(boulder);

        return CastResult.success();
    }

    @Listener
    public void collide(CollideEntityEvent event, @Getter("getSource") FallingBlock iceBolt) {
        Living user = iceBolts.get(iceBolt.getUniqueId());

        if (user != null && event.getEntities().get(0) instanceof Living) {
            Living target = (Living) event.getEntities().get(0);

            if (arePlayersInParty(user, target)) return;

            if (!target.equals(user)) {
                Vector3d velocity = iceBolt.getVelocity().normalize();
                iceBolts.remove(iceBolt.getUniqueId());
                iceBolt.remove();

                double damage = asDouble(user, target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION));
                boolean dealtDamage = target.damage(damage, DamageUtils.directMagical(user));

                if (dealtDamage) {
                    int duration = asInt(user, getProperty(TIME, String.class, "5000"));
                    Applyable slowness = Effects.ofSlowness("icebolt", "IceBolt", duration, 2);
                }
            }
        }
    }

    @Listener
    public void onWallCollide(CollideBlockEvent event, @Getter("getSource") FallingBlock iceBolt) {
        if (iceBolts.containsKey(iceBolt.getUniqueId())) {
            iceBolts.remove(iceBolt.getUniqueId());
            iceBolt.remove();
        }
    }
}
