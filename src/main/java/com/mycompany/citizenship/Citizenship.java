/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import java.net.UnknownHostException;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.citizenship.config.ConfigManager;
import com.mycompany.citizenship.command.RankCommand;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class Citizenship extends JavaPlugin implements Listener {

    public ConfigManager config;
    private RanksControl RankC;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents( this, this );
        RankC = new RanksControl( this );
        config = new ConfigManager( this );
        getCommand( "ranks" ).setExecutor( new RankCommand( this ) );
    }

    @Override
    public void onDisable() {
        super.onDisable(); //To change body of generated methods, choose Tools | Templates.
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
    @EventHandler
    public void onPlayerLogin( PlayerJoinEvent event ) throws UnknownHostException {
        Player player = event.getPlayer();

        Tools.Prt( "onPlayerLogin process", Tools.consoleMode.full, programCode );
        Tools.Prt( "PlayTime = " + Float.toString( ( float ) player.getStatistic( Statistic.PLAY_ONE_MINUTE ) ), Tools.consoleMode.full, programCode );

        RankC.CheckRank( player );
    }
}
