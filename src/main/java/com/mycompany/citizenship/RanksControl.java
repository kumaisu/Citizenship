/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.database.MySQLControl;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import static com.mycompany.citizenship.config.Config.programCode;
import net.milkbowl.vault.permission.Permission;

/**
 *
 * @author sugichan
 */
public class RanksControl {

    private final Plugin plugin;
    private final MySQLControl DBRec;

    public RanksControl( Plugin plugin ){
        this.plugin = plugin;
        DBRec = new MySQLControl();
    }

    /**
     * 昇格
     *
     * @param player
     * @return 
     */
    public boolean Promotion( Player player ) {
        Tools.Prt( "Promotion Process", Tools.consoleMode.full, programCode );
        String baseGroup = getGroup( player );
        
        if ( baseGroup.equals("") ) { return false; }

        try {
            String NewGroup = Config.rankName.get( Config.rankName.indexOf( baseGroup ) + 1 );
            String Cmd = "pex user " + player.getDisplayName() + " group set " + NewGroup;
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.full, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );

            String LevelupMessage = 
                ChatColor.YELLOW + player.getDisplayName() + "さんが " +
                ChatColor.AQUA + NewGroup +
                ChatColor.YELLOW + " に昇格しました";
            if ( Config.PromotBroadcast ) {
                Bukkit.broadcastMessage( "<鯖アナウンス> " + LevelupMessage );
            } else {
                Tools.Prt( player, LevelupMessage, Tools.consoleMode.normal, programCode );
            }

            return true;
        } catch( ArrayIndexOutOfBoundsException e ) {
            return false;
        }
    }

    /**
     * 降格
     *
     * @param player
     * @return 
     */
    public boolean Demotion( Player player ){
        Tools.Prt( "Demotion Process", Tools.consoleMode.full, programCode );
        String baseGroup = getGroup( player );

        if ( baseGroup.equals("") ) { return false; }

        try {
            String Cmd = "pex user " + player.getDisplayName() + " group set " + Config.rankName.get( Config.rankName.indexOf( baseGroup ) -1 );
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.full, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
            DBRec.SetOffsetToSQL( player.getUniqueId(), player.getStatistic( Statistic.PLAY_ONE_MINUTE ) );
            return true;
        } catch ( ArrayIndexOutOfBoundsException e ) {
            return false;
        }
    }

    /**
     * グループ取得
     *
     * @param player
     * @return 
     */
    public String getGroup( Player player ) {
        Permission perm = null;
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration( net.milkbowl.vault.permission.Permission.class );
        if (permissionProvider != null) {
            perm = permissionProvider.getProvider();
        }
        if ( perm == null ) {
            Tools.Prt( "Cant get Group", programCode );
            return "";
        }
        
        for ( String StrItem1 : perm.getPlayerGroups( player ) ) Tools.Prt( "{" + StrItem1 + "}", Tools.consoleMode.full, programCode );

        String NowGroup = perm.getPlayerGroups( player )[0];
        Tools.Prt( "NowGroup [" + NowGroup + "]", Tools.consoleMode.full, programCode );
        return NowGroup;
    }

    /**
     * 具体的に、昇格・降格を判断処理するメソッド
     *
     * @param player
     * @return 
     */
    public boolean CheckRank( Player player ) {
        Tools.Prt( "PlayTime = " + Float.toString( ( float ) player.getStatistic( Statistic.PLAY_ONE_MINUTE ) ), Tools.consoleMode.full, programCode );

        //
        //  DBからデータ取得、無ければ初期化および新規登録
        //
        if ( !DBRec.GetSQL( player.getUniqueId() ) ) {
            Tools.Prt( "New Database Entry", Tools.consoleMode.full, programCode );
            DBRec.AddSQL( player );
        }

        int progress = Utility.dateDiff( MySQLControl.logout, new Date() );
        int checkHour = ( int ) Math.round( ( player.getStatistic( Statistic.PLAY_ONE_MINUTE ) - MySQLControl.offset ) * 0.05 / 60 / 60 );
        String NowGroup = getGroup( player );
        
        Tools.Prt( player,
            ChatColor.YELLOW + "貴方の通算接続時間は " +
            ChatColor.AQUA + checkHour +
            ChatColor.YELLOW + " 時間です" ,
            Tools.consoleMode.normal,
            programCode
        );
        if ( Config.rankTime.get( NowGroup ) == null ) { return false; }

        //
        //  降格判定
        //
        if ( Config.demotion != 0 ) {
            Tools.Prt( "Diff Date : " + progress, Tools.consoleMode.full, programCode );
            if ( progress > Config.demotion ) {
                Demotion( player );
                return true;
            }
        }

        //
        //  ペナルティユーザーに対する処理
        //
        if ( NowGroup.equals( Config.Prison ) ) {
            if ( progress > Config.Penalty ) {
                //  リセット
                String Cmd = "pex user " + player.getDisplayName() + " group set " + Config.rankName.get( 0 );
                Tools.Prt( "Command : " + Cmd, Tools.consoleMode.full, programCode );
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
                DBRec.SetOffsetToSQL( player.getUniqueId(), player.getStatistic( Statistic.PLAY_ONE_MINUTE ) );
                return true;
            }
            return false;
        }

        //
        //  経過時間によるユーザーの昇格処理
        //
        Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ) + " : " + checkHour, Tools.consoleMode.full, programCode );
        if ( ( Config.rankTime.get( NowGroup ) > 0 ) && ( Config.rankTime.get( NowGroup ) < checkHour ) ) {
            Promotion( player );
            return true;
        }

        return false;
    }
}
