/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mycompany.citizenship.Citizenship;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.citizenship.database.MySQLControl;
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
        Player player = ( sender instanceof Player ) ? ( Player )sender:( Player )null;

        String CtlCmd = "None";
        String CmdArg = "none";
        Player lookPlayer = player;

        if ( args.length > 0 ) CtlCmd = args[0];
        if ( args.length > 1 ) {
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
                Tools.Prt( player, "Player Times:", programCode );
                if ( lookPlayer != null ) {
                    MySQLControl DBRec = new MySQLControl();
                    DBRec.GetSQL( lookPlayer.getUniqueId() );
                    Tools.Prt( player, "Total TickTime  : " + Float.toString( ( float ) lookPlayer.getStatistic( Statistic.PLAY_ONE_MINUTE ) ) + " Ticks(0.05sec)", programCode );
                    Tools.Prt( player, "総接続時間      : " + Float.toString( ( float ) ( lookPlayer.getStatistic( Statistic.PLAY_ONE_MINUTE ) * 0.05 / 60 / 60)) + " hour" , programCode );
                    Tools.Prt( player, "ランク判定時間  : " + Float.toString( ( float ) ( ( lookPlayer.getStatistic( Statistic.PLAY_ONE_MINUTE ) - MySQLControl.offset ) * 0.05 / 60 / 60)) + " hour" , programCode );
                } else { Tools.Prt( player, ChatColor.RED + "Player が Offline か存在しません", programCode ); }
                return true;
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
