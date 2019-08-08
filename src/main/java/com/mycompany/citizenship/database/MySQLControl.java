/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.util.Date;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.entity.Player;
import org.bukkit.Statistic;
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

    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private Connection connection;

    public static String name = "Unknown";
    public static Date logout;
    public static Date basedate;
    public static int tick = 0;
    public static int offset = 0;
    public static int jail = 0;

    /**
     * ライブラリー読込時の初期設定
     *
     */
    public MySQLControl() {
        HikariDataSource dataSource;

        // HikariCPの初期化
        HikariConfig config = new HikariConfig();

        // MySQL用ドライバを設定
        config.setDriverClassName( "com.mysql.jdbc.Driver" );

        // 「jdbc:mysql://ホスト:ポート/DB名」の様なURLで指定
        config.setJdbcUrl( "jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database );

        // ユーザ名、パスワード指定
        config.addDataSourceProperty( "user", Config.username );
        config.addDataSourceProperty( "password", Config.password );

        // キャッシュ系の設定(任意)
        config.addDataSourceProperty( "cachePrepStmts", "true" );
        config.addDataSourceProperty( "prepStmtCacheSize", "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit", "2048" );
        // サーバサイドプリペアードステートメントを使用する(任意)
        config.addDataSourceProperty( "useServerPrepStmts", "true" );
        // エンコーディング
        config.addDataSourceProperty( "characterEncoding", "utf8" );

        // 最小接続数まで接続を確保できない時に例外を投げる
        config.setInitializationFailFast( true );
        // 接続をテストするためのクエリ
        config.setConnectionInitSql( "SELECT 1" );

        // 接続
        dataSource = new HikariDataSource( config );

        try {
            connection = dataSource.getConnection();
        } catch( SQLException e ) {
            Tools.Prt( "Connection Error : " + e.getMessage(), programCode);
        }

        updateTables();
    }

    /**
     * MySQLへのコネクション処理
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void updateTables() {
        try {
            //  テーブルの作成
            //		uuid : varchar(36)	player uuid
            //		name : varchar(20)	player name
            //		logiut : DATETIME	last Logout Date
            //          basedate : DATETIME     update Date
            //          tick : int              total Tick Time
            //		offset : int 		total Login Time offset
            //		jail : int		to jail flag
            //  存在すれば、無視される
            String sql = "CREATE TABLE IF NOT EXISTS player( uuid varchar(36), name varchar(20), logout DATETIME, basedate DATETIME, tick int, offset int, jail int )";
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.executeUpdate();
        } catch ( SQLException e ) {
            Tools.Prt( "Error SQL update tables : " + e.getMessage(), programCode );
        }
    }

    /**
     * プレイヤー情報を新規追加する
     *
     * @param player
     */
    public void AddSQL( Player player ) {
        try {
            String sql = "INSERT INTO player (uuid, name, logout, basedate, tick, offset, jail) VALUES (?, ?, ?, ?, ?, ?, ?);";
            Tools.Prt( "SQL Command : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.setString( 1, player.getUniqueId().toString() );
            preparedStatement.setString( 2, player.getName() );
            preparedStatement.setString( 3, sdf.format( new Date() ) );
            preparedStatement.setString( 4, sdf.format( new Date() ) );
            preparedStatement.setInt( 5, player.getStatistic( Statistic.PLAY_ONE_MINUTE ) );
            preparedStatement.setInt( 6, 0 );
            preparedStatement.setInt( 7, 0 );

            preparedStatement.executeUpdate();

            this.name = player.getName();
            this.logout = new Date();
            this.basedate = new Date();
            this.offset = 0;

            Tools.Prt( "Add Data to SQL Success.", Tools.consoleMode.full , programCode );

        } catch ( SQLException e ) {
            Tools.Prt( "Error AddToSQL", programCode );
        }
    }

    /**
     * プレイヤー情報を削除する
     *
     * @param uuid
     * @return
     */
    public boolean DelSQL( UUID uuid ) {
        try {
            String sql = "DELETE FROM player WHERE uuid = '" + uuid.toString() + "'";
            Tools.Prt( "SQL Command : " + sql, Tools.consoleMode.max , programCode );
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.executeUpdate();
            Tools.Prt( "Delete Data from SQL Success.", Tools.consoleMode.full , programCode );
            return true;
        } catch ( SQLException e ) {
            Tools.Prt( "Error DelFromSQL", programCode );
            return false;
        }
    }

    /**
     * UUIDからプレイヤー情報を取得する
     *
     * @param uuid
     * @return
     */
    public boolean GetSQL( UUID uuid ) {
        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM player WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL Command : " + sql, Tools.consoleMode.max , programCode );
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                this.name       = rs.getString( "name" );
                this.logout     = rs.getTimestamp( "logout" );
                this.basedate   = rs.getTimestamp( "basedate" );
                this.tick       = rs.getInt( "tick" );
                this.offset     = rs.getInt( "offset" );
                this.jail       = rs.getInt( "jail" );
                Tools.Prt( "Get Data from SQL Success.", Tools.consoleMode.full , programCode );
                return true;
            }
        } catch ( SQLException e ) {
            Tools.Prt( "Error GetPlayer", programCode );
        }
        return false;
    }

    /**
     * UUIDからプレイヤーのログアウト日時を更新する
     *
     * @param uuid
     */
    public void SetLogoutToSQL( UUID uuid ) {
        try {
            String sql = "UPDATE player SET logout = '" + sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL Command : " + sql, Tools.consoleMode.max , programCode );

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

            Tools.Prt( "Set logout Date to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( "Error ChangeStatus", programCode );
        }
    }

    /**
     * UUID からプレイヤーのランク変更日を更新する
     *
     * @param uuid
     */
    public void SetBaseDateToSQL( UUID uuid ) {
        try {
            String sql = "UPDATE player SET basedate = '" + sdf.format( new Date() ) + "' WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL Command : " + sql, Tools.consoleMode.max , programCode );

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

            Tools.Prt( "Set logout Date to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( "Error ChangeStatus", programCode );
        }
    }

    /**
     * UUIDからプレイヤーのTickTimeを更新する
     *
     * @param uuid
     * @param tickTime
     */
    public void SetTickTimeToSQL( UUID uuid, int tickTime ) {
        try {
            String sql = "UPDATE player SET tick = " + tickTime + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL Command : " + sql, Tools.consoleMode.max , programCode );

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

            Tools.Prt( "Set TickTime to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( "Error ChangeStatus", programCode );
        }
    }

    /**
     * UUIDからプレイヤーのオフセット値を設定する
     *
     * @param uuid
     * @param offset
     */
    public void SetOffsetToSQL( UUID uuid, int offset ) {
        try {
            String sql = "UPDATE player SET offset = " + offset + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL Command : " + sql, Tools.consoleMode.max , programCode );

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

            Tools.Prt( "Set Offset Data to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( "Error ChangeStatus", programCode );
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
    public void SetJailToSQL( UUID uuid, int jail ) {
        try {
            String sql = "UPDATE player SET jail = " + jail + " WHERE uuid = '" + uuid.toString() + "';";
            Tools.Prt( "SQL Command : " + sql, Tools.consoleMode.max , programCode );

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

            Tools.Prt( "Set Jail Data to SQL Success.", Tools.consoleMode.full , programCode );
        } catch ( SQLException e ) {
            Tools.Prt( "Error ChangeStatus", programCode );
        }
    }
}
