package me.marnic.permissions.command;

import me.marnic.permissions.logger.MarnicLogger;
import me.marnic.permissions.main.MarnicPermissions;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 03.02.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class CommandHelper {
    public void showDefaultHelp() {
        MarnicLogger.info("HELP:");
        for(MCommand command:MarnicPermissions.commandHandler.getCommands()) {
            MarnicLogger.info(command.getName() + " [" + fromArgs(command.getArgs()) + "] description: [" + command.getDescription()+"]");
        }
    }

    public void showDefaultHelp(Player player) {
        MarnicLogger.info("HELP:",player);
        for(MCommand command:MarnicPermissions.commandHandler.getCommands()) {
            MarnicLogger.info(command.getName() + " [" + fromArgs(command.getArgs()) + "] description: [" + command.getDescription()+"]",player);
        }
    }

    private String fromArgs(String[] args) {

        if(args.length==0)
            return "";

        String a = "";

        for(String s:args) {
            a=a+" "+s;
        }

        return a.substring(1);
    }

    public void showRankHelp() {
        MarnicLogger.info("Use /mp rank add [Name] To create a rank");
        MarnicLogger.info("Use /mp rank remove [Name] To remove a rank");
        MarnicLogger.info("Use /mp rank [Name] permissions add [Name] To add permissions to a rank");
        MarnicLogger.info("Use /mp rank [Name] permissions add [Name] To remove permissions from a rank");
    }

    public void showUserHelp() {
        MarnicLogger.info("Use /mp user [Name] set [Rank] To set the only rank of a user");
        MarnicLogger.info("Use /mp user [Name] remove [Rank] To remove a rank from a user");
        MarnicLogger.info("Use /mp user [Name] add [Rank] To add a rank to a user");
        MarnicLogger.info("Use /mp user [Name] clear To remove all ranks from a user");
    }

    public void showRankHelp(Player player) {
        MarnicLogger.info("Use /mp rank add [Name] To create a rank",player);
        MarnicLogger.info("Use /mp rank remove [Name] To remove a rank",player);
        MarnicLogger.info("Use /mp rank [Name] permissions add [Name] To add permissions to a rank",player);
        MarnicLogger.info("Use /mp rank [Name] permissions add [Name] To remove permissions from a rank",player);
    }

    public void showUserHelp(Player player) {
        MarnicLogger.info("Use /mp user [Name] set [Rank] To set the only rank of a user",player);
        MarnicLogger.info("Use /mp user [Name] remove [Rank] To remove a rank from a user",player);
        MarnicLogger.info("Use /mp user [Name] add [Rank] To add a rank to a user",player);
        MarnicLogger.info("Use /mp user [Name] clear To remove all ranks from a user",player);
    }
}
