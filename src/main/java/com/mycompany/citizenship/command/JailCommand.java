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
import com.mycompany.citizenship.database.Database;
import com.mycompany.citizenship.database.PlayerData;
import com.mycompany.citizenship.database.ReasonData;
import static com.mycompany.citizenship.config.Config.programCode;
import com.mycompany.citizenship.config.ConfigManager;

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
        String enforcer = ( player == null ? "System" : player.getName() );

        Tools.Prt( "CitizenShip Jail Command", Tools.consoleMode.max, programCode );

        if ( ( player != null ) && !player.hasPermission( "citizenship.admin" ) ) {
            Tools.Prt( player,ChatColor.RED + "操作権限がありません", Tools.consoleMode.normal, programCode );
            return false;
        }

        Player jailPlayer = null;
        OfflinePlayer offPlayer = null;
        UUID jailUUID = null;
        boolean offlineMode = false;
        String Reason = "System";
        int reasonID = 0;

        if ( args.length > 0 ) {
            for ( String arg : args ) {
                String[] param = arg.split( ":" );
                switch ( param[0] ) {
                    case "u":
                        String Prisoner = param[1];
                        jailPlayer = Bukkit.getServer().getPlayer( Prisoner );
                        if ( jailPlayer == null ) {
                            offPlayer = Bukkit.getServer().getOfflinePlayer( param[1] );
                            if ( offPlayer == null ) {
                                Tools.Prt( player, ChatColor.RED + "対象プレイヤーが居ません", Tools.consoleMode.full, programCode );
                                return false;
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
                case "status":
                    ConfigManager.JailStatus( player );
                    return true;
                case "release":
                    if ( jailPlayer != null ) {
                        PlayerData.GetSQL( jailPlayer.getUniqueId() );
                        if ( Database.ReasonID != 0 ) {
                            PlayerControl.outJail( jailPlayer );
                            return true;
                        } else Tools.Prt( player, ChatColor.RED + "対象プレイヤーは投獄されていません", Tools.consoleMode.normal, programCode );
                    } else Tools.Prt( player, ChatColor.YELLOW + "指定プレイヤーがログインしていません", Tools.consoleMode.full, programCode );
                    break;
                case "list":
                    PlayerControl.JailerList( player );
                    return true;
                case "alllist":
                    ReasonData.ListReason( player, jailUUID );
                    return true;
                case "change":
                    if ( reasonID > 0 ) {
                        ReasonData.ChangeReason( reasonID, Reason );
                        ReasonData.PrintReason( player, reasonID );
                        return true;
                    } else Tools.Prt( player, ChatColor.RED + "Reson ID を指定してください", Tools.consoleMode.normal, programCode );
                    break;
                case "info":
                    if ( jailUUID != null ) {
                        Tools.Prt( player, "Player Information for : " + jailUUID.toString(), programCode );
                        return PlayerControl.getInfo( player, jailUUID );
                    } else { Tools.Prt( player, "指定プレイヤーの情報はありません", programCode ); }
                    return false;
                default:
                    if ( jailUUID != null ) {
                        boolean RetFlag;
                        if ( offlineMode ) {
                            Tools.Prt( player, ChatColor.RED + enforcer + " Jail to offline " + offPlayer.getName(), Tools.consoleMode.normal, programCode );
                            PlayerData.SetReasonID( jailUUID, ReasonData.AddReason( jailUUID, Reason, enforcer ) );
                            RetFlag = PlayerData.SetJailToSQL( jailUUID, 1 );
                            Tools.Prt( player, 
                                ChatColor.RED + "Reson : "
                                + ChatColor.YELLOW + Reason
                                + ChatColor.AQUA + " By." + enforcer,
                                Tools.consoleMode.normal, programCode
                            );
                        } else {
                            Tools.Prt( player, ChatColor.RED + enforcer + " Jail to " + jailPlayer.getName(), Tools.consoleMode.normal, programCode );
                            int ReasonID = ReasonData.AddReason( jailUUID, Reason, enforcer );
                            PlayerData.SetReasonID( jailUUID, ReasonID );
                            RetFlag = PlayerControl.toJail( jailPlayer, ReasonID );
                        }
                        return RetFlag;
                    } else { Tools.Prt( player, "正しくプレイヤー指定してください", programCode ); }
            }
        }
        Tools.Prt( player, "\n=== Jail Command Help ===", programCode );
        Tools.Prt( player, "/jail u:<PlayerName> r:<Reason>", programCode );
        Tools.Prt( player, "/jail release u:<PlayerName>", programCode );
        Tools.Prt( player, "/jail list", programCode );
        Tools.Prt( player, "/jail alllist [u:<PlayerName>]", programCode );
        Tools.Prt( player, "/jail change i:<ReasonID> r:<Reason>", programCode );
        Tools.Prt( player, "/jail info u:<PlayerName>", programCode );
        return false;
    }
}
