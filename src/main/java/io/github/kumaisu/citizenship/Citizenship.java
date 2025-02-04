package io.github.kumaisu.citizenship;

import java.net.UnknownHostException;
import java.sql.SQLException;

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
import io.github.kumaisu.citizenship.Lib.Tools;
import io.github.kumaisu.citizenship.tools.Rewards;
import io.github.kumaisu.citizenship.config.Config;
import io.github.kumaisu.citizenship.config.ConfigManager;
import io.github.kumaisu.citizenship.command.RankCommand;
import io.github.kumaisu.citizenship.command.JailCommand;
import io.github.kumaisu.citizenship.command.YellowCommand;
import io.github.kumaisu.citizenship.command.GeneralCommand;
import io.github.kumaisu.citizenship.database.Database;
import io.github.kumaisu.citizenship.database.MySQLControl;
import io.github.kumaisu.citizenship.database.PlayerData;
import io.github.kumaisu.citizenship.database.ReasonData;
import io.github.kumaisu.citizenship.database.YellowData;

import static io.github.kumaisu.citizenship.config.Config.programCode;

/**
 * @author NineTailedFox
 */
public class Citizenship extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents( this, this );
        new ConfigManager( this );
        try {
            MySQLControl.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        MySQLControl.TableUpdate();
        getCommand( "ranks" ).setExecutor( new RankCommand( this ) );
        getCommand( "jail" ).setExecutor( new JailCommand( this ) );
        getCommand( "yellow" ).setExecutor( new YellowCommand( this ) );
        getCommand( "fly" ).setExecutor( new GeneralCommand( this ) );
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
     */
    @EventHandler( priority = EventPriority.HIGH )
    public void onPlayerLogin( PlayerJoinEvent event ) throws UnknownHostException {
        Player player = event.getPlayer();
        Tools.Prt( "onPlayerLogin process", Tools.consoleMode.max, programCode );

        RanksControl.CheckRank( player );

        //  Daily Rewards の判定
        Rewards.CheckRewards( player );

        //  Yellow Card 警告表示判定
        if ( player.hasPermission( "citizenship.yellow" ) ) { YellowData.CardLog( player, Database.logout ); }
    }

    /**
     * プレイヤーがログアウトした時に発生するイベント
     */
    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();
        PlayerData.SetLogoutToSQL( player.getUniqueId() );
        if ( Config.AutoDeop && ( !Config.OPName.contains( player.getName() ) ) && player.isOp() ) {
            Tools.Prt(
                ChatColor.YELLOW + "Temporary Player [" +
                ChatColor.AQUA + player.getName() +
                ChatColor.YELLOW + "] DEOP Success",
                Tools.consoleMode.full,
                programCode
            );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "deop " + player.getName() );
        }
    }

    /**
     * 違法コマンドを検閲するためのイベントキャッチ
     */
    @EventHandler
    public void onPreprocess( PlayerCommandPreprocessEvent event ) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if ( ( player == null ) ||
                ( player.hasPermission( "citizenship.admin" ) ) ||
                ( player.hasPermission( "citizenship.AlertPass" ) ) )
        {
            return;
        }

        for ( String key : Config.Alert ) {
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
                    Tools.Prt( msgLog, Tools.consoleMode.normal, programCode );
                    Bukkit.getOnlinePlayers().stream().filter( ( p ) -> ( p.hasPermission( "citizenship.admin" ) ) ).forEachOrdered( ( p ) -> {
                        p.sendMessage( msgLog );
                    } );

                    if ( Config.Imprisonment ) {
                        if ( ( Config.AutoJail > 0 ) && ( Database.yellow >= Config.AutoJail ) ) {
                            Tools.Prt( player, ChatColor.RED + "Auto Jail to " + player.getName(), Tools.consoleMode.normal, programCode );
                            String reason = "Exceeded the specified number of times";
                            int ReasonID = ReasonData.AddReason( player.getUniqueId(), reason, "Auto Jail" );
                            PlayerData.SetReasonID( player.getUniqueId(), ReasonID );
                            PlayerControl.toJail( player, ReasonID );
                        }
                    }
                }
            }
        }
    }
}
