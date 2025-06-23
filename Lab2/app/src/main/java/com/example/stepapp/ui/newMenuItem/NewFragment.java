package com.example.stepapp.ui.newMenuItem;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stepapp.R;
import com.example.stepapp.databinding.FragmentHomeBinding;
import com.example.stepapp.databinding.FragmentNewBinding;
import com.example.stepapp.ui.home.HomeViewModel;

public class NewFragment extends Fragment {

    private FragmentNewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewViewModel newViewModel =
                new ViewModelProvider(this).get(NewViewModel.class);

        binding = FragmentNewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNew;
        newViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}