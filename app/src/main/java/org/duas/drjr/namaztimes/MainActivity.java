package org.duas.drjr.namaztimes;

import org.duas.drjr.namaztimes.DailyTimeFragment.OnListFragmentInteractionListener;
import org.duas.drjr.namaztimes.dummy.DailyTimeContent;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnListFragmentInteractionListener {

    private OnListFragmentInteractionListener mListener;
    private List<DailyTimeContent.DailyTime> dailyTimeList;
    private RecyclerView recyclerView;
    private MyDailyTimeRecyclerViewAdapter dtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        prepareDailyTimeData();

        recyclerView = (RecyclerView) findViewById(R.id.dailytime_list_main);
        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new MyDailyTimeRecyclerViewAdapter(dailyTimeList, mListener));

    }

    private void prepareDailyTimeData() {

        dailyTimeList = new ArrayList<DailyTimeContent.DailyTime>();

        DailyTimeContent.DailyTime dailyTime;

        dailyTime = new DailyTimeContent.DailyTime("Sehri End", "Sehri End Time", "03:45am");
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Subh-e-Sadiq", "Astro Twilight Time", "03:50am");
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Fajar", "Azan Fajar Time", "04:00am");
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Zohar", "Azan Zohar Time", "12:15pm");
        dailyTimeList.add(dailyTime);

        dailyTime = new DailyTimeContent.DailyTime("Asar", "Azan Asar Time", "3:15pm");
        dailyTimeList.add(dailyTime);
    }

    @Override
    public void onListFragmentInteraction(DailyTimeContent.DailyTime item) {

    }
}
