package com.spectrasonic.LlegaAlaCima.managers;

import com.spectrasonic.LlegaAlaCima.Utils.ItemBuilder;
import com.spectrasonic.LlegaAlaCima.listeners.GameListener;
import com.spectrasonic.LlegaAlaCima.Utils.PointsManager;
import com.spectrasonic.LlegaAlaCima.Utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class GameManager {

    private final JavaPlugin plugin;
    private GameListener gameListener;
    private boolean running;
    private int currentRound;
    private final PointsManager pointsManager;
    private final Set<UUID> scoredPlayers = new HashSet<>();

    public GameManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pointsManager = new PointsManager(plugin);
        this.gameListener = new GameListener(this, plugin);
        this.running = false;
    }

    public void startGame(int round) {
        running = true;
        this.currentRound = round;
        this.scoredPlayers.clear();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                int main_slot = player.getInventory().getHeldItemSlot();
                player.getInventory().setItem(main_slot, createImpulsorItem());
            }
        }
        pasteSchematicForRound(round);
    }

    public void stopGame() {
        this.running = false;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SPECTATOR) {
                player.getInventory().clear();
                player.setGameMode(GameMode.ADVENTURE);
            }
        }
        pasteEmptySchematic();
    }

    public void reloadPlugin() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection jumpboost = config.getConfigurationSection("jumpboost");
        if (jumpboost != null) {
            double jumpPower = jumpboost.getDouble("jump");
            double dashPower = jumpboost.getDouble("dash");
            gameListener.updateJumpAndDashPower(jumpPower, dashPower);
            MessageUtils.sendConsoleMessage("Config Realoaded");
        } else {
            MessageUtils.sendConsoleMessage("Error al recargar la configuración");
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public boolean hasScored(Player p) {
        return scoredPlayers.contains(p.getUniqueId());
    }

    public void recordScore(Player p) {
        scoredPlayers.add(p.getUniqueId());
    }

    private ItemStack createImpulsorItem() {
        return ItemBuilder.setMaterial("PAPER")
                .setName("<gold><b>Resorte Impulsor</b>")
                .setLore("<gray>Algunas lanas",
                        "<gray>pueden impulsarte")
                .setCustomModelData(1125)
                .build();
    }

    private void pasteSchematicForRound(int round) {
        FileConfiguration config = plugin.getConfig();
        String schematicName = config.getStringList("schematic_rounds").get(round - 1);
        SchematicUtils.pasteSchematic(plugin, schematicName, getPastePivot());
    }

    private void pasteEmptySchematic() {
        SchematicUtils.pasteSchematic(plugin, "cima_empty", getPastePivot());
    }

    private Location getPastePivot() {
        FileConfiguration config = plugin.getConfig();
        return new Location(
                plugin.getServer().getWorlds().get(0),
                config.getDouble("paste_pivot.x"),
                config.getDouble("paste_pivot.y"),
                config.getDouble("paste_pivot.z"));
    }
}
