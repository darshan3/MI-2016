package com.iitb.moodindigo.mi2016;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iitb.moodindigo.mi2016.ServerConnection.GsonModels;

import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventPageFragment extends Fragment {

    GsonModels.Event event;
    private SharedPreferences.Editor goingSharedPreferencesEditor;
    private SharedPreferences goingPreferences;

    public EventPageFragment() {
        // Required empty public constructor
    }

    public EventPageFragment(Context context, GsonModels.Event event) {
        this.event = event;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        goingPreferences = getContext().getSharedPreferences("GOING", Context.MODE_PRIVATE);
        final String goingList = goingPreferences.getString("GOING_LIST", null);
        Type type = new TypeToken<List<GsonModels.Event>>() {
        }.getType();
        final List<GsonModels.Event> goingListGson = (new Gson()).fromJson(goingList, type);
        final View rootView = inflater.inflate(R.layout.fragment_event_page, container, false);
        final ImageButton notification = (ImageButton) rootView.findViewById(R.id.icon_button);
        final TextView eventVenue = (TextView) rootView.findViewById(R.id.event_venue);
        final TextView time = (TextView) rootView.findViewById(R.id.time);
        final ImageView clockIcon = (ImageView) rootView.findViewById(R.id.clock_icon);
        final ImageView locationIcon = (ImageView) rootView.findViewById(R.id.location_icon);
        final TextView description = (TextView) rootView.findViewById(R.id.description);
        final TextView title = (TextView) rootView.findViewById(R.id.event_name);
        final TextView genre = (TextView) rootView.findViewById(R.id.genre_name);
        goingSharedPreferencesEditor = getContext().getSharedPreferences("GOING", Context.MODE_PRIVATE).edit();
        title.setText(event.getTitle());
        String timeString = String.valueOf(event.getTime());
        if (timeString.length() == 3)
            timeString = "0" + timeString;
        time.setText(timeString.substring(0, 2) + ":" + timeString.substring(2));
        description.setText(event.getDescription());
        eventVenue.setText(event.getLocation());
        genre.setText(event.getCategory());
        getActivity().setTitle("Event");
        Cache.setDay(event.getDay());
        Cache.setCategoryPosition(event.getCategory());
        Cache.setGoingdayPosition(event.getDay());
        if (goingListGson == null) {
            ;
        } else if (goingListGson.contains(event)) {
            eventVenue.setTextColor(Color.parseColor("#DEB951"));
            time.setTextColor(Color.parseColor("#DEB951"));
            clockIcon.setColorFilter(Color.parseColor("#DEB951"));
            locationIcon.setColorFilter(Color.parseColor("#DEB951"));
            notification.setColorFilter(Color.parseColor("#DEB951"));
            notification.setImageResource(R.drawable.ic_notifications_black_48dp);
        }
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventVenue.getCurrentTextColor() != Color.parseColor("#DEB951")) {
                    eventVenue.setTextColor(Color.parseColor("#DEB951"));
                    time.setTextColor(Color.parseColor("#DEB951"));
                    clockIcon.setColorFilter(Color.parseColor("#DEB951"));
                    locationIcon.setColorFilter(Color.parseColor("#DEB951"));
                    notification.setColorFilter(Color.parseColor("#DEB951"));
                    notification.setImageResource(R.drawable.ic_notifications_black_48dp);
                    Cache.addToGoingList(event);
                } else {
                    eventVenue.setTextColor(Color.parseColor("#FFFFFF"));
                    time.setTextColor(Color.parseColor("#FFFFFF"));
                    clockIcon.setColorFilter(Color.parseColor("#FFFFFF"));
                    locationIcon.setColorFilter(Color.parseColor("#FFFFFF"));
                    notification.setColorFilter(Color.parseColor("#FFFFFF"));
                    notification.setImageResource(R.drawable.ic_notifications_none_black_48dp);
                    Cache.removeFromGoingList(event);
                }
                String goingEventsListJson = (new Gson()).toJson(Cache.getGoingEventsList());
                goingSharedPreferencesEditor.putString("GOING_LIST", goingEventsListJson);
                goingSharedPreferencesEditor.apply();
            }
        });
        return rootView;

    }
}
