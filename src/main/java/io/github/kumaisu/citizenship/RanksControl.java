package io.github.kumaisu.citizenship;

import java.util.Date;

import io.github.kumaisu.citizenship.tools.Discord;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import io.github.kumaisu.citizenship.Lib.Tools;
import io.github.kumaisu.citizenship.Lib.Utility;
import io.github.kumaisu.citizenship.config.Config;
import io.github.kumaisu.citizenship.database.Database;
import io.github.kumaisu.citizenship.database.PlayerData;
import io.github.kumaisu.citizenship.database.ReasonData;
import static io.github.kumaisu.citizenship.config.Config.programCode;

/**
 * @author NineTailedFox
 */
public class RanksControl {

    /**
     * 昇格
     * @param player    処理プレイヤー
     * @param target    対象プレイヤー
     * @return          成功可否
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
            String Cmd = "lp user " + target.getName() + " group set " + NewGroup;
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );

            String LevelupMessage = 
                ChatColor.YELLOW + target.getName() + " さんが " +
                ChatColor.AQUA + NewGroup +
                ChatColor.YELLOW + " に昇格しました";

            if ( Config.PromotBroadcast ) {
                LevelupMessage = "<鯖アナウンス> " + LevelupMessage;
                Bukkit.broadcastMessage( LevelupMessage );
                Discord.sendMessage( Config.WebHookURL, "Level", LevelupMessage );
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
     * @param player    処理プレイヤー
     * @param target    対象プレイヤー
     * @return          成功可否
     */
    public static boolean Demotion( Player player, Player target ){
        Tools.Prt( "Demotion Process", Tools.consoleMode.max, programCode );
        if ( target == null ) target = player;
        String baseGroup = getGroup( target );

        if ( baseGroup.equals( "" ) || baseGroup == null ) {
            Tools.Prt( player, "グループ設定がありません", Tools.consoleMode.max, programCode );
            return false;
        }
        if ( Config.demot.containsKey( baseGroup ) == false ) {
            Tools.Prt( player, "降格対象外グループです", Tools.consoleMode.max, programCode );
            return false;
        }
        if ( Config.rankName.indexOf( baseGroup ) == 0 ) {
            if ( player != target ) {
                Tools.Prt( player, "これ以上の降格はできません", Tools.consoleMode.max, programCode );
            }
            return false;
        }

        try {
            String NewGroup = Config.rankName.get( Config.rankName.indexOf( baseGroup ) - 1 );
            String Cmd = "lp user " + target.getName() + " group set " + NewGroup;
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
            PlayerData.SetOffsetTickToSQL( target.getUniqueId(), TickTime.get( target ) );

            String LeveldownMessage = 
                ChatColor.YELLOW + target.getName() + " さんを " +
                ChatColor.AQUA + NewGroup +
                ChatColor.YELLOW + " に降格しました";

            if ( Config.DemotBroadcast ) {
                LeveldownMessage = "<鯖アナウンス> " + LeveldownMessage;
                Bukkit.broadcastMessage( LeveldownMessage );
                Discord.sendMessage( Config.WebHookURL, "Level", LeveldownMessage );
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
     * @param player    処理プレイヤー
     * @return          現在のグループ名（称号名）
     */
    public static String getGroup( Player player ) {
        String NowGroup = "Unknown";
        UserManager userManager = LuckPermsProvider.get().getUserManager();
        User user = userManager.loadUser( player.getUniqueId() ).join();
        if (user != null) {
            NowGroup = user.getPrimaryGroup();
            Tools.Prt( player.getName() + "のグループ: " + NowGroup, Tools.consoleMode.max, programCode );
        } else {
            Tools.Prt( "ユーザーが見つかりません: " + player.getName(), Tools.consoleMode.max, programCode );
        }
        return NowGroup;
    }

    /**
     * グループ設定
     * @param player    対象プレイヤー
     * @param newGroup  登録グループ名（称号名）
     * @return          成功可否
     */
    public static boolean setGroup( Player player, String newGroup ) {
        try {
            String Cmd = "lp user " + player.getName() + " group set " + newGroup;
            Tools.Prt( "Command : " + Cmd, Tools.consoleMode.max, programCode );
            Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
            return true;
        } catch( ArrayIndexOutOfBoundsException e ) {
            return false;
        }
    }

    /**
     * 具体的に、昇格・降格を判断処理するメソッド
     * @param player
     */
    public static void CheckRank( Player player ) {
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

        int BaseTick = TickTime.get( player );
        int totalMin = ( int ) Math.round( ( BaseTick - Database.baseTick ) * 0.05 / 60 );
        int totalHour = ( int ) Math.round( ( BaseTick - Database.baseTick ) * 0.05 / 60 / 60 );
        int totalDate = ( int ) Math.round( ( BaseTick - Database.baseTick ) * 0.05 / 60 / 60 / 24 );

        Tools.Prt( "Database.basedate　: " + Database.basedate, Tools.consoleMode.max, programCode );
        Tools.Prt( "Database.logout    : " + Database.logout, Tools.consoleMode.max, programCode );
        Tools.Prt( "初Login時Tick : " + Database.baseTick, Tools.consoleMode.max, programCode );
        Tools.Prt( "現時点のTick   : " + BaseTick, Tools.consoleMode.max, programCode );
        int progress = Utility.dateDiff( Database.logout, new Date() );

        Tools.Prt( ChatColor.GREEN + "CheckMin = " + ChatColor.AQUA + totalMin + ChatColor.GREEN + " minutes", Tools.consoleMode.max, programCode );
        Tools.Prt( ChatColor.GREEN + "CheckHour = " + ChatColor.AQUA + totalHour + ChatColor.GREEN + " hour", Tools.consoleMode.max, programCode );
        Tools.Prt( ChatColor.GREEN + "CheckDate = " + ChatColor.AQUA + totalDate + ChatColor.GREEN + " days", Tools.consoleMode.max, programCode );
        Tools.Prt( ChatColor.GREEN + "progress = " + ChatColor.YELLOW + Database.logout + ChatColor.GREEN + " - " + ChatColor.AQUA + progress + ChatColor.GREEN + " days", Tools.consoleMode.max, programCode );
        Tools.Prt( "PlayTime = " + Float.toString( ( float ) BaseTick ), Tools.consoleMode.full, programCode );

        String ElapsedTime = "%$6貴方の通算接続時間は ";

        if ( totalDate == 0 ) {
            if ( totalHour == 0 ) {
                ElapsedTime = Utility.StringBuild( ElapsedTime, "%$3" + totalMin + "%$6 分です" );
            } else {
                int totalHourMin = totalMin - ( totalHour * 60 );
                if ( totalHourMin == 0 ) {
                    ElapsedTime = Utility.StringBuild(ElapsedTime, "%$3" + totalHour + "%$6 時間です" );
                } else {
                    ElapsedTime = Utility.StringBuild(ElapsedTime, "%$3" + totalHour + "%$6 時間 %$3" + totalHourMin + "%$6 分です" );
                }
            }
        } else {
            int totalDateHour = totalHour - ( totalDate * 24 );
            if ( totalDateHour > 0 ) {
                ElapsedTime = Utility.StringBuild( ElapsedTime, "%$3" + totalDate + "%$6 日と", "%$3" + totalDateHour + "%$6 時間です" );
            } else {
                ElapsedTime = Utility.StringBuild( ElapsedTime, "%$3" + totalDate + "%$6 日です" );
            }
        }

        Tools.Prt( player, Utility.ReplaceString( ElapsedTime ), Tools.consoleMode.normal, programCode );

        String NowGroup = getGroup( player );
        
        //
        //  グループ表示 => コンソールへ
        // Tools.Prt( ChatColor.GREEN + player.getName() + " [" + NowGroup + "] Login", Tools.consoleMode.normal, programCode );
        //
        Tools.Prt( player, ChatColor.GREEN + "現在のランクは [ " + ChatColor.AQUA + NowGroup + ChatColor.GREEN + " ] です", Tools.consoleMode.normal, programCode );

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
        int checkMin = ( int ) Math.round( ( BaseTick - Database.offsetTick ) * 0.05 / 60 );
        int checkHour = ( int ) Math.round( ( BaseTick - Database.offsetTick ) * 0.05 / 60 / 60 );
        int checkDate = ( int ) Math.round( ( BaseTick - Database.offsetTick ) * 0.05 / 60 / 60 / 24 );

        Tools.Prt( "Rank起算時Tick : " + Database.offsetTick, Tools.consoleMode.max, programCode );
        Tools.Prt( "現時点のTick   : " + BaseTick, Tools.consoleMode.max, programCode );
        Tools.Prt( ChatColor.GREEN + "CheckMin = " + ChatColor.AQUA + checkMin + ChatColor.GREEN + " minutes", Tools.consoleMode.max, programCode );
        Tools.Prt( ChatColor.GREEN + "CheckHour = " + ChatColor.AQUA + checkHour + ChatColor.GREEN + " hour", Tools.consoleMode.max, programCode );
        Tools.Prt( ChatColor.GREEN + "CheckDate = " + ChatColor.AQUA + checkDate + ChatColor.GREEN + " days", Tools.consoleMode.max, programCode );

        if ( Config.rankTime.get( NowGroup ).get( "E" ) == null ) {
            boolean UpCheck = false;
            if ( Config.rankTime.get( NowGroup ).get( "M" ) != null ) {
                Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ).get( "M" ) + " : " + checkMin + " 分間", Tools.consoleMode.full, programCode );
                UpCheck = ( Config.rankTime.get( NowGroup ).get( "M" ) <= checkMin );
            }
            if ( Config.rankTime.get( NowGroup ).get( "H" ) != null ) {
                Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ).get( "H" ) + " : " + checkHour + " 時間", Tools.consoleMode.full, programCode );
                UpCheck = ( Config.rankTime.get( NowGroup ).get( "H" ) <= checkHour );
            }
            if ( Config.rankTime.get( NowGroup ).get( "D" ) != null ) {
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
