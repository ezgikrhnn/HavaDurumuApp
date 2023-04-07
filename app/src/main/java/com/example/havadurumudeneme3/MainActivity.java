package com.example.havadurumudeneme3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    EditText etcity, etCountry;
    TextView tvResultId;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "5c9da24c383ffa322d1fd81c4fc5c728";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etcity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        tvResultId = findViewById(R.id.tvResultId);

    }

    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String city = etcity.getText().toString().trim(); //trim metnin sonunda ve başındaki boşlukları yok eder, böylece daha sağlıklı bir arama yapılabilir.
        String country = etCountry.getText().toString().trim();

        if(city.equals("")){ //eger şehir boşsa, girilmemişse error yazısı göndersin
            tvResultId.setText("City field can not be empty");
        }else{
            if(!country.equals("")){
                tempUrl = url + "?q=" +city+ "," +country+ "&appid=" +appid;
            }else{
                tempUrl = url + "?q=" +city+ "&appid=" +appid;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                //stringResquest sınıfı bir dize isteği göndermek için kullanılır.Request.Method.POST bir POST isteği gönderildiğini belirtir.
                //tempUrl, hava durumu API'si için yapılan isteğin URL'sidir.
                //new Response.Listener<String>() bir yanıt dinleyicisidir ve hava durumu API'sinden gelen yanıtı işlemek için kullanılır.


                @Override
                public void onResponse(String response) {  //onResponse, hava durumu API'sinden başarılı bir yanıt aldığında tetiklenir.
                    // Log.d("response", response);
                    // response parametresi, API'den dönen yanıtın kendisidir.
                    //JSONObject sınıfı, bir JSON nesnesi oluşturmak için kullanılır.
                    //jsonResponse değişkeni, API'den dönen JSON yanıtını temsil eder.
                    //JSONArray sınıfı, bir JSON dizisine erişmek için kullanılır.
                    //jsonArray değişkeni, JSON yanıtındaki weather alanına erişir.
                    //jsonObjectWeather değişkeni, weather dizisinin ilk öğesine erişir.
                    //description değişkeni, description alanını temsil eder ve hava durumu açıklamasını içerir.
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);  //
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15; //kelvinden dolayı bu işlemi yaptım
                        double feelslike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        tvResultId.setTextColor(Color.rgb(68, 134,199));

                        output += " Current Weather of " +cityName+ " ("+countryName+")"
                                + "\n Temp:" +df.format(temp) + "°C"
                                + "\n Feels Like: " +df.format(feelslike) + " °C"
                                + "\n Humidity: " +humidity+ "%"
                                + "\n Description " +description
                                + "\n Wind Speed: " +wind+ "m/s"
                                + "\n Cloudiness " +clouds+ "%"
                                + "\n Pressure: " +pressure+ "hPa";
                        tvResultId.setText(output);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}
