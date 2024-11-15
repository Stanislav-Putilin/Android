package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private final String authorInfoFilename = "author_info.chat";
    private ImageButton saveAuthorNameButton;
    private ImageButton sendBtn;
    private String authorName;
    private final String chatUrl = "https://chat.momentfor.fun/";
    private TextView tvTitle;
    private LinearLayout chatContainer;
    private ScrollView chatScroller;
    private EditText etAuthor;
    private EditText etMessage;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private final Gson gson = new Gson();
    private static final SimpleDateFormat sqlDateFormat = new SimpleDateFormat(
           "yyyy-MM-dd HH:mm:ss", Locale.ROOT
    );
    private final Handler handler = new Handler();
    private final List<ChatMessage> messages = new ArrayList<>();

    private Animation bellAnimation;
    private View vBell;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvTitle = findViewById(R.id.chat_tv_title);
        chatContainer = findViewById(R.id.chat_ll_container);
        chatScroller = findViewById(R.id.chat_scroller);
        etAuthor = findViewById(R.id.chat_et_author);
        etMessage = findViewById(R.id.chat_et_massage);
        bellAnimation = AnimationUtils.loadAnimation(this, R.anim.bell);
        vBell = findViewById(R.id.chat_bell);

        saveAuthorNameButton = findViewById(R.id.chat_btn_authorName);
        sendBtn = findViewById(R.id.chat_btn_send);

        sendBtn.setOnClickListener(this::sendButtonClick);
        saveAuthorNameButton.setOnClickListener(this::saveNameAuthorButtonClick);

        sendBtn.setVisibility(View.GONE);

        loadAuthorNameFromFile();

        if(authorName != null && !authorName.trim().isEmpty())
        {
            etAuthor.setText(authorName);
            saveAuthorNameButton.setVisibility(View.GONE);
            sendBtn.setVisibility(View.VISIBLE);
        }


        handler.post(this::periodic);
        chatScroller.addOnLayoutChangeListener(
                (View v,
                 int left,    int top,    int right,    int bottom,
                 int leftWas, int topWas, int rightWas, int bottomWas) ->
                        chatScroller.post( ()-> chatScroller.fullScroll( View.FOCUS_DOWN )));
    }

    private void periodic(){
        loadChat();
        handler.postDelayed(this::periodic, 3000);
    }

    private void sendButtonClick(View view){
        String author = etAuthor.getText().toString();

        if(author.isEmpty()){
            Toast.makeText(this,"Empty author",Toast.LENGTH_SHORT).show();
            return;}

        String message = etMessage.getText().toString();
        if(message.isEmpty()){
            Toast.makeText(this,"Empty message",Toast.LENGTH_SHORT).show();
            return;}

        CompletableFuture.runAsync(()->
                sendChatMessage(
                        new ChatMessage()
                                .setAuthor(author)
                                .setText(message)
                                .setMoment(sqlDateFormat.format((new Date())))),
                threadPool
        );
    }

    private void sendChatMessage(ChatMessage chatMessage){

        try {
            URL url = new URL(chatUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setChunkedStreamingMode(0); //одним пакетом(не делить)
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Connection", "Close");

            OutputStream bodyStream = connection.getOutputStream();

            bodyStream.write(
            String.format("author=%s&msg=%s",
                    URLEncoder.encode(chatMessage.getAuthor(),StandardCharsets.UTF_8.name()),
                    URLEncoder.encode(chatMessage.getText(),StandardCharsets.UTF_8.name())
                    ).getBytes(StandardCharsets.UTF_8));

            bodyStream.flush(); //передача запроса
            bodyStream.close();

            int statusCode = connection.getResponseCode();
            if(statusCode >= 200 && statusCode < 300){
                Log.i("sendChatMessage", "Message sent");
                loadChat();
            }
            else
            {
                InputStream responseStream = connection.getErrorStream();

                Log.e("sendChatMessage", readString(responseStream));

                responseStream.close();
            }
            connection.disconnect();
        }catch (Exception ex)
        {
            Log.e("sendChatMessage", ex.getMessage() == null ? ex.getClass().toString() : ex.getMessage());
        }
    }

    private void loadChat()
    {
        CompletableFuture
                .supplyAsync(this::getChatAsString, threadPool)
                .thenApply(this::processChatResponse)
                .thenAccept(m -> runOnUiThread(() -> displayChatMessages(m)));
    }

    private ChatMessage[] processChatResponse( String jsonString ) {
        ChatResponse chatResponse = gson.fromJson( jsonString, ChatResponse.class );
        return chatResponse.data;
    }

    private void displayChatMessages(ChatMessage[] chatMessages  ){

        boolean wasNew = false;
        for( ChatMessage cm : chatMessages )
        {
            if(messages.stream().noneMatch(m->m.getId().equals(cm.getId()))){
                messages.add(cm);
                wasNew = true;
            }
        }

        if(!wasNew) return;

        messages.sort(Comparator.comparing(ChatMessage::getMoment));

        Drawable bgOther = getResources().getDrawable(R.drawable.chat_msg_other, getTheme());
        Drawable bgAuthor = getResources().getDrawable(R.drawable.chat_msg_author, getTheme());

        //runOnUiThread(()-> chatContainer.removeAllViews());

        for( ChatMessage m : messages )
        {
            if(m.getView() != null) continue;

            LinearLayout linearLayout = new LinearLayout(ChatActivity.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            TextView tv = new TextView( ChatActivity.this );
            tv.setText( m.getAuthor() + " " + m.getMoment());

            tv.setPadding( 30, 15, 30, 5 );
            linearLayout.addView(tv);

            tv = new TextView( ChatActivity.this );
            tv.setText( m.getText());
            tv.setPadding( 20, 5, 20, 5 );
            linearLayout.addView(tv);

            boolean isAuthor = m.getAuthor().equals(etAuthor.getText().toString());
            linearLayout.setBackground( isAuthor ? bgAuthor : bgOther );

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            if (isAuthor) {
                layoutParams.setMargins(50, 5, 20, 5);
                layoutParams.gravity = Gravity.END;
            } else {
                layoutParams.setMargins(20, 5, 50, 5);
                layoutParams.gravity = Gravity.START;
            }

            linearLayout.setLayoutParams( layoutParams );
            m.setView(linearLayout);
            chatContainer.addView(linearLayout);
        }
        chatContainer.post(()->
        {
            chatScroller.fullScroll(View.FOCUS_DOWN);
            vBell.startAnimation(bellAnimation);
        });
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
        handler.removeCallbacksAndMessages(null);
        threadPool.shutdownNow();
        super.onDestroy();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            EditText focusedView = getCurrentFocus() instanceof EditText ? (EditText) getCurrentFocus() : null;
            if (focusedView != null) {
                int[] location = new int[2];
                focusedView.getLocationOnScreen(location);
                float x = ev.getRawX();
                float y = ev.getRawY();

                if (x < location[0] || x > location[0] + focusedView.getWidth() ||
                        y < location[1] || y > location[1] + focusedView.getHeight())
                {
                    hideKeyboard();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null && imm != null)
        {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void saveNameAuthorButtonClick(View view)
    {
        saveNameAuthorButtonClick();
    }

    private void saveNameAuthorButtonClick()
    {
        if(!etAuthor.getText().toString().trim().isEmpty())
        {
            try(FileOutputStream fos = openFileOutput(authorInfoFilename, Context.MODE_PRIVATE)) {
                DataOutputStream writer = new DataOutputStream(fos);
                writer.writeUTF(etAuthor.getText().toString());
                writer.flush();
                writer.close();
                authorName = etAuthor.getText().toString();
                saveAuthorNameButton.setVisibility(View.GONE);
                sendBtn.setVisibility(View.VISIBLE);
            } catch (IOException ex) {
                Log.e("GameActivity::saveBestScore", ex.getMessage() != null ? ex.getMessage() : "Error writing file" );
            }
        }else{
            Toast.makeText(ChatActivity.this, "Enter name author!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAuthorNameFromFile(){
        try(FileInputStream fis = openFileInput(authorInfoFilename)) {
            DataInputStream read = new DataInputStream(fis);
            authorName = read.readUTF();
            read.close();
        } catch (IOException ex) {
            Log.e("ChatActivity::loadAuthorNameFromFile", ex.getMessage() != null ? ex.getMessage() : "Error reading file" );
        }
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
       private  View view;

        public void setView(View view) {
            this.view = view;
        }

        public View getView() {
            return view;
        }

        private String id;
        private String author;
        private String text;
        private String moment;

        public void setId(String id) {
            this.id = id;
        }

        public ChatMessage setAuthor(String author) {
            this.author = author;
            return this;
        }

        public ChatMessage setText(String text) {
            this.text = text;
            return this;
        }

        public ChatMessage setMoment(String moment) {
            this.moment = moment;
            return this;
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