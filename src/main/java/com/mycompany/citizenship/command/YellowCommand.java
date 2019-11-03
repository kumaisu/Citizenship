/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.command;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.citizenship.Citizenship;
import com.mycompany.citizenship.PlayerControl;
import com.mycompany.citizenship.database.PlayerData;
import com.mycompany.citizenship.database.YellowData;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class YellowCommand implements CommandExecutor {

    private final Citizenship instance;

    public YellowCommand( Citizenship instance ) {
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

        Tools.Prt( "CitizenShip Yellow Command", Tools.consoleMode.max, programCode );

        if ( ( player != null ) && !player.hasPermission( "citizenship.yellow" ) ) {
            Tools.Prt( player,ChatColor.RED + "操作権限がありません", Tools.consoleMode.normal, programCode );
            return false;
        }

        int PrtLine = 5;
        String YellowName = "";
        String YellowDate = "";

        if ( args.length > 0 ) {
            for ( String arg : args ) {
                String[] param = arg.split( ":" );
                switch ( param[0] ) {
                    case "u":
                        YellowName = param[1];
                        break;
                    case "d":
                        YellowDate = param[1];
                        break;
                    case "l":
                        try {
                            PrtLine = Integer.valueOf( param[1] );
                        } catch( NumberFormatException e ) {}
                    default:
                }
            }

            switch( args[0].toLowerCase() ) {
                case "list":
                    return YellowData.CardList( player, YellowName, YellowDate, PrtLine );
                case "reset":
                    if ( "".equals( YellowName ) ) {
                        Tools.Prt( player, ChatColor.RED + "プレイヤーを指定してください", Tools.consoleMode.max, programCode );
                    } else {
                        Player YellowPlayer;
                        OfflinePlayer offPlayer;
                        UUID YellowUUID;

                        YellowPlayer = Bukkit.getServer().getPlayer( YellowName );
                        if ( YellowPlayer == null ) {
                            offPlayer = Bukkit.getServer().getOfflinePlayer( YellowName );
                            if ( offPlayer == null ) {
                                Tools.Prt( player, ChatColor.RED + "対象プレイヤーが居ません", Tools.consoleMode.full, programCode );
                                return false;
                            } else {
                                YellowUUID = offPlayer.getUniqueId();
                            }
                        } else {
                            YellowUUID = YellowPlayer.getUniqueId();
                        }

                        PlayerData.zeroAleart( YellowUUID );
                        Tools.Prt( player, "Player Information for : " + YellowUUID.toString(), programCode );
                        return PlayerControl.getInfo( player, YellowUUID );
                    }
                default:
            }
        }
        Tools.Prt( player, "\n=== Yellow Card Command Help ===", programCode );
        Tools.Prt( player, "/yellow list [u:<PlayerName>] [d:<Date>] [l:<line>]", programCode );
        Tools.Prt( player, "/yellow reset u:<PlayerName>", programCode );
        return false;
    }
}
