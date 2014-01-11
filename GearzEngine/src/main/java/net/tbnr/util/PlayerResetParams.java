package net.tbnr.util;

import lombok.Getter;
import lombok.Setter;
import net.tbnr.util.player.TPlayer;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Joey
 * Date: 10/7/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerResetParams {
    @SuppressWarnings("unused") @Getter @Setter
    private TPlayer player;
    @SuppressWarnings("unused") @Getter @Setter
    private List<ItemStack> doNotclear;
    @SuppressWarnings("unused") @Getter @Setter
    private boolean clearXP = true;
    @SuppressWarnings("unused") @Getter @Setter
    private boolean clearPotions = true;
    @SuppressWarnings("unused") @Getter @Setter
    private boolean restoreHealth = true;
    @SuppressWarnings("unused") @Getter @Setter
    private boolean restoreFood = true;
    @SuppressWarnings("unused") @Getter @Setter
    private boolean resetFlight = true;
    @SuppressWarnings("unused") @Getter @Setter
    private boolean movePlayerDown = true;
    @SuppressWarnings("unused") @Getter @Setter
    private boolean restoreSpeeds = true;
    @SuppressWarnings("unused") @Getter @Setter
    private boolean resetInventory = true;
    @SuppressWarnings("unused") @Getter @Setter
    private GameMode resetGamemode = GameMode.SURVIVAL;
}