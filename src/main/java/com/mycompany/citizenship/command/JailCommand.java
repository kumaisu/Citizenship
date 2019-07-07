/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.mycompany.citizenship.Citizenship;
import com.mycompany.citizenship.config.Config;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.citizenship.PlayerControl.JailTeleport;
import static com.mycompany.citizenship.PlayerControl.ReleaseTeleport;
import static com.mycompany.citizenship.RanksControl.getGroup;
import static com.mycompany.citizenship.RanksControl.setGroup;
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

        Tools.Prt( "CitizenShip Jail Command", Tools.consoleMode.normal, programCode );

        Player jailPlayer = null;
        String Reson = "";

        if ( args.length > 0 ) {
            String CmdArg = args[0];
            jailPlayer = Bukkit.getServer().getPlayer( CmdArg );
        }

        if ( jailPlayer == null ) {
            Tools.Prt( player, ChatColor.RED + "対象プレイヤーが居ません", programCode );
            return false;
        }
        
        if ( args.length > 1 ) { Reson = args[1]; }
        Tools.Prt( "Jail Reson : " + Reson, Tools.consoleMode.normal, programCode );

        //  釈放処理
        if ( Reson.equalsIgnoreCase( "release" ) ) {
            if ( getGroup( jailPlayer ).equals( Config.Prison ) ) {
                setGroup( jailPlayer, Config.rankName.get( 0 ) );
                ReleaseTeleport( jailPlayer );
            }
            return true;
        }

        //  降格処理
        if ( !Config.Prison.equals( "" ) ) {
            Tools.Prt( "Demotion Citizenship", Tools.consoleMode.full, programCode );
            setGroup( jailPlayer, Config.Prison );
        }

        if ( Config.Imprisonment ) {
            JailTeleport( jailPlayer );
            jailPlayer.sendTitle(
                ChatColor.RED + "投獄されました",
                ChatColor.YELLOW + Reson,
                0, 100, 0 );
            Bukkit.broadcastMessage( ChatColor.RED + jailPlayer.getName() + " さんは、投獄されました" );
        }

        return true;
    }
}
