/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Statistic;
import static org.bukkit.Bukkit.getWorld;
import com.mycompany.citizenship.config.Config;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.citizenship.RanksControl.setGroup;
import static com.mycompany.citizenship.config.Config.programCode;
import com.mycompany.citizenship.database.MySQLControl;
import java.util.UUID;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author sugichan
 */
public class PlayerControl {

    /**
     * 投獄処理
     *
     * @param player
     * @param Reason
     * @return 
     */
    public static boolean toJail( Player player, String Reason ) {
        boolean retStat = false;

        //  降格処理
        if ( !Config.Prison.equals( "" ) ) {
            Tools.Prt( "Demotion Citizenship", Tools.consoleMode.max, programCode );
            setGroup( player, Config.Prison );
            retStat = true;
        }

        //  投獄処理
        if ( Config.Imprisonment ) {
            JailTeleport( player );
            player.sendTitle(
                ChatColor.RED + "投獄されました",
                ChatColor.YELLOW + Reason,
                0, 100, 0 );
            Bukkit.broadcastMessage( ChatColor.RED + player.getDisplayName() + " さんは、投獄されました" );
            retStat = true;
        }

        return retStat;
    }

    /**
     * 釈放処理
     *
     * @param player
     * @return 
     */
    public static boolean outJail( Player player ) {
        boolean retStat = false;

        setGroup( player, Config.rankName.get( 0 ) );
        ReleaseTeleport( player );

        return retStat;
    }
    
    /**
     * 牢獄エリアへの強制転送コマンド
     *
     * @param player 
     */
    public static void JailTeleport( Player player ) {
        Tools.Prt( player.getName() + " is JAIL to teleport", Tools.consoleMode.normal, programCode );
        World world = getWorld( Config.fworld );
        Location loc = new Location( world, Config.fx, Config.fy, Config.fz );
        loc.setPitch( Config.fpitch );
        loc.setYaw( Config.fyaw );
        player.teleport( loc );
    }

    /**
     * 釈放エリアへの強制転送コマンド
     *
     * @param player 
     */
    public static void ReleaseTeleport( Player player ) {
        Tools.Prt( player.getName() + " is JAIL from teleport", Tools.consoleMode.normal, programCode );
        World world = getWorld( Config.rworld );
        Location loc = new Location( world, Config.rx, Config.ry, Config.rz );
        loc.setPitch( Config.rpitch );
        loc.setYaw( Config.ryaw );
        player.teleport( loc );
    }

    /**
     * プレイヤーの接続時間取得
     *
     * @param player
     * @param name
     * @return 
     */
    public static boolean getAccess( Player player, String name ) {
        if ( name.equals( "" ) ) { return false; }

        UUID lookUUID;
        
        Tools.Prt( "Get Access Time [" + name + "]", Tools.consoleMode.max, programCode );

        if ( Bukkit.getServer().getPlayer( name ) == null ) {
            Tools.Prt( "Get Offline Player Data : " + Bukkit.getServer().getOfflinePlayer( name ).getName(), Tools.consoleMode.full, programCode );
            lookUUID = Bukkit.getServer().getOfflinePlayer( name ).getUniqueId();
        } else {
            lookUUID = Bukkit.getServer().getPlayer( name ).getUniqueId();
        }

        MySQLControl DBRec = new MySQLControl();
        if ( DBRec.GetSQL( lookUUID ) ) {
            Tools.Prt( player, "Total TickTime  : " + Float.toString( ( float ) MySQLControl.tick ) + " Ticks(0.05sec)", programCode );
            Tools.Prt( player, "総接続時間      : " + Float.toString( ( float ) ( MySQLControl.tick * 0.05 / 60 / 60)) + " hour" , programCode );
            Tools.Prt( player, "ランク判定時間  : " + Float.toString( ( float ) ( ( MySQLControl.tick - MySQLControl.offset ) * 0.05 / 60 / 60)) + " hour" , programCode );
            return true;
        } else {
            Tools.Prt( player, ChatColor.RED + "Player[" + name + "]が存在しません", programCode );
            return false;
        }
    }
}
