/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.config;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sugichan
 */
public class Config {

    public static String programCode = "CR";

    public static String host = "local";
    public static String port = "3306";
    public static String database = "CitizenRank";
    public static String username = "root";
    public static String password = "";

    public static Map< String, Integer > rankTime = new HashMap<>();
    public static List< String > rankName;
}
