/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.mycompany.citizenship.Citizenship;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import static com.mycompany.citizenship.config.Config.programCode;
import net.milkbowl.vault.permission.Permission;

/**
 *
 * @author sugichan
 */
public class RankCommand implements CommandExecutor {

    private final Citizenship instance;

    public RankCommand( Citizenship instance ) {
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
    public boolean onCommand( CommandSender sender,Command cmd, String commandLabel, String[] args ) {
        Player player = ( sender instanceof Player ) ? ( Player )sender:( Player )null;

        if ( cmd.getName().toLowerCase().equalsIgnoreCase( "ranks" ) ) {
            String CtlCmd = "None";
            String CmdArg = "none";

            if ( args.length > 0 ) CtlCmd = args[0];
            if ( args.length > 1 ) CmdArg = args[1];

            switch ( CtlCmd ) {
                case "reload":
                    instance.config.load();
                    Tools.Prt( player, Utility.ReplaceString( "%$aLoginList Config Reloaded." ), programCode );
                    return true;
                case "status":
                    instance.config.Status( player );
                    return true;
                case "Console":
                    Tools.setDebug( CmdArg, programCode );
                    Tools.Prt( player,
                        ChatColor.GREEN + "System Debug Mode is [ " +
                        ChatColor.RED + Tools.consoleFlag.get( programCode ) +
                        ChatColor.GREEN + " ]",
                        programCode
                    );
                    break;
                case "Time":
                    Tools.Prt( player, "Player Times:", programCode );
                    Player lookUser = Bukkit.getServer().getPlayer( CmdArg );
                    if ( lookUser != null ) {
                        Tools.Prt( player, "PlayTime = " + Float.toString( ( float ) ( ( float ) lookUser.getStatistic( Statistic.PLAY_ONE_MINUTE ) * 0.05 / 60 / 60)) + " h" , programCode );
                        Tools.Prt( player, "PlayTime = " + Float.toString( ( float ) lookUser.getStatistic( Statistic.PLAY_ONE_MINUTE ) ) + " Ticks(0.05sec)", programCode );
                    } else { Tools.Prt( player, ChatColor.RED + "Player が Offline か存在しません", programCode ); }
                    break;
                case "Groups":
                    Tools.Prt( player, "Player Groups:", programCode );
                    Player lookPlayer = Bukkit.getServer().getPlayer( CmdArg );
                    if ( lookPlayer != null ) {
                        Permission perm = null;
                        RegisteredServiceProvider<Permission> permissionProvider = instance.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
                        if (permissionProvider != null) {
                            perm = permissionProvider.getProvider();
                        }
                        if ( perm != null ) { for ( String StrItem1 : perm.getPlayerGroups( lookPlayer ) ) Tools.Prt( player, "[" + StrItem1 + "]", programCode ); }
                        //PermissionUser user = PermissionEx.getUser( lookPlayer );
                    } else { Tools.Prt( player, ChatColor.RED + "Player が Offline か存在しません", programCode ); }
                    break;
                default:
                    return false;
            }
            return true;
        }
        return false;
    }

}
