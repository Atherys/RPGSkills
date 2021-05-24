package com.atherys.rpgskills.t2;

import com.atherys.core.utils.Sound;
import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.Effects;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.effect.Applyable;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Tuple;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpg.api.skill.DescriptionArguments.ofSource;
import static com.atherys.rpgskills.util.CommonProperties.*;
import static com.atherys.rpgskills.util.DescriptionUtils.otherText;
import static org.spongepowered.api.text.TextTemplate.arg;
import static org.spongepowered.api.text.format.TextColors.GOLD;

public class IceBolt extends RPGSkill implements PartySkill {
    private static final String DEFAULT_DAMAGE_EXPRESSION = "10";
    private static final String DEFAULT_SLOW_AMPLIFIER = "0";
    private static final String DEFAULT_SLOW_DURATION = "10000";

    private final Map<UUID, UUID> projectileToItem = new WeakHashMap<>();
    private final Map<UUID, Living> iceBolts = new WeakHashMap<>();

    private static final Sound glass = Sound.builder(SoundTypes.BLOCK_GLASS_BREAK, 1)
            .pitch(0.8)
            .build();

    private static final Sound orb = Sound.builder(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, 1)
            .pitch(1.5)
            .build();

    public IceBolt() {
        super(
                SkillSpec.create()
                        .id("ice-bolt")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Launch a bolt of ice in the direction you are looking.",
                                " It deals ", arg(DAMAGE), " magical damage to target and slows them by ",
                                arg(AMPLIFIER), GOLD, "%", " for 3 seconds."
                        ))
                        .name("Ice Bolt")
                        .cooldown("0")
                        .resourceCost("0")
        );

        setDescriptionArguments(
                Tuple.of(DAMAGE, ofProperty(this, DAMAGE, DEFAULT_DAMAGE_EXPRESSION)),
                Tuple.of(AMPLIFIER, ofSource("(1 +" + getProperty(AMPLIFIER, String.class, DEFAULT_SLOW_AMPLIFIER) + ")*15"))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Vector3d spawnPosition = user.getLocation().getPosition().add(0, 1.25, 0);

        ItemStack ice = ItemStack.builder().itemType(ItemTypes.PACKED_ICE).quantity(1).build();
        Item bolt = (Item) user.getWorld().createEntity(EntityTypes.ITEM, spawnPosition);
        bolt.offer(Keys.REPRESENTED_ITEM, ice.createSnapshot());
        bolt.offer(Keys.DESPAWN_DELAY, 10);
        bolt.offer(Keys.INFINITE_PICKUP_DELAY, true);

        Vector3d velocity = PhysicsUtils.getUnitDirection(user).mul(2, 2, 2);
        user.launchProjectile(Snowball.class, velocity).ifPresent(snowball -> {
            snowball.offer(Keys.VANISH, true);
            snowball.offer(Keys.INVISIBLE, true);
            bolt.setVelocity(velocity);
            user.getWorld().spawnEntity(bolt);
            iceBolts.put(snowball.getUniqueId(), user);
            projectileToItem.put(snowball.getUniqueId(), bolt.getUniqueId());
        });

        return CastResult.success();
    }

    @Listener
    public void collide(CollideEntityEvent event, @Getter("getSource") Snowball iceBolt) {

        UUID bolt = projectileToItem.get(iceBolt.getUniqueId());
        Living user = iceBolts.get(iceBolt.getUniqueId());

        if (user != null && event.getEntities().get(0) instanceof Living) {
            Living target = (Living) event.getEntities().get(0);

            if (arePlayersInParty(user, target)) return;

            if (!target.equals(user)) {
                iceBolts.remove(iceBolt.getUniqueId());
                projectileToItem.remove(iceBolt.getUniqueId());
                iceBolt.getWorld().getEntity(bolt).ifPresent(Entity::remove);
                iceBolt.remove();

                double damage = asDouble(user, getProperty(DAMAGE, String.class, DEFAULT_DAMAGE_EXPRESSION));
                boolean dealtDamage = target.damage(damage, DamageUtils.directMagical(user));

                if (dealtDamage) {
                    int duration = asInt(user, target, getProperty(TIME, String.class, DEFAULT_SLOW_DURATION));
                    int modifier = asInt(user, getProperty(AMPLIFIER, String.class, DEFAULT_SLOW_AMPLIFIER));
                    Applyable slowness = Effects.ofSlowness(getId(), getName(), duration, modifier);

                    AtherysSkills.getInstance().getEffectService().applyEffect(target, slowness);
                }

                Vector3d position = target.getLocation().getPosition();

                Sound.playSound(glass, target.getWorld(), position);
                Sound.playSound(orb, target.getWorld(), position);
            }
        }
    }

    @Listener
    public void onWallCollide(CollideBlockEvent event, @Getter("getSource") Item iceBolt) {
        Iterator<Map.Entry<UUID, UUID>> iter = projectileToItem.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, UUID> entry = iter.next();
            if (entry.getValue().equals(iceBolt.getUniqueId())) {
                iceBolts.remove(entry.getKey());
                iceBolt.getWorld().getEntity(entry.getKey()).ifPresent(Entity::remove);
                iceBolt.remove();
                iter.remove();
                return;
            }
        }
    }
}
