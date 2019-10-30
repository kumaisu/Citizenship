/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import java.net.UnknownHostException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.citizenship.config.ConfigManager;
import com.mycompany.citizenship.command.RankCommand;
import com.mycompany.citizenship.command.JailCommand;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.database.MySQLControl;
import com.mycompany.citizenship.database.PlayerData;
import static com.mycompany.citizenship.config.Config.programCode;
import org.bukkit.Bukkit;

/**
 *
 * @author sugichan
 */
public class Citizenship extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents( this, this );
        ConfigManager config = new ConfigManager( this );
        MySQLControl.connect();
        MySQLControl.TableUpdate();
        getCommand( "ranks" ).setExecutor( new RankCommand( this ) );
        getCommand( "jail" ).setExecutor( new JailCommand( this ) );
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
        MySQLControl.disconnect();
    }

    @Override
    public void onLoad() {
        super.onLoad(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * プレイヤーがログインを成功すると発生するイベント
     * ここでプレイヤーに対して、様々な処理を実行する
     *
     * @param event
     * @throws UnknownHostException
     */
    @EventHandler( priority = EventPriority.HIGH )
    public void onPlayerLogin( PlayerJoinEvent event ) throws UnknownHostException {
        Player player = event.getPlayer();
        Tools.Prt( "onPlayerLogin process", Tools.consoleMode.max, programCode );
        RanksControl.CheckRank( player );
    }

    /**
     * プレイヤーがログアウトした時に発生するイベント
     *
     * @param event
     */
    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();
        PlayerData.SetLogoutToSQL( player.getUniqueId() );
        PlayerData.SetTickTimeToSQL( player.getUniqueId(), TickTime.get( player ) );
    }

    /**
     * 違法コマンドを検閲するためのイベントキャッチ
     *
     * @param event
     * @return 
     */
    @EventHandler
    public boolean onPreprocess( PlayerCommandPreprocessEvent event ) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if ( ( player == null ) || ( player.hasPermission( "citizenship.admin" ) ) ) { return false; }

        for ( String key : Config.Aleart ) {
            if ( message.toLowerCase().contains( key.toLowerCase() ) ) {
                player.sendTitle( ChatColor.RED + "なにかしようとしてますか？", ChatColor.YELLOW + "イエローカード" + ChatColor.AQUA + "( 1 )", 0, 300, 0 );
                String msgLog = Utility.StringBuild( ChatColor.RED.toString(), player.getName(), " ", message );
                Tools.Prt( msgLog, Tools.consoleMode.full, programCode );
                Bukkit.getOnlinePlayers().stream().filter( ( p ) -> ( p.hasPermission( "citizenship.admin" ) || p.isOp() ) ).forEachOrdered( ( p ) -> {
                    p.sendMessage( ChatColor.YELLOW + "Command Aleart : " + msgLog );
                } );
                return true;
            }
        }
        return false;
    }
}
