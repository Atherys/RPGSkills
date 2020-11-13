package com.atherys.rpgskills.t1;

import com.atherys.core.utils.Sound;
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
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.CollideEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Tuple;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class FireballSkill extends RPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "CLAMP(SOURCE_WIS * 1.5, 0.5, 10.0)";
    private static final String DEFAULT_OTHER_TEXT = "";

    private Map<UUID, Living> fireballs = new WeakHashMap<>();

    private static final ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.FLAME)
            .quantity(25)
            .offset(Vector3d.ONE)
            .build();

    private static final Sound blaze = Sound.builder(SoundTypes.ENTITY_BLAZE_SHOOT, 1)
            .build();

    private static final Sound extinguish = Sound.builder(SoundTypes.ENTITY_GENERIC_EXTINGUISH_FIRE, 1)
            .build();

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
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d spawnPosition = user.getLocation().getPosition().add(0, 1.5, 0);
        Snowball fireball = (Snowball) user.getWorld().createEntity(EntityTypes.SNOWBALL, spawnPosition);

        fireball.setShooter(user);
        fireball.offer(Keys.FIRE_TICKS, Integer.MAX_VALUE);
        Vector3d velocity = PhysicsUtils.getUnitDirection(user).mul(2.5);
        fireball.setVelocity(velocity);
        fireball.offer(Keys.ACCELERATION, velocity.mul(0.05));

        fireballs.put(fireball.getUniqueId(), user);


        user.getWorld().spawnEntity(fireball);
        Sound.playSound(blaze, user.getWorld(), spawnPosition);

        return CastResult.success();
    }

    @Listener
    public void onFireballCollide(CollideEntityEvent event, @Getter("getSource") Snowball fireball) {
        Living user = fireballs.get(fireball.getUniqueId());

        if (user != null && event.getEntities().get(0) instanceof Living) {
            Living target = (Living) event.getEntities().get(0);

            if (arePlayersInParty(user, target)) return;

            if (!target.equals(user)) {
                fireballs.remove(fireball.getUniqueId());
                double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION));
                target.damage(damage, DamageUtils.directMagical(user));
                target.getWorld().spawnParticles(particleEffect, target.getLocation().getPosition());
                Sound.playSound(extinguish, user.getWorld(), target.getLocation().getPosition());
            }
        }
    }
}
