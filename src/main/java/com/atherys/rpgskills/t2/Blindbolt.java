package com.atherys.rpgskills.t2;

import com.atherys.core.utils.EntityUtils;
import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.data.DamageExpressionData;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.CommonProperties.OTHER_TEXT;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Blindbolt extends RPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE = "5.0";
    private static final String DEFAULT_TIME = "5000";
    private static final String BLINDBOLT_EFFECT = "blindbolt-effect";

    private static final String DEFAULT_OTHER_TEXT = "";

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
        Arrow bolt = (Arrow) user.getWorld().createEntity(EntityTypes.TIPPED_ARROW, spawnPosition);

        bolt.setShooter(user);
        bolt.offer(new DamageExpressionData(getProperty(DAMAGE, String.class, DEFAULT_DAMAGE)));
        Vector3d velocity = PhysicsUtils.getUnitDirection(user).mul(3);
        bolt.setVelocity(velocity);
        bolt.offer(Keys.ACCELERATION, velocity.mul(0.05));
        bolt.offer(Keys.INVISIBLE, true);

        user.getWorld().spawnEntity(bolt);

        /*
        if (arePlayersInParty(user, target)) throw isInParty();

        double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE));
        target.damage(damage, DamageUtils.directMagical(user));
        int time = asInt(user, getProperty(TIME, String.class, DEFAULT_TIME));

        AtherysSkills.getInstance().getEffectService().applyEffect(
                target,
                Effects.ofBlindness(BLINDBOLT_EFFECT, "Blindbolt", time)
        );
        */
        return CastResult.success();
    }
}
