/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import net.milkbowl.vault.permission.Permission;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import static org.bukkit.Bukkit.getServer;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.database.Database;
import com.mycompany.citizenship.database.PlayerData;
import com.mycompany.citizenship.database.ReasonData;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class RanksControl {

    /**
     * 昇格
     *
     * @param player
     * @param target
     * @return 
     */
    public static boolean Promotion( Player player, Player target ) {
        Tools.Prt( "Promotion Process", Tools.consoleMode.max, programCode );
        if ( target == null ) target = player;
        String baseGroup = getGroup( target );

        if ( baseGroup.equals( "" ) || baseGroup == null ) {
            Tools.Prt( player, "グループ設定がありません", Tools.consoleMode.max, programCode );
            return false;
        }
        if ( Config.rankName.contains( baseGroup ) == false ) {
            Tools.Prt( player, ChatColor.BLUE + "ランク制御対象外グループです", Tools.consoleMode.max, programCode );
            return false;
        }
        if ( Config.rankTime.get( baseGroup ).get( "E" ) != null ) {
            Tools.Prt( player, ChatColor.LIGHT_PURPLE + "これ以上の昇格はできません", Tools.consoleMode.max, programCode );
            return false;
        }

        try {
            String NewGroup = Config.rankName.get( Config.rankName.indexOf( baseGroup ) + 1 );
            String Cmd = "pex user " + target.getName() + " group set " + NewGroup;
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );

            String LevelupMessage = 
                ChatColor.YELLOW + target.getName() + " さんが " +
                ChatColor.AQUA + NewGroup +
                ChatColor.YELLOW + " に昇格しました";

            if ( Config.PromotBroadcast ) {
                LevelupMessage = "<鯖アナウンス> " + LevelupMessage;
                Bukkit.broadcastMessage( LevelupMessage );
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "discord broadcast " + LevelupMessage );
            } else {
                Tools.Prt( target, LevelupMessage, Tools.consoleMode.normal, programCode );
            }
            Tools.Prt( "Player new Group is " + NewGroup, Tools.consoleMode.full, programCode );
            return true;
        } catch( ArrayIndexOutOfBoundsException e ) {
            return false;
        }
    }

    /**
     * 降格
     *
     * @param player
     * @param target
     * @return 
     */
    public static boolean Demotion( Player player, Player target ){
        Tools.Prt( "Demotion Process", Tools.consoleMode.max, programCode );
        if ( target == null ) target = player;
        String baseGroup = getGroup( target );

        if ( baseGroup.equals( "" ) || baseGroup == null ) {
            Tools.Prt( player, "グループ設定がありません", Tools.consoleMode.max, programCode );
            return false;
        }
        if ( Config.rankName.contains( baseGroup ) == false ) {
            Tools.Prt( player, "ランク制御対象外グループです", Tools.consoleMode.max, programCode );
            return false;
        }
        if ( Config.rankName.indexOf( baseGroup ) == 0 ) {
            if ( player != target ) {
                Tools.Prt( player, "これ以下へ降格はできません", Tools.consoleMode.max, programCode );
            }
            return false;
        }

        try {
            String NewGroup = Config.rankName.get( Config.rankName.indexOf( baseGroup ) - 1 );
            String Cmd = "pex user " + target.getName() + " group set " + NewGroup;
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
            PlayerData.SetOffsetToSQL( target.getUniqueId(), TickTime.get( target ) );
            PlayerData.SetBaseDateToSQL( target.getUniqueId() );

            String LeveldownMessage = 
                ChatColor.YELLOW + target.getName() + " さんを " +
                ChatColor.AQUA + NewGroup +
                ChatColor.YELLOW + " に降格しました";

            if ( Config.DemotBroadcast ) {
                LeveldownMessage = "<鯖アナウンス> " + LeveldownMessage;
                Bukkit.broadcastMessage( LeveldownMessage );
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), "discord broadcast " + LeveldownMessage );
            } else {
                Tools.Prt( target, LeveldownMessage, Tools.consoleMode.normal, programCode );
            }
            Tools.Prt( "Player new Group is " + NewGroup, Tools.consoleMode.full, programCode );
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

        for ( String StrItem1 : perm.getPlayerGroups( player ) ) Tools.Prt( "Have Gr. {" + StrItem1 + "}", Tools.consoleMode.max, programCode );

        String NowGroup = perm.getPlayerGroups( player )[0];
        Tools.Prt( "NowGroup [" + NowGroup + "]", Tools.consoleMode.max, programCode );
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
            PlayerData.SetOffsetToSQL( player.getUniqueId(), TickTime.get( player ) );
            PlayerData.SetBaseDateToSQL( player.getUniqueId() );
            return true;
        } catch( ArrayIndexOutOfBoundsException e ) {
            return false;
        }
    }

    /**
     * 具体的に、昇格・降格を判断処理するメソッド
     *
     * @param player
     */
    public static void CheckRank( Player player ) {
        int BaseTick = TickTime.get( player );
        int allTime = ( int ) Math.round( BaseTick * 0.05 / 60 / 60 );
        Tools.Prt( "PlayTime = " + Float.toString( ( float ) BaseTick ), Tools.consoleMode.full, programCode );
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
        if ( !PlayerData.GetSQL( player.getUniqueId() ) ) {
            Tools.Prt( "New Database Entry", Tools.consoleMode.full, programCode );
            PlayerData.AddSQL( player );
        }

        //
        //  不在投獄時処理
        //
        if ( Database.jail == 1 ) {
            PlayerControl.toJail( player, Database.ReasonID );
            PlayerData.SetJailToSQL( player.getUniqueId(), 0 );
            return;
        }

        int progress = Utility.dateDiff( Database.logout, new Date() );
        int checkHour = ( int ) Math.round( ( BaseTick - Database.offset ) * 0.05 / 60 / 60 );

        String NowGroup = getGroup( player );
        
        //
        //  グループ表示 => コンソールへ
        //
        Tools.Prt( ChatColor.GREEN + player.getName() + " [" + NowGroup + "] Login", Tools.consoleMode.normal, programCode );

        //
        //  経過時間によるユーザーの昇格処理
        //
        if ( NowGroup.equals( "" ) || Config.rankTime.get( NowGroup ) == null ) {
            Tools.Prt( ChatColor.GOLD + "チェック対象グループではありません", Tools.consoleMode.full, programCode );
            return;
        }

        //
        //  ペナルティユーザーに対する処理
        //
        if ( Database.ReasonID != 0 ) {
            if ( ( Config.Penalty > 0 ) && ( progress > Config.Penalty ) ) {
                PlayerControl.outJail( player );
            } else {
                ReasonData.GetReason( Database.ReasonID );
                Tools.Prt( player, ChatColor.RED + "投獄理由 : " + Database.Reason + " By." + Database.enforcer, Tools.consoleMode.normal, programCode );
            }
        }

        //
        //  降格判定
        //
        if ( Config.demotion ) {
            Tools.Prt( "Logout date = " + Database.logout.toString(), Tools.consoleMode.full, programCode);
            Tools.Prt( "Elapsed Date : " + progress + " 日", Tools.consoleMode.max, programCode );
            int days = ( Config.demot.get( NowGroup ) == null ? Config.demotionDefault : Config.demot.get( NowGroup ) );
            Tools.Prt( "Config Date  : " + days + " 日", Tools.consoleMode.max, programCode );
            if ( ( days > 0 ) && ( progress > days ) ) {
                Demotion( player, null );
                return;
            } else Tools.Prt( ChatColor.YELLOW + "No demotion process", Tools.consoleMode.full, programCode );
        }

        //
        //  昇格判定
        //
        if ( Config.rankTime.get( NowGroup ).get( "E" ) == null ) {
            boolean UpCheck = false;
            if ( Config.rankTime.get( NowGroup ).get( "H" ) != null ) {
                Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ).get( "H" ) + " : " + checkHour + " 時間", Tools.consoleMode.full, programCode );
                UpCheck = ( Config.rankTime.get( NowGroup ).get( "H" ) <= checkHour );
            }
            if ( Config.rankTime.get( NowGroup ).get( "D" ) != null ) {
                int checkDate = Utility.dateDiff( Database.basedate, new Date() );
                Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ).get( "D" ) + " : " + checkDate + " 日", Tools.consoleMode.full, programCode );
                UpCheck = ( Config.rankTime.get( NowGroup ).get( "D" ) <= checkDate );
            }
            if ( UpCheck ) {
                Tools.Prt( ChatColor.YELLOW + "Player promotion!!", Tools.consoleMode.full, programCode);
                Promotion( player, null );
            }
        } else Tools.Prt( ChatColor.AQUA + "This player is Last Group", Tools.consoleMode.full, programCode );
    }
}
