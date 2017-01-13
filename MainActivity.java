package com.example.arunbhaskar.arlocation;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.URL;

import Util.Utils;
import data.CityPreference;
import data.JasonWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity {
    private TextView cityName;
    private TextView temp;
    private TextView celsius;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    String[] City_World;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.cityText);
        temp = (TextView) findViewById(R.id.tempText);
        celsius=(TextView)findViewById(R.id.tempText1);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidityText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.sunriseText);
        sunset = (TextView) findViewById(R.id.sunsetText);
        updated = (TextView) findViewById(R.id.updateText);

        CityPreference cityPreference = new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());
    }
    // OnCreate End

    public void renderWeatherData(String city){
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(city + "&units=metric");

    }

    private class WeatherTask extends AsyncTask<String, Void, Weather>{

        @Override
        protected Weather doInBackground(String... strings) {
            String data = ( (new WeatherHttpClient()).getWeatherData(strings[0]));

            // Catch error if a city is entered with a space

            try {
                weather = JasonWeatherParser.getWeather(data);
            } catch (Exception NullWeatherData){
                Log.e("Error Parsing: ", "City has space in text");
            }
            if (weather == null){
                showChangeCityDialog();
            }
            return weather;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);


            //Convert times into readable formats
            //get unix times and multiply by 1000 to get proper length since it converts down to milliseconds
            long unixSunrise = weather.place.getSunrise() * 1000;
            long unixSunset  = weather.place.getSunset() * 1000;
            long unixUpdated = weather.place.getLastupdate() * 1000;
            //do the conversion
            java.util.Date sunriseDate = new java.util.Date(unixSunrise);
            java.util.Date sunsetDate  = new java.util.Date(unixSunset);
            java.util.Date updatedDate = new java.util.Date(unixUpdated);
            //Strip away everything but the time in 24hr time (use hh in place of kk or 12hour clock)
            //need to change sunset and rise times to time zone for where the location is not my local timezone
            String sunriseTime = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", sunriseDate));
            String sunsetTime  = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", sunsetDate));
            String updatedTime = String.valueOf(android.text.format.DateFormat.format("kk:mm:ss zzz", updatedDate));

            // Set Text for all items
            cityName.setText(weather.place.getCity() + ", " + weather.place.getCountry());
            temp.setText(weather.temperature.getTemp() + " °C");
            celsius.setText((int) (((weather.temperature.getTemp() * 9) / 5) + 32)+" °F");
            wind.setText("Wind: " + weather.wind.getSpeed() + " m/s");
            description.setText("Cloudiness: " + weather.currentCondition.getDescription());
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + " hPa");
            sunrise.setText("Sunrise: " + sunriseTime);
            sunset.setText("Sunset: " + sunsetTime);
            updated.setText("Last Updated: " + updatedTime);




        }
    }


    //--- Change city Dialog Start ---//
    private void showChangeCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");

        final AutoCompleteTextView cityInput =  new AutoCompleteTextView(MainActivity.this);
        cityInput.setMovementMethod(new ScrollingMovementMethod());
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        City_World=getResources().getStringArray(R.array.cities_world);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,City_World);
        cityInput.setAdapter(adapter);
        cityInput.setHint("Enter city ");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());

                // Remove Spaces in city string to avoid fatal error
                String newCity = cityPreference.getCity().replace(" ", "");

                renderWeatherData(newCity);

            }
        });
          builder.show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.change_cityId){
            showChangeCityDialog();
        }

        return super.onOptionsItemSelected(item);
    }
}
