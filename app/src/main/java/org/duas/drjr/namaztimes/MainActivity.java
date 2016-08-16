package org.duas.drjr.namaztimes;

import org.duas.drjr.namaztimes.DailyTimeFragment.OnListFragmentInteractionListener;
import org.duas.drjr.namaztimes.namaztime.CalculationMethod;
import org.duas.drjr.namaztimes.namaztime.DailyTimeContent;
import org.duas.drjr.namaztimes.namaztime.PrayTime;
import org.duas.drjr.namaztimes.namaztime.PrayerTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
//
//        dailyTime = new DailyTimeContent.DailyTime("Sehri End", "Sehri End Time", prayerTimes.get(0), prayerTimes2.get(0));
//        dailyTimeList.add(dailyTime);
//
//        dailyTime = new DailyTimeContent.DailyTime("Subh-e-Sadiq", "Astro Twilight Time", prayerTimes.get(0), prayerTimes2.get(0));
//        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Fajar", "Azan-Namaz Fajar Time", prayerTimes.get(0), prayerTimes2.get(0));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Sun Rise", "Sun Rise", prayerTimes.get(1), prayerTimes2.get(1));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Zohar", "Azan-Namaz Zohar Time", prayerTimes.get(2), prayerTimes2.get(2));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Asar", "Azan-Namaz Asar Time", prayerTimes.get(3), prayerTimes2.get(3));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Sun Set", "Sun Set", prayerTimes.get(4), prayerTimes2.get(4));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Maghrib", "Azan-Namaz Maghrib Time", prayerTimes.get(5), prayerTimes2.get(5));
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Isha", "Azan-Namaz Isha Time", prayerTimes.get(6), prayerTimes2.get(6));
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
