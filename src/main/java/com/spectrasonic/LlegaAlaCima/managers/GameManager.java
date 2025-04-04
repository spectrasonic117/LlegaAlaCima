// File: managers/GameManager.java
package com.spectrasonic.LlegaAlaCima.managers;

import com.spectrasonic.LlegaAlaCima.Utils.ItemBuilder;
import com.spectrasonic.LlegaAlaCima.Utils.PointsManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public class GameManager {

    private final JavaPlugin plugin;
    private boolean running;
    private final PointsManager pointsManager;

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pointsManager = new PointsManager(plugin);
        this.running = false;
    }

    public void startGame() {
        running = true;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            // Dar el item de papel con customModelData 1086
            player.getInventory().addItem(
                    ItemBuilder.setMaterial("PAPER")
                            .setName("<gold><b>Impulsor</b>")
                            .setCustomModelData(1086)
                            .build());
        }
    }

    public void stopGame() {
        running = false;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    public void reloadPlugin() {
        plugin.reloadConfig();
    }
}
