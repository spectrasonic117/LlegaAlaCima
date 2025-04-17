package com.spectrasonic.LlegaAlaCima.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import lombok.RequiredArgsConstructor;
import com.spectrasonic.LlegaAlaCima.managers.GameManager;
import com.spectrasonic.LlegaAlaCima.Utils.MessageUtils;

@RequiredArgsConstructor
public class ScoringListener implements Listener {

    private final GameManager gameManager;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!gameManager.isRunning()) return;

        // Sólo cuando cruza un bloque de altura
        if (e.getFrom().getBlockY() == e.getTo().getBlockY()) return;

        if (gameManager.hasScored(e.getPlayer())) return;

        Block under = e.getPlayer()
            .getLocation()
            .subtract(0, 1, 0)
            .getBlock();
        if (under.getType() == Material.BLACK_WOOL) {
            gameManager.recordScore(e.getPlayer());
            MessageUtils.sendMessage(e.getPlayer(),
              "<gold>¡Has puntuado!</gold>");
        }
    }
}
