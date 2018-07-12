package guardmonitor.gpg.za.controlroom.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.controlroom.fragments.interaction.listeners.OnRouteFragmentInteractionListener;
import guardmonitor.gpg.za.db.pojo.Route;


/**
 * {@link RecyclerView.Adapter} that can display a {@link Route} and makes a call to the
 * specified {@link OnRouteFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class RouteRecyclerViewAdapter extends RecyclerView.Adapter<RouteRecyclerViewAdapter.ViewHolder> {

    private final List<Route> mRouteValues;
    private final OnRouteFragmentInteractionListener mListener;

    public RouteRecyclerViewAdapter(List<Route> items, OnRouteFragmentInteractionListener listener) {
        mRouteValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mRouteValues.get(position);
        holder.mIdView.setText(mRouteValues.get(position).getId()+"");
        holder.mContentView.setText(mRouteValues.get(position).getRouteName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onRouteFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRouteValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Route mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.route_id);
            mContentView = (TextView) view.findViewById(R.id.route_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
