package com.ngm.smartot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

import com.ngm.smartot.Model.Action;
import com.ngm.smartot.db.DatabaseAdapter;

import org.eazegraph.lib.charts.VerticalBarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.Vector;

public class StatisticsActivity2 extends ActionBarActivity {
    Button lifxButton,hueButton;
    Context context;
    GridView gridView;
    DatabaseAdapter dbAdapter;
    SharedPreferences sharedPref;
    boolean firstRun = true;
    ActionBar actionBar;
    private Menu menu;
    private BroadcastReceiver receiver;
    Spinner spinner;
    VerticalBarChart mBarChart;
    Vector<Action> actions;
    int fe = 0, wf = 0, we = 0, hs = 0, hp = 0;
    int[] time = {0, 2, 4, 6};
    String[] atime = {"All", "Last 2 Hours", "Last 4 Hours", "Last 6 Hours"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics2);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.smart_home_icon);
        actionBar.setTitle(" Smart OT Analytics");

        context = this;
        dbAdapter = new DatabaseAdapter(context);

        spinner = (Spinner) findViewById(R.id.spinner);
        mBarChart = (VerticalBarChart) findViewById(R.id.barchart);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, atime);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actions = dbAdapter.getActionsByTime(time[position]+"");
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

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        lifxButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                actions = dbAdapter.getActionsByDevice("lifx");
//                Log.i("actions", actions.toString());
//                fe = 0; wf = 0; we = 0; hs = 0; hp = 0;
//                for (Action action : actions) {
//                    if (action.getAction().equalsIgnoreCase("Finger Extension")){
//                        fe++;
//                    } else if (action.getAction().equalsIgnoreCase("Wrist Flexion")){
//                        wf++;
//                    } else if (action.getAction().equalsIgnoreCase("Wrist Extension")){
//                        we++;
//                    } else if (action.getAction().equalsIgnoreCase("Hand Supination")){
//                        hs++;
//                    } else if (action.getAction().equalsIgnoreCase("Hand Pronation")){
//                        hp++;
//                    }
//                }
//                setGraph();
//            }
//        });
//
//        hueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                actions = dbAdapter.getActionsByDevice("hue");
//                Log.i("hue actions", actions.toString());
//                fe = 0; wf = 0; we = 0; hs = 0; hp = 0;
//                for (Action action : actions) {
//                    if (action.getAction().equalsIgnoreCase("Finger Extension")){
//                        fe++;
//                    } else if (action.getAction().equalsIgnoreCase("Wrist Flexion")){
//                        wf++;
//                    } else if (action.getAction().equalsIgnoreCase("Wrist Extension")){
//                        we++;
//                    } else if (action.getAction().equalsIgnoreCase("Hand Supination")){
//                        hs++;
//                    } else if (action.getAction().equalsIgnoreCase("Hand Pronation")){
//                        hp++;
//                    }
//                }
//                setGraph();
//            }
//        });

        mBarChart.addBar(new BarModel("Finger Extension" ,45, 0xFF123456));
        mBarChart.addBar(new BarModel("Wrist Flexion", 30, 0xFF343456));
        mBarChart.addBar(new BarModel("Wrist Extension",50, 0xFF563456));
        mBarChart.addBar(new BarModel("Hand Supination",60, 0xFF873F56));
        mBarChart.addBar(new BarModel("Hand Pronation", 50, 0xFF56B7F1));
        mBarChart.startAnimation();
    }

    private void setGraph() {
        mBarChart.clearChart();
//        piechart.addPieSlice(new PieModel("Finger Extension", fe, Color.parseColor("#FE6DA8")));
//        piechart.addPieSlice(new PieModel("Wrist Flexion", wf, Color.parseColor("#56B7F1")));
//        piechart.addPieSlice(new PieModel("Wrist Extension", we, Color.parseColor("#CDA67F")));
//        piechart.addPieSlice(new PieModel("Hand Supination", hs, Color.parseColor("#FED70E")));
//        piechart.addPieSlice(new PieModel("Hand Pronation", hp, Color.parseColor("#FED70E")));

        mBarChart.addBar(new BarModel("Finger Extension" , fe, 0xFF123456));
        mBarChart.addBar(new BarModel("Wrist Flexion", wf, 0xFF343456));
        mBarChart.addBar(new BarModel("Wrist Extension", we, 0xFF563456));
        mBarChart.addBar(new BarModel("Hand Supination", hs, 0xFF873F56));
        mBarChart.addBar(new BarModel("Hand Pronation", hp, 0xFF56B7F1));
        mBarChart.startAnimation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_room_details, menu);
//        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

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
