/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import java.util.Map;
import java.util.Date;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import static org.bukkit.Bukkit.getWorld;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.database.Database;
import com.mycompany.citizenship.database.PlayerData;
import com.mycompany.citizenship.database.ReasonData;
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
     * @param ReasonID
     * @return 
     */
    public static boolean toJail( Player player, int ReasonID ) {
        boolean retStat = false;

        //  降格処理
        if ( !Config.PrisonGroup.equals( "" ) ) {
            Tools.Prt( "Demotion Citizenship", Tools.consoleMode.max, programCode );
            RanksControl.setGroup( player, Config.PrisonGroup );
            retStat = true;
        }

        //  投獄処理
        if ( Config.Imprisonment ) {
            JailTeleport( player );
            ReasonData.GetReason( ReasonID );
            player.sendTitle(
                ChatColor.RED + "投獄されました",
                ChatColor.YELLOW + Database.Reason + "(By." + Database.enforcer + ")",
                0, 100, 0 );
            Bukkit.broadcastMessage( ChatColor.RED + player.getDisplayName() + " さんは投獄されました" );
            retStat = true;
        }

        Tools.Prt(
            ChatColor.RED + "Reson : " +
            ChatColor.YELLOW + Database.Reason +
            ChatColor.AQUA + " By." + Database.enforcer,
            Tools.consoleMode.normal, programCode
        );

        PlayerData.addImprisonment( player.getUniqueId() );
        PlayerData.zeroAleart(  player.getUniqueId() );
        PlayerData.GetSQL( player.getUniqueId() );
        String message = ChatColor.AQUA + player.getName()
                + ChatColor.GREEN + "さんの"
                + ChatColor.RED + "投獄回数"
                + ChatColor.GREEN + "は : "
                + ChatColor.RED + Database.imprisonment + "回"
                + ChatColor.GREEN + "です";
        Tools.Prt( message, Tools.consoleMode.normal , programCode );
        Bukkit.getOnlinePlayers().stream().filter( ( p ) -> ( p.hasPermission( "citizenship.admin" ) || p.isOp() ) ).forEachOrdered( ( p ) -> { p.sendMessage( message ); } );
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
        Tools.Prt( Config.PrisonGroup + " : " + RanksControl.getGroup( player ), Tools.consoleMode.max, programCode );
        if ( Config.PrisonGroup.equals( RanksControl.getGroup( player ) ) ) {
            Tools.Prt( "Jail Player to : " + Config.rankName.get( 0 ), Tools.consoleMode.full, programCode );
            RanksControl.setGroup( player, Config.rankName.get( 0 ) );
        }
        
        //  初期値への強制転送
        if ( Config.Outprisonment ) {
            ReleaseTeleport( player );
            Bukkit.broadcastMessage( ChatColor.RED + player.getDisplayName() + " さんは釈放されました" );
        }

        PlayerData.SetReasonID( player.getUniqueId(), 0 );
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
     * 投獄中のプレイヤー一覧表示
     *
     * @param player 
     */
    public static void JailerList( Player player ) {
        Map< UUID, Integer > getList = PlayerData.ListJailMenber();

        Tools.Prt( player, ChatColor.RED + "=== Players currently imprisoned ===", programCode );

        getList.keySet().forEach( ( key ) -> {
            PlayerData.GetSQL( key );
            ReasonData.GetReason( getList.get( key ) );
            Tools.Prt( player,
                ChatColor.WHITE + String.format( "%3d", getList.get( key ) ) + ": " +
                ChatColor.GREEN + Database.sdf.format( Database.ReasonDate ) + " " +
                ChatColor.AQUA + Database.name + " " +
                ChatColor.RED + Database.Reason +
                ChatColor.WHITE + "(by." + Database.enforcer + ")",
                programCode );
        } );

        Tools.Prt( player, ChatColor.RED + "=== [EOF] ===", programCode );
    }

    /**
     * プレイヤーの接続時間取得
     *
     * @param player
     * @param uuid
     * @return 
     */
    public static boolean getInfo( Player player, UUID uuid ) {
        if ( uuid == null ) { return false; }

        String lookNAME;
        
        if ( Bukkit.getServer().getPlayer( uuid ) == null ) {
            lookNAME = Bukkit.getServer().getOfflinePlayer( uuid ).getName();
            Tools.Prt( player, "Get Offline Player Data : " + lookNAME, Tools.consoleMode.full, programCode );
        } else {
            lookNAME = Bukkit.getServer().getPlayer( uuid ).getName();
        }

        Tools.Prt( "Get Access Time [" + lookNAME + "]", Tools.consoleMode.max, programCode );

        if ( PlayerData.GetSQL( uuid ) ) {
            Tools.Prt( player, "Player Name    : " + lookNAME, programCode );
            Tools.Prt( player, "Total TickTime : " + Float.toString( ( float ) Database.tick ) + " Ticks(0.05sec)", programCode );
            Tools.Prt( player, "総接続時間     : " + Float.toString( ( float ) ( Database.tick * 0.05 / 60 / 60)) + " hour" , programCode );
            Tools.Prt( player, "ランク判定時間 : " + Float.toString( ( float ) ( ( Database.tick - Database.offset ) * 0.05 / 60 / 60)) + " hour" , programCode );
            Tools.Prt( player, "起算日         : " + Database.basedate.toString(), programCode );
            Tools.Prt( player, "経過日数       : " + Utility.dateDiff( Database.basedate, new Date() ) + " 日", programCode );
            Tools.Prt( player, "Logout日       : " + Database.logout.toString(), programCode );
            Tools.Prt( player, "Logout経過日数 : " + Utility.dateDiff( Database.logout, new Date() ) + " 日", programCode );
            Tools.Prt( player, "イエローカード : " + ( Database.yellow == 0 ? "無し":" " + Database.yellow + " 枚"), programCode );
            Tools.Prt( player, "投獄回数       : 前科" + ( Database.imprisonment == 0 ? "無し":" " + Database.imprisonment + " 犯"), programCode );
            return true;
        } else {
            Tools.Prt( player, ChatColor.RED + "Player[" + lookNAME + "]が存在しません", programCode );
            return false;
        }
    }

    /**
     * 未ログイン者の事前登録
     *
     * @param player
     * @param name
     * @return 
     */
    public static boolean putPlayer( Player player, String name ) {
        if ( name.equals( "" ) ) { return false; }

        UUID lookUUID;
        int lookTick = 0;
        
        Tools.Prt( "Put Player [" + name + "]", Tools.consoleMode.max, programCode );
        if ( Bukkit.getServer().getPlayer( name ) == null ) {
            Tools.Prt( player, "Get Offline Player Data : " + Bukkit.getServer().getOfflinePlayer( name ).getName(), Tools.consoleMode.max, programCode );
            lookUUID = Bukkit.getServer().getOfflinePlayer( name ).getUniqueId();
        } else {
            lookUUID = Bukkit.getServer().getPlayer( name ).getUniqueId();
            lookTick = TickTime.get( Bukkit.getServer().getPlayer( name ) );
        }

        if ( PlayerData.GetSQL( lookUUID ) ) {
            Tools.Prt( player, ChatColor.RED + "既に存在しているプレイヤーです", Tools.consoleMode.full, programCode );
            return false;
        } else {
            PlayerData.AddSQL( lookUUID, name, lookTick );
            PlayerData.SetLogoutToSQL( lookUUID );
            return true;
        }
    }
}
