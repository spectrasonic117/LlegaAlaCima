package com.spectrasonic.LlegaAlaCima.listeners;

import com.spectrasonic.LlegaAlaCima.managers.GameManager;
import com.spectrasonic.LlegaAlaCima.Utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class GameListener implements Listener {

    private final GameManager gameManager;
    private final JavaPlugin plugin;
    private double jumpPower;
    private double dashPower;

    public GameListener(GameManager gameManager, JavaPlugin plugin) {
        this.gameManager = gameManager;
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection jumpboost = config.getConfigurationSection("jumpboost");
        this.jumpPower = jumpboost.getDouble("jump");
        this.dashPower = jumpboost.getDouble("dash");
    }

    @EventHandler
    public void onRightClickWoolBlock(PlayerInteractEvent event) {
        if (!gameManager.isRunning()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        // Verificamos los 3 tipos de lana válidos
        Material clickedType = event.getClickedBlock().getType();
        if (clickedType != Material.WHITE_WOOL
            && clickedType != Material.YELLOW_WOOL
            && clickedType != Material.RED_WOOL) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand == null || mainHand.getType() != Material.PAPER) return;
        if (!mainHand.hasItemMeta() || !mainHand.getItemMeta().hasCustomModelData())
            return;
        if (mainHand.getItemMeta().getCustomModelData() != 1086) return;

        // Mecánica de empujón (igual que antes)
        Vector dir = player.getLocation().getDirection().normalize()
                        .multiply(dashPower);
        dir.setY(jumpPower);
        player.setVelocity(dir);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!gameManager.isRunning()) return;
        if (e.getFrom().getBlockY() == e.getTo().getBlockY()) return;
        Player player = e.getPlayer();
        if (gameManager.hasScored(player)) return;

        Block under = player.getLocation().subtract(0, 1, 0).getBlock();
        if (under.getType() != Material.BLACK_WOOL) return;

        gameManager.recordScore(player);
        gameManager.getPointsManager().addPoints(player, 10);

        MessageUtils.sendActionBar(player, "<green><b>+10 Puntos");
        MessageUtils.sendTitle(player,
            "<green><b>¡Has llegado!", "", 2, 40, 2);
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void updateJumpAndDashPower(double jumpPower, double dashPower) {
        this.jumpPower = jumpPower;
        this.dashPower = dashPower;
    }
}
