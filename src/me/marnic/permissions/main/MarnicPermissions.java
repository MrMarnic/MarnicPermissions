package me.marnic.permissions.main;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import me.marnic.permissions.command.CommandHandler;
import me.marnic.permissions.command.CommandHelper;
import me.marnic.permissions.command.MCommand;
import me.marnic.permissions.handler.Message;
import me.marnic.permissions.handler.PermissionHandler;
import me.marnic.permissions.handler.UUIDNameBundle;
import me.marnic.permissions.logger.MarnicLogger;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Copyright (c) 02.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class MarnicPermissions extends JavaPlugin {

    public static CommandHandler commandHandler;
    public static PermissionHandler permissionHandler;
    public static CommandHelper commandHelper;

    @Override
    public void onEnable() {
        MarnicLogger.info("Plugin Starting...");
        loadConfig();
        permissionHandler = new PermissionHandler();
        permissionHandler.init(getConfig(),"mp",this);
        commandHelper = new CommandHelper();
        initCommands();
        getServer().getPluginManager().registerEvents(permissionHandler,this);
        MarnicLogger.info("Plugin started");
    }

    @Override
    public void onDisable() {
        MarnicLogger.info("Disabling Plugin...");

        MarnicLogger.info("Plugin disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        commandHandler.onCommand(sender,command.getName(),args);
        return super.onCommand(sender, command, label, args);
    }

    public void initCommands() {
        commandHandler = new CommandHandler(){
            @Override
            public void onCmdDoesNotExist(CommandSender sender, String name,String args) {
                MarnicLogger.warn("Command " + name + " with args [" + args + "] does not exist");
            }

            @Override
            public void onCmdPermissionError(Player player, String name) {
                MarnicLogger.warn("You do not have the needed permissions for " +name,player);
            }
        };
        commandHandler.addCmd(new MCommand("mp","","","mp") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                MarnicLogger.info("/mp help for help");
            }

            @Override
            public void onExecute(Player player, String... args) {
                MarnicLogger.info("/mp help for help",player);

            }
        });
        commandHandler.addCmd(new MCommand("mp","help","","mp.help") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                commandHelper.showDefaultHelp();
            }
            @Override
            public void onExecute(Player sender,String... args) {
                commandHelper.showDefaultHelp(sender);
            }
        });
        //RANK
        //RANK
        commandHandler.addCmd(new MCommand("mp","rank","","mp.rank.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                commandHelper.showRankHelp();
            }

            @Override
            public void onExecute(Player player, String... args) {
                commandHelper.showRankHelp(player);
            }
        });
        commandHandler.addCmd(new MCommand("mp","rank add p","","mp.rank.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                MarnicLogger.log(permissionHandler.addRank(args[2]));
            }

            @Override
            public void onExecute(Player player, String... args) {
                MarnicLogger.log(permissionHandler.addRank(args[2]),player);
            }
        });
        commandHandler.addCmd(new MCommand("mp","rank remove p","","mp.rank.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                MarnicLogger.log(permissionHandler.removeRank(args[2]));
            }

            @Override
            public void onExecute(Player player, String... args) {
                MarnicLogger.log(permissionHandler.removeRank(args[2]),player);
            }
        });
        commandHandler.addCmd(new MCommand("mp","rank p permissions add p","","mp.rank.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                MarnicLogger.log(permissionHandler.addPermissionToRank(args[1],args[4]));
            }

            @Override
            public void onExecute(Player player, String... args) {
                MarnicLogger.log(permissionHandler.addPermissionToRank(args[1],args[4]),player);
            }
        });
        commandHandler.addCmd(new MCommand("mp","rank p permissions remove p","","mp.rank.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                MarnicLogger.log(permissionHandler.removePermissionFromRank(args[1],args[4]));
            }

            @Override
            public void onExecute(Player player, String... args) {
                MarnicLogger.log(permissionHandler.removePermissionFromRank(args[1],args[4]),player);
            }
        });
        commandHandler.addCmd(new MCommand("mp","rank p permissions clear","","mp.rank.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                MarnicLogger.log(permissionHandler.clearPermissions(args[1]));
            }

            @Override
            public void onExecute(Player player, String... args) {
                MarnicLogger.log(permissionHandler.clearPermissions(args[1]),player);
            }
        });
        //USER
        //USER
        commandHandler.addCmd(new MCommand("mp","user","","mp.user.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                commandHelper.showUserHelp();
            }

            @Override
            public void onExecute(Player player, String... args) {
                commandHelper.showUserHelp(player);
            }
        });
        commandHandler.addCmd(new MCommand("mp","user p set p","","mp.user.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.setRankOfUser(player, args[3]));
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!");
                }
            }

            @Override
            public void onExecute(Player playe, String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.setRankOfUser(player, args[3]),playe);
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!",playe);
                }
            }
        });
        commandHandler.addCmd(new MCommand("mp","user p remove p","","mp.user.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.removeRankFromUser(player, args[3]));
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!");
                }
            }

            @Override
            public void onExecute(Player playe, String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.removeRankFromUser(player, args[3]),playe);
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!",playe);
                }
            }
        });
        commandHandler.addCmd(new MCommand("mp","user p add p","","mp.user.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.addRankToUser(player, args[3]));
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!");
                }
            }

            @Override
            public void onExecute(Player playe, String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.addRankToUser(player, args[3]),playe);
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!",playe);
                }
            }
        });
        commandHandler.addCmd(new MCommand("mp","user p clear","","mp.user.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.clearRanksFromUser(player));
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!");
                }
            }

            @Override
            public void onExecute(Player playe, String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.clearRanksFromUser(player),playe);
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!",playe);
                }
            }
        });
        commandHandler.addCmd(new MCommand("mp","user p list","","mp.user.*") {
            @Override
            public void onExecute(CommandSender sender,String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.listPermissions(player));
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!");
                }
            }

            @Override
            public void onExecute(Player playe, String... args) {
                UUIDNameBundle player = fromPlayerName(args[1]);
                if(player!=null) {
                    MarnicLogger.log(permissionHandler.listPermissions(player,playe),playe);
                }else{
                    MarnicLogger.warn("Player " + args[1] + " was not found!",playe);
                }
            }
        });

    }

    private UUIDNameBundle fromPlayerName(String name) {
        Player player = Bukkit.getPlayer(name);

        if(player!=null) {
            return new UUIDNameBundle(player);
        }else{
            OfflinePlayer player1 = Bukkit.getOfflinePlayer(name);
            if(player1!=null) {
                return new UUIDNameBundle(player1);
            }
        }

        return null;
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
