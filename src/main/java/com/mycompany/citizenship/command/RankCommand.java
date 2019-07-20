/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mycompany.citizenship.Citizenship;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.citizenship.config.Config;
import static com.mycompany.citizenship.RanksControl.Demotion;
import static com.mycompany.citizenship.RanksControl.Promotion;
import static com.mycompany.citizenship.RanksControl.setGroup;
import static com.mycompany.citizenship.PlayerControl.getAccess;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class RankCommand implements CommandExecutor {

    private final Citizenship instance;

    public RankCommand( Citizenship instance ) {
        this.instance = instance;
    }

    /**
     * コマンド入力があった場合に発生するイベント
     *
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param args
     * @return
     */
    @Override
    public boolean onCommand( CommandSender sender,Command cmd, String commandLabel, String[] args ) {
        Player player = ( sender instanceof Player ) ? ( Player ) sender:( Player ) null;

        String CtlCmd = "";
        String CmdArg = ( player == null ? "":player.getName() );
        Player lookPlayer = null;

        if ( args.length > 0 ) CtlCmd = args[0];
        if ( ( args.length > 1 ) && ( !CmdArg.equals( args[1] ) ) ) {
            CmdArg = args[1];
            lookPlayer = Bukkit.getServer().getPlayer( CmdArg );
        }

        if ( ( ( player == null ) || player.hasPermission( "citizenship.initialize" ) ) && CtlCmd.equals( "initialize" ) ) {
            setGroup( ( lookPlayer == null ? player:lookPlayer ), Config.rankName.get( 0 ) );
            return true;
        }

        if ( ( player == null ) || player.hasPermission( "citizenship.console" ) ) {
            switch ( CtlCmd ) {
                case "Reload":
                    instance.config.load();
                    Tools.Prt( player, Utility.ReplaceString( "%$aCitizenShip Config Reloaded." ), programCode );
                    return true;
                case "Console":
                    Tools.setDebug( CmdArg, programCode );
                    Tools.Prt( player,
                        ChatColor.GREEN + "System Debug Mode is [ " +
                        ChatColor.RED + Tools.consoleFlag.get( programCode ) +
                        ChatColor.GREEN + " ]",
                        programCode
                    );
                    return true;
                default:
            }
        }

        if ( ( player == null ) || player.hasPermission( "citizenship.admin" ) ) {
            switch ( CtlCmd ) {
                case "promotion":
                    if ( lookPlayer != null ) {
                        Promotion( lookPlayer );
                        return true;
                    }
                    break;
                case "demotion":
                    if ( lookPlayer != null ) {
                        Demotion( lookPlayer );
                        return true;
                    }
                    break;
                case "time":
                    return getAccess( player, CmdArg );
                case "Status":
                    instance.config.Status( player );
                    return true;
                default:
            }
        }

        if ( ( player == null ) || player.hasPermission( "citizenship.initialize" ) ) {
            Tools.Prt( player, "ranks initialize <player>", programCode );
        }
        if ( ( player == null ) || player.hasPermission( "citizenship.console" ) ) {
            Tools.Prt( player, "ranks Reload", programCode );
            Tools.Prt( player, "ranks Console [max,full,normal,none]", programCode );
        }

        if ( ( player == null ) || player.hasPermission( "citizenship.admin" ) ) {
            Tools.Prt( player, "ranks promotion <player>", programCode );
            Tools.Prt( player, "ranks demotion <player>", programCode );
            Tools.Prt( player, "ranks time <player>", programCode );
            Tools.Prt( player, "ranks Status", programCode );
        }

        return false;
    }
}
