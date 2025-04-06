package com.spectrasonic.LlegaAlaCima.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.CommandCompletion;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.spectrasonic.LlegaAlaCima.managers.GameManager;
import com.spectrasonic.LlegaAlaCima.Utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
@CommandAlias("llegaalacima|cima")
public class GameCommand extends BaseCommand {

    private final JavaPlugin plugin;
    private final GameManager gameManager;

    @Subcommand("game")
    @CommandCompletion("start|stop")

    public void onGame(CommandSender sender, String action) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "<red>Este comando solo puede ser usado por jugadores.");
            return;
        }
        switch (action.toLowerCase()) {
            case "start":
                if (gameManager.isRunning()) {
                    MessageUtils.sendMessage(sender, "<red>El minijuego ya está iniciado.");
                } else {
                    gameManager.startGame();
                    MessageUtils.sendMessage(sender, "Minijuego iniciado.");
                }
                break;
            case "stop":
                if (!gameManager.isRunning()) {
                    MessageUtils.sendMessage(sender, "<red>El minijuego no está iniciado.");
                } else {
                    gameManager.stopGame();
                    MessageUtils.sendMessage(sender, "Minijuego detenido.");
                }
                break;
            default:
                MessageUtils.sendMessage(sender, "<yellow>Uso: /llegaalacima game <start|stop>");
        }
    }

    @Subcommand("reload")
    public void onReload(CommandSender sender) {
        gameManager.reloadPlugin();
        MessageUtils.sendMessage(sender, "<green>Configuración recargada correctamente.");
    }

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage("Uso: /llegaalacima game <start|stop> | /llegaalacima reload");
    }
}
