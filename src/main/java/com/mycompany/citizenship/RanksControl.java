/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.citizenship;

import java.util.Date;
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
     */
    public void Promotion() {
    }

    /**
     * 降格
     */
    public void Demotion(){
        Tools.Prt( "Demotion Process", programCode );
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

        //
        //  降格判定
        //
        if ( Config.demotion != 0 ) {
            int progress = Utility.dateDiff( MySQLControl.logout, new Date() );
            Tools.Prt( "Diff Date : " + progress, programCode);
            if ( progress > Config.demotion ) {
                Demotion();
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
        Tools.Prt( player, "[" + NowGroup + "]", Tools.consoleMode.full, programCode );
        
        //  Prisonerである
        //      ペナルティ時間チェック
        //          オフセットリセット
        //              rerutn true:
        //      Return false;
        
        //  通算時間ーオフセット時間
        //  昇格該当する
        //      昇格処理
        //      Promotion;
        //      return true;
        //  return false;
        return true;
    }
}
