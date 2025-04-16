package com.spectrasonic.LlegaAlaCima;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.LlegaAlaCima.commands.GameCommand;
import com.spectrasonic.LlegaAlaCima.listeners.GameListener;
import com.spectrasonic.LlegaAlaCima.managers.GameManager;
import com.spectrasonic.LlegaAlaCima.managers.SchematicSequenceManager;
import com.spectrasonic.LlegaAlaCima.Utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private GameManager gameManager;
    private SchematicSequenceManager schematicSequenceManager;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        gameManager = new GameManager(this);
        schematicSequenceManager = new SchematicSequenceManager(this, getConfig());

        registerCommands();
        registerEvents();

        MessageUtils.sendStartupMessage(this);
    }

    @Override
    public void onDisable() {
        if (gameManager.isRunning()) {
            gameManager.stopGame();
            schematicSequenceManager.stopSequence();
        }
        MessageUtils.sendShutdownMessage(this);
    }

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new GameCommand(this, gameManager, schematicSequenceManager));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new GameListener(gameManager, this), this);
    }
}
