package org.eu.pcraft.pepperminecart;

import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.plugin.java.JavaPlugin;
import org.eu.pcraft.bStats.Metrics;
import org.eu.pcraft.template.ConfigTemplate;

import java.util.*;

public final class PepperMinecart extends JavaPlugin {
    public static HashMap<Material, EntityType> changeMap = new HashMap<>();

    @Getter
    private static PepperMinecart instance;

    @Getter
    private final ConfigTemplate configTemplate = new ConfigTemplate();
  
    public static Configuration yaml;
  
    public static Set<UUID> redstoneMinecartSet=new HashSet<>();

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
        configTemplate.loadConfig();

        ////Commands////
        CommandAPI.onEnable();
        new CommandAPICommand("PepperMinecart")
                .withArguments(
                        new GreedyStringArgument("subCommand")
                                .includeSuggestions(
                                        ArgumentSuggestions.strings("reload")
                                )
                )
                .withPermission(CommandPermission.OP)
                .withAliases("pm", "minecart")
                .executes((sender, args) -> {
                    if(Objects.equals(args.get("subCommand"), "reload")){
                        sender.sendMessage("[PepperMinecart] 正在重新加载……");
                        this.reloadConfig();
                        configTemplate.loadConfig();
                        sender.sendMessage("[PepperMinecart] 完成！");
                    }
                })
                .register();
        Bukkit.getScheduler().runTaskTimer(this,()->{
            for(UUID uuid:redstoneMinecartSet){
                Entity minecart=Bukkit.getEntity(uuid);
                Block block=minecart.getWorld().getBlockAt(minecart.getLocation());
                for(BlockFace face:BlockFace.values()){
                    if(block.getRelative(face).getBlockData() instanceof AnaloguePowerable){
                        AnaloguePowerable x=(AnaloguePowerable) block.getRelative(face).getBlockData();
                        x.setPower(14);
                    }
                }
            }

        },0,0);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

}
