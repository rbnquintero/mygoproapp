package com.rbnquintero.personal.mygoproapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.rbnquintero.personal.mygoproapp.R;
import com.rbnquintero.personal.mygoproapp.service.GoProStatusServiceNew;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rbnquintero on 3/9/16.
 */
public class GoProMediaGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private GoProMediaGridViewAdapterDelegate delegate;
    private Map<Integer, Bitmap> images = new HashMap<Integer, Bitmap>();
    private Map<Integer, Boolean> imagesQueue = new HashMap<Integer, Boolean>();
    private List<String> mThumbIds;

    public GoProMediaGridViewAdapter(Context c, GoProMediaGridViewAdapterDelegate delegate, List<String> mThumbIds) {
        this.mContext = c;
        this.delegate = delegate;
        this.mThumbIds = mThumbIds;
    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(320, 240));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if (imagesQueue.get(position) != null || images.get(position) != null) {
            if (images.get(position) != null) {
                imageView.setImageBitmap(images.get(position));
            } else {
                imageView.setImageResource(R.drawable.no_photo);
            }
        } else {
            DownloadImageTask task = new DownloadImageTask(delegate, position);
            task.execute(mThumbIds.get(position));
            imagesQueue.put(position, true);
            imageView.setImageResource(R.drawable.no_photo);
        }
        return imageView;
    }

    public void invalidateView(Integer position) {
        imagesQueue.remove(position);
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        GoProMediaGridViewAdapterDelegate delegate;
        Integer position;

        public DownloadImageTask(GoProMediaGridViewAdapterDelegate delegate, Integer position) {
            this.delegate = delegate;
            this.position = position;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = GoProStatusServiceNew.ROOT_URL + GoProStatusServiceNew.MEDIA_THUMBNAIL + urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            images.put(position, result);
            delegate.updateImage(position);
        }
    }
}
