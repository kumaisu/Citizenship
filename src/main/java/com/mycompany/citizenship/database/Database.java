/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.database;

import java.util.Date;
import com.zaxxer.hikari.HikariDataSource;

/**
 *
 * @author sugichan
 */
public class Database {
    public static HikariDataSource dataSource = null;

    public static String name = "Unknown";
    public static Date logout;
    public static Date basedate;
    public static int tick = 0;
    public static int offset = 0;
    public static int jail = 0;
    public static int imprisonment = 0;
}
