/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.util.Date;
import com.zaxxer.hikari.HikariDataSource;
import java.text.SimpleDateFormat;

/**
 *
 * @author sugichan
 */
public class Database {
    public static HikariDataSource dataSource = null;
    public static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    public static String name = "Unknown";
    public static Date logout;
    public static Date basedate;
    public static int tick = 0;
    public static int offset = 0;
    public static int jail = 0;
    public static int imprisonment = 0;
    public static int ReasonID = 0;
    
    public static String Reason = "";
    public static String enforcer = "";
    public static Date ReasonDate;
}
