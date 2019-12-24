package com.atherys.rpgskills.util;

import com.atherys.party.AtherysParties;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;

public interface PartySkill {
    default boolean arePlayersInParty(Living first, Living second) {
        return (first instanceof Player && second instanceof Player) &&
                AtherysParties.getInstance().getPartyFacade().arePlayersInSameParty((Player) first, (Player) second) &&
                !AtherysParties.getInstance().getPartyFacade().getPlayerParty((Player) first).get().isPvp();
    }
}
