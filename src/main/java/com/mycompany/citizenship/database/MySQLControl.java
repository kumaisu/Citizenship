/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.mycompany.kumaisulibraries.InetCalc;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import com.mycompany.citizenship.config.Config;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class MySQLControl {

    SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    private Connection connection;

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

            //  mysql> create table list(id int auto_increment, date DATETIME,name varchar(20), uuid varchar(36), ip INTEGER UNSIGNED, status byte, index(id));
            //  テーブルの作成
            //  存在すれば、無視される
            String sql = "CREATE TABLE IF NOT EXISTS player(id uuid varchar(36), logoutdate DATETIME,name varchar(20), status int, index(id))";
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.executeUpdate();

            //  mysql> create table IF NOT EXISTS unknowns (ip varchar(22), host varchar(60), count int, newdate DATETIME, lastdate DATETIME );
            //  Unknowns テーブルの作成
            //  存在すれば、無視される
            sql = "CREATE TABLE IF NOT EXISTS hosts (ip INTEGER UNSIGNED, host varchar(60), count int, newdate DATETIME, lastdate DATETIME )";
            preparedStatement = connection.prepareStatement( sql );
            preparedStatement.executeUpdate();
        }
    }

    /**
     * ホスト名を新規追加する
     *
     * @param IP
     * @param Host
     */
    public void AddHostToSQL( String IP, String Host ) {
        try {
            openConnection();

            String sql = "INSERT INTO hosts ( ip, host, count, newdate, lastdate ) VALUES ( INET_ATON( ? ), ?, ?, ?, ? );";
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.setString( 1, IP );
            preparedStatement.setString( 2, Host );
            preparedStatement.setInt( 3, 0 );
            preparedStatement.setString( 4, sdf.format( new Date() ) );
            preparedStatement.setString( 5, sdf.format( new Date() ) );

            preparedStatement.executeUpdate();

        } catch ( ClassNotFoundException | SQLException e ) {
            Tools.Prt( "Error AddHostToSQL", programCode );
        }
    }

    /**
     * 登録IPアドレスを削除する
     *
     * @param IP
     * @return
     */
    public boolean DelHostFromSQL( String IP ) {
        try {
            openConnection();
            String sql = "DELETE FROM hosts WHERE INET_NTOA(ip) = '" + IP + "'";
            PreparedStatement preparedStatement = connection.prepareStatement( sql );
            preparedStatement.executeUpdate();
            return true;
        } catch ( ClassNotFoundException | SQLException e ) {
            Tools.Prt( "Error DelHostFromSQL", programCode );
            return false;
        }
    }

    /**
     * IPアドレスからホスト名を取得する
     *
     * @param IP
     * @return
     */
    public String GetHost( String IP ) {
        try {
            openConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM hosts WHERE INET_NTOA(ip) = '" + IP + "';";
            ResultSet rs = stmt.executeQuery( sql );
            if ( rs.next() ) return rs.getString( "host" );
        } catch ( ClassNotFoundException | SQLException e ) {
            Tools.Prt( "Error GetUnknownHost", programCode );
        }
        return "Unknown";
    }
}
