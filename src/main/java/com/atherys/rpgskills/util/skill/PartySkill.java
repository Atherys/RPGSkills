package com.atherys.rpgskills.util.skill;

import com.atherys.party.AtherysParties;
import com.atherys.skills.api.exception.CastException;
import com.atherys.skills.api.skill.Castable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Provides some useful utilities for dealing with parties inside a skill.
 */
public interface PartySkill extends Castable {
    default boolean arePlayersInParty(Living first, Living second) {
        return (first instanceof Player && second instanceof Player) &&
                AtherysParties.getInstance().getPartyFacade().arePlayersInSameParty((Player) first, (Player) second) &&
                !AtherysParties.getInstance().getPartyFacade().getPlayerParty((Player) first).get().isPvp();
    }

    default CastException notInParty() {
        return new CastException(Text.of("Target is not in your party!"));
    }

    default CastException isInParty() {
        return new CastException(Text.of("Target is in your party!"));
    }

}
