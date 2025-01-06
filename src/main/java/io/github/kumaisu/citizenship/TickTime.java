/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.citizenship;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

/**
 *
 * @author sugichan
 */
public class TickTime {
    
    public static int get( Player player ) {
        try {
            return player.getStatistic( Statistic.valueOf( "PLAY_ONE_MINUTE" ) );
        } catch( IllegalArgumentException e ) {
            return player.getStatistic( Statistic.valueOf( "PLAY_ONE_TICK" ) );
        }
    }
    
}
