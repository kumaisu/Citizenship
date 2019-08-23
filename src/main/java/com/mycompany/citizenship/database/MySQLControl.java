/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.util.Date;
import java.util.UUID;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.entity.Player;
import org.bukkit.Statistic;
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

    private static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private static HikariDataSource dataSource = null;

    /**
     * Database Open(接続) 処理
     */
    public static void connect() {
        if ( dataSource != null ) {
            if ( dataSource.isClosed() ) {
                Tools.Prt( ChatColor.RED + "database closed.", Tools.consoleMode.full, programCode );
                disconnect();
            } else {
                Tools.Prt( ChatColor.AQUA + "dataSource is not null", Tools.consoleMode.max, programCode );
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

        dataSource = new HikariDataSource( config );
    }

    /**
     * Database Close 処理
     */
    public static void disconnect() {
        if ( dataSource != null ) {
            dataSource.close();
        }
    }

    /**
     * Database Table Initialize
     */
    public static void TableUpdate() {
        try ( Connection con = dataSource.getConnection() ) {
            //  テーブルの作成
            //		uuid : varchar(36)	player uuid
            //		name : varchar(20)	player name
            //		logiut : DATETIME	last Logout Date
            //          basedate : DATETIME     update Date
            //          tick : int              total Tick Time
            //		offset : int 		total Login Time offset
            //		jail : int		to jail flag
            //          Imprisonment : int      Imprisonment Count
            //  存在すれば、無視される
            String sql = "CREATE TABLE IF NOT EXISTS player( uuid varchar(36), name varchar(20), logout DATETIME, basedate DATETIME, tick int, offset int, jail int, imprisonment int )";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();

            Tools.Prt( ChatColor.AQUA + "dataSource Open Success.", Tools.consoleMode.max, programCode );
            con.close();
        } catch( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Connection Error : " + e.getMessage(), programCode);
        }
    }

    /**
     * プレイヤー情報を新規追加する
     *
     * @param player
     */
    public static void AddSQL( Player player ) {
        try ( Connection con = dataSource.getConnection() ) {
            String sql = "INSERT INTO player (uuid, name, logout, basedate, tick, offset, jail, imprisonment ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.setString( 1, player.getUniqueId().toString() );
            preparedStatement.setString( 2, player.getName() );
            preparedStatement.setString( 3, sdf.format( new Date() ) );
            preparedStatement.setString( 4, sdf.format( new Date() ) );
            preparedStatement.setInt( 5, player.getStatistic( Statistic.PLAY_ONE_MINUTE ) );
            preparedStatement.setInt( 6, 0 );
            preparedStatement.setInt( 7, 0 );
            preparedStatement.setInt( 8, 0 );

            preparedStatement.executeUpdate();
            con.close();

            Database.name = player.getName();
            Database.logout = new Date();
            Database.basedate = new Date();
            Database.offset = 0;
            Database.imprisonment = 0;

            Tools.Prt( "Add Data to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error AddToSQL", programCode );
        }
    }

    /**
     * プレイヤー情報を削除する
     *
     * @param uuid
     * @return
     */
    public static boolean DelSQL( UUID uuid ) {
        try ( Connection con = dataSource.getConnection() ) {
            String sql = "DELETE FROM player WHERE uuid = '" + uuid.toString() + "'";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement( sql );
            preparedStatement.executeUpdate();
            Tools.Prt( "Delete Data from SQL Success.", Tools.consoleMode.full , programCode );
            con.close();
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error DelFromSQL", programCode );
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
        try ( Connection con = dataSource.getConnection() ) {
            boolean retStat = false;
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM player WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                Database.name           = rs.getString( "name" );
                Database.logout         = rs.getTimestamp( "logout" );
                Database.basedate       = rs.getTimestamp( "basedate" );
                Database.tick           = rs.getInt( "tick" );
                Database.offset         = rs.getInt( "offset" );
                Database.jail           = rs.getInt( "jail" );
                Database.imprisonment   = rs.getInt( "imprisonment" );
                Tools.Prt( "Get Data from SQL Success.", Tools.consoleMode.full , programCode );
                retStat = true;
            }
            con.close();
            return retStat;
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error GetPlayer", programCode );
            return false;
        }
    }

    /**
     * UUIDからプレイヤーのログアウト日時を更新する
     *
     * @param uuid
     */
    public static void SetLogoutToSQL( UUID uuid ) {
        try ( Connection con = dataSource.getConnection() ) {
            String sql = "UPDATE player SET logout = '" + sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set logout Date to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus", programCode );
        }
    }

    /**
     * UUID からプレイヤーのランク変更日を更新する
     *
     * @param uuid
     */
    public static void SetBaseDateToSQL( UUID uuid ) {
        try ( Connection con = dataSource.getConnection() ) {
            String sql = "UPDATE player SET basedate = '" + sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set logout Date to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus", programCode );
        }
    }

    /**
     * UUIDからプレイヤーのTickTimeを更新する
     *
     * @param uuid
     * @param tickTime
     */
    public static void SetTickTimeToSQL( UUID uuid, int tickTime ) {
        try ( Connection con = dataSource.getConnection() ) {
            String sql = "UPDATE player SET tick = " + tickTime + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set TickTime to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus", programCode );
        }
    }

    /**
     * UUIDからプレイヤーのオフセット値を設定する
     *
     * @param uuid
     * @param offset
     */
    public static void SetOffsetToSQL( UUID uuid, int offset ) {
        try ( Connection con = dataSource.getConnection() ) {
            String sql = "UPDATE player SET offset = " + offset + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set Offset Data to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus", programCode );
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
     */
    public static void SetJailToSQL( UUID uuid, int jail ) {
        try ( Connection con = dataSource.getConnection() ) {
            String sql = "UPDATE player SET jail = " + jail + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Set Jail Data to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error ChangeStatus", programCode );
        }
    }

    /**
     * CountUP imprsioment 投獄回数カウントアップ
     *
     * @param uuid
     */
    public static void addImprisonment( UUID uuid ) {
        try ( Connection con = dataSource.getConnection() ) {
            String sql = "UPDATE player SET imprisonment = imprisonment + 1 WHERE uuid = '" + uuid.toString() + "'";
            Tools.Prt( "SQL : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.executeUpdate();
            con.close();
            Tools.Prt( "Added Imprisonment Count Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( ChatColor.RED + "Error Add Imprisonment : " + e.getMessage(), programCode );
        }
    }

}
