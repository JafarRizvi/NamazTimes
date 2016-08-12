package org.duas.drjr.namaztimes;

import org.duas.drjr.namaztimes.DailyTimeFragment.OnListFragmentInteractionListener;
import org.duas.drjr.namaztimes.namaztime.DailyTimeContent;
import org.duas.drjr.namaztimes.namaztime.PrayTime;
import org.duas.drjr.namaztimes.namaztime.PrayerTime;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener {

    private OnListFragmentInteractionListener mListener;
    private List<DailyTimeContent.DailyTime> dailyTimeList;
    private RecyclerView recyclerView;
    private MyDailyTimeRecyclerViewAdapter dtAdapter;

    // ---------------------- Global Variables --------------------
    private int calcMethod; // caculation method
    private int asrJuristic; // Juristic method for Asr
    private int dhuhrMinutes; // minutes after mid-day for Dhuhr
    private int adjustHighLats; // adjusting method for higher latitudes
    private int timeFormat; // time format
    private double lat; // latitude
    private double lng; // longitude
    private double timeZone; // time-zone
    private double JDate; // Julian date
    // ------------------------------------------------------------
    // Calculation Methods
    private int Jafari; // Ithna Ashari
    private int Karachi; // University of Islamic Sciences, Karachi
    private int ISNA; // Islamic Society of North America (ISNA)
    private int MWL; // Muslim World League (MWL)
    private int Makkah; // Umm al-Qura, Makkah
    private int Egypt; // Egyptian General Authority of Survey
    private int Custom; // Custom Setting
    private int Tehran; // Institute of Geophysics, University of Tehran
    // Juristic Methods
    private int Shafii; // Shafii (standard)
    private int Hanafi; // Hanafi
    // Adjusting Methods for Higher Latitudes
    private int None; // No adjustment
    private int MidNight; // middle of night
    private int OneSeventh; // 1/7th of night
    private int AngleBased; // angle/60th of night
    // Time Formats
    private int Time24; // 24-hour format
    private int Time12; // 12-hour format
    private int Time12NS; // 12-hour format with no suffix
    private int Floating; // floating point number

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

        prepareDailyTimeData();

        recyclerView = (RecyclerView) findViewById(R.id.dailytime_list_main);
        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new MyDailyTimeRecyclerViewAdapter(dailyTimeList, mListener));
    }

    private void prepareDailyTimeData() {

        double latitude = 33.54711732937501;
        double longitude = 73.15452607031239;
        double timezone = 5;
        // Test Prayer times here
        PrayTime prayers = new PrayTime();
        PrayerTime prayer2 = new PrayerTime();
        prayers.setTimeFormat(Time12);
        prayers.setCalcMethod(Jafari);
        prayers.setAsrJuristic(Shafii);
        prayers.setAdjustHighLats(AngleBased);
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
    }

    @Override
    public void onListFragmentInteraction(DailyTimeContent.DailyTime item) {

    }
}
