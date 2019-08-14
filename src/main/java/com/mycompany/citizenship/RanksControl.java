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

        if ( baseGroup.equals( "" ) ) { return false; }
        if ( Config.rankName.contains( baseGroup ) == false ) {
            Tools.Prt( player, ChatColor.BLUE + "ランク制御対象外グループです", Tools.consoleMode.full, programCode );
            return false;
        }
        if ( Config.rankTime.get( baseGroup ).get( "E" ) != null ) {
            Tools.Prt( player, ChatColor.LIGHT_PURPLE + "これ以上、昇格はできません", Tools.consoleMode.full, programCode );
            return false;
        }

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
        if ( Config.rankName.contains( baseGroup ) == false ) {
            Tools.Prt( player, "ランク制御対象外グループです", Tools.consoleMode.full, programCode );
            return false;
        }
        if ( Config.rankName.indexOf( baseGroup ) == 0 ) {
            Tools.Prt( player, "これ以上、降格はできません", Tools.consoleMode.full, programCode );
            return false;
        }

        try {
            String Cmd = "pex user " + player.getName() + " group set " + Config.rankName.get( Config.rankName.indexOf( baseGroup ) - 1 );
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
            MySQLControl DBRec = new MySQLControl();
            DBRec.SetOffsetToSQL( player.getUniqueId(), player.getStatistic( Statistic.PLAY_ONE_MINUTE ) );
            DBRec.SetBaseDateToSQL( player.getUniqueId() );
            DBRec.close();
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
            //  本来は設定したランクに応じて再計算が必要になる
            MySQLControl DBRec = new MySQLControl();
            DBRec.SetOffsetToSQL( player.getUniqueId(), player.getStatistic( Statistic.PLAY_ONE_MINUTE ) );
            DBRec.SetBaseDateToSQL( player.getUniqueId() );
            DBRec.close();
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

        //
        //  不在投獄時処理
        //
        if ( MySQLControl.jail == 1 ) {
            toJail( player, "不在時処理されました" );
            DBRec.SetJailToSQL( player.getUniqueId(), 0 );
            DBRec.close();
            return true;
        }

        int progress = Utility.dateDiff( MySQLControl.logout, new Date() );
        int checkHour = ( int ) Math.round( ( player.getStatistic( Statistic.PLAY_ONE_MINUTE ) - MySQLControl.offset ) * 0.05 / 60 / 60 );

        String NowGroup = getGroup( player );

        //
        //  ペナルティユーザーに対する処理
        //
        if ( NowGroup.equals( Config.Prison ) ) {
            if ( ( Config.Penalty > 0 ) && ( progress > Config.Penalty ) ) { return outJail( player ); }
            DBRec.close();
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
                DBRec.close();
                return true;
            }
        }

        //
        //  経過時間によるユーザーの昇格処理
        //
        if ( Config.rankTime.get( NowGroup ) == null ) {
            DBRec.close();
            return false;
        }

        if ( Config.rankTime.get( NowGroup ).get( "E" ) == null ) {
            boolean UpCheck = false;
            if ( Config.rankTime.get( NowGroup ).get( "H" ) != null ) {
                Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ).get( "H" ) + " : " + checkHour + " 時間", Tools.consoleMode.full, programCode );
                UpCheck = ( Config.rankTime.get( NowGroup ).get( "H" ) <= checkHour );
            }
            if ( Config.rankTime.get( NowGroup ).get( "D" ) != null ) {
                int checkDate = Utility.dateDiff( MySQLControl.basedate, new Date() );
                Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ).get( "D" ) + " : " + checkDate + " 日", Tools.consoleMode.full, programCode );
                UpCheck = ( Config.rankTime.get( NowGroup ).get( "D" ) <= checkDate );
            }
            if ( UpCheck ) {
                Tools.Prt( ChatColor.YELLOW + "Player promotion!!", Tools.consoleMode.full, programCode);
                Promotion( player );
                DBRec.close();
                return true;
            }
        } else Tools.Prt( "This player is Last Group", Tools.consoleMode.full, programCode );

        DBRec.close();
        return false;
    }
}
