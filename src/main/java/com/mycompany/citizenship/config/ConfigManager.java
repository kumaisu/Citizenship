/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.config;

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

    private final Plugin plugin;
    private FileConfiguration config = null;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        Tools.entryDebugFlag( programCode, Tools.consoleMode.none );
        Tools.Prt( "Config Loading now...", programCode );
        load();
    }

    /*
     * 設定をロードします
     */
    public void load() {
        // 設定ファイルを保存
        plugin.saveDefaultConfig();
        if (config != null) { // configが非null == リロードで呼び出された
            Tools.Prt( "Config Reloading now...", programCode );
            plugin.reloadConfig();
        }
        config = plugin.getConfig();

        Config.host = config.getString( "mysql.host" );
        Config.port = config.getString( "mysql.port" );
        Config.database = config.getString( "mysql.database" );
        Config.username = config.getString( "mysql.username" );
        Config.password = config.getString( "mysql.password" );

        Tools.consoleMode DebugFlag;
        try {
            DebugFlag = Tools.consoleMode.valueOf( config.getString( "Debug" ) );
        } catch( IllegalArgumentException e ) {
            Tools.Prt( ChatColor.RED + "Config Debugモードの指定値が不正なので、normal設定にしました", programCode );
            DebugFlag = Tools.consoleMode.normal;
        }
        Tools.entryDebugFlag( programCode, DebugFlag );
    }

    public void Status( Player p ) {
        Tools.consoleMode consolePrintFlag = ( ( p == null ) ? Tools.consoleMode.none:Tools.consoleMode.stop );
        Tools.Prt( p, ChatColor.GREEN + "=== LoginContrl Status ===", consolePrintFlag, programCode );
        Tools.Prt( p, ChatColor.WHITE + "Degub Mode : " + ChatColor.YELLOW + Tools.consoleFlag.get( programCode ).toString(), consolePrintFlag, programCode );
        Tools.Prt( p, ChatColor.WHITE + "Mysql : " + ChatColor.YELLOW + Config.host + ":" + Config.port, consolePrintFlag, programCode );
        Tools.Prt( p, ChatColor.WHITE + "DB Name : " + ChatColor.YELLOW + Config.database, consolePrintFlag, programCode );
        Tools.Prt( p, ChatColor.GREEN + "==========================", consolePrintFlag, programCode );
    }
}
