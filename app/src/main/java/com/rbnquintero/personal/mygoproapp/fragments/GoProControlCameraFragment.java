package com.rbnquintero.personal.mygoproapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rbnquintero.personal.mygoproapp.GoProControlActivity;
import com.rbnquintero.personal.mygoproapp.R;
import com.rbnquintero.personal.mygoproapp.service.GoProControlService;

/**
 * Created by rbnquintero on 2/26/16.
 */
public class GoProControlCameraFragment extends Fragment {
    private String TAG = this.getClass().getSimpleName();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public GoProControlCameraFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GoProControlCameraFragment newInstance(int sectionNumber) {
        GoProControlCameraFragment fragment = new GoProControlCameraFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_go_pro_control_camera, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        Log.i(TAG, "###");
        Log.i(TAG, getActivity().getLocalClassName());


        return rootView;
    }



}
