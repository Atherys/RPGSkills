package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Leap extends RPGSkill {
    private static final String HORIZONTAL = "horizontal";
    private static final String VERTICAL = "vertical";

    private static final String DEFAULT_HORIZONTAL = "1";
    private static final String DEFAULT_OTHER_TEXT = "";

    private static ParticleEffect particleEffect = ParticleEffect.builder()
            .type(ParticleTypes.CLOUD)
            .quantity(5)
            .build();

    private static Sound sound = Sound.builder(SoundTypes.ENTITY_BAT_TAKEOFF, 1)
            .build();

    public Leap() {
        super(
                SkillSpec.create()
                        .id("leap")
                        .name("Leap")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Jump forward in the direction you're facing. ", arg(OTHER_TEXT)
                        ))
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(OTHER_TEXT, otherText(this))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d direction = PhysicsUtils.getUnitDirection(user);
        double horizontal = asDouble(user, getProperty(HORIZONTAL, String.class, DEFAULT_HORIZONTAL));
        double vertical = asDouble(user, getProperty(VERTICAL, String.class, DEFAULT_HORIZONTAL));

        user.setVelocity(Vector3d.from(direction.getX() * horizontal, vertical, direction.getZ() * horizontal));
        user.offer(Keys.FALL_DISTANCE, 0f);

        Sound.playSound(sound, user.getWorld(), user.getLocation().getPosition());
        user.getWorld().spawnParticles(particleEffect, user.getLocation().getPosition());
        user.getWorld().spawnParticles(particleEffect, user.getLocation().getPosition().add(0.25, 0, 0.25));
        user.getWorld().spawnParticles(particleEffect, user.getLocation().getPosition().add(-0.25, 0, -0.25));
        user.getWorld().spawnParticles(particleEffect, user.getLocation().getPosition().add(-0.25, 0, 0.25));
        user.getWorld().spawnParticles(particleEffect, user.getLocation().getPosition().add(0.25, 0, -0.25));

        return CastResult.success();
    }
}
