package com.atherys.rpgskills.t3;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static org.spongepowered.api.text.TextTemplate.arg;

public class BoulderToss extends RPGSkill implements PartySkill {
    private static final String AOE_RANGE = "aoe-range";
    private static final String AOE_DAMAGE_MODIFIER = "aoe-damage-modifier";

    private static final String DEFAULT_DAMAGE_EXPRESSION = "5.0";
    private static final String DEFAULT_AOE_RANGE = "3";
    private static final String DEFAULT_AOE_DAMAGE_MODIFIER = "3";

    private static final Sound boulder = Sound.builder(SoundTypes.ENTITY_GENERIC_EXPLODE, 3)
            .pitch(1.2)
            .build();

    private static final ParticleEffect effect = ParticleEffect.builder()
            .type(ParticleTypes.LARGE_SMOKE)
            .option(ParticleOptions.COLOR, Color.GRAY)
            .quantity(5)
            .build();

    private final Map<UUID, Living> boulders = new WeakHashMap<>();

    public BoulderToss() {
        super(
                SkillSpec.create()
                        .id("boulder-toss")
                        .name("Boulder Toss")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
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

    private void applyAOEDamage(Location<World> eventLocation, Living user, Living excludeEntity) {
        double damageModifier = asDouble(user, getProperty(AOE_DAMAGE_MODIFIER, String.class, DEFAULT_AOE_DAMAGE_MODIFIER));
        double range = asDouble(user, getProperty(AOE_RANGE, String.class, DEFAULT_AOE_RANGE));
        Collection<Entity> entities = eventLocation.getExtent().getNearbyEntities(eventLocation.getPosition(), range);

        Sound.playSound(boulder, user.getWorld(), eventLocation.getPosition());
        Vector3d position = eventLocation.getPosition();
        PhysicsUtils.spawnParticleCloud(effect, eventLocation.setPosition(new Vector3d(position.getX(), position.getFloorY() - 2, position.getZ())));

        entities.forEach(target -> {
            if (target instanceof Living && !arePlayersInParty(user, (Living) target) && !target.equals(user) && !target.equals(excludeEntity)) {
                double damage = (asDouble(user, (Living) target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION))) / damageModifier;
                target.damage(damage, DamageUtils.directMagical(user));
            }
        });
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

            applyAOEDamage(target.getLocation(), user, target);

            if (arePlayersInParty(user, target)) return;

            if (!target.equals(user)) {
                Vector3d velocity = boulder.getVelocity().normalize();
                boulders.remove(boulder.getUniqueId());
                boulder.remove();

                double damage = asDouble(user, target, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION));
                boolean dealtDamage = target.damage(damage, DamageUtils.directMagical(user));

                AtherysRPG.getInstance().getLogger().info("Damaged: {}", damage);

                if (dealtDamage) {
                    target.setVelocity(Vector3d.from(velocity.getX() * 1.5, 0.6, velocity.getZ() * 1.5));
                }
            }
        }
    }

    @Listener
    public void onWallCollide(CollideBlockEvent event, @Getter("getSource") FallingBlock boulder) {
        if (boulders.containsKey(boulder.getUniqueId())) {
            Living user = boulders.get(boulder.getUniqueId());
            applyAOEDamage(event.getTargetLocation(), user, user);
            boulders.remove(boulder.getUniqueId());
            boulder.remove();
        }
    }
}
