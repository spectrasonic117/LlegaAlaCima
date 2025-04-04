// File: managers/GameManager.java
package com.spectrasonic.LlegaAlaCima.managers;

import com.spectrasonic.LlegaAlaCima.Utils.ItemBuilder;
import com.spectrasonic.LlegaAlaCima.listeners.GameListener;
import com.spectrasonic.LlegaAlaCima.Utils.PointsManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

@Getter
@Setter
public class GameManager {

    private final JavaPlugin plugin;
    private GameListener gameListener;
    private boolean running;
    private final PointsManager pointsManager;

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pointsManager = new PointsManager(plugin);
        this.gameListener = new GameListener(this, plugin);
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
        // Actualizar las variables dependientes de la configuración
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection jumpboost = config.getConfigurationSection("jumpboost");
        double jumpPower = jumpboost.getDouble("jump");
        double dashPower = jumpboost.getDouble("dash");

        // Suponiendo que tienes una referencia a GameListener
        gameListener.updateJumpAndDashPower(jumpPower, dashPower);
    }
}
