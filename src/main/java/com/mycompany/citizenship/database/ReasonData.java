/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.util.UUID;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class ReasonData {
    /**
     * プレイヤー情報を新規追加する
     *
     * @param uuid
     * @param reason
     * @param enforcer
     * @return 
     */
    public static int AddReason( UUID uuid, String reason, String enforcer ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "INSERT INTO reason (uuid, date, reason, enforcer) VALUES (?, ?, ?, ?);";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.setString( 1, uuid.toString() );
            preparedStatement.setString( 2, Database.sdf.format( new Date() ) );
            preparedStatement.setString( 3, reason );
            preparedStatement.setString( 4, enforcer );
            preparedStatement.executeUpdate();

            Statement stmt = con.createStatement();
            sql = "SELECT * FROM reason ORDER BY id DESC;";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( sql );
            int RetID = 0;
            if ( rs.next() ) {
                RetID = rs.getInt( "id" );
                Tools.Prt( "Get Reason ID Success", Tools.consoleMode.max, programCode );
            }
            con.close();
            Tools.Prt( "Add Reason to SQL Success", Tools.consoleMode.max, programCode );
            return RetID;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error AddReason" + e.getMessage(), programCode );
            return 0;
        }
    }

    /**
     * プレイヤー情報を削除する
     *
     * @param ID
     * @return
     */
    public static boolean DelReason( int ID ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "DELETE FROM reason WHERE id = " + ID +";";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            Tools.Prt( "Delete Reason from SQL Success.", Tools.consoleMode.max, programCode );
            con.close();
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error DelReason" + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * UUIDからプレイヤー情報を取得する
     *
     * @param ID
     */
    public static void GetReason( int ID ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM reason WHERE id = " + ID + " ORDER BY id DESC;";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                Database.ReasonDate = rs.getTimestamp( "date" );
                Database.Reason = rs.getString( "reason" );
                Database.enforcer = rs.getString( "enforcer" );
                Tools.Prt( "Get Reason from SQL Success.", Tools.consoleMode.max, programCode );
            }
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetReason" + e.getMessage(), programCode );
        }
    }

    /**
     * 表示１行分の成形処理
     *
     * @param gs
     * @return 
     */
    public static String LineMake( ResultSet gs ) {
        try {
            String retStr =
                ChatColor.WHITE + String.format( "%3d", gs.getInt( "id" ) ) + ": " +
                ChatColor.GREEN + Database.sdf.format( gs.getTimestamp( "date" ) ) + " " +
                ChatColor.AQUA + Bukkit.getOfflinePlayer( UUID.fromString( gs.getString( "uuid" ) ) ).getName() + " " +
                ChatColor.RED + gs.getString( "reason" ) +
                ChatColor.WHITE + "(by." + gs.getString( "enforcer" ) + ")";
            return retStr;
        } catch ( SQLException ex ) {
            Tools.Prt( ChatColor.RED + "Line Make Error : " + ex.getMessage(), programCode );
            return "Error";
        }
    }

    /**
     * 投獄理由のリスト表示
     *
     * @param player
     * @param uuid
     */
    public static void ListReason( Player player, UUID uuid ) {
        String Msg = ChatColor.GREEN + "=== Jail Reason List ===";
        Tools.Prt( player, Msg, programCode );
        try ( Connection con = Database.dataSource.getConnection() ) {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM reason";
            if ( uuid == null ) {
                sql += ";";
            } else {
                sql += " WHERE uuid = '" + uuid.toString() + "';";
            }
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( sql );
            while( rs.next() ) {
                Tools.Prt( player, LineMake( rs ), programCode );
            }
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ListReason" + e.getMessage(), programCode );
        }
        Tools.Prt( player, ChatColor.GREEN + "=== end ===", programCode );
    }

    /**
     * 投獄理由の書き換え
     *
     * @param ID
     * @param Reason 
     */
    public static void ChangeReason( int ID, String Reason ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            Statement stmt = con.createStatement();
            String sql = "UPDATE reason SET reason = '" + Reason + "' WHERE id = " + ID + ";";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Change Database Error : " + e.getMessage(), programCode );
        }
    }

    /**
     * IDナンバーでの、理由の表示
     *
     * @param player
     * @param ID
     */
    public static void PrintReason( Player player, int ID ) {
        try ( Connection con = Database.dataSource.getConnection() ) {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM reason WHERE id = " + ID;
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                Tools.Prt( player, 
                    ChatColor.WHITE + String.format( "%3d", rs.getInt( "id" ) ) + ": " +
                    ChatColor.GREEN + Database.sdf.format( rs.getTimestamp( "date" ) ) + " " +
                    ChatColor.AQUA + Bukkit.getOfflinePlayer( UUID.fromString( rs.getString( "uuid" ) ) ).getName() + " " +
                    ChatColor.RED + rs.getString( "reason" ),
                    programCode
                );
            }
            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error PrintReason" + e.getMessage(), programCode );
        }
    }
}
