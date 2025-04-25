package com.spectrasonic.LlegaAlaCima.listeners;

import com.spectrasonic.LlegaAlaCima.managers.GameManager;
import com.spectrasonic.LlegaAlaCima.Utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class ScoringListener implements Listener {

    private final GameManager gameManager;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!gameManager.isRunning()) return;

        // Verificamos si el jugador ya ha puntuado
        if (gameManager.hasScored(e.getPlayer())) return;

        // Obtenemos el bloque sobre el que está parado el jugador
        Block standingOn = e.getPlayer().getLocation().getBlock();

        // Si está parado sobre la lana negra,registramos la puntuación
        if (standingOn.getType() == Material.BLACK_WOOL) {
            gameManager.recordScore(e.getPlayer());
            MessageUtils.sendMessage(e.getPlayer(),
              "<gold>¡Has Llegado!</gold>");
        }
    }
}
