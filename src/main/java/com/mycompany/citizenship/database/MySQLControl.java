/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.ChatColor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.citizenship.config.Config;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class MySQLControl {
    /**
     * Database Open(接続) 処理
     */
    public static void connect() {
        if ( Database.dataSource != null ) {
            if ( Database.dataSource.isClosed() ) {
                Tools.Prt( ChatColor.RED + "database closed.", programCode );
                disconnect();
            } else {
                Tools.Prt( ChatColor.AQUA + "dataSource is not null", programCode );
                return;
            }
        }

        // HikariCPの初期化
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl( "jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database );
        config.setPoolName( Config.database );
        config.setAutoCommit( true );
        config.setConnectionInitSql( "SET SESSION query_cache_type=0" );
        config.setMaximumPoolSize( 2 );
        config.setMinimumIdle( 2 );
        config.setMaxLifetime( TimeUnit.MINUTES.toMillis( 15 ) );
        //  config.setConnectionTimeout(0);
        //  config.setIdleTimeout(0);
        config.setUsername( Config.username );
        config.setPassword( Config.password );

        Properties properties = new Properties();
        properties.put( "useSSL", "false" );
        properties.put( "autoReconnect", "true" );
        properties.put( "maintainTimeStats", "false" );
        properties.put( "elideSetAutoCommits", "true" );
        properties.put( "useLocalSessionState", "true" );
        properties.put( "alwaysSendSetIsolation", "false" );
        properties.put( "cacheServerConfiguration", "true" );
        properties.put( "cachePrepStmts", "true" );
        properties.put( "prepStmtCacheSize", "250" );
        properties.put( "prepStmtCacheSqlLimit", "2048" );
        properties.put( "useUnicode", "true" );
        properties.put( "characterEncoding", "UTF-8" );
        properties.put( "characterSetResults", "UTF-8" );
        properties.put( "useServerPrepStmts", "true" );

        config.setDataSourceProperties( properties );

        Database.dataSource = new HikariDataSource( config );
    }

    /**
     * Database Close 処理
     */
    public static void disconnect() {
        if ( Database.dataSource != null ) {
            Database.dataSource.close();
        }
    }

    /**
     * Database Table Initialize
     */
    public static void TableUpdate() {
        try ( Connection con = Database.dataSource.getConnection() ) {
            //  テーブルの作成
            //		uuid : varchar(36)	player uuid
            //		name : varchar(20)	player name
            //		logiut : DATETIME	last Logout Date
            //          basedate : DATETIME     update Date
            //          tick : int              total Tick Time
            //		offset : int 		total Login Time offset
            //		jail : int		to jail flag
            //          imprisonment : int      Imprisonment Count
            //          reason : int            Reason ID
            //  存在すれば、無視される
            String sql = "CREATE TABLE IF NOT EXISTS player( "
                    + "uuid varchar(36), "
                    + "name varchar(20), "
                    + "logout DATETIME, "
                    + "basedate DATETIME, "
                    + "tick int, "
                    + "offset int, "
                    + "jail int, "
                    + "imprisonment "
                    + "int, reason int );";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max, programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();

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
            preparedStatement.executeUpdate();

            Tools.Prt( ChatColor.AQUA + "dataSource Open Success.", programCode );
            con.close();
        } catch( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Connection Error : " + e.getMessage(), programCode);
        }
    }
}
