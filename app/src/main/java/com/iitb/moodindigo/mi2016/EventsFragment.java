package com.iitb.moodindigo.mi2016;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iitb.moodindigo.mi2016.ServerConnection.GsonModels;
import com.iitb.moodindigo.mi2016.ServerConnection.RetrofitInterface;
import com.iitb.moodindigo.mi2016.ServerConnection.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment implements Callback<List<GsonModels.Event>> {

    private ProgressDialog progressDialog;
    private RecyclerView eventsRecyclerView;
    private EventsListAdapter eventsListAdapter;

    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Events");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        eventsRecyclerView = (RecyclerView) getActivity().findViewById(R.id.event_list);
        if(Cache.isSendEventRequest()) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Requesting Details");
            RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
            retrofitInterface.getEvents().enqueue(this);
            progressDialog.show();
        }
        else {
            eventsListAdapter = new EventsListAdapter(Cache.getEventList(), new ItemCLickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Fragment eventPageFragment = new EventPageFragment(getContext(), Cache.getEventList().get(position));
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    transaction.replace(R.id.relativelayout_for_fragment, eventPageFragment, eventPageFragment.getTag());
                    transaction.commit();
                }
            });
            eventsRecyclerView.setAdapter(eventsListAdapter);
            eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    @Override
    public void onResponse(Call<List<GsonModels.Event>> call, Response<List<GsonModels.Event>> response) {
        if (response.isSuccessful()) {
            Cache.setEventList(response.body());
            eventsListAdapter = new EventsListAdapter(Cache.getEventList(), new ItemCLickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Fragment eventPageFragment = new EventPageFragment(getContext(), Cache.getEventList().get(position));
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    transaction.replace(R.id.relativelayout_for_fragment, eventPageFragment, eventPageFragment.getTag());
                    transaction.commit();
                }
            });
            eventsRecyclerView.setAdapter(eventsListAdapter);
            eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Cache.setSendEventRequest(false);
        } else {
            Toast.makeText(getContext(), "Response code " + response.code(), Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
    }

    @Override
    public void onFailure(Call<List<GsonModels.Event>> call, Throwable t) {
        Toast.makeText(getContext(), "Network error occurred", Toast.LENGTH_LONG).show();
        Log.d("TAG", "onFailure: " + t.toString());
        progressDialog.dismiss();
    }
}
