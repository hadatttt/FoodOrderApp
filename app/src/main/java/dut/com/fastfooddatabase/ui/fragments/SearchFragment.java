package dut.com.fastfooddatabase.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import dut.com.fastfooddatabase.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private List<String> historyList;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeData();
    }

    private void initializeData() {
        // Initialize history list
        historyList = new ArrayList<>();
        historyList.add("Burger");
        historyList.add("Sandwich");
        historyList.add("Pizza");
        historyList.add("Khoai tây chiên");
        historyList.add("Mì cay");

        // Initialize restaurant list
    }
}