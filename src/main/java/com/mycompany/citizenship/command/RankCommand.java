/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.command;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mycompany.citizenship.Citizenship;
import com.mycompany.citizenship.PlayerControl;
import com.mycompany.citizenship.RanksControl;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.config.ConfigManager;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
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
        Player lookPlayer = ( player == null ? null:player );
        UUID lookUUID = ( player == null ? null:player.getUniqueId() );

        if ( args.length > 0 ) CtlCmd = args[0];
        if ( ( args.length > 1 ) && ( !CmdArg.equals( args[1] ) ) ) {
            CmdArg = args[1];
            lookPlayer = Bukkit.getServer().getPlayer( CmdArg );
            lookUUID = ( Bukkit.getServer().getPlayer( CmdArg ) == null ?
                    Bukkit.getServer().getOfflinePlayer( CmdArg ).getUniqueId() : 
                    Bukkit.getServer().getPlayer( CmdArg ).getUniqueId() );
        }

        if ( ( ( player == null ) || player.hasPermission( "citizenship.initialize" ) ) && CtlCmd.equals( "initialize" ) ) {
            RanksControl.setGroup( ( lookPlayer == null ? player:lookPlayer ), Config.rankName.get( 0 ) );
            return true;
        }

        if ( ( player == null ) || player.hasPermission( "citizenship.console" ) ) {
            switch ( CtlCmd ) {
                case "Reload":
                    ConfigManager.load();
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
                        RanksControl.Promotion( player, lookPlayer );
                        return true;
                    } else Tools.Prt( ChatColor.RED + "正しいプレイヤー名を指定してください", Tools.consoleMode.max, programCode );
                    break;
                case "demotion":
                    if ( lookPlayer != null ) {
                        RanksControl.Demotion( player, lookPlayer );
                        return true;
                    } else Tools.Prt( ChatColor.RED + "正しいプレイヤー名を指定してください", Tools.consoleMode.max, programCode );
                    break;
                case "time":
                    Tools.Prt( player, "Player Information for : " + lookUUID.toString(), programCode );
                    if ( lookUUID != null ) {
                        return PlayerControl.getInfo( player, lookUUID );
                    } else { Tools.Prt( player, "指定プレイヤーの情報はありません", programCode ); }
                    break;
                case "addplayer":
                    return PlayerControl.putPlayer( player, CmdArg );
                case "getuuid":
                    if ( Bukkit.getServer().getPlayer( CmdArg ) == null ) {
                        Tools.Prt( player, ChatColor.GREEN + "Offline : " +
                            Bukkit.getServer().getOfflinePlayer( CmdArg ).getName() + " [" +
                            Bukkit.getServer().getOfflinePlayer( CmdArg ).getUniqueId().toString() + "]",
                            programCode
                        );
                    } else {
                        Tools.Prt( player, ChatColor.GREEN + "Online : " +
                            Bukkit.getServer().getPlayer( CmdArg ).getName() + " [" +
                            Bukkit.getServer().getPlayer( CmdArg ).getUniqueId().toString() + "]",
                            programCode
                        );
                    }
                    return true;
                case "Status":
                    ConfigManager.Status( player );
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
            Tools.Prt( player, "ranks getuuid <player>", programCode );
            Tools.Prt( player, "ranks addplayer <player>", programCode );
            Tools.Prt( player, "ranks Status", programCode );
        }
        return false;
    }
}
