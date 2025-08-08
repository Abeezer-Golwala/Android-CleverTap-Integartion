package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CleverTapAPI;

import java.util.ArrayList;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {
    private static final String TAG = "ViewPager2AdapterLog"; // Unique tag for Adapter
    private ArrayList<SliderData> sliderData;
    private Context context; // Added context

    public ViewPager2Adapter(Context context, ArrayList<SliderData> sliderData) { // Added context
        Log.d(TAG, "Constructor: CALLED. Initial data size: " + (sliderData != null ? sliderData.size() : "null data"));
        this.context = context; // Added context
        this.sliderData = sliderData;
        if (this.sliderData == null) {
            Log.e(TAG, "Constructor: sliderData is NULL after assignment! Initializing to empty list.");
            this.sliderData = new ArrayList<>();
        }
    }

    public void updateData(ArrayList<SliderData> newSliderData) {
        Log.d(TAG, "updateData: CALLED. New data size: " + (newSliderData != null ? newSliderData.size() : "null data"));
        this.sliderData = newSliderData;
        if (this.sliderData == null) {
            Log.e(TAG, "updateData: newSliderData was NULL, setting to empty list!");
            this.sliderData = new ArrayList<>();
        }
        notifyDataSetChanged(); // Essential to refresh the ViewPager2
        Log.d(TAG, "updateData: notifyDataSetChanged() called. Current item count: " + getItemCount());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: CALLED. Parent: " + parent);
        View view;
        try {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
            Log.d(TAG, "onCreateViewHolder: LayoutInflater.inflate(R.layout.slider_layout) SUCCESSFUL.");
        } catch (Exception e) {
            Log.e(TAG, "onCreateViewHolder: CRASH or ERROR during LayoutInflater.inflate", e);
            throw e; // Re-throw to make it obvious if inflation fails
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: CALLED for position: " + position);

        if (sliderData == null || sliderData.isEmpty()) {
            Log.e(TAG, "onBindViewHolder: sliderData is null or empty. Cannot bind position " + position);
            // Attempt to load a default placeholder in the ImageView to make it visible
            if (holder.imageView != null) {
                Glide.with(context) // Use stored context
                        .load(R.drawable.default_slider_1) // A default local drawable
                        .into(holder.imageView);
            }
            return;
        }
        if (position >= sliderData.size()) {
            Log.e(TAG, "onBindViewHolder: Position " + position + " is out of bounds for sliderData size " + sliderData.size());
            return;
        }

        SliderData currentItem = sliderData.get(position);
        if (currentItem == null) {
            Log.e(TAG, "onBindViewHolder: currentItem at position " + position + " is NULL.");
            return;
        }

        Object imageSource = currentItem.getImageSource();
        Log.d(TAG, "onBindViewHolder: Image source for position " + position + ": " + (imageSource != null ? imageSource.toString() : "null"));


        if (holder.imageView == null) {
            Log.e(TAG, "onBindViewHolder: holder.imageView is NULL at position " + position + ". Cannot load image or set listener.");
            return;
        }

        // Glide loading logic
        if (imageSource instanceof String || imageSource instanceof Integer) {
            Glide.with(context) // Use stored context
                    .load(imageSource)
                    .placeholder(R.drawable.default_slider_1)
                    .error(R.drawable.default_slider_1) // Show error placeholder
                    .into(holder.imageView);
        } else {
            Log.e(TAG, "onBindViewHolder: Unknown image source type at position " + position + ": " + (imageSource != null ? imageSource.getClass().getName() : "null"));
            holder.imageView.setImageResource(R.drawable.default_slider_1); // Fallback
        }


        Log.d(TAG, "onBindViewHolder: Setting OnClickListener for position: " + position);
        holder.imageView.setClickable(true);
        holder.imageView.setFocusable(true);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getBindingAdapterPosition();
                Log.d(TAG, "onClick: CALLED for item at adapter position: " + currentPosition);

                if (currentPosition == RecyclerView.NO_POSITION) {
                    Log.e(TAG, "onClick: Position is NO_POSITION. Cannot get item data.");
                    return;
                }
                if (sliderData == null || currentPosition >= sliderData.size() || sliderData.get(currentPosition) == null) {
                    Log.e(TAG, "onClick: Slider data invalid or position out of bounds. Position: " + currentPosition + ", Data size: " + (sliderData != null ? sliderData.size() : "null"));
                    return;
                }

                SliderData clickedItemData = sliderData.get(currentPosition);
                String targetUrl = clickedItemData.getTargetUrl();
                String wzrkId = clickedItemData.getWzrkId();

                Log.d(TAG, "onClick: Item Data - TargetURL: " + targetUrl + ", WZRK_ID: " + wzrkId);

                CleverTapAPI ctInstance = CleverTapAPI.getDefaultInstance(context.getApplicationContext());
                if (ctInstance != null && wzrkId != null && !wzrkId.isEmpty()) {
                    Log.d(TAG, "onClick: Pushing CT Clicked Event for ID: " + wzrkId);
                    ctInstance.pushDisplayUnitClickedEventForID(wzrkId);
                } else {
                    Log.w(TAG, "onClick: CT instance is null or wzrkId is empty. Not pushing event.");
                }

                if (targetUrl != null && !targetUrl.isEmpty()) {
                    Log.d(TAG, "onClick: Attempting to open URL: " + targetUrl);
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Important if context is not an Activity
                        context.startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e(TAG, "onClick: Error opening URL: " + targetUrl, e);
                        Toast.makeText(context, "Could not open link: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "onClick: No target URL for this item.");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = sliderData != null ? sliderData.size() : 0;
        Log.d(TAG, "getItemCount: CALLED, returning: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(ViewPager2Adapter.TAG, "ViewHolder Constructor: CALLED for itemView: " + itemView);
            imageView = itemView.findViewById(R.id.myimage);
            if (imageView == null) {
                Log.e(ViewPager2Adapter.TAG, "ViewHolder Constructor: ImageView with ID R.id.myimage NOT FOUND!");
            } else {
                Log.d(ViewPager2Adapter.TAG, "ViewHolder Constructor: ImageView R.id.myimage found successfully.");
            }
        }
    }
}
