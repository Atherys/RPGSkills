package com.atherys.rpgskills.t3;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.DAMAGE;
import static com.atherys.rpgskills.util.CommonProperties.RADIUS;
import static com.atherys.rpgskills.util.CommonProperties.TIME;
import static org.spongepowered.api.text.TextTemplate.arg;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.CollideEvent.Impact;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import com.atherys.rpg.api.skill.DescriptionArguments;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.skill.RadiusSkill;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;

public class ExplosiveShot extends RPGSkill implements RadiusSkill {
	public static final String EXPLOSIVE_SHOT_EFFECT = "explosive-shot-user-effect";

	private Map<UUID, Living> arrows = new HashMap<UUID, Living>();

	public ExplosiveShot() {
        super(SkillSpec.create()
                .id("explosive-shot")
                .name("Explosive Shot")
                .cooldown("0")
                .resourceCost("0")
                .descriptionTemplate(DescriptionUtils.buildTemplate("Your next arrow shot within ", arg(TIME),
                        " seconds will explode on impact, dealing ", arg(DAMAGE),
                        " physical damage to all enemies within ", arg(RADIUS), " blocks.")));

		setDescriptionArguments(Tuple.of(RADIUS, ofProperty(this, RADIUS, "3")),
				Tuple.of(TIME, DescriptionArguments.ofTimeProperty(this, TIME, "10000")),
				Tuple.of(DAMAGE, ofProperty(this, DAMAGE, "100")));
	}

	@Override
	public CastResult cast(Living user, long timestamp, String... args) throws CastException {
		AtherysSkills.getInstance().getEffectService().applyEffect(user, EXPLOSIVE_SHOT_EFFECT);
		return CastResult.success();
	}

	@Listener
	public void onShoot(SpawnEntityEvent event, @First Living living) {
		if (!AtherysSkills.getInstance().getEffectService().hasEffect(living, EXPLOSIVE_SHOT_EFFECT))
			return;

        Optional<Projectile> originalArrow = event.getEntities()
                .stream()
                .filter(entity -> entity instanceof Projectile)
                .filter(entity -> entity.getType() == EntityTypes.TIPPED_ARROW)
                .map(entity -> (Projectile) entity)
                .findFirst();

		if (originalArrow.isPresent()) {
			arrows.put(originalArrow.get().getUniqueId(), living);
		}
	}

	@Listener
	public void onImpact(Impact event, @Getter("getImpactPoint") Location<World> location,
			@Getter("getSource") Arrow arrow) {

		Living living;
		if ((living = arrows.remove(arrow.getUniqueId())) != null) {

			int radius = asInt(living, getProperty(RADIUS, String.class, "3"));
			double damage = asDouble(living, getProperty(DAMAGE, String.class, "100"));

            Explosion explosion = Explosion.builder()
                    .location(location)
                    .shouldDamageEntities(false)
                    .shouldBreakBlocks(false)
                    .canCauseFire(false)
                    .radius(radius)
                    .build();

			arrow.getWorld().triggerExplosion(explosion);

			applyToRadius(location, radius, e -> e.damage(damage, DamageUtils.directPhysical(e)));
		}
	}

}
