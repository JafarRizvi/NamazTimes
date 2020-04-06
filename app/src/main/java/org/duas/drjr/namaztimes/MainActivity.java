package org.duas.drjr.namaztimes;

import org.duas.drjr.namaztimes.DailyTimeFragment.OnListFragmentInteractionListener;
import org.duas.drjr.namaztimes.namaztime.CalculationMethod;
import org.duas.drjr.namaztimes.namaztime.DailyTimeContent;
import org.duas.drjr.namaztimes.namaztime.DayPoint;
import org.duas.drjr.namaztimes.namaztime.PrayTime;
import org.duas.drjr.namaztimes.namaztime.PrayerTime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;


import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener {

    private float latitude;
    private float longitude;
    private float timezone;

    private OnListFragmentInteractionListener mListener;
    private List<DailyTimeContent.DailyTime> dailyTimeList;
    private RecyclerView recyclerView;
    private MyDailyTimeRecyclerViewAdapter dtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();

        // Now we display formattedDate value in TextView
        TextView txtView = (TextView) findViewById(R.id.timenow);
        txtView.setText(formattedDate);

        latitude = (float) 33.54711732937501;  // River Gardens
        longitude = (float) 73.15452607031239;
        timezone = 5;

        SharedPreferences settings = getSharedPreferences("LOCATION", 0);
        final EditText etLat = (EditText) findViewById(R.id.editLatitude);
        etLat.setText(Float.toString(settings.getFloat("LAT", latitude)));
        final EditText etLng = (EditText) findViewById(R.id.editLongitude);
        etLng.setText(Float.toString(settings.getFloat("LNG", longitude)));
        final EditText etTZ = (EditText) findViewById(R.id.editTimeZone);
        etTZ.setText(Float.toString(settings.getFloat("TZone", timezone)));

        Button button = (Button) findViewById(R.id.Calculate);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                latitude = Float.parseFloat(etLat.getText().toString());
                longitude = Float.parseFloat(etLng.getText().toString());
                timezone = Float.parseFloat(etTZ.getText().toString());
                prepareDailyTimeData();
            }
        });

        prepareDailyTimeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.daily_time_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pref) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.location) {
            Log.d("Location", "Location is clicked!");
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareDailyTimeData() {
        // Test Prayer times here
        PrayerTime prayer = new PrayerTime();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayer.getPrayerTimes(cal, latitude, longitude, timezone);

        dailyTimeList = new ArrayList<DailyTimeContent.DailyTime>();

        DailyTimeContent.DailyTime dailyTime;

        dailyTime = new DailyTimeContent.DailyTime("روزے کا وقت شروع", prayerTimes.get(DayPoint.FastingStart.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("صبح صادق", prayerTimes.get(DayPoint.AstroTwilight.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("اذان اور نماز فجر کا وقت", prayerTimes.get(DayPoint.Fajr.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("طلوع آفتاب", prayerTimes.get(DayPoint.Sunrise.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("نماز ظہر کا وقت", prayerTimes.get(DayPoint.Dhuhr.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("نماز عصر کا وقت", prayerTimes.get(DayPoint.Asr.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("غروب آفتاب", prayerTimes.get(DayPoint.Sunset.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("نماز مغرب اور روزہ کھولنے کا وقت", prayerTimes.get(DayPoint.Maghrib.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("نماز عشاء کا وقت", prayerTimes.get(DayPoint.Isha.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("نماز عشاء کا وقت", prayerTimes.get(DayPoint.Midnight.ordinal()));
        dailyTimeList.add(dailyTime);

        recyclerView = (RecyclerView) findViewById(R.id.dailytime_list_main);
        recyclerView.setAdapter(new MyDailyTimeRecyclerViewAdapter(dailyTimeList, mListener));
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences("LOCATION", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("LAT", latitude);
        editor.putFloat("LNG", longitude);
        editor.putFloat("TZone", timezone);
    }

    @Override
    public void onListFragmentInteraction(DailyTimeContent.DailyTime item) {

    }
}
