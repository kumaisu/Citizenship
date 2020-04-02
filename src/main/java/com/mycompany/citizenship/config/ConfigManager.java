/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.config;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class ConfigManager {

    private static Plugin plugin;
    private static FileConfiguration config = null;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        Tools.entryDebugFlag( programCode, Tools.consoleMode.print );
        Tools.Prt( "Config Loading now...", programCode );
        load();
    }

    /*
     * 設定をロードします
     */
    public static void load() {
        // 設定ファイルを保存
        plugin.saveDefaultConfig();
        if ( config != null ) { // configが非null == リロードで呼び出された
            Tools.Prt( "Config Reloading now...", programCode );
            plugin.reloadConfig();
        }
        config = plugin.getConfig();

        Config.host     = config.getString( "mysql.host" );
        Config.port     = config.getString( "mysql.port" );
        Config.database = config.getString( "mysql.database" );
        Config.username = config.getString( "mysql.username" );
        Config.password = config.getString( "mysql.password" );

        Config.rankName = new ArrayList<>();
        List< String > getstr = ( List< String > ) config.getList( "Rank" );
        for( int i = 0; i<getstr.size(); i++ ) {
            String[] param = getstr.get( i ).split(",");
            Map< String, Integer > TimeData = new HashMap<>();
            TimeData.put( param[2].toUpperCase(), Integer.valueOf( param[1] ) );
            Config.rankTime.put( param[0], TimeData );
            Config.rankName.add( param[0] );
        }

        Config.demotion         = config.getBoolean( "Demotion.enable", false );
        Config.demotionDefault  = config.getInt( "Demotion.default", 0 );
        List< String > getd = ( List< String > ) config.getList( "Demotion.Rank" );
        for( int i = 0; i<getd.size(); i++ ) {
            String[] param = getd.get( i ).split(",");
            Config.demot.put( param[0], Integer.valueOf( param[1] ) );
        }

        Config.PromotBroadcast  = config.getBoolean( "PromotBroadcast", false );
        Config.DemotBroadcast   = config.getBoolean( "DemotBroadcast", false );
        Config.PrisonGroup      = config.getString( "PrisonGroup", "" );
        Config.Penalty          = config.getInt( "PenaltyTime", 0 );
        
        Config.Imprisonment     = config.getBoolean( "Prison.enable", false );
        Config.fworld   = config.getString( "Prison.world" );
        Config.fx       = Float.valueOf( config.getString( "Prison.x" ) );
        Config.fy       = Float.valueOf( config.getString( "Prison.y" ) );
        Config.fz       = Float.valueOf( config.getString( "Prison.z" ) );
        Config.fyaw     = Float.valueOf( config.getString( "Prison.yaw" ) );
        Config.fpitch   = Float.valueOf( config.getString( "Prison.pitch" ) );

        Config.Outprisonment    = config.getBoolean( "Release.enable", false );
        Config.rworld   = config.getString( "Release.world" );
        Config.rx       = Float.valueOf( config.getString( "Release.x" ) );
        Config.ry       = Float.valueOf( config.getString( "Release.y" ) );
        Config.rz       = Float.valueOf( config.getString( "Release.z" ) );
        Config.ryaw     = Float.valueOf( config.getString( "Release.yaw" ) );
        Config.rpitch   = Float.valueOf( config.getString( "Release.pitch" ) );

        Config.Aleart   = ( List< String > ) config.getList( "Aleart" );
        Config.AutoJail = config.getInt( "AutoJail", 0 );

        Config.AutoDeop = config.getBoolean( "AutoDeop", false );
        Config.OPName   = config.getString( "ForceOP", "None" );

        Reward.sound_play       = config.getBoolean( "rewards.sound.enabled", false );
        Reward.sound_type       = config.getString( "rewards.sound.type", "" );
        Reward.sound_volume     = config.getInt( "rewards.sound.volume", 1 );
        Reward.sound_pitch      = config.getInt( "rewards.sound.pitch", 1 );
        Reward.basic_message    = config.getString( "rewards.basic.claim-message", "error" );
        Reward.basic_command    = config.getStringList( "rewards.basic.commands" );
        Reward.advance_message  = config.getString( "rewards.advanced.claim-message", "error" );
        Reward.advance_command  = config.getStringList( "rewards.advanced.commands" );

        Yellow.sound_play       = config.getBoolean( "yellow.sound.enabled", false );
        Yellow.sound_type       = config.getString( "yellow.sound.type", "" );
        Yellow.sound_volume     = config.getInt( "yellow.sound.volume", 1 );
        Yellow.sound_pitch      = config.getInt( "yellow.sound.pitch", 1 );

        if ( !Tools.setDebug( config.getString( "Debug" ), programCode ) ) {
            Tools.entryDebugFlag( programCode, Tools.consoleMode.normal );
            Tools.Prt( ChatColor.RED + "Config Debugモードの指定値が不正なので、normal設定にしました", programCode );
        }
    }

    public static void Status( Player p ) {
        Tools.Prt( p, ChatColor.GREEN + "=== Citizenship Status ===", programCode );
        Tools.Prt( p, ChatColor.WHITE + "Degub Mode   : " + ChatColor.YELLOW + Tools.consoleFlag.get( programCode ).toString(), programCode );
        Tools.Prt( p, ChatColor.WHITE + "Mysql        : " + ChatColor.YELLOW + Config.host + ":" + Config.port, programCode );
        Tools.Prt( p, ChatColor.WHITE + "DB Name      : " + ChatColor.YELLOW + Config.database, programCode );
        if ( ( p == null ) || p.hasPermission( "citizenship.console" ) ) {
            Tools.Prt( p, ChatColor.WHITE + "DB UserName  : " + ChatColor.YELLOW + Config.username, programCode );
            Tools.Prt( p, ChatColor.WHITE + "DB Password  : " + ChatColor.YELLOW + Config.password, programCode );
        }
        Tools.Prt( p, ChatColor.WHITE + "昇格時ｱﾅｳﾝｽ  : " + ChatColor.YELLOW + ( Config.PromotBroadcast ? "する":"しない" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "CitizenShip List : 昇格時間",programCode );
        Config.rankName.stream().map( ( gn ) -> {
            String msg = ChatColor.WHITE + String.format( "%-10s", gn ) + " : " + ChatColor.YELLOW;
            if ( Config.rankTime.get( gn ).get( "H" ) != null ) {
                msg = msg + Config.rankTime.get( gn ).get( "H" ) + " 時間";
            }
            if ( Config.rankTime.get( gn ).get( "D" ) != null ) {
                msg = msg + Config.rankTime.get( gn ).get( "D" ) + " 日";
            }
            if ( Config.rankTime.get( gn ).get( "E" ) != null ) {
                msg = msg + "最終ランク";
            }
            return msg;            
        } ).forEachOrdered( ( msg ) -> { Tools.Prt( p, msg, programCode ); } );
        if ( Config.demotion ) {
            Tools.Prt( p, ChatColor.WHITE + "降格基礎日数 : " + ChatColor.YELLOW + Config.demotionDefault + " 日", programCode );
            Tools.Prt( p, ChatColor.WHITE + "ランク別降格日数",programCode );
            Config.demot.forEach( ( key, value ) -> { Tools.Prt( p, String.format( "%-10s", key ) + " : " + value + " 日", programCode ); } );
        }
        Tools.Prt( p, ChatColor.WHITE + "Auto Deop    : " + ChatColor.YELLOW + ( Config.AutoDeop ? "する":"しない" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "ForceOperator: " + ChatColor.YELLOW + Config.OPName, programCode );
        Tools.Prt( p, ChatColor.GREEN + "==========================", programCode );
    }

    public static void JailStatus( Player p ) {
        Tools.Prt( p, ChatColor.GREEN + "=== Citizenship Jail Status ===", programCode );
        Tools.Prt( p, ChatColor.WHITE + "牢獄グループ : " + ChatColor.YELLOW + Config.PrisonGroup, programCode );
        Tools.Prt( p, ChatColor.WHITE + "投獄期間     : " + ChatColor.YELLOW + Config.Penalty + "日", programCode );
        Tools.Prt( p, ChatColor.WHITE + "牢獄ジャンプ : " + ChatColor.YELLOW + ( Config.Imprisonment ? "する":"しない" ), programCode );
        if ( Config.Imprisonment ) {
            Tools.Prt( p, ChatColor.WHITE + "牢獄行き先 : " +
                ChatColor.YELLOW + "[" + Config.fworld + "] " +
                ChatColor.WHITE + "x:" + ChatColor.YELLOW + String.valueOf( Config.fx ) + "," +
                ChatColor.WHITE + "y:" + ChatColor.YELLOW + String.valueOf( Config.fy ) + "," +
                ChatColor.WHITE + "z:" + ChatColor.YELLOW + String.valueOf( Config.fz ) + "," +
                ChatColor.WHITE + "pit:" + ChatColor.YELLOW + String.valueOf( Config.fpitch ) + "," +
                ChatColor.WHITE + "yaw:" + ChatColor.YELLOW + String.valueOf( Config.fyaw ),
                programCode
            );
            Tools.Prt( p, ChatColor.WHITE + "釈放行き先 : " +
                ChatColor.YELLOW + "[" + Config.rworld + "] " +
                ChatColor.WHITE + "x:" + ChatColor.YELLOW + String.valueOf( Config.rx ) + "," +
                ChatColor.WHITE + "y:" + ChatColor.YELLOW + String.valueOf( Config.ry ) + "," +
                ChatColor.WHITE + "z:" + ChatColor.YELLOW + String.valueOf( Config.rz ) + "," +
                ChatColor.WHITE + "pit:" + ChatColor.YELLOW + String.valueOf( Config.rpitch ) + "," +
                ChatColor.WHITE + "yaw:" + ChatColor.YELLOW + String.valueOf( Config.ryaw ),
                programCode
            );
        }
        Tools.Prt( p, ChatColor.GREEN + "==========================", programCode );
    }

    public static void YellowStatus( Player p ) {
        Tools.Prt( p, ChatColor.GREEN + "=== Citizenship Yellow Status ===", programCode );
        Tools.Prt( p, ChatColor.WHITE + "自動投獄 : " + ChatColor.YELLOW + Config.AutoJail + "回以上", programCode );
        Tools.Prt( p, ChatColor.WHITE + "警戒Keyword", programCode );
        Config.Aleart.forEach( ( key ) -> { Tools.Prt( p, ChatColor.YELLOW + " - " + key, programCode ); } );
        Tools.Prt( p, ChatColor.GREEN + "==========================", programCode );
    }

    public static void RewardStatus( Player player ) {
        Tools.Prt( player, ChatColor.GREEN + "=== LoginContrl Rewards ===", programCode );
        Tools.Prt( player, ChatColor.WHITE + "Play Sound   : " + ChatColor.YELLOW + ( Reward.sound_play ? "Yes" : "No" ), programCode );
        if ( Reward.sound_play ) {
            Tools.Prt( player, ChatColor.WHITE + "Sound Type   : " + ChatColor.YELLOW + Reward.sound_type, programCode );
            Tools.Prt( player, ChatColor.WHITE + "      Volume : " + ChatColor.YELLOW + Reward.sound_volume, programCode );
            Tools.Prt( player, ChatColor.WHITE + "      Pitch  : " + ChatColor.YELLOW + Reward.sound_pitch, programCode );
        }
        Tools.Prt( player, ChatColor.WHITE + "---Basic Rewards---", programCode );
        Tools.Prt( player, ChatColor.WHITE + "Message     : " + ChatColor.YELLOW + Reward.basic_message, programCode );
        Tools.Prt( player, ChatColor.WHITE + "Commands    :", programCode );
        Reward.basic_command.stream().forEach( BR -> { Tools.Prt( player, ChatColor.WHITE + " - " + ChatColor.YELLOW + BR, programCode ); } );
        Tools.Prt( player, ChatColor.WHITE + "---Advanced Rewards---", programCode );
        Tools.Prt( player, ChatColor.WHITE + "Message     : " + ChatColor.YELLOW + Reward.advance_message, programCode );
        Tools.Prt( player, ChatColor.WHITE + "Commands    :", programCode );
        Reward.advance_command.stream().forEach( AR -> { Tools.Prt( player, ChatColor.WHITE + " - " + ChatColor.YELLOW + AR, programCode ); } );
    }
}
