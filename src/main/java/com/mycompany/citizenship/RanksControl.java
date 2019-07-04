/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import com.mycompany.citizenship.config.Config;
import com.mycompany.citizenship.database.MySQLControl;
import com.mycompany.kumaisulibraries.Tools;
import com.mycompany.kumaisulibraries.Utility;
import static com.mycompany.citizenship.config.Config.programCode;
import net.milkbowl.vault.permission.Permission;

/**
 *
 * @author sugichan
 */
public class RanksControl {

    private final Plugin plugin;
    private final MySQLControl DBRec;

    public RanksControl( Plugin plugin ){
        this.plugin = plugin;
        DBRec = new MySQLControl();
    }

    /**
     * 昇格
     *
     * @param player
     */
    public void Promotion( Player player ) {
        Tools.Prt( "Promotion Process", Tools.consoleMode.full, programCode );
        //  ランクを上げ
    }

    /**
     * 降格
     *
     * @param player
     */
    public void Demotion( Player player ){
        Tools.Prt( "Demotion Process", Tools.consoleMode.full, programCode );
        //  ランクを下げ
        //  Offsetを再セットする
    }

    /**
     * 具体的に、昇格・降格を判断処理するメソッド
     *
     * @param player
     * @return 
     */
    public boolean CheckRank( Player player ) {
        //
        //  DBからデータ取得、無ければ初期化および新規登録
        //
        if ( !DBRec.GetSQL( player.getUniqueId() ) ) {
            Tools.Prt( "New Database Entry", Tools.consoleMode.full, programCode );
            DBRec.AddSQL( player );
        }

        int progress = Utility.dateDiff( MySQLControl.logout, new Date() );
        int checkHour = ( int ) Math.round( ( player.getStatistic( Statistic.PLAY_ONE_MINUTE ) - MySQLControl.offset ) * 0.05 / 60 / 60 );

        //
        //  降格判定
        //
        if ( Config.demotion != 0 ) {
            Tools.Prt( "Diff Date : " + progress, programCode );
            if ( progress > Config.demotion ) {
                Demotion( player );
                return true;
            }
        }

        //
        //  グループ取得
        //
        Permission perm = null;
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration( net.milkbowl.vault.permission.Permission.class );
        if (permissionProvider != null) {
            perm = permissionProvider.getProvider();
        }
        if ( perm == null ) {
            Tools.Prt( "Cant get Group", programCode );
            return false;
        }
        
        for ( String StrItem1 : perm.getPlayerGroups( player ) ) Tools.Prt( "[" + StrItem1 + "]", Tools.consoleMode.full, programCode );

        String NowGroup = perm.getPlayerGroups( player )[0];
        Tools.Prt( player, "NowGroup [" + NowGroup + "]", Tools.consoleMode.full, programCode );

        //
        //  ペナルティユーザーに対する処理
        //
        if ( NowGroup.equals( Config.Prison ) ) {
            if ( progress > Config.Penalty ) {
                //  リセット
                String Cmd = "pex user " + player.getDisplayName() + " group set " + Config.rankName.get( 0 );
                Tools.Prt( "Command : " + Cmd, Tools.consoleMode.full, programCode );
                Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), Cmd );
                DBRec.SetOffsetToSQL( player.getUniqueId(), player.getStatistic( Statistic.PLAY_ONE_MINUTE ) );
                return true;
            }
            return false;
        }

        //
        //  経過時間によるユーザーの昇格処理
        //
        Tools.Prt( player,
            ChatColor.YELLOW + "貴方の通算接続時間は " +
            ChatColor.AQUA + checkHour +
            ChatColor.YELLOW + " 時間です" ,
            programCode
        );

        Tools.Prt( "Check Time " + Config.rankTime.get( NowGroup ) + " : " + checkHour, Tools.consoleMode.full, programCode );
        if ( Config.rankTime.get( NowGroup ) > checkHour ) {
            Promotion( player );
            return true;
        }

        return false;
    }
}
