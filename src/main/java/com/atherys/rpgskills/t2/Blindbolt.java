package com.atherys.rpgskills.t2;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
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
import org.spongepowered.api.entity.projectile.EnderPearl;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Tuple;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Blindbolt extends RPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_TIME = "5000";
    private static final String BLINDBOLT_EFFECT = "blindbolt-effect";
    private static final String DEFAULT_OTHER_TEXT = "";

    private static final ParticleEffect trail = ParticleEffect.builder()
            .type(ParticleTypes.REDSTONE_DUST)
            .option(ParticleOptions.COLOR, Color.BLACK)
            .build();

    private final Map<UUID, Living> blindBolts = new WeakHashMap<>();

    public Blindbolt() {
        super(
                SkillSpec.create()
                        .id("blindbolt")
                        .name("Blindbolt")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Strike your target with a bolt of energy, dealing ",
                                arg(DAMAGE), " magical damage and blinding them for ", arg(TIME), ". ",
                                arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(TIME, DescriptionArguments.timeProperty(this, TIME, DEFAULT_TIME)),
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE)),
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d spawnPosition = user.getLocation().getPosition().add(0, 1.5, 0);
        EnderPearl bolt = (EnderPearl) user.getWorld().createEntity(EntityTypes.ENDER_PEARL, spawnPosition);

        bolt.setShooter(user);
        Vector3d velocity = PhysicsUtils.getUnitDirection(user).mul(3);
        bolt.setVelocity(velocity);
        bolt.offer(Keys.ACCELERATION, velocity.mul(0.05));

        user.getWorld().spawnEntity(bolt);
        blindBolts.put(bolt.getUniqueId(), user);

        return CastResult.success();
    }

    @Listener
    public void onEnderPearlTeleport(MoveEntityEvent.Teleport event, @First EnderPearl bolt) {
        if (blindBolts.containsKey(bolt.getUniqueId())) {
            event.setCancelled(true);
            blindBolts.remove(bolt.getUniqueId());
        }
    }

    @Listener
    public void onBlindBoltCollide(CollideEntityEvent event, @Getter("getSource") EnderPearl bolt) {
        Living user = blindBolts.get(bolt.getUniqueId());

        if (user != null && event.getEntities().get(0) instanceof Player) {
            Player target = (Player) event.getEntities().get(0);

            if (arePlayersInParty(user, target)) return;

            if (!target.equals(user)) {
                double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
                target.damage(damage, DamageUtils.directMagical(user));

                PhysicsUtils.playSoundForLiving(target, SoundTypes.BLOCK_END_PORTAL_SPAWN, 1);
                int time = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));
                AtherysSkills.getInstance().getEffectService().applyEffect(
                        target,
                        Effects.ofBlindness(BLINDBOLT_EFFECT, "Blindbolt", time)
                );
            }
        }
    }
}
