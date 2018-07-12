package guardmonitor.gpg.za.controlroom.fragments;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.controlroom.fragments.interaction.listeners.OnLocationFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Location} and makes a call to the
 * specified {@link OnLocationFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> {

    private final List<Location> mValues;
    private final OnLocationFragmentInteractionListener mListener;

    public LocationRecyclerViewAdapter(List<Location> items, OnLocationFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(position+"");
        holder.mLat.setText(mValues.get(position).getLatitude()+"");
        holder.mLng.setText(mValues.get(position).getLongitude()+"");
        holder.mAlt.setText(mValues.get(position).getAltitude()+"");
        holder.mAcc.setText(mValues.get(position).getAccuracy()+"");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onLocationFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mLat,mLng,mAlt,mAcc;
        public Location mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.location_id);
            mLat = (TextView) view.findViewById(R.id.location_lat);
            mLng = (TextView) view.findViewById(R.id.location_lng);
            mAlt = (TextView) view.findViewById(R.id.location_alt);
            mAcc = (TextView) view.findViewById(R.id.location_acc);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }
}
