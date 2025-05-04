package dut.com.fastfooddatabase.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dut.com.fastfooddatabase.R;
import dut.com.fastfooddatabase.data.repository.ApplicationRepository;
import dut.com.fastfooddatabase.data.repository.OrderRepository;
import dut.com.fastfooddatabase.databinding.FragmentTrackOrderBinding;

public class TrackOrderFragment extends Fragment {

    private String orderId = "0";
    private FragmentTrackOrderBinding binding;

    private OrderRepository orderRepository;

    public TrackOrderFragment(String orderId) {
        // Required empty public constructor
        this.orderId = orderId;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderRepository = ApplicationRepository.getInstance().getOrderRepository();

        orderRepository.getOrderById(orderId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
            } else {
                // Handle the error
            }
        });
    }
}