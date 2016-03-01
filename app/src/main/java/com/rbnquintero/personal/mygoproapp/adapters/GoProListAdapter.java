package com.rbnquintero.personal.mygoproapp.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rbnquintero.personal.mygoproapp.R;
import com.rbnquintero.personal.mygoproapp.objects.GoPro;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by rbnquintero on 2/23/16.
 */
public class GoProListAdapter extends ArrayAdapter<GoPro> {
    Context context;
    int layoutResourceId;
    List<GoPro> data = null;

    public GoProListAdapter(Context context, int layoutResourceId, List<GoPro> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GoProListHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((AppCompatActivity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GoProListHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtStatus = (TextView)row.findViewById(R.id.txtStatus);

            row.setTag(holder);
        }
        else
        {
            holder = (GoProListHolder)row.getTag();
        }

        GoPro goPro = data.get(position);
        holder.txtTitle.setText(goPro.title);
        holder.txtStatus.setText(goPro.status);
        return row;
    }

    static class GoProListHolder
    {
        TextView txtTitle;
        TextView txtStatus;
    }
}
