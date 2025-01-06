/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.citizenship.database;

import io.github.kumaisu.citizenship.config.Config;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import io.github.kumaisu.citizenship.Lib.Tools;
import static io.github.kumaisu.citizenship.config.Config.programCode;

import io.github.kumaisu.citizenship.config.Yellow;
import org.bukkit.Sound;

/**
 *
 * @author sugichan
 */
public class YellowData {
    /**
     * イエローカード情報を新規追加する
     *
     * @param name
     * @param command
     */
    public static void AddCard( String name, String command ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "INSERT INTO yellow (name, date, command) VALUES (?, ?, ?);";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.setString( 1, name );
            preparedStatement.setString( 2, Database.sdf.format( new Date() ) );
            preparedStatement.setString( 3, command );
            int rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "Add Card Success." + rowsAffected + "row(s) inserted.", io.github.kumaisu.citizenship.Lib.Tools.consoleMode.max, programCode );
            con.close();
            Tools.Prt( "Add Yellow to SQL Success", Tools.consoleMode.max, programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error AddCard" + e.getMessage(), programCode );
        }
    }

    /**
     * イエローカード情報を削除する
     *
     * @param ID
     * @return
     */
    public static boolean DelCard( int ID ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            String sql = "DELETE FROM yellow WHERE id = " + ID +";";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            int rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "Delete Yellow from SQL Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            con.close();
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error DelCard" + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * IDからイエローカード情報を取得する
     *
     * @param ID
     * @return 
     */
    public static boolean GetCard( int ID ) {
        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM yellow WHERE id = " + ID + " ORDER BY id DESC;";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            ResultSet rs = stmt.executeQuery( sql );
            boolean Ret = false;
            if ( rs.next() ) {
                Database.YellowName = rs.getString( "name" );
                Database.YellowDate = rs.getTimestamp( "date" );
                Database.YellowCommand = rs.getString( "command" );
                Tools.Prt( "Get Yellow from SQL Success.", Tools.consoleMode.max, programCode );
                Ret = true;
            }
            con.close();
            return Ret;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetCard" + e.getMessage(), programCode );
            return false;
        }
    }

    /**
     * DBからリストを取得する
     *
     * @param player
     * @param sqlCmd
     * @param Title
     * @param line
     * @return 
     */
    public static boolean GetList( Player player, String sqlCmd, String Title, int line ) {
        List< String > StringData = new ArrayList<>();
        Tools.Prt( "SQL : " + sqlCmd, Tools.consoleMode.max, programCode );

        try ( Connection con = DriverManager.getConnection( Database.DB_URL, Config.username, Config.password ) ) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( sqlCmd );

            int loopCount = 0;
            while( rs.next() && ( loopCount<line ) ) {
                StringData.add(
                    ChatColor.WHITE + String.format( "%6d", rs.getInt( "id" ) ) + ": " +
                    ChatColor.GREEN + Database.sdf.format( rs.getTimestamp( "date" ) ) + " " +
                    ChatColor.AQUA + rs.getString( "name" ) +
                    ChatColor.GREEN + " : " +
                    ChatColor.YELLOW + rs.getString( "command" )
                );
                loopCount++;
            }

            con.close();
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetList : " + e.getMessage(), programCode );
            return false;
        }

        if ( StringData.size() > 0 ) {
            Tools.Prt( player, Title, programCode );
            StringData.forEach( ( s ) -> { Tools.Prt( player, s, programCode ); } );
            Tools.Prt( player, "=== End ===", programCode );
            return true;
        }
        return false;
    }

    /**
     * リスト表示
     *
     * @param player
     * @param name
     * @param date
     * @param keyword
     * @param line
     * @return 
     */
    public static boolean CardList( Player player, String name, String date, String keyword, int line ) {
        String TitleString = ChatColor.WHITE + "== Yellow Card List == ";
        String sqlCmd = "SELECT * FROM yellow";
        boolean sqlAdd = false;

        if ( !"".equals( name ) ) {
            TitleString += "[Name:" + name + "] ";
            sqlCmd += " WHERE name LIKE '%" + name +"%'";
            sqlAdd = true;
        }

        if ( !"".equals( date ) ) {
            if ( sqlAdd ) { sqlCmd += " ADD "; } else { sqlCmd += " WHERE "; }
            TitleString += "[Date:" + date + "]";
            sqlCmd += "date BETWEEN '" + date + " 00:00:00' AND '" + date + " 23:59:59'";
            sqlAdd = true;
        }

        if ( !"".equals( keyword ) ) {
            if ( sqlAdd ) { sqlCmd += " ADD "; } else { sqlCmd += " WHERE "; }
            TitleString += "[Keyword:" + keyword + "]";
            sqlCmd += "command LIKE '%" + keyword + "%'";
        }

        sqlCmd +=  " ORDER BY date DESC;";

        return GetList( player, sqlCmd, TitleString, line );
    }

    /**
     * 指定日以降のリストを表示する
     * 
     * @param player
     * @param date
     * @return 
     */
    public static boolean CardLog( Player player, Date date ) {
        String sqlCmd = "SELECT * FROM yellow WHERE date BETWEEN '" +
            Database.sdf.format( date ) + "' AND '" +
            Database.sdf.format( new Date() ) + "' ORDER BY date DESC;";
        boolean ret = GetList( player, sqlCmd, ChatColor.RED + "== Yellow Card Logs == " + Database.sdf.format( date ), 5 );
        if ( ret ) {
            if ( Yellow.sound_play ) {
                Tools.Prt( "Sound Play !!", Tools.consoleMode.full, Config.programCode );
                ( player.getWorld() ).playSound(
                    player.getLocation(),                   // 鳴らす場所
                    Sound.valueOf( Yellow.sound_type ),     // 鳴らす音
                    Yellow.sound_volume,                    // 音量
                    Yellow.sound_pitch                      // 音程
                );
            }
        }
        return ret;
    }
}
