package com.ngm.smartot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.ngm.smartot.Model.Action;
import com.ngm.smartot.db.DatabaseAdapter;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.Vector;

public class StatisticsActivity extends ActionBarActivity {
    Button lifxButton,hueButton;
    Context context;
    GridView gridView;
    DatabaseAdapter dbAdapter;
    SharedPreferences sharedPref;
    boolean firstRun = true;
    ActionBar actionBar;
    private Menu menu;
    private BroadcastReceiver receiver;
    PieChart piechart;
    Vector<Action> actions;
    int fe = 0, wf = 0, we = 0, hs = 0, hp = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.smart_home_icon);
        actionBar.setTitle(" Smart OT");

        context = this;
        dbAdapter = new DatabaseAdapter(context);

        lifxButton = (Button) findViewById(R.id.lifxButton);
        hueButton = (Button) findViewById(R.id.hueButton);
        piechart = (PieChart) findViewById(R.id.piechart);

        lifxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions = dbAdapter.getActionsByDevice("lifx");
                Log.i("actions", actions.toString());
                fe = 0; wf = 0; we = 0; hs = 0; hp = 0;
                for (Action action : actions) {
                    if (action.getAction().equalsIgnoreCase("Finger Extension")){
                        fe++;
                    } else if (action.getAction().equalsIgnoreCase("Wrist Flexion")){
                        wf++;
                    } else if (action.getAction().equalsIgnoreCase("Wrist Extension")){
                        we++;
                    } else if (action.getAction().equalsIgnoreCase("Hand Supination")){
                        hs++;
                    } else if (action.getAction().equalsIgnoreCase("Hand Pronation")){
                        hp++;
                    }
                }
                setGraph();
            }
        });

        hueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions = dbAdapter.getActionsByDevice("hue");
                Log.i("hue actions", actions.toString());
                fe = 0; wf = 0; we = 0; hs = 0; hp = 0;
                for (Action action : actions) {
                    if (action.getAction().equalsIgnoreCase("Finger Extension")){
                        fe++;
                    } else if (action.getAction().equalsIgnoreCase("Wrist Flexion")){
                        wf++;
                    } else if (action.getAction().equalsIgnoreCase("Wrist Extension")){
                        we++;
                    } else if (action.getAction().equalsIgnoreCase("Hand Supination")){
                        hs++;
                    } else if (action.getAction().equalsIgnoreCase("Hand Pronation")){
                        hp++;
                    }
                }
                setGraph();
            }
        });

//        piechart.addPieSlice(new PieModel("Finger Extension", 15, Color.parseColor("#FE6DA8")));
//        piechart.addPieSlice(new PieModel("Wrist Flexion", 25, Color.parseColor("#56B7F1")));
//        piechart.addPieSlice(new PieModel("Wrist Extension", 35, Color.parseColor("#CDA67F")));
//        piechart.addPieSlice(new PieModel("Hand Supination", 9, Color.parseColor("#FED70E")));
//        piechart.addPieSlice(new PieModel("Hand Pronation", 9, Color.parseColor("#FED70E")));

        piechart.startAnimation();
    }

    private void setGraph() {
        piechart.clearChart();
        piechart.addPieSlice(new PieModel("Finger Extension", fe, Color.parseColor("#FE6DA8")));
        piechart.addPieSlice(new PieModel("Wrist Flexion", wf, Color.parseColor("#56B7F1")));
        piechart.addPieSlice(new PieModel("Wrist Extension", we, Color.parseColor("#CDA67F")));
        piechart.addPieSlice(new PieModel("Hand Supination", hs, Color.parseColor("#FED70E")));
        piechart.addPieSlice(new PieModel("Hand Pronation", hp, Color.parseColor("#FED70E")));

        piechart.startAnimation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room_details, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        if (id == R.id.action_myo) {
//            startService(new Intent(this, BackgroundService.class));
//            onScanActionSelected();
//            return true;
//        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        stopService(new Intent(context, BackgroundService.class));
    }

    @Override
    protected void onResume() {
//        reloadGridData();
        super.onResume();
    }

}
