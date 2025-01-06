/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kumaisu.citizenship.tools;

import java.util.Date;
import java.util.Random;
import org.bukkit.Sound;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import io.github.kumaisu.citizenship.Lib.Tools;
import io.github.kumaisu.citizenship.Lib.Utility;
import io.github.kumaisu.citizenship.config.Config;
import io.github.kumaisu.citizenship.config.Reward;
import io.github.kumaisu.citizenship.database.Database;
import io.github.kumaisu.citizenship.database.PlayerData;

/**
 *
 * @author sugichan
 */
public class Rewards {

    public static void Reward( Player player ) {
        Tools.Prt( ChatColor.YELLOW + "Daily Rewards !!", Tools.consoleMode.full, Config.programCode );
        PlayerData.SetRewardDate( player.getUniqueId() );

        if ( Reward.sound_play ) {
            Tools.Prt( "Sound Play !!", Tools.consoleMode.full, Config.programCode );
            ( player.getWorld() ).playSound(
                player.getLocation(),                   // 鳴らす場所
                Sound.valueOf( Reward.sound_type ),     // 鳴らす音
                Reward.sound_volume,                    // 音量
                Reward.sound_pitch                      // 音程
            );
        }

        Tools.Prt( player, Utility.ReplaceString( Reward.basic_message, player.getName() ), Config.programCode );
        Reward.basic_command.stream().forEach( BR -> {
            String BRC = Utility.ReplaceString( BR, player.getName() );
            Tools.ExecOtherCommand( player, BRC, "" );
            Tools.Prt( ChatColor.AQUA + "Command Execute : " + ChatColor.WHITE + BRC, Tools.consoleMode.max, Config.programCode );
        } );

        if ( Reward.advance_command.size() > 0 ) {
            Random random = new Random();
            int randomValue = random.nextInt( Reward.advance_command.size() + 1 );
            Tools.Prt( "Advance : " + Reward.advance_command.size() + " ( " + randomValue + ")", Tools.consoleMode.full, Config.programCode );
            if ( randomValue > 0 ) {
                Tools.Prt( player, Utility.ReplaceString( Reward.advance_message, player.getName() ), Config.programCode );
                String AR = Utility.ReplaceString( Reward.advance_command.get( randomValue - 1 ), player.getName() );
                Tools.ExecOtherCommand( player, AR, "" );
                Tools.Prt( ChatColor.AQUA + "Command Execute : " + ChatColor.WHITE + AR, Tools.consoleMode.max, Config.programCode );
            }
        }
    }

    public static void CheckRewards( Player player ) {
        //  Daily Rewards の判定
        PlayerData.GetSQL( player.getUniqueId() );
        int progress = Utility.dateDiff( Database.Rewards, new Date() );
        if ( progress >= 1 ) {
            Tools.Prt( "Rewards distribution : " + progress, Tools.consoleMode.full, Config.programCode );
            Reward( player );
        } else {
            Tools.Prt( "Reward Progress   : " + progress, Tools.consoleMode.full, Config.programCode );
            long dateTimeTo = new Date().getTime();
            long dateTimeFrom = Database.Rewards.getTime();
            long dayDiff = dateTimeTo - dateTimeFrom;
            Tools.Prt( "Current time      : " + dateTimeTo, Tools.consoleMode.max, Config.programCode );
            Tools.Prt( "Last distribution : " + dateTimeFrom, Tools.consoleMode.max, Config.programCode );
            Tools.Prt( "Differential time : " + dayDiff, Tools.consoleMode.max, Config.programCode );
            //  純粋に時間なのでms秒での数値を時間に修正
            int NextTime = ( int ) Math.round( dayDiff / 1000 / 60 );
            Tools.Prt( player,
                ChatColor.YELLOW + "次のREWARDまで " +
                ChatColor.AQUA + ( 1440 - NextTime ) +
                ChatColor.YELLOW + " 分です" ,
                Tools.consoleMode.max,
                Config.programCode
            );
        }
    }
}
