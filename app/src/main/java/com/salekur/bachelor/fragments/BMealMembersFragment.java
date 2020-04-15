package com.salekur.bachelor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.salekur.bachelor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BMealMembersFragment extends Fragment {

    public BMealMembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b_meal_members, container, false);
    }
}
