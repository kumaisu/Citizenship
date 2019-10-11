/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.UUID;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.mycompany.citizenship.TickTime;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.citizenship.config.Config.programCode;

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
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "INSERT INTO player (uuid, name, logout, basedate, tick, offset, jail, imprisonment, reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.setString( 1, uuid.toString() );
            preparedStatement.setString( 2, name );
            preparedStatement.setString( 3, Database.sdf.format( new Date() ) );
            preparedStatement.setString( 4, Database.sdf.format( new Date() ) );
            preparedStatement.setInt( 5, Tick );
            preparedStatement.setInt( 6, 0 );
            preparedStatement.setInt( 7, 0 );
            preparedStatement.setInt( 8, 0 );
            preparedStatement.setInt( 9, 0 );

            preparedStatement.executeUpdate();
            con.close();

            Database.name = name;
            Database.logout = new Date();
            Database.basedate = new Date();
            Database.offset = 0;
            Database.imprisonment = 0;
            Database.ReasonID = 0;

            Tools.Prt( "Add Data to SQL Success.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error AddToSQL" + e.getMessage(), programCode );
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
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "DELETE FROM player WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            Tools.Prt( "Delete Data from SQL Success.", Tools.consoleMode.max, programCode );
            con.close();
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error DelFromSQL" + e.getMessage(), programCode );
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
        try ( Connection con = Database.dataSource.getConnection() ) {
            boolean retStat = false;
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM player WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                Database.name           = rs.getString( "name" );
                Database.logout         = rs.getTimestamp( "logout" );
                Database.basedate       = rs.getTimestamp( "basedate" );
                Database.tick           = rs.getInt( "tick" );
                Database.offset         = rs.getInt( "offset" );
                Database.jail           = rs.getInt( "jail" );
                Database.imprisonment   = rs.getInt( "imprisonment" );
                Database.ReasonID       = rs.getInt( "reason" );
                Tools.Prt( "Get Data from SQL Success.", Tools.consoleMode.max, programCode );
                retStat = true;
            }
            con.close();
            return retStat;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetPlayer" + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * UUIDからプレイヤーのログアウト日時を更新する
     *
     * @param uuid
     */
    public static void SetLogoutToSQL( UUID uuid ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "UPDATE player SET logout = '" + Database.sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set logout Date to SQL Success.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus" + e.getMessage(), programCode );
        }
    }

    /**
     * UUID からプレイヤーのランク変更日を更新する
     *
     * @param uuid
     */
    public static void SetBaseDateToSQL( UUID uuid ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "UPDATE player SET basedate = '" + Database.sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set logout Date to SQL Success.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus" + e.getMessage(), programCode );
        }
    }

    /**
     * UUIDからプレイヤーのTickTimeを更新する
     *
     * @param uuid
     * @param tickTime
     */
    public static void SetTickTimeToSQL( UUID uuid, int tickTime ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "UPDATE player SET tick = " + tickTime + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set TickTime to SQL Success.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus" + e.getMessage(), programCode );
        }
    }

    /**
     * UUIDからプレイヤーのオフセット値を設定する
     *
     * @param uuid
     * @param offset
     */
    public static void SetOffsetToSQL( UUID uuid, int offset ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "UPDATE player SET offset = " + offset + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set Offset Data to SQL Success.", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus" + e.getMessage(), programCode );
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
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "UPDATE player SET jail = " + jail + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set Jail Data to SQL Success.", Tools.consoleMode.max, programCode );
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus" + e.getMessage(), programCode );
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
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "UPDATE player SET reason = " + ReasonID + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set ReasonID to SQL Success.", Tools.consoleMode.max, programCode );
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ReasonID Write : " + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * CountUP imprsioment 投獄回数カウントアップ
     *
     * @param uuid
     */
    public static void addImprisonment( UUID uuid ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "UPDATE player SET imprisonment = imprisonment + 1 WHERE uuid = '" + uuid.toString() + "'";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
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
        try ( Connection con = Database.dataSource.getConnection() ) {
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
