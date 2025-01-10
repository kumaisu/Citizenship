package io.github.kumaisu.citizenship.command;

import io.github.kumaisu.citizenship.Citizenship;
import io.github.kumaisu.citizenship.Lib.Tools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.kumaisu.citizenship.config.Config.programCode;

/**
 * @author NineTailedFox
 */
public class GeneralCommand implements CommandExecutor {

    private final Citizenship instance;

    public GeneralCommand(Citizenship instance) {
        this.instance = instance;
    }

    /**
     * コマンド入力があった場合に発生するイベント
     *
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (sender instanceof Player) ? (Player) sender : (Player) null;

        Tools.Prt("CitizenShip General Command", Tools.consoleMode.max, programCode );

        if ( ( player != null ) && !player.hasPermission( "citizenship.fly" ) ) {
            Tools.Prt( player, ChatColor.RED + "操作権限がありません", Tools.consoleMode.normal, programCode );
            return false;
        } else {
            if ( player.getAllowFlight() ) {
                // 無効化
                player.setFlying( false );
                player.setAllowFlight( false );
                Tools.Prt(player, ChatColor.YELLOW + "Fly Mode Disabled", programCode);
            } else {
                // 飛行許可
                player.setAllowFlight( true );
                player.setFlySpeed( 0.1F );
                Tools.Prt( player, ChatColor.GREEN + "Fly Mode Enabled", programCode );
            }
            return true;
        }
    }
}
