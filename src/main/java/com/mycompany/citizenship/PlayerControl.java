/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import java.util.Date;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import static org.bukkit.Bukkit.getWorld;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.database.Database;
import com.mycompany.citizenship.database.PlayerData;
import com.mycompany.citizenship.database.ReasonData;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class PlayerControl {

    /**
     * 投獄処理
     *
     * @param player
     * @return 
     */
    public static boolean toJail( Player player ) {
        boolean retStat = false;

        //  降格処理
        if ( !Config.Prison.equals( "" ) ) {
            Tools.Prt( "Demotion Citizenship", Tools.consoleMode.max, programCode );
            RanksControl.setGroup( player, Config.Prison );
            retStat = true;
        }

        //  投獄処理
        if ( Config.Imprisonment ) {
            JailTeleport( player );
            ReasonData.GetReason( player.getUniqueId() );
            player.sendTitle(
                ChatColor.RED + "投獄されました",
                ChatColor.YELLOW + Database.Reason + "(By." + Database.enforcer + ")",
                0, 100, 0 );
            Bukkit.broadcastMessage( ChatColor.RED + player.getDisplayName() + " さんは投獄されました" );
            Tools.Prt( ChatColor.RED + "Reason:" + Database.Reason + " By." + Database.enforcer, programCode );
            retStat = true;
        }

        PlayerData.addImprisonment( player.getUniqueId() );
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

        //  投獄処理で降格された場合のみ一般への変更処理
        Tools.Prt( Config.Prison + " : " + RanksControl.getGroup( player ), Tools.consoleMode.max, programCode );
        if ( Config.Prison.equals( RanksControl.getGroup( player ) ) ) {
            Tools.Prt( "Jail Player to : " + Config.rankName.get( 0 ), Tools.consoleMode.full, programCode );
            RanksControl.setGroup( player, Config.rankName.get( 0 ) );
        }
        
        //  初期値への強制転送
        if ( Config.Outprisonment ) {
            ReleaseTeleport( player );
            Bukkit.broadcastMessage( ChatColor.RED + player.getDisplayName() + " さんは釈放されました" );
        }

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
            Tools.Prt( player, "Get Offline Player Data : " + Bukkit.getServer().getOfflinePlayer( name ).getName(), Tools.consoleMode.full, programCode );
            lookUUID = Bukkit.getServer().getOfflinePlayer( name ).getUniqueId();
        } else {
            lookUUID = Bukkit.getServer().getPlayer( name ).getUniqueId();
        }

        if ( PlayerData.GetSQL( lookUUID ) ) {
            Tools.Prt( player, "Player Name    : " + name, programCode );
            Tools.Prt( player, "Total TickTime : " + Float.toString( ( float ) Database.tick ) + " Ticks(0.05sec)", programCode );
            Tools.Prt( player, "総接続時間     : " + Float.toString( ( float ) ( Database.tick * 0.05 / 60 / 60)) + " hour" , programCode );
            Tools.Prt( player, "ランク判定時間 : " + Float.toString( ( float ) ( ( Database.tick - Database.offset ) * 0.05 / 60 / 60)) + " hour" , programCode );
            Tools.Prt( player, "起算日         : " + Database.basedate.toString(), programCode );
            Tools.Prt( player, "経過日数       : " + Utility.dateDiff( Database.basedate, new Date() ) + " 日", programCode );
            Tools.Prt( player, "Logout日       : " + Database.logout.toString(), programCode );
            Tools.Prt( player, "Logout経過日数 : " + Utility.dateDiff( Database.logout, new Date() ) + " 日", programCode );
            Tools.Prt( player, "投獄回数       : 前科" + ( Database.imprisonment == 0 ? "無し":" " + Database.imprisonment + " 犯"), programCode );
            return true;
        } else {
            Tools.Prt( player, ChatColor.RED + "Player[" + name + "]が存在しません", programCode );
            return false;
        }
    }
}
