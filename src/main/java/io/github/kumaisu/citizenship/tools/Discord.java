package io.github.kumaisu.citizenship.tools;

import io.github.kumaisu.citizenship.Lib.Tools;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import static io.github.kumaisu.citizenship.config.Config.programCode;

/**
 *
 * @author NineTailedFox
 */
public class Discord {

    public static void sendMessage( String URL, String name, String Message ) {
        if ( URL.equals( "NONE" ) ) {
            return;
        }
        try ( CloseableHttpClient client = HttpClients.createDefault() ) {
            HttpPost post = new HttpPost( URL );
            post.setHeader("Content-Type", "application/json; charset=UTF-8");

            String regex = "§."; // '%$'の後に任意の1文字
            String msg = Message.replaceAll( regex, "" );
            String json = String.format( "{\"username\": \"%s\",\"content\": \"%s\"}", name, msg );
            StringEntity entity = new StringEntity( json, "UTF-8" );
            post.setEntity( entity );

            try (CloseableHttpResponse response = client.execute(post)) {
                Tools.Prt( "メッセージが送信されました。ステータスコード: " + response.getStatusLine().getStatusCode(), Tools.consoleMode.max, programCode );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
