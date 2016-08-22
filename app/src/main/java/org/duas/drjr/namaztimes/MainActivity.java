package org.duas.drjr.namaztimes;

import org.duas.drjr.namaztimes.DailyTimeFragment.OnListFragmentInteractionListener;
import org.duas.drjr.namaztimes.namaztime.CalculationMethod;
import org.duas.drjr.namaztimes.namaztime.DailyTimeContent;
import org.duas.drjr.namaztimes.namaztime.DayPoint;
import org.duas.drjr.namaztimes.namaztime.PrayTime;
import org.duas.drjr.namaztimes.namaztime.PrayerTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
            Log.d("pref", "Preference is clicked!");
        } else if (item.getItemId() == R.id.location) {
            Log.d("Location", "Location is clicked!");
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareDailyTimeData() {
        // Test Prayer times here
        PrayTime prayers = new PrayTime();
        PrayerTime prayer2 = new PrayerTime();
        prayers.setTimeFormat(1);
        prayers.setCalcMethod(0);
        prayers.setAsrJuristic(0);
        prayers.setAdjustHighLats(3);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);
        ArrayList<String> prayerTimes2 = prayer2.getPrayerTimes(cal, latitude, longitude, timezone);

        dailyTimeList = new ArrayList<DailyTimeContent.DailyTime>();

        DailyTimeContent.DailyTime dailyTime;

        dailyTime = new DailyTimeContent.DailyTime("Sawm Start", "Start of Fasting", prayerTimes.get(0), prayerTimes2.get(DayPoint.FastingStart.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Twilight", "Astro Twilight", prayerTimes.get(0), prayerTimes2.get(DayPoint.AstroTwilight.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Fajar", "Azan-Namaz Fajar Time", prayerTimes.get(0), prayerTimes2.get(DayPoint.Fajr.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Sun Rise", "Sun Rise", prayerTimes.get(1), prayerTimes2.get(DayPoint.Sunrise.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Zohar", "Azan-Namaz Zohar Time", prayerTimes.get(2), prayerTimes2.get(DayPoint.Dhuhr.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Asar", "Azan-Namaz Asar Time", prayerTimes.get(3), prayerTimes2.get(DayPoint.Asr.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Sun Set", "Sun Set", prayerTimes.get(4), prayerTimes2.get(DayPoint.Sunset.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Maghrib", "Azan-Namaz Maghrib Time", prayerTimes.get(5), prayerTimes2.get(DayPoint.Maghrib.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Isha", "Azan-Namaz Isha Time", prayerTimes.get(6), prayerTimes2.get(DayPoint.Isha.ordinal()));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("MidNight", "Namaz Isha Qaza", prayerTimes.get(6), prayerTimes2.get(DayPoint.Midnight.ordinal()));
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
