package guardmonitor.gpg.za.controlroom.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.controlroom.fragments.interaction.listeners.OnLocationFragmentInteractionListener;
import guardmonitor.gpg.za.db.EntityManager;
import guardmonitor.gpg.za.db.structure.LocationContract;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnLocationFragmentInteractionListener}
 * interface.
 */
public class LocationFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_LOCATION_LIST = "location-list";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnLocationFragmentInteractionListener mListener;

    private ArrayList<Location> mLocations;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LocationFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LocationFragment newInstance(int columnCount,ArrayList<Location> locationList) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelableArrayList(ARG_LOCATION_LIST, locationList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mLocations = getArguments().getParcelableArrayList(ARG_LOCATION_LIST);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }


            recyclerView.setAdapter(new LocationRecyclerViewAdapter(mLocations, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLocationFragmentInteractionListener) {
            mListener = (OnLocationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLocationFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
