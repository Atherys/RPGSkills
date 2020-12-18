package com.atherys.rpgskills.t2;

import com.atherys.rpg.AtherysRPG;
import com.atherys.rpg.api.skill.RPGSkill;
import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.data.DamageExpressionData;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.skills.AtherysSkills;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.PickupRules;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.util.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.AMPLIFIER;
import static org.spongepowered.api.text.TextTemplate.arg;

public class SplitShot extends RPGSkill {
    public static final String SPLITSHOT_EFFECT = "split-shot";
    public static final String DEFAULT_AMPLIFIER = "0.90";

    private static final List<Double> angles = Arrays.asList(0.17, -0.17, 0.34, -0.34);

    public SplitShot() {
        super(
                SkillSpec.create()
                        .id("split-shot")
                        .name("Split Shot")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Your next arrow shot will fire additional arrows in a spread, each dealing ",
                                arg(AMPLIFIER), "% damage of the original arrow. Uses only one arrow from your inventory."))
        );

        setDescriptionArguments(
            Tuple.of(AMPLIFIER, ofProperty(this, AMPLIFIER, DEFAULT_AMPLIFIER))
        );
    }

    @Override
    public CastResult cast(Living user, long timestamp, String... args) throws CastException {
        AtherysSkills.getInstance().getEffectService().applyEffect(user, SPLITSHOT_EFFECT);
        return CastResult.success();
    }

    @Listener
    public void onShoot(SpawnEntityEvent event, @First Living living) {
        if (!AtherysSkills.getInstance().getEffectService().hasEffect(living, SPLITSHOT_EFFECT))
            return;

        Optional<Projectile> originalArrow = event.getEntities().stream()
                .filter(entity -> entity instanceof Projectile)
                .filter(entity -> entity.getType() == EntityTypes.TIPPED_ARROW)
                .map(entity -> (Projectile) entity).findFirst();

        if (originalArrow.isPresent()) {
            AtherysSkills.getInstance().getEffectService().removeEffect(living, SPLITSHOT_EFFECT);
            Vector3d velocity = originalArrow.get().getVelocity();

            // Workout the damage expression for the additional arrows
            String arrowDmg = AtherysRPG
                    .getInstance().getDamageService()
                    .getRangedDamageExpression(originalArrow.get().getType());
            String dmgExpression = "(" + arrowDmg + ")*" + getProperty(AMPLIFIER, String.class, DEFAULT_AMPLIFIER);

            angles.forEach(a -> {
                double x = velocity.getX();
                double z = velocity.getZ();
                Vector3d newVelocity = Vector3d.from((Math.cos(a) * x + Math.sin(a) * z), velocity.getY(), Math.sin(a) * x + Math.cos(a) * z);

                living.launchProjectile(Arrow.class, newVelocity).ifPresent(arrow -> {
                    arrow.offer(Keys.PICKUP_RULE, PickupRules.DISALLOWED);
                    arrow.offer(new DamageExpressionData(dmgExpression));
                });
            });
        }
    }
}
