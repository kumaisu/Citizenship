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

    public static String programCode = "CR";

    public static String host = "local";
    public static String port = "3306";
    public static String database = "Citizenship";
    public static String username = "root";
    public static String password = "";

    public static Map< String, Integer > rankTime = new HashMap<>();
    public static List< String > rankName;
    public static int demotion = 0;

    public static boolean PromotBroadcast = false;
    public static String Prison = "";
    public static int Penalty = 0;
    
    /*
#   剥奪時、牢獄エリアにジャンプさせるか？
Imprisonment: false
#   牢獄エリアの座標
world: world
x: 0
y: 0
z: 0
pitch: 0
yaw: 0
    */
}
