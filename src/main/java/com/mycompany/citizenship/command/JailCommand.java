/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.mycompany.citizenship.Citizenship;
import com.mycompany.citizenship.config.Config;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.citizenship.database.MySQLControl;
import static com.mycompany.citizenship.PlayerControl.toJail;
import static com.mycompany.citizenship.PlayerControl.outJail;
import static com.mycompany.citizenship.RanksControl.getGroup;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class JailCommand implements CommandExecutor {

    private final Citizenship instance;

    public JailCommand( Citizenship instance ) {
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
        Player player = ( sender instanceof Player ) ? ( Player ) sender : ( Player ) null;

        Tools.Prt( "CitizenShip Jail Command", Tools.consoleMode.full, programCode );

        Player jailPlayer = null;
        String Reson = "";
        String PrisonerName = "";

        if ( args.length > 0 ) {
            PrisonerName = args[0];
            jailPlayer = Bukkit.getServer().getPlayer( PrisonerName );
        }

        if ( jailPlayer == null ) {
            OfflinePlayer offlineJailPlayer = Bukkit.getOfflinePlayer( PrisonerName );
            if ( offlineJailPlayer == null ) {
                Tools.Prt( player, ChatColor.RED + "対象プレイヤーが居ません", programCode );
                return false;
            } else {
                MySQLControl DBRec = new MySQLControl();
                DBRec.SetJailToSQL( offlineJailPlayer.getUniqueId(), 1 );
                return true;
            }
        }

        if ( args.length > 1 ) { Reson = args[1]; }
        Tools.Prt( "Jail Reson : " + Reson, Tools.consoleMode.normal, programCode );

        //  釈放処理
        if ( Reson.equalsIgnoreCase( "release" ) ) {
            if ( getGroup( jailPlayer ).equals( Config.Prison ) ) { outJail( jailPlayer ); }
            return true;
        }

        return toJail( jailPlayer, Reson );
    }
}
