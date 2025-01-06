/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.citizenship.database;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import io.github.kumaisu.citizenship.TickTime;
import io.github.kumaisu.citizenship.Lib.Tools;
import io.github.kumaisu.citizenship.config.Config;
import static io.github.kumaisu.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class PlayerData {
    /**
     * プレイヤー情報を新規追加する
     *
     * @param uuid
     * @param name
     * @param Tick
     */
    public static void AddSQL( UUID uuid, String name, int Tick ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            //  テーブルの作成
            //		uuid : varchar(36)	player uuid
            //		name : varchar(20)	player name
            //		logout : DATETIME	last Logout Date
            //          rewards : DATETIME      Rewards Date
            //          basedate : DATETIME     update Date
            //          tick : int              total Tick Time
            //		offset : int 		total Login Time offset
            //		jail : int		to jail flag
            //          Yellow : int            Yellow Card Count
            //          imprisonment : int      Imprisonment Count
            //          reason : int            Reason ID
            String sql = "INSERT INTO player (uuid, name, logout, rewards, basedate, tick, offset, jail, yellow, imprisonment, reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.setString( 1, uuid.toString() );
            preparedStatement.setString( 2, name );
            preparedStatement.setString( 3, Database.sdf.format( new Date() ) );
            preparedStatement.setString( 4, Database.sdf.format( new Date() ) );
            preparedStatement.setString( 5, Database.sdf.format( new Date() ) );
            preparedStatement.setInt( 6, Tick );
            preparedStatement.setInt( 7, 0 );
            preparedStatement.setInt( 8, 0 );
            preparedStatement.setInt( 9, 0 );
            preparedStatement.setInt( 10, 0 );
            preparedStatement.setInt( 11, 0 );

            int rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "Add SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            con.close();

            Database.name = name;
            Database.logout = new Date();
            Database.basedate = new Date();
            Database.offset = 0;
            Database.yellow = 0;
            Database.imprisonment = 0;
            Database.ReasonID = 0;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error AddToSQL : " + e.getMessage(), programCode );
        }
    }

    public static void AddSQL( Player player ) {
        AddSQL( player.getUniqueId(), player.getName(), TickTime.get( player ) );
    }

    /**
     * プレイヤー情報を削除する
     *
     * @param uuid
     * @return
     */
    public static boolean DelSQL( UUID uuid ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "DELETE FROM player WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "Delete Data from SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            con.close();
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error DelSQL." + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * UUIDからプレイヤー情報を取得する
     *
     * @param uuid
     * @return
     */
    public static boolean GetSQL( UUID uuid ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            boolean retStat = false;
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM player WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                Database.name           = rs.getString( "name" );
                Database.logout         = rs.getTimestamp( "logout" );
                Database.Rewards        = rs.getTimestamp( "rewards" );
                Database.basedate       = rs.getTimestamp( "basedate" );
                Database.tick           = rs.getInt( "tick" );
                Database.offset         = rs.getInt( "offset" );
                Database.jail           = rs.getInt( "jail" );
                Database.yellow         = rs.getInt( "yellow" );
                Database.imprisonment   = rs.getInt( "imprisonment" );
                Database.ReasonID       = rs.getInt( "reason" );
                Tools.Prt( "Get Data from SQL Success.", Tools.consoleMode.max, programCode );
                retStat = true;
            }
            con.close();
            return retStat;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetSQL : " + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * UUIDからプレイヤーのログアウト日時を更新する
     *
     * @param uuid
     */
    public static void SetLogoutToSQL( UUID uuid ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET logout = '" + Database.sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            int rowsAffected = preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set logout Date to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error LogoutSQL : " + e.getMessage(), programCode );
        }
    }

    /**
     * Reward 配布日のセット
     *
     * @param uuid
     */
    public static void SetRewardDate( UUID uuid ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET rewards = '" + Database.sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Database.Rewards = new Date();
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Reward Date Reset Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error SetRewardDate : " + e.getMessage(), programCode );
        }
    }

    /**
     * UUID からプレイヤーのランク変更日を更新する
     *
     * @param uuid
     */
    public static void SetBaseDateToSQL( UUID uuid ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET basedate = '" + Database.sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set logout Date to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error SetBaseDateSQL : " + e.getMessage(), programCode );
        }
    }

    /**
     * UUIDからプレイヤーのTickTimeを更新する
     *
     * @param uuid
     * @param tickTime
     */
    public static void SetTickTimeToSQL( UUID uuid, int tickTime ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET tick = " + tickTime + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set TickTime to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error SetTickTimeToSQL : " + e.getMessage(), programCode );
        }
    }

    /**
     * UUIDからプレイヤーのオフセット値を設定する
     *
     * @param uuid
     * @param offset
     */
    public static void SetOffsetToSQL( UUID uuid, int offset ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET offset = " + offset + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set Offset Data to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error SetOffsetToSQL : " + e.getMessage(), programCode );
        }
    }

    /**
     * Jail Flag 投獄フラグ
     * o : 通常（なし）
     * 1 : 未ログイン者の投獄フラグ
     * 2 : 未ログイン者の釈放フラグ（未使用）
     *
     * @param uuid
     * @param jail
     * @return 
     */
    public static boolean SetJailToSQL( UUID uuid, int jail ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET jail = " + jail + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set Jail Data to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error SetJailToSQL : " + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * ReasonID を記録する
     *
     * @param uuid
     * @param ReasonID
     * @return 
     */
    public static boolean SetReasonID( UUID uuid, int ReasonID ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET reason = " + ReasonID + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set ReasonID to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ReasonID Write : " + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * CountUP Aleart 警告回数カウントアップ
     *
     * @param uuid
     */
    public static void addYellow( UUID uuid ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET yellow = yellow + 1 WHERE uuid = '" + uuid.toString() + "'";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "addYellow to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error Add Yellow Card : " + e.getMessage(), programCode );
        }
    }

    /**
     * 警告回数をリセット
     *
     * @param uuid 
     */
    public static void zeroAleart( UUID uuid ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET yellow = 0 WHERE uuid = '" + uuid.toString() + "'";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "zeroAleart to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error Zero Aleart : " + e.getMessage(), programCode );
        }
    }

    /**
     * CountUP imprsioment 投獄回数カウントアップ
     *
     * @param uuid
     */
    public static void addImprisonment( UUID uuid ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "UPDATE player SET imprisonment = imprisonment + 1 WHERE uuid = '" + uuid.toString() + "'";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "addImprisonment to SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error Add Imprisonment : " + e.getMessage(), programCode );
        }
    }

    /**
     * 投獄者のリストアップする
     *
     * @return 
     */
    public static Map< UUID, Integer > ListJailMenber() {
        Map< UUID, Integer > getList = new HashMap<>();
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM player WHERE reason > 0;";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( sql );
            while( rs.next() ) {
                getList.put( UUID.fromString( rs.getString( "uuid" ) ), rs.getInt( "reason" ) );
            }
            con.close();
            Tools.Prt( "Listed Jail Member Success", Tools.consoleMode.max , programCode );
            return getList;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetPlayer" + e.getMessage(), programCode );
            return null;
        }
    }
}
