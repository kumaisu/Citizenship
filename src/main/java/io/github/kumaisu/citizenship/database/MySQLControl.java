/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.citizenship.database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.ChatColor;
import io.github.kumaisu.citizenship.Lib.Tools;
import io.github.kumaisu.citizenship.config.Config;
import static io.github.kumaisu.citizenship.config.Config.programCode;

/**
 *
 * @author NineTailedFox
 */
public class MySQLControl {
    /**
     * Database Open(接続) 処理
     */
    public static void connect() throws SQLException {
        if ( Database.dataSource != null ) {
            if ( Database.dataSource.isClosed() ) {
                Tools.Prt( ChatColor.RED + "database closed.", programCode );
                disconnect();
            } else {
                Tools.Prt( ChatColor.AQUA + "dataSource is not null", programCode );
                return;
            }
        }

        Database.DB_URL = "jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database;
    }

    /**
     * Database Close 処理
     */
    public static void disconnect() throws SQLException {
        if ( Database.dataSource != null ) {
            try {
                Database.dataSource.close();
            } catch ( SQLException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * Database Table Initialize
     */
    public static void TableUpdate() {
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
            //  存在すれば、無視される
            String sql = "CREATE TABLE IF NOT EXISTS player( "
                    + "uuid varchar(36), "
                    + "name varchar(20), "
                    + "logout DATETIME, "
                    + "rewards DATETIME, "
                    + "basedate DATETIME, "
                    + "tick int, "
                    + "offset int, "
                    + "jail int, "
                    + "yellow int, "
                    + "imprisonment int, "
                    + "reason int );";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            int rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "Create Table player Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );

            //  テーブルの作成
            //          id : int auto_increment Jail_ID
            //		uuid : varchar(36)	player uuid
            //          date : DATETIME         update Date
            //          Reason : varchar(50)    Last Jail Reason
            //          enforcer : varchar(20)  The person who caught
            //  存在すれば、無視される
            sql = "CREATE TABLE IF NOT EXISTS reason( "
                    + "id int auto_increment, "
                    + "uuid varchar(36), "
                    + "date DATETIME, "
                    + "reason varchar(50), "
                    + "enforcer varchar(20), "
                    + "index(id) );";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            preparedStatement = con.prepareStatement( sql );
            rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "Create Table reason Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );

            //  テーブルの作成
            //          id : int auto_increment Aleart ID
            //		name : varchar(20)	player uuid
            //          date : DATETIME         update Date
            //          command : varchar(20)   Aleart Command
            //  存在すれば、無視される
            sql = "CREATE TABLE IF NOT EXISTS yellow( "
                    + "id int auto_increment, "
                    + "name varchar(20), "
                    + "date DATETIME, "
                    + "command varchar(20), "
                    + "index(id) );";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            preparedStatement = con.prepareStatement( sql );
            rowsAffected = preparedStatement.executeUpdate();
            Tools.Prt( "Create Table yellow Success." + rowsAffected + "row(s) inserted.", Tools.consoleMode.max, programCode );
            Tools.Prt( ChatColor.AQUA + "dataSource Open Success.", programCode );
            con.close();
        } catch( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Create Tables Error : " + e.getMessage(), programCode);
        }
    }
}
