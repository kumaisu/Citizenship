/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.config;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

/**
 *
 * @author sugichan
 */
public class Config {

    public static String programCode = "CS";

    public static String host = "local";
    public static String port = "3306";
    public static String database = "Citizenship";
    public static String username = "root";
    public static String password = "";

    public static Map< String, Map< String, Integer > > rankTime = new HashMap<>();
    public static List< String > rankName;
    public static int demotion = 0;

    public static boolean PromotBroadcast = false;
    public static String PrisonGroup = "";
    public static int Penalty = 0;

    public static boolean Imprisonment = false;
    public static float fx;
    public static float fy;
    public static float fz;
    public static float fyaw;
    public static float fpitch;
    public static String fworld;

    public static boolean Outprisonment = false;
    public static float rx;
    public static float ry;
    public static float rz;
    public static float ryaw;
    public static float rpitch;
    public static String rworld;
}
