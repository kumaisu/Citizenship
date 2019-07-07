# Citizenship
Citizenship Control Manager Repository  

## Overview  
  
Citizenship は、プレイヤーのログイン経過時間に応じて、Permissionグループを変更するプラグインです  
Citizenship is a plugin that changes the Permission group according to the player login elapsed time  
  
## Support  
Open a new issue here: [https//github.com/kumaisu/LoginControl/issues](https://github.com/kumaisu/Citizenship/issues)  
  
## Features  
ログインしていないプレイヤーの投獄処理  
Imprisonment processing for players who are not logged in  
  
## Releases  
Github projects have a "releases" link on their home page.  
If you still don't see it, [click here](https://github.com/kumaisu/Citizenship/releases) for PremisesEvent releases.  
  
## Wikis  
[Login Control Wiki](https://github.com/kumaisu/Citizenship/wiki)  
  
## Function
1.設定されたログイン時間を経過すると、PermissionExのコマンドでグループを変更します  
2.長期間ログインの無いプレイヤーに対してグループを変更します（降格などに利用）  
3.一時的にプレイヤーを作業不能にするグループを使って、投獄を行えます  
4.投獄期間を設定し、自動的に復帰できるようにします  
  
1. Change the group with PermissionEx command when the set login time has passed  
2. Change the group for players who have not logged in for a long time (use for demotion etc.)  
3. You can imprison using a group that temporarily renders players inoperable  
4. Set the period of imprisonment and allow it to return automatically  
  
## Usage  
  
/jail [player name] [reason]    : imprison
/jail [player name] release     : release
  
/ranks time [player name]
/ranks Reload  
/ranks Status  
/ranks Console [max/full/normal/none]  
  
**How to Install**  
1.サーバーのプラグインディレクトリに Citizenship.jar を入れて起動します  
2.一旦終了し、作成されたConfig.ymlを編集します  
3.config設定の通りMySQLにデーターベースをCreateします  
4.再度サーバーを起動  
  
Contact is Discord Kitsune#5955  
Discord Server https://discord.gg/AgX3UxR  
