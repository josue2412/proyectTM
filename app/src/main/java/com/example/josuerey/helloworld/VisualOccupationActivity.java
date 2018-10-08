package com.example.josuerey.helloworld;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.example.josuerey.helloworld.domain.busroute.BusRoute;
import com.example.josuerey.helloworld.domain.busroute.BusRouteRepository;
import com.example.josuerey.helloworld.domain.routeviarelationship.RouteViaRelationship;
import com.example.josuerey.helloworld.domain.routeviarelationship.RouteViaRelationshipRepository;

import java.util.ArrayList;
import java.util.List;

public class VisualOccupationActivity extends AppCompatActivity {

    private LinearLayout parentLinearLayout;
    private final String TAG = this.getClass().getSimpleName();
    private BusRouteRepository busRouteRepository;
    private List<String> busRoutes;
    private RouteViaRelationshipRepository routeViaRelationshipRepository;
    private int viaOfStudyId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tracker_activity_menu, menu);
        return true;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visual_occupation_activity);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            viaOfStudyId = Integer.valueOf(extras
                    .getString("ViaOfStudyId"));
        }

        routeViaRelationshipRepository = new RouteViaRelationshipRepository(getApplication());
        // Autocomplete bus routes with database information
        busRouteRepository  = new BusRouteRepository(getApplication());
        //BusRoute[] existingBusRoutes = busRouteRepository.findAll();

        RouteViaRelationship[] allRelations = routeViaRelationshipRepository.findAll();

        Log.d(TAG, "All relations: " + allRelations.length);

        BusRoute[] existingBusRoutes = routeViaRelationshipRepository.findRoutesByViaOfStudyId(viaOfStudyId);
        busRoutes = new ArrayList<>();

        Log.d(TAG, "Existing bus routes: " + existingBusRoutes.length + " associated with " +
                "via of study id:" + viaOfStudyId);

        for (BusRoute existingBusRoute : existingBusRoutes) {
            busRoutes.add(existingBusRoute.toString());
            Log.d(TAG, existingBusRoute.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, busRoutes.toArray(new String[busRoutes.size()]));
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.route_edit_text);
        textView.setAdapter(adapter);
    }

    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.visual_occupation_record, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
    }

    public void onSave(View v) {
        Log.d(TAG, "Trying to save form");

        parentLinearLayout.removeView((View)((View) v.getParent()).getParent());
    }

}