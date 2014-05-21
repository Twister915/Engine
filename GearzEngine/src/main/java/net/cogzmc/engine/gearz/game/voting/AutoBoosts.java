/*
 * Copyright (c) 2014.
 * CogzMC LLC USA
 * All Right reserved
 *
 * This software is the confidential and proprietary information of Cogz Development, LLC.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Cogz LLC.
 */

package net.cogzmc.engine.gearz.game.voting;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

@RequiredArgsConstructor
@Data
public class AutoBoosts implements Listener {

    @NonNull
    private final Map<String, Integer> permissions;

    @EventHandler
    public void onVote(PlayerMapVoteEvent event) {
        Player player = event.getPlayer().getPlayer();
        for (Map.Entry<String, Integer> stringIntegerEntry : permissions.entrySet()) {
            if (player.hasPermission("gearz.voteboost." + stringIntegerEntry.getKey())) {
                Integer value = stringIntegerEntry.getValue();
                Integer numberOfVotes = event.getNumberOfVotes();
                event.setNumberOfVotes(numberOfVotes > value ? numberOfVotes : value);
            }
        }
    }
}
