package com.spectrasonic.LlegaAlaCima.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.spectrasonic.LlegaAlaCima.managers.GameManager;
import com.spectrasonic.LlegaAlaCima.Utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
@CommandAlias("llegaalacima|cima")
@CommandPermission("llegaalacima.admin")
public class GameCommand extends BaseCommand {

    private final JavaPlugin plugin;
    private final GameManager gameManager;

    @Subcommand("game start")
    @CommandCompletion("1|2|3")
    public void onGameStart(CommandSender sender, int round) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender,
                "<red>Este comando solo puede ser usado por jugadores.");
            return;
        }
        if (gameManager.isRunning()) {
            MessageUtils.sendMessage(sender,
                "<red>El minijuego ya está iniciado.");
            return;
        }

        ((Player) sender).performCommand("id false");

        gameManager.startGame(round);
        MessageUtils.sendMessage(sender,
            "<green>Minijuego iniciado en la ronda " + round + ".");
    }

    @Subcommand("game stop")
    public void onGameStop(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender,
                "<red>Este comando solo puede ser usado por jugadores.");
            return;
        }
        if (!gameManager.isRunning()) {
            MessageUtils.sendMessage(sender,
                "<red>El minijuego no está iniciado.");
            return;
        }

        // Ejecutar "id true" al detener
        ((Player) sender).performCommand("id true");

        gameManager.stopGame();
        MessageUtils.sendMessage(sender,
            "<red>Minijuego detenido.");
    }

    @Subcommand("reload")
    public void onReload(CommandSender sender) {
        gameManager.reloadPlugin();
        MessageUtils.sendMessage(sender,
            "<green>Configuración recargada correctamente.");
    }

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage(
            "Uso:\n" +
            "/cima game start <ronda>  — Iniciar juego (ronda 1,2,3...)\n" +
            "/cima game stop            — Detener juego\n" +
            "/cima reload               — Recargar configuración"
        );
    }
}
