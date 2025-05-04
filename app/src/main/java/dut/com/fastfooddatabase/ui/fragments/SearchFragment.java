package dut.com.fastfooddatabase.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dut.com.fastfooddatabase.data.models.SearchHistory;
import dut.com.fastfooddatabase.data.models.SearchResultItem;
import dut.com.fastfooddatabase.databinding.FragmentSearchBinding;
import dut.com.fastfooddatabase.ui.adapters.SearchFavoritesAdapter;
import dut.com.fastfooddatabase.ui.adapters.SearchHistoryAdapter;
import dut.com.fastfooddatabase.ui.adapters.SearchResultAdapter;
import dut.com.fastfooddatabase.ui.adapters.SearchSuggestionAdapter;
import dut.com.fastfooddatabase.ui.viewmodels.SearchViewModel;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchViewModel viewModel;
    private SearchHistoryAdapter historyAdapter;
    private SearchResultAdapter searchResultsAdapter;
    private SearchFavoritesAdapter favoritesAdapter;
    private SearchSuggestionAdapter suggestionsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        setupRecyclerViews();
        setupClickListeners();
        observeViewModel();

        viewModel.loadFavoriteItems();
        viewModel.loadSearchHistoryItems();
        viewModel.loadSuggestionsItems();
    }

    private void setupRecyclerViews() {
        // Search Results RecyclerView
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchResultsAdapter = new SearchResultAdapter(item -> {
            Toast.makeText(requireContext(), "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
            // Navigate to detail page
        });
        binding.searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // History RecyclerView
        binding.historyRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        historyAdapter = new SearchHistoryAdapter(
                new ArrayList<>(),
                historyItem -> {
                    binding.searchInput.setText(historyItem.getKeyword());
                    viewModel.performSearch(historyItem.getKeyword());
                },
                historyItem -> viewModel.removeFromSearchHistory(historyItem)
        );
        binding.historyRecyclerView.setAdapter(historyAdapter);

        // Favorites RecyclerView
        binding.favoritesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        favoritesAdapter = new SearchFavoritesAdapter(new ArrayList<>(), item -> {
            Toast.makeText(requireContext(), "Selected: " + item.getName(), Toast.LENGTH_SHORT).show();
            // Navigate to detail page
        });
        binding.favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Suggestions RecyclerView
        binding.suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        suggestionsAdapter = new SearchSuggestionAdapter(new ArrayList<>(), item -> {
            Toast.makeText(requireContext(), "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
        });
        binding.suggestionsRecyclerView.setAdapter(suggestionsAdapter);
    }

    private void setupClickListeners() {
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    viewModel.performSearch(query);
                }
                return true;
            }
            return false;
        });

        binding.searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String query = s.toString().trim();
                viewModel.performSearch(query);
            }
        });

        binding.btnClearHistory.setOnClickListener(v -> viewModel.clearSearchHistory());
    }

    private void observeViewModel() {
        // Observe search results
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            searchResultsAdapter.updateResults(results);
            updateSearchResultsVisibility();
        });

        // Observe favorite items
        viewModel.getFavoriteItems().observe(getViewLifecycleOwner(), favoriteItems -> {
            favoritesAdapter.updateItems(favoriteItems);
        });

        // Observe search history
        viewModel.getSearchHistory().observe(getViewLifecycleOwner(), historyItems -> {
            historyAdapter.updateItems(historyItems); // Use updateItems instead of just notifyDataSetChanged
            updateEmptyHistoryVisibility(historyItems);
        });

        // Observe suggestions
        viewModel.getSuggestions().observe(getViewLifecycleOwner(), suggestions -> {
            suggestionsAdapter.updateItems(suggestions);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Update UI based on loading state if needed
        });

        // Observe hasResults state
        viewModel.getHasResults().observe(getViewLifecycleOwner(), hasResults -> {
            updateSearchResultsVisibility();
        });
    }

    private void updateSearchResultsVisibility() {
        boolean hasResults = viewModel.getHasResults().getValue() != null && viewModel.getHasResults().getValue();
        binding.searchResultsRecyclerView.setVisibility(hasResults ? View.VISIBLE : View.GONE);
        binding.noResultsTextView.setVisibility(hasResults ? View.GONE : View.VISIBLE);

        String query = binding.searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            showDefaultUI();
        } else {
            showSearchResultsUI();
        }
    }

    private void showDefaultUI() {
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.noResultsTextView.setVisibility(View.GONE);
        binding.historyTitle.setVisibility(View.VISIBLE);
        binding.historyRecyclerView.setVisibility(View.VISIBLE);
        binding.btnClearHistory.setVisibility(View.VISIBLE);
        binding.suggestionsTitle.setVisibility(View.VISIBLE);
        binding.suggestionsRecyclerView.setVisibility(View.VISIBLE);
        binding.favoritesTitle.setVisibility(View.VISIBLE);
        binding.favoritesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showSearchResultsUI() {
        binding.historyTitle.setVisibility(View.GONE);
        binding.historyRecyclerView.setVisibility(View.GONE);
        binding.btnClearHistory.setVisibility(View.GONE);
        binding.txtEmptyHistory.setVisibility(View.GONE);
        binding.suggestionsTitle.setVisibility(View.GONE);
        binding.suggestionsRecyclerView.setVisibility(View.GONE);
        binding.favoritesTitle.setVisibility(View.GONE);
        binding.favoritesRecyclerView.setVisibility(View.GONE);
    }

    private void updateEmptyHistoryVisibility(List<SearchHistory> historyItems) {
        binding.txtEmptyHistory.setVisibility(
                historyItems == null || historyItems.isEmpty() ? View.VISIBLE : View.GONE
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}