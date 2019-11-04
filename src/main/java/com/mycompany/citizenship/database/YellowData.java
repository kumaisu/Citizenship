/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.citizenship.config.Config.programCode;

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
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "INSERT INTO yellow (name, date, command) VALUES (?, ?, ?);";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.setString( 1, name );
            preparedStatement.setString( 2, Database.sdf.format( new Date() ) );
            preparedStatement.setString( 3, command );
            preparedStatement.executeUpdate();
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
        try ( Connection con = Database.dataSource.getConnection() ) {
            String sql = "DELETE FROM yellow WHERE id = " + ID +";";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            Tools.Prt( "Delete Yellow from SQL Success.", Tools.consoleMode.max, programCode );
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
        try ( Connection con = Database.dataSource.getConnection() ) {
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

        try ( Connection con = Database.dataSource.getConnection() ) {
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
            Tools.Prt( ChatColor.RED + "Error CardList : " + e.getMessage(), programCode );
            return false;
        }

        if ( StringData.size() > 0 ) {
            Tools.Prt( player, Title, programCode );
            StringData.forEach( ( s ) -> { Tools.Prt( player, s, programCode ); } );
            Tools.Prt( player, "=== End ===", programCode );
        }
        return true;
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
            sqlCmd += " WHERE name = '%" + name +"%'";
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
        return GetList( player, sqlCmd, ChatColor.WHITE + "== Yellow Card Logs == " + Database.sdf.format( date ), 5 );
    }
}
