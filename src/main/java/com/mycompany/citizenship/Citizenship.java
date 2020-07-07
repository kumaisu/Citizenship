/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import java.net.UnknownHostException;
import org.bukkit.Bukkit;
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
import com.mycompany.citizenship.tools.Rewards;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.config.ConfigManager;
import com.mycompany.citizenship.command.RankCommand;
import com.mycompany.citizenship.command.JailCommand;
import com.mycompany.citizenship.command.YellowCommand;
import com.mycompany.citizenship.database.Database;
import com.mycompany.citizenship.database.MySQLControl;
import com.mycompany.citizenship.database.PlayerData;
import com.mycompany.citizenship.database.ReasonData;
import com.mycompany.citizenship.database.YellowData;

/**
 *
 * @author sugichan
 */
public class Citizenship extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents( this, this );
        new ConfigManager( this );
        MySQLControl.connect();
        MySQLControl.TableUpdate();
        getCommand( "ranks" ).setExecutor( new RankCommand( this ) );
        getCommand( "jail" ).setExecutor( new JailCommand( this ) );
        getCommand( "yellow" ).setExecutor( new YellowCommand( this ) );
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
        //  MySQLControl.disconnect();
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
        Tools.Prt( "onPlayerLogin process", Tools.consoleMode.max, Config.programCode );

        RanksControl.CheckRank( player );

        //  Daily Rewards の判定
        Rewards.CheckRewards( player );

        //  Yellow Card 警告表示判定
        if ( player.hasPermission( "citizenship.yellow" ) ) { YellowData.CardLog( player, Database.logout ); }
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
        if ( Config.AutoDeop && ( !Config.OPName.contains( player.getName() ) ) && player.isOp() ) {
            Tools.Prt(
                ChatColor.YELLOW + "Temporary Player [" +
                ChatColor.AQUA + player.getName() +
                ChatColor.YELLOW + "] DEOP Success",
                Tools.consoleMode.full,
                Config.programCode
            );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "deop " + player.getName() );
        }
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
                YellowData.AddCard( player.getName(), message );
                PlayerData.addYellow( player.getUniqueId() );
                
                if ( PlayerData.GetSQL( player.getUniqueId() ) ) {
                    player.sendTitle(
                        ChatColor.RED + "なにかしようとしてますか？",
                        ChatColor.YELLOW + "イエローカード : " + ChatColor.AQUA + Database.yellow,
                        0, 300, 0
                    );
                    String msgLog = ChatColor.YELLOW + "Command Aleart : " + ChatColor.RED + player.getName() + " " + message;
                    Tools.Prt( msgLog, Tools.consoleMode.normal, Config.programCode );
                    Bukkit.getOnlinePlayers().stream().filter( ( p ) -> ( p.hasPermission( "citizenship.admin" ) ) ).forEachOrdered( ( p ) -> {
                        p.sendMessage( msgLog );
                    } );

                    if ( Config.Imprisonment ) {
                        if ( ( Config.AutoJail > 0 ) && ( Database.yellow >= Config.AutoJail ) ) {
                            Tools.Prt( player, ChatColor.RED + "Auto Jail to " + player.getName(), Tools.consoleMode.normal, Config.programCode );
                            String reason = "Exceeded the specified number of times";
                            int ReasonID = ReasonData.AddReason( player.getUniqueId(), reason, "Auto Jail" );
                            PlayerData.SetReasonID( player.getUniqueId(), ReasonID );
                            PlayerControl.toJail( player, ReasonID );
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}
