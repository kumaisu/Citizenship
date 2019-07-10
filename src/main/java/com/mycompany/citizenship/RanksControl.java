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
import org.bukkit.plugin.RegisteredServiceProvider;
import static org.bukkit.Bukkit.getServer;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.database.MySQLControl;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import static com.mycompany.citizenship.PlayerControl.toJail;
import static com.mycompany.citizenship.PlayerControl.outJail;
import static com.mycompany.citizenship.config.Config.programCode;
import net.milkbowl.vault.permission.Permission;

/**
 *
 * @author sugichan
 */
public class RanksControl {

    /**
     * 昇格
     *
     * @param player
     * @return 
     */
    public static boolean Promotion( Player player ) {
        Tools.Prt( "Promotion Process", Tools.consoleMode.full, programCode );
        String baseGroup = getGroup( player );

        if ( baseGroup.equals("") ) { return false; }

        try {
            String NewGroup = Config.rankName.get( Config.rankName.indexOf( baseGroup ) + 1 );
            String Cmd = "pex user " + player.getName() + " group set " + NewGroup;
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );

            String LevelupMessage = 
                ChatColor.YELLOW + player.getName() + " さんが " +
                ChatColor.AQUA + NewGroup +
                ChatColor.YELLOW + " に昇格しました";
            if ( Config.PromotBroadcast ) {
                LevelupMessage = "<鯖アナウンス> " + LevelupMessage;
                Bukkit.broadcastMessage( LevelupMessage );
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "discord broadcast " + LevelupMessage );
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
    public static boolean Demotion( Player player ){
        Tools.Prt( "Demotion Process", Tools.consoleMode.full, programCode );
        String baseGroup = getGroup( player );

        if ( baseGroup.equals("") ) { return false; }

        try {
            String Cmd = "pex user " + player.getName() + " group set " + Config.rankName.get( Config.rankName.indexOf( baseGroup ) -1 );
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
            MySQLControl DBRec = new MySQLControl();
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
    public static String getGroup( Player player ) {
        Permission perm = null;
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration( net.milkbowl.vault.permission.Permission.class );
        if (permissionProvider != null) {
            perm = permissionProvider.getProvider();
        }
        if ( perm == null ) {
            Tools.Prt( "Cant get Group", programCode );
            return "";
        }

        for ( String StrItem1 : perm.getPlayerGroups( player ) ) Tools.Prt( "{" + StrItem1 + "}", Tools.consoleMode.max, programCode );

        String NowGroup = perm.getPlayerGroups( player )[0];
        Tools.Prt( "NowGroup [" + NowGroup + "]", Tools.consoleMode.full, programCode );
        return NowGroup;
    }

    /**
     * グループ設定
     *
     * @param player
     * @param newGroup
     * @return 
     */
    public static boolean setGroup( Player player, String newGroup ) {
        try {
            String Cmd = "pex user " + player.getName() + " group set " + newGroup;
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
            //  現状、牢獄処理しか使っていないので、ここでオフセットをリセットしている
            MySQLControl DBRec = new MySQLControl();
            DBRec.SetOffsetToSQL( player.getUniqueId(), player.getStatistic( Statistic.PLAY_ONE_MINUTE ) );
            return true;
        } catch( ArrayIndexOutOfBoundsException e ) {
            return false;
        }
    }

    /**
     * 具体的に、昇格・降格を判断処理するメソッド
     *
     * @param player
     * @return 
     */
    public static boolean CheckRank( Player player ) {
        int allTime = ( int ) Math.round( player.getStatistic( Statistic.PLAY_ONE_MINUTE ) * 0.05 / 60 /60 );
        Tools.Prt( "PlayTime = " + Float.toString( ( float ) player.getStatistic( Statistic.PLAY_ONE_MINUTE ) ), Tools.consoleMode.full, programCode );
        Tools.Prt( player,
            ChatColor.YELLOW + "貴方の通算接続時間は " +
            ChatColor.AQUA + allTime +
            ChatColor.YELLOW + " 時間です" ,
            Tools.consoleMode.normal,
            programCode
        );

        //
        //  DBからデータ取得、無ければ初期化および新規登録
        //
        MySQLControl DBRec = new MySQLControl();
        if ( !DBRec.GetSQL( player.getUniqueId() ) ) {
            Tools.Prt( "New Database Entry", Tools.consoleMode.full, programCode );
            DBRec.AddSQL( player );
        }

        int progress = Utility.dateDiff( MySQLControl.logout, new Date() );
        int checkHour = ( int ) Math.round( ( player.getStatistic( Statistic.PLAY_ONE_MINUTE ) - MySQLControl.offset ) * 0.05 / 60 / 60 );

        String NowGroup = getGroup( player );

        //
        //  不在投獄時処理
        //
        if ( MySQLControl.jail == 1 ) {
            toJail( player, "不在時処理されました" );
            DBRec.SetJailToSQL( player.getUniqueId(), 0 );
            return true;
        }

        //
        //  ペナルティユーザーに対する処理
        //
        if ( NowGroup.equals( Config.Prison ) ) {
            if ( ( Config.Penalty > 0 ) && ( progress > Config.Penalty ) ) { return outJail( player ); }
            return false;
        }

        //
        //  降格判定
        //
        if ( Config.demotion != 0 ) {
            Tools.Prt( "Logout date = " + MySQLControl.logout.toString(), Tools.consoleMode.full, programCode);
            Tools.Prt( "Diff Date : " + progress + " 日", Tools.consoleMode.full, programCode );
            if ( progress > Config.demotion ) {
                Demotion( player );
                return true;
            }
        }

        //
        //  経過時間によるユーザーの昇格処理
        //
        Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ) + " : " + checkHour, Tools.consoleMode.full, programCode );
        if ( Config.rankTime.get( NowGroup ) == null ) { return false; }
        if ( ( Config.rankTime.get( NowGroup ) > 0 ) && ( Config.rankTime.get( NowGroup ) < checkHour ) ) {
            Promotion( player );
            return true;
        }

        return false;
    }
}
