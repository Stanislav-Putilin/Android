package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private final String chatUrl = "https://chat.momentfor.fun/";
    private TextView tvTitle;
    private LinearLayout chatContainer;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tvTitle = findViewById(R.id.chat_tv_title);
        chatContainer = findViewById(R.id.chat_ll_container);
        loadChat();
    }

    private void loadChat()
    {
        CompletableFuture
                .supplyAsync(this::getChatAsString, threadPool)
                .thenApply(this::processChatResponse)
                .thenAccept(this::displayChatMessages);
    }

    private ChatMessage[] processChatResponse( String jsonString ) {
        ChatResponse chatResponse = gson.fromJson( jsonString, ChatResponse.class );
        return chatResponse.data;
    }

    private void displayChatMessages(ChatMessage[] chatMessages  ){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);layoutParams.setMargins(10, 5, 10, 5);for( ChatMessage cm : chatMessages ) {    TextView tv = new TextView( ChatActivity.this );    tv.setText( cm.getAuthor() + cm.getText() );    tv.setPadding( 10, 5, 10, 5 );    tv.setBackgroundColor( R.color.app_background );    tv.setLayoutParams( layoutParams );    runOnUiThread( () -> chatContainer.addView( tv ) ) ;}

    }

    private String getChatAsString(){
        try(InputStream urlStream = new URL(chatUrl).openStream())
        {
            return readString(urlStream);

        }catch( MalformedURLException ex ) {
            Log.e( "ChatActivity::loadChat",
                    ex.getMessage() == null ? "MalformedURLException" : ex.getMessage() );
        }
        catch( IOException ex ) {
            Log.e( "ChatActivity::loadChat",
                    ex.getMessage() == null ? "IOException" : ex.getMessage() );

        }
        return null;
    }

    @Override
    protected void onDestroy() {
        threadPool.shutdownNow();
        super.onDestroy();
    }

    private String readString(InputStream stream) throws IOException{
        ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while((len = stream.read(buffer))!= -1){
            byteBuilder.write(buffer,0,len);
        }
        String res = byteBuilder.toString(StandardCharsets.UTF_8.name());
        byteBuilder.close();
        return res;
    }
    class ChatResponse{
        private int status;
        private ChatMessage[] data;

        public void setStatus(int status) {
            this.status = status;
        }

        public void setData(ChatMessage[] data) {
            this.data = data;
        }

        public int getStatus() {
            return status;
        }

        public ChatMessage[] getData() {
            return data;
        }
    }
    class ChatMessage{
        private String id;
        private String author;
        private String text;
        private String moment;

        public void setId(String id) {
            this.id = id;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setMoment(String moment) {
            this.moment = moment;
        }

        public String getId() {
            return id;
        }

        public String getAuthor() {
            return author;
        }

        public String getText() {
            return text;
        }

        public String getMoment() {
            return moment;
        }
    }
}

