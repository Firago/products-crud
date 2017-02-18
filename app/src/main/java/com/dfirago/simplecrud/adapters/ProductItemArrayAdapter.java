package com.dfirago.simplecrud.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dfirago.simplecrud.R;
import com.dfirago.simplecrud.domain.ProductItem;
import com.dfirago.simplecrud.utils.Utils;

import java.io.IOException;
import java.util.List;

/**
 * Created by dmfi on 30/10/2016.
 */

public class ProductItemArrayAdapter extends ArrayAdapter<ProductItem> {

    private static final int THUMBNAIL_SIZE = 240;

    // View lookup cache
    private static class ViewHolder {
        ImageView image;
        TextView title;
        TextView description;
    }

    public ProductItemArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ProductItemArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ProductItemArrayAdapter(Context context, int resource, ProductItem[] objects) {
        super(context, resource, objects);
    }

    public ProductItemArrayAdapter(Context context, int resource, int textViewResourceId, ProductItem[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ProductItemArrayAdapter(Context context, int resource, List<ProductItem> objects) {
        super(context, resource, objects);
    }

    public ProductItemArrayAdapter(Context context, int resource, int textViewResourceId, List<ProductItem> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ProductItem productItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_product_list, parent, false);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.product_list_item_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.product_list_item_title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.product_list_item_description);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        try {
            Bitmap bitmap = Utils.getBitmap(parent.getContext(), Uri.parse(productItem.getImageUri()));
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
            viewHolder.image.setImageBitmap(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewHolder.title.setText(productItem.getTitle());
        viewHolder.description.setText(productItem.getDescription());
        // Return the completed view to render on screen
        return convertView;
    }
}
