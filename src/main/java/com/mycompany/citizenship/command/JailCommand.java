/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.command;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.citizenship.Citizenship;
import com.mycompany.citizenship.PlayerControl;
import com.mycompany.citizenship.RanksControl;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.database.PlayerData;
import com.mycompany.citizenship.database.ReasonData;
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

        if ( ( player != null ) && !player.hasPermission( "citizenship.admin" ) ) {
            Tools.Prt( player,ChatColor.RED + "操作権限がありません", Tools.consoleMode.normal, programCode );
            return false;
        }

        Player jailPlayer = null;
        UUID jailUUID = null;
        boolean offlineMode = false;
        String Reason = "System";
        int reasonID = 0;
        if ( player != null ) { Reason = player.getDisplayName() + " が逮捕しました"; }

        if ( args.length > 0 ) {
            for ( String arg : args ) {
                String[] param = arg.split( ":" );
                switch ( param[0] ) {
                    case "u":
                        String Prisoner = param[1];
                        jailPlayer = Bukkit.getServer().getPlayer( Prisoner );
                        if ( jailPlayer == null ) {
                            OfflinePlayer offPlayer = Bukkit.getServer().getOfflinePlayer( param[1] );
                            if ( offPlayer == null ) {
                                Tools.Prt( player, ChatColor.RED + "対象プレイヤーが居ません", programCode );
                            } else {
                                jailUUID = offPlayer.getUniqueId();
                                offlineMode = true;
                            }
                        } else {
                            jailUUID = jailPlayer.getUniqueId();
                        }
                        break;
                    case "r":
                        Reason = param[1];
                        break;
                    case "i":
                        try {
                            reasonID = Integer.valueOf( param[1] );
                        } catch( NumberFormatException e ) {
                            Tools.Prt( player, ChatColor.YELLOW + "IDを指定してください", programCode );
                        }
                    default:
                }
            }

            switch( args[0].toLowerCase() ) {
                case "release":
                    if ( jailPlayer == null ) {
                        Tools.Prt( ChatColor.YELLOW + "指定プレイヤーがログインしていません", programCode );
                        return false;
                    } else {
                        if ( RanksControl.getGroup( jailPlayer ).equals( Config.Prison ) ) {
                            PlayerControl.outJail( jailPlayer );
                        }
                        return true;
                    }
                case "list":
                    ReasonData.ListReason( player, jailUUID );
                    return true;
                case "change":
                    if ( reasonID > 0 ) {
                        ReasonData.ChangeReason( reasonID, Reason );
                        ReasonData.PrintReason( player, reasonID );
                        return true;
                    }
                    break;
                case "print":
                    if ( reasonID > 0 ) {
                        ReasonData.PrintReason( player, reasonID );
                        return true;
                    }
                    break;
                default:
                    if ( jailPlayer != null ) {
                        Tools.Prt( "Jail Reson : " + Reason, Tools.consoleMode.normal, programCode );
                        if ( offlineMode ) {
                            ReasonData.AddReason( jailUUID, Reason );
                            return PlayerData.SetJailToSQL( jailUUID, 1 );
                        } else {
                            ReasonData.AddReason( jailUUID, Reason );
                            return PlayerControl.toJail( jailPlayer );
                        }
                    }
            }
        }
        Tools.Prt( player, "/jail u:<PlayerName> r:<Reason>", programCode );
        Tools.Prt( player, "/jail release u:<PlayerName>", programCode );
        Tools.Prt( player, "/jail list [u:<PlayerName>]", programCode );
        Tools.Prt( player, "/jail print <ReasonID>", programCode );
        Tools.Prt( player, "/jail change <ReasonID>", programCode );
        return false;
    }
}
