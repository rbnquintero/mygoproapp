package com.rbnquintero.personal.mygoproapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.rbnquintero.personal.mygoproapp.R;
import com.rbnquintero.personal.mygoproapp.adapters.GoProMediaGridViewAdapter;
import com.rbnquintero.personal.mygoproapp.adapters.GoProMediaGridViewAdapterDelegate;
import com.rbnquintero.personal.mygoproapp.business.GoProStatusBusiness;
import com.rbnquintero.personal.mygoproapp.business.delegate.GoProStatusBusinessDelegate;
import com.rbnquintero.personal.mygoproapp.objects.GoProStatus;

import java.util.List;


public class GoProControlMediaFragment extends Fragment implements GoProStatusBusinessDelegate, GoProMediaGridViewAdapterDelegate {
    private String TAG = this.getClass().getSimpleName();

    GoProStatusBusiness statusBusiness;

    GridView gridview;

    private static final String ARG_SECTION_NUMBER = "section_number";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GoProControlMediaFragment() {
    }

    public static GoProControlMediaFragment newInstance(int sectionNumber) {
        GoProControlMediaFragment fragment = new GoProControlMediaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_go_pro_control_media, container, false);
        return rootView;
    }

    @Override
    public void setUserVisibleHint (boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            statusBusiness = new GoProStatusBusiness(this);
            statusBusiness.execute(GoProStatusBusiness.GET_MEDIA_LIST);
        }
    }

    @Override
    public void updateStatus(GoProStatus response) {
        //TODO nothing
    }

    @Override
    public void updateMediaList(List<String> media) {
        System.out.println(media);
        final FragmentActivity activity = this.getActivity();
        gridview = (GridView) this.getActivity().findViewById(R.id.gridview);
        this.getActivity().runOnUiThread(new CreateGridViewRunnable(media, activity, this));
    }

    @Override
    public void updateImage(Integer position) {
        System.out.println("Image updated: " + position);
        //((ImageAdapter)gridview.getAdapter()).invalidateView(position);
        ((GoProMediaGridViewAdapter)gridview.getAdapter()).notifyDataSetChanged();
    }

    class CreateGridViewRunnable implements Runnable {
        List<String> media;
        FragmentActivity activity;
        GoProMediaGridViewAdapterDelegate delegate;

        public CreateGridViewRunnable(List<String> media, FragmentActivity activity, GoProMediaGridViewAdapterDelegate delegate) {
            this.media = media;
            this.activity = activity;
            this.delegate = delegate;
        }

        @Override
        public void run() {
            gridview.setAdapter(new GoProMediaGridViewAdapter(activity, delegate, media));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Toast.makeText(activity, "" + position,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
