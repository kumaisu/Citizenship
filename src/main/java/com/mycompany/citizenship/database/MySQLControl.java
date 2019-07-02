/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.entity.Player;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.citizenship.config.Config;
import static com.mycompany.citizenship.config.Config.programCode;
import java.util.UUID;

/**
 *
 * @author sugichan
 */
public class MySQLControl {

    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private Connection connection;

    public static String name = "Unknown";
    public static Date logout;
    public static int connect = 0;
    public static int rank = 1;
    
    /**
     * ライブラリー読込時の初期設定
     *
     */
    public MySQLControl() {
    }

    /**
     * MySQLへのコネクション処理
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void openConnection() throws SQLException, ClassNotFoundException {

        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized ( this ) {
            if ( connection != null && !connection.isClosed() ) {
                return;
            }
            Class.forName( "com.mysql.jdbc.Driver" );
            connection = DriverManager.getConnection( "jdbc:mysql://" + Config.host + ":" + Config.port + "/" + Config.database, Config.username, Config.password );

            //  テーブルの作成
            //		uuid : varchar(36)	player uuid
            //		name : varchar(20)	player name
            //		logiut : DATETIME	last Logout Date
            //		connect : int 		total Login Time
            //		Rank : int		Citizenship Rank
            //  存在すれば、無視される
            String sql = "CREATE TABLE IF NOT EXISTS player(id uuid varchar(36), name varchar(20), logout DATETIME, connect int, rank int, index(id))";
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.executeUpdate();
        }
    }

    /**
     * プレイヤー情報を新規追加する
     *
     * @param player
     * @param con
     * @param rank
     */
    public void AddSQL( Player player, int con, int rank ) {
        try {
            openConnection();

            String sql = "INSERT INTO player ( uuid, name, logout, connect, rank ) VALUES ( ?, ?, ?, ?, ? );";
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.setString( 1, player.getUniqueId().toString() );
            preparedStatement.setString( 2, player.getDisplayName() );
            preparedStatement.setString( 3, sdf.format( new Date() ) );
            preparedStatement.setInt( 4, con );
            preparedStatement.setInt( 5, rank );

            preparedStatement.executeUpdate();

        } catch ( ClassNotFoundException | SQLException e ) {
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
            openConnection();
            String sql = "DELETE FROM player WHERE uuid = '" + uuid.toString() + "'";
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.executeUpdate();
            return true;
        } catch ( ClassNotFoundException | SQLException e ) {
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
            openConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM plsyer WHERE uuid = '" + uuid.toString() + "';";
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) {
                this.name = rs.getString( "name" );
                this.logout = rs.getDate( "logout" );
                this.connect = rs.getInt( "connect" );
                this.rank = rs.getInt( "rank" );
                return true;
            }
        } catch ( ClassNotFoundException | SQLException e ) {
            Tools.Prt( "Error GetPlayer", programCode );
        }
        return false;
    }

    /**
     * UUIDからプレイヤーのログアウト日時を更新する
     *
     * @param uuid
     */
    public void UpdateSQL( UUID uuid ) {
        try {
            openConnection();

            String sql = "UPDATE player SET logout = " + sdf.format( new Date() ) + " WHERE uuid = '" + uuid.toString() + "';";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();

        } catch ( ClassNotFoundException | SQLException e ) {
            Tools.Prt( "Error ChangeStatus", programCode );
        }
    }

}
