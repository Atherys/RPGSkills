package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.util.Tuple;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static org.spongepowered.api.text.TextTemplate.arg;

public class BoulderToss extends RPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "5.0";

    private Map<UUID, Living> boulders = new WeakHashMap<>();

    public BoulderToss() {
        super(
                SkillSpec.create()
                        .id("boulder-toss")
                        .name("Boulder Toss")
                        .descriptionTemplate(TextTemplate.of(
                                "Throw a boulder in the direction you’re facing. The first enemy hit takes ",
                                arg(DAMAGE), " physical damage and is knocked back."
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE_EXPRESSION))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d spawnPosition = user.getLocation().getPosition().add(0, 2, 0);
        FallingBlock boulder = (FallingBlock) user.getWorld().createEntity(EntityTypes.FALLING_BLOCK, spawnPosition);

        boulder.offer(Keys.FALLING_BLOCK_STATE, BlockState.builder().blockType(BlockTypes.COBBLESTONE).build());
        boulder.offer(Keys.FALL_TIME, 1);
        boulder.offer(Keys.CAN_PLACE_AS_BLOCK, false);

        Vector3d velocity = PhysicsUtils.getUnitDirection(user).mul(1.25, 1.25, 1.25);

        boulders.put(boulder.getUniqueId(), user);
        boulder.setVelocity(velocity);
        user.getWorld().spawnEntity(boulder);

        return CastResult.success();
    }

    @Listener
    public void collide(CollideEntityEvent event, @Getter("getSource") FallingBlock boulder) {
        Living user = boulders.get(boulder.getUniqueId());

        if (user != null && event.getEntities().get(0) instanceof Living) {
            Living target = (Living) event.getEntities().get(0);

            if (arePlayersInParty(user, target)) return;

            if (!target.equals(user)) {
                boulders.remove(boulder.getUniqueId());
                boulder.remove();
                double damage = asDouble(user, target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION));
                target.damage(damage, DamageUtils.directMagical(user));
                AtherysRPG.getInstance().getLogger().info("{} was hit", target.toString());
                Vector3d normalized = boulder.getVelocity().normalize();
                target.setVelocity(Vector3d.from(normalized.getX() * 1.5, 0.6, normalized.getZ() * 1.5));
            }
        }
    }

    @Listener
    public void onWallCollide(CollideBlockEvent event, @Getter("getSource") FallingBlock boulder) {
        if (boulders.containsKey(boulder.getUniqueId())) {
            boulder.remove();
        }
    }
}
