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
import org.bukkit.ChatColor;
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

    /*
    sqlCmd = "SELECT * FROM yellow WHERE date BETWEEN '" + checkString + " 00:00:00' AND '" + checkString + " 23:59:59' ORDER BY date DESC;";
    */
}
