package io.thinger.thinger;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.thinger.thinger.views.DeviceResource;

public class DeviceResourceAdapter extends RecyclerView.Adapter<DeviceResourceAdapter.ViewHolder> {
    private List<DeviceResource> devices;
    private OnDeviceResourceClicked deviceResourceClickListener;

    public void clear() {
        int size = devices.size();
        if(size>0){
            devices.clear();
            notifyDataSetChanged();
        }
    }

    public interface OnDeviceResourceClicked{
        public void onDeviceTokenClick(DeviceResource token);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public DeviceResourceAdapter(OnDeviceResourceClicked listener) {
        this.deviceResourceClickListener = listener;
        this.devices = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DeviceResourceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_resource, parent, false);
        // set the view's size, margins, paddings and layout parameters

        /*
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceResourceClickListener.onDeviceTokenClick((DeviceResource) v.getTag());
            }
        });
        */

        ViewHolder vh = new ViewHolder(cardView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DeviceResource resource = devices.get(position);
        resource.fillView(holder.cardView);

        holder.cardView.setTag(resource);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addResource(DeviceResource resource){
        devices.add(resource);
        notifyItemInserted(devices.size()-1);
    }
}