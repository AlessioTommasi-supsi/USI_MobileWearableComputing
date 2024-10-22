package com.example.pocopenstreetmap.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pocopenstreetmap.R;

public class NearFragment extends Fragment {


    public NearFragment() {
        // Required empty public constructor
    }


    public static NearFragment newInstance() {

        return new NearFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_near, container, false);
    }
}