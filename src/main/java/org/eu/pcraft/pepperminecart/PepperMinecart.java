package org.eu.pcraft.pepperminecart;

import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.eu.pcraft.bStats.Metrics;
import org.eu.pcraft.template.ConfigTemplate;

import java.util.HashMap;
import java.util.Objects;

public final class PepperMinecart extends JavaPlugin {
    public static HashMap<Material, EntityType> changeMap = new HashMap<>();
    public static JavaPlugin instance;
    public static Configuration yaml;
    public static ConfigTemplate config = new ConfigTemplate();

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));
    }

    @Override
    public void onEnable() {
        ////bStats////
        int pluginId = 21763;
        Metrics metrics = new Metrics(this, pluginId);
        //metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));

        ////init////
        Bukkit.getPluginManager().registerEvents(new listener(), this);
        instance = this;
        changeMap.put(Material.HOPPER, EntityType.MINECART_HOPPER);
        changeMap.put(Material.CHEST, EntityType.MINECART_CHEST);
        changeMap.put(Material.TNT, EntityType.MINECART_TNT);
        changeMap.put(Material.COMMAND_BLOCK, EntityType.MINECART_COMMAND);
        changeMap.put(Material.FURNACE, EntityType.MINECART_FURNACE);

        ////config////
        saveDefaultConfig();
        yaml = this.getConfig();
        config.loadConfig();

        ////Commands////
        CommandAPI.onEnable();
        new CommandAPICommand("PepperMinecart")
                .withArguments(new GreedyStringArgument("subCommand"))
                .withPermission(CommandPermission.OP)
                .withAliases("pm", "minecart")
                .executes((sender, args) -> {
                    if(Objects.equals(args.get("subCommand"), "reload")){
                        Bukkit.getLogger().info("正在重新加载……");
                        this.reloadConfig();
                        yaml=this.getConfig();
                        config.loadConfig();
                        Bukkit.getLogger().info("完成！");
                    }
                })
                .register();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}