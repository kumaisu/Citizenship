/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.citizenship.database;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Connection;

/**
 *
 * @author sugichan
 */
public class Database {
    public static Connection dataSource = null;
    public static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    public static String DB_URL = "";
    public static String name = "Unknown";
    public static Date logout;
    public static Date basedate;
    public static Date Rewards;
    public static int baseTick = 0;
    public static int offsetTick = 0;
    public static int jail = 0;
    public static int yellow = 0;
    public static int imprisonment = 0;
    public static int ReasonID = 0;
    
    public static String Reason = "";
    public static String enforcer = "";
    public static Date ReasonDate;

    public static String YellowName;
    public static Date YellowDate;
    public static String YellowCommand;
}
