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
import static com.mycompany.citizenship.PlayerControl.getAccess;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import static com.mycompany.citizenship.RanksControl.Demotion;
import static com.mycompany.citizenship.RanksControl.Promotion;
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

        switch ( CtlCmd ) {
            case "promotion":
                if ( lookPlayer != null ) {
                    Promotion( lookPlayer );
                }
                return true;
            case "demotion":
                if ( lookPlayer != null ) {
                    Demotion( lookPlayer );
                }
                return true;
            case "time":
                return getAccess( player, CmdArg );
            case "Reload":
                instance.config.load();
                Tools.Prt( player, Utility.ReplaceString( "%$aCitizenShip Config Reloaded." ), programCode );
                return true;
            case "Status":
                instance.config.Status( player );
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
                return false;
        }
    }
}
