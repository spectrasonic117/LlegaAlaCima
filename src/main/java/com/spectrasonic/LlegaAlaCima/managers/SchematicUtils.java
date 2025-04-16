package com.spectrasonic.LlegaAlaCima.managers;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicUtils {

    public static void pasteSchematic(JavaPlugin plugin, String schematicName, Location location) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File schematicFile = new File("plugins/FastAsyncWorldEdit/schematics", schematicName + ".schem");
            if (!schematicFile.exists()) {
                plugin.getLogger().severe("No se encontró el schematic: " + schematicFile.getAbsolutePath());
                return;
            }

            Clipboard clipboard;
            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                plugin.getLogger().severe("Formato de schematic no soportado para el archivo: " + schematicFile.getName());
                return;
            }

            try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                clipboard = reader.read();
            } catch (IOException e) {
                plugin.getLogger().severe("Error al leer el schematic: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            World world = FaweAPI.getWorld(location.getWorld().getName());
            if (world == null) {
                plugin.getLogger().severe("No se pudo obtener el mundo: " + location.getWorld().getName());
                return;
            }

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                ClipboardHolder holder = new ClipboardHolder(clipboard);
                Operation operation = holder.createPaste(editSession)
                        .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
                        .ignoreAirBlocks(false)  // Cambiado a false para reemplazar bloques con aire también
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                plugin.getLogger().severe("Error al pegar el schematic: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    }
