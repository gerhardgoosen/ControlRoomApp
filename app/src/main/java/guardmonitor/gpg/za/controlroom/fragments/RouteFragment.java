package guardmonitor.gpg.za.controlroom.fragments;

import android.content.Context;
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
import guardmonitor.gpg.za.controlroom.fragments.interaction.listeners.OnRouteFragmentInteractionListener;
import guardmonitor.gpg.za.db.EntityManager;
import guardmonitor.gpg.za.db.pojo.Route;
import guardmonitor.gpg.za.db.structure.RouteContract;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnRouteFragmentInteractionListener}
 * interface.
 */
public class RouteFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnRouteFragmentInteractionListener mListener;
    private EntityManager entityManager;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RouteFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RouteFragment newInstance(int columnCount) {
        RouteFragment fragment = new RouteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        entityManager = new EntityManager(this, getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            List<Route> routes = JSONtoLIST(entityManager.findSavedRouteNames());

            recyclerView.setAdapter(new RouteRecyclerViewAdapter(routes, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRouteFragmentInteractionListener) {
            mListener = (OnRouteFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRouteFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public List<Route> JSONtoLIST(JSONObject data) {
        List<Route> retList = new ArrayList<>();
        JSONObject jsonData = new JSONObject();

        try {
            jsonData = (JSONObject) data.get("data");

        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < jsonData.length(); i++) {

            try {
                JSONObject row = ((JSONObject) ((JSONObject) jsonData.get("row_" + i)).get("row"));

                retList.add(new Route(i, row.getString(RouteContract.RouteEntry.COLUMN_NAME_route_name)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retList;
    }
}