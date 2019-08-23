/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.config;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.citizenship.config.Config.programCode;
import java.util.Map;

/**
 *
 * @author sugichan
 */
public class ConfigManager {

    private static Plugin plugin;
    private static FileConfiguration config = null;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        Tools.entryDebugFlag( programCode, Tools.consoleMode.none );
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

        Config.demotion         = config.getInt( "Demotion", 0 );
        Config.PromotBroadcast  = config.getBoolean( "PlayerBroadcast", false );
        Config.Prison           = config.getString( "PrisonGroup", "" );
        Config.Penalty          = config.getInt( "PenaltyTime", 0 );
        Config.Imprisonment     = config.getBoolean( "Imprisonment", false );
        
        Config.fworld   = config.getString( "Prison.world" );
        Config.fx       = Float.valueOf( config.getString( "Prison.x" ) );
        Config.fy       = Float.valueOf( config.getString( "Prison.y" ) );
        Config.fz       = Float.valueOf( config.getString( "Prison.z" ) );
        Config.fyaw     = Float.valueOf( config.getString( "Prison.yaw" ) );
        Config.fpitch   = Float.valueOf( config.getString( "Prison.pitch" ) );

        Config.rworld   = config.getString( "Release.world" );
        Config.rx       = Float.valueOf( config.getString( "Release.x" ) );
        Config.ry       = Float.valueOf( config.getString( "Release.y" ) );
        Config.rz       = Float.valueOf( config.getString( "Release.z" ) );
        Config.ryaw     = Float.valueOf( config.getString( "Release.yaw" ) );
        Config.rpitch   = Float.valueOf( config.getString( "Release.pitch" ) );

        Tools.consoleMode DebugFlag;
        try {
            DebugFlag = Tools.consoleMode.valueOf( config.getString( "Debug" ) );
        } catch( IllegalArgumentException e ) {
            Tools.Prt( ChatColor.RED + "Config Debugモードの指定値が不正なので、normal設定にしました", programCode );
            DebugFlag = Tools.consoleMode.normal;
        }
        Tools.entryDebugFlag( programCode, DebugFlag );
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
        Tools.Prt( p, ChatColor.WHITE + "降格日数     : " + ChatColor.YELLOW + Config.demotion + " 日", programCode );
        Tools.Prt( p, ChatColor.WHITE + "昇格時ｱﾅｳﾝｽ  : " + ChatColor.YELLOW + ( Config.PromotBroadcast ? "する":"しない" ), programCode );
        Tools.Prt( p, ChatColor.WHITE + "牢獄グループ : " + ChatColor.YELLOW + Config.Prison, programCode );
        Tools.Prt( p, ChatColor.WHITE + "投獄期間     : " + ChatColor.YELLOW + Config.Penalty + "日", programCode );
        Tools.Prt( p, ChatColor.WHITE + "牢獄ジャンプ : " + ChatColor.YELLOW + ( Config.Imprisonment ? "する":"しない" ), programCode );
        if ( Config.Imprisonment ) {
            Tools.Prt( p, ChatColor.WHITE + "牢獄行き先", programCode );
            Tools.Prt( p, ChatColor.WHITE + "  world: " + ChatColor.YELLOW + Config.fworld, programCode );
            Tools.Prt( p, ChatColor.WHITE + "  x    : " + ChatColor.YELLOW + String.valueOf( Config.fx ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "  y    : " + ChatColor.YELLOW + String.valueOf( Config.fy ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "  z    : " + ChatColor.YELLOW + String.valueOf( Config.fz ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "  yaw  : " + ChatColor.YELLOW + String.valueOf( Config.fyaw ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "  pitch: " + ChatColor.YELLOW + String.valueOf( Config.fpitch ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "釈放行き先", programCode );
            Tools.Prt( p, ChatColor.WHITE + "  world: " + ChatColor.YELLOW + Config.rworld, programCode );
            Tools.Prt( p, ChatColor.WHITE + "  x    : " + ChatColor.YELLOW + String.valueOf( Config.rx ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "  y    : " + ChatColor.YELLOW + String.valueOf( Config.ry ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "  z    : " + ChatColor.YELLOW + String.valueOf( Config.rz ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "  yaw  : " + ChatColor.YELLOW + String.valueOf( Config.ryaw ), programCode );
            Tools.Prt( p, ChatColor.WHITE + "  pitch: " + ChatColor.YELLOW + String.valueOf( Config.rpitch ), programCode );
        }
        Tools.Prt( p, ChatColor.WHITE + "CitizenShip List : 昇格時間",programCode );
        for( String gn : Config.rankName ) {
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
            
            Tools.Prt( p, msg, programCode );
        }
        Tools.Prt( p, ChatColor.GREEN + "==========================", programCode );
    }
}
