/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import static org.bukkit.Bukkit.getWorld;
import com.mycompany.citizenship.config.Config;
import com.mycompany.kumaisulibraries.Tools;
import static com.mycompany.citizenship.RanksControl.setGroup;
import static com.mycompany.citizenship.config.Config.programCode;

/**
 *
 * @author sugichan
 */
public class PlayerControl {

    /**
     * 投獄処理
     *
     * @param player
     * @param Reason
     * @return 
     */
    public static boolean toJail( Player player, String Reason ) {
        boolean retStat = false;

        //  降格処理
        if ( !Config.Prison.equals( "" ) ) {
            Tools.Prt( "Demotion Citizenship", Tools.consoleMode.full, programCode );
            setGroup( player, Config.Prison );
            retStat = true;
        }

        //  投獄処理
        if ( Config.Imprisonment ) {
            JailTeleport( player );
            player.sendTitle(
                ChatColor.RED + "投獄されました",
                ChatColor.YELLOW + Reason,
                0, 100, 0 );
            Bukkit.broadcastMessage( ChatColor.RED + player.getDisplayName() + " さんは、投獄されました" );
            retStat = true;
        }

        return retStat;
    }

    /**
     * 釈放処理
     *
     * @param player
     * @return 
     */
    public static boolean outJail( Player player ) {
        boolean retStat = false;

        setGroup( player, Config.rankName.get( 0 ) );
        ReleaseTeleport( player );

        return retStat;
    }
    
    /**
     * 牢獄エリアへの強制転送コマンド
     *
     * @param player 
     */
    public static void JailTeleport( Player player ) {
        Tools.Prt( player.getName() + " is JAIL to teleport", Tools.consoleMode.normal, programCode );
        World world = getWorld( Config.fworld );
        Location loc = new Location( world, Config.fx, Config.fy, Config.fz );
        loc.setPitch( Config.fpitch );
        loc.setYaw( Config.fyaw );
        player.teleport( loc );
    }

    /**
     * 釈放エリアへの強制転送コマンド
     *
     * @param player 
     */
    public static void ReleaseTeleport( Player player ) {
        Tools.Prt( player.getName() + " is JAIL from teleport", Tools.consoleMode.normal, programCode );
        World world = getWorld( Config.rworld );
        Location loc = new Location( world, Config.rx, Config.ry, Config.rz );
        loc.setPitch( Config.rpitch );
        loc.setYaw( Config.ryaw );
        player.teleport( loc );
    }
}
