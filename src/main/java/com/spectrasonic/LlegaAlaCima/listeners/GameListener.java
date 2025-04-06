package com.spectrasonic.LlegaAlaCima.listeners;

import com.spectrasonic.LlegaAlaCima.Utils.MessageUtils;
import com.spectrasonic.LlegaAlaCima.managers.GameManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


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
    public void onRightClickAmethystBlock(PlayerInteractEvent event) {
        if (!gameManager.isRunning())
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.AMETHYST_BLOCK)
            return;

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand == null || mainHand.getType() != Material.PAPER)
            return;
        if (!mainHand.hasItemMeta() || !mainHand.getItemMeta().hasCustomModelData())
            return;
        if (mainHand.getItemMeta().getCustomModelData() != 1086)
            return;

        // Lanzar al jugador hacia arriba y ligeramente adelante.
        Vector direction = player.getLocation().getDirection().normalize().multiply(dashPower);
        direction.setY(jumpPower);
        player.setVelocity(direction);
    }

    @EventHandler
    public void onRightClickBell(PlayerInteractEvent event) {
        if (!gameManager.isRunning())
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.BELL)
            return;

        Player player = event.getPlayer();
        // Mostrar ActionBar y Título.
        MessageUtils.sendActionBar(player, "<green><b>+1 Punto");
        MessageUtils.sendTitle(player, "<green><b>Has llegado", "", 2, 40, 2);
        // Establecer modo espectador.
        player.setGameMode(GameMode.SPECTATOR);
        // Dar punto usando PointsManager.
        gameManager.getPointsManager().addPoints(player, 1);
    }

    public void updateJumpAndDashPower(double jumpPower, double dashPower) {
        this.jumpPower = jumpPower;
        this.dashPower = dashPower;
    }
}
