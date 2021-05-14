package com.atherys.rpgskills.t1;

import com.atherys.rpg.api.skill.SkillSpec;
import com.atherys.rpg.api.skill.TargetedRPGSkill;
import com.atherys.rpgskills.util.DamageUtils;
import com.atherys.rpgskills.util.DescriptionUtils;
import com.atherys.rpgskills.util.PhysicsUtils;
import com.atherys.rpgskills.util.skill.PartySkill;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.CastResult;
import com.atherys.skills.api.util.LivingUtils;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Tuple;

import static com.atherys.rpg.api.skill.DescriptionArguments.ofProperty;
import static com.atherys.rpgskills.util.CommonProperties.HEALING;
import static org.spongepowered.api.text.TextTemplate.arg;

public class Invocation extends TargetedRPGSkill implements PartySkill {

    private static ParticleEffect ally = ParticleEffect.builder()
            .type(ParticleTypes.HAPPY_VILLAGER)
            .quantity(2)
            .build();

    private static ParticleEffect enemy = ParticleEffect.builder()
            .type(ParticleTypes.WITCH_SPELL)
            .quantity(2)
            .build();

    public Invocation() {
        super(
                SkillSpec.create()
                        .id("invocation")
                        .name("Invocation")
                        .cooldown("0")
                        .resourceCost("0")
                        .descriptionTemplate(DescriptionUtils.buildTemplate(
                                "Heal target for 35+(0.4*WIS). If they are an enemy, deal that much damage instead."
                        ))
        );

        setDescriptionArguments(
                Tuple.of(HEALING, ofProperty(this, HEALING, "50"))
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        double amount = asDouble(user, target, getProperty(HEALING, String.class, "50"));

        if (arePlayersInParty(user, target)) {
            LivingUtils.healLiving(target, amount);
            PhysicsUtils.spawnParticleCloud(ally, target.getLocation());
        } else {
            target.damage(amount, DamageUtils.directMagical(user));
            PhysicsUtils.spawnParticleCloud(enemy, target.getLocation().add(0, -1, 0));
        }
        return CastResult.success();
    }
}
