package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeRateActivity extends AppCompatActivity {

    private final String exchangeUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private LinearLayout exchangeContainer;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private final Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate);

        exchangeContainer = findViewById(R.id.exchange_ll_container);
        loadExchange();
    }
    private void loadExchange()
    {
        CompletableFuture
                .supplyAsync(this::getExchangeResponseAsString, threadPool)
                .thenApply(this::processExchangeResponse)
                .thenAccept(this::displayExchangeRate);
    }
    private String getExchangeResponseAsString(){
        try(InputStream urlStream = new URL(exchangeUrl).openStream())
        {
            return readString(urlStream);

        }catch( MalformedURLException ex ) {
            Log.e( "ExchangeRateActivity::loadExchange",
                    ex.getMessage() == null ? "MalformedURLException" : ex.getMessage() );
        }
        catch( IOException ex ) {
            Log.e( "ExchangeRateActivity::loadExchange",
                    ex.getMessage() == null ? "IOException" : ex.getMessage() );

        }
        return null;
    }
    private ExchangeRate[] processExchangeResponse(String jsonString) {
        return gson.fromJson(jsonString, ExchangeRate[].class);
    }
    private void displayExchangeRate(ExchangeRateActivity.ExchangeRate[] exchangeRate  ){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(10, 5, 10, 5);

        for( ExchangeRateActivity.ExchangeRate er : exchangeRate )
        {
            TextView tv = new TextView( ExchangeRateActivity.this );

            String displayText = "Валюта: " + er.getTxt() + "\n" +
                    "Курс: " + er.getRate() + "\n" +
                    "Код валюты: " + er.getCc() + "\n" +
                    "Дата курса: " + er.getExchangedate();

            tv.setText( displayText );
            tv.setPadding( 10, 5, 10, 5 );
            tv.setBackgroundColor( R.color.app_background );
            tv.setLayoutParams( layoutParams );
            runOnUiThread( () -> exchangeContainer.addView( tv ) ) ;}

    }
    @Override
    protected void onDestroy() {
        threadPool.shutdownNow();
        super.onDestroy();
    }
   class ExchangeRate {
        private int r030;
        private String txt;
        private double rate;
        private String cc;
        private String exchangedate;

        public ExchangeRate(int r030, String txt, double rate, String cc, String exchangedate) {
            this.r030 = r030;
            this.txt = txt;
            this.rate = rate;
            this.cc = cc;
            this.exchangedate = exchangedate;
        }
        public int getR030() {
            return r030;
        }
        public void setR030(int r030) {
            this.r030 = r030;
        }
        public String getTxt() {
            return txt;
        }
        public void setTxt(String txt) {
            this.txt = txt;
        }
        public double getRate() {
            return rate;
        }
        public void setRate(double rate) {
            this.rate = rate;
        }
        public String getCc() {
            return cc;
        }
        public void setCc(String cc) {
            this.cc = cc;
        }
        public String getExchangedate() {
            return exchangedate;
        }
        public void setExchangedate(String exchangedate) {
            this.exchangedate = exchangedate;
        }
        @Override
        public String toString() {
            return "ExchangeRate{" +
                    "r030=" + r030 +
                    ", txt='" + txt + '\'' +
                    ", rate=" + rate +
                    ", cc='" + cc + '\'' +
                    ", exchangedate='" + exchangedate + '\'' +
                    '}';
        }
    }
    private String readString(InputStream stream) throws IOException {
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
}