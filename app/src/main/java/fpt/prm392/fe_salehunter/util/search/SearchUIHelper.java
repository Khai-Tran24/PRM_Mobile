package fpt.prm392.fe_salehunter.util.search;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.button.MaterialButtonToggleGroup;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.databinding.FragmentSearchResultsBinding;
import fpt.prm392.fe_salehunter.model.SortAndFilterModel;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.SearchResultsViewModel;

/**
 * Helper class for managing search UI components and interactions
 */
public class SearchUIHelper {
    
    private Context context;
    private FragmentSearchResultsBinding binding;
    private SearchResultsViewModel viewModel;
    private LifecycleOwner lifecycleOwner;
    
    // UI State
    private boolean isMapViewActive = false;
    private int totalResultsCount = 0;
    private int appliedFiltersCount = 0;
    
    // Interfaces for callbacks
    public interface SearchCallback {
        void onSearchSubmitted(String keyword);
    }
    
    public interface ViewToggleCallback {
        void onListViewSelected();
        void onMapViewSelected();
    }
    
    public interface SortFilterCallback {
        void onSortChanged(SortAndFilterModel model);
        void onFilterChanged(SortAndFilterModel model);
    }
    
    private SearchCallback searchCallback;
    private ViewToggleCallback viewToggleCallback;
    private SortFilterCallback sortFilterCallback;
    
    public SearchUIHelper(Context context, FragmentSearchResultsBinding binding, 
                         SearchResultsViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.binding = binding;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
    }
    
    /**
     * Set up all UI components and listeners
     */
    public void setupUI() {
        setupBasicUI();
        setupViewToggle();
        setupSwipeRefresh();
        updateFiltersChip();
    }
    
    /**
     * Setup basic UI components
     */
    private void setupBasicUI() {
        // Back button
        binding.resultBack.setOnClickListener(v -> {
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).onBackPressed();
            }
        });
        
        // Search keyword click
        binding.resultKeyword.setOnClickListener(v -> showSearchDialog());
        
        // Sort and filter buttons
        binding.resultSort.setOnClickListener(v -> {
            if (sortFilterCallback != null) {
                showSortDialog();
            }
        });
        
        binding.resultFilter.setOnClickListener(v -> {
            if (sortFilterCallback != null) {
                showFilterDialog();
            }
        });
    }
    
    /**
     * Setup view toggle between list and map
     */
    private void setupViewToggle() {
        MaterialButtonToggleGroup toggle = binding.resultViewToggle;
        
        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && viewToggleCallback != null) {
                if (checkedId == R.id.result_list_view) {
                    switchToListView();
                } else if (checkedId == R.id.result_map_view) {
                    switchToMapView();
                }
            }
        });
    }
    
    /**
     * Setup swipe refresh functionality
     */
    private void setupSwipeRefresh() {
        binding.resultSwipeRefresh.setOnRefreshListener(() -> {
            // Refresh will be handled by the fragment
            // Just update UI state here
            setSearching(true);
        });
    }
    
    /**
     * Show search dialog to edit keyword
     */
    public void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Search");
        
        EditText editText = new EditText(context);
        editText.setText(viewModel.getQuery());
        editText.setHint("Enter search term");
        editText.setSingleLine(true);
        editText.selectAll();
        
        builder.setView(editText);
        
        builder.setPositiveButton("Search", (dialog, which) -> {
            String newKeyword = editText.getText().toString().trim();
            if (!newKeyword.isEmpty() && searchCallback != null) {
                updateKeywordDisplay(newKeyword);
                searchCallback.onSearchSubmitted(newKeyword);
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Auto-focus and show keyboard
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    
    /**
     * Switch to list view
     */
    private void switchToListView() {
        isMapViewActive = false;
        binding.resultSwipeRefresh.setVisibility(View.VISIBLE);
        binding.coordinatorLayout.setVisibility(View.GONE);
        
        if (viewToggleCallback != null) {
            viewToggleCallback.onListViewSelected();
        }
    }
    
    /**
     * Switch to map view
     */
    private void switchToMapView() {
        isMapViewActive = true;
        binding.resultSwipeRefresh.setVisibility(View.GONE);
        binding.coordinatorLayout.setVisibility(View.VISIBLE);
        
        if (viewToggleCallback != null) {
            viewToggleCallback.onMapViewSelected();
        }
    }
    
    /**
     * Show sort dialog
     */
    private void showSortDialog() {
        DialogsProvider.get((android.app.Activity) context).sortAndFilterDialog(
                viewModel.getSortAndFilterModel(),
                viewModel.getCategories(), 
                viewModel.getBrands(),
                sortAndFilterModel -> {
                    SortAndFilterModel currentModel = viewModel.getSortAndFilterModel();
                    currentModel.setSortBy(sortAndFilterModel.getSortBy());
                    
                    if (sortFilterCallback != null) {
                        sortFilterCallback.onSortChanged(currentModel);
                    }
                    updateFiltersChip();
                }
        );
    }
    
    /**
     * Show filter dialog
     */
    private void showFilterDialog() {
        DialogsProvider.get((android.app.Activity) context).sortAndFilterDialog(
                viewModel.getSortAndFilterModel(),
                viewModel.getCategories(),
                viewModel.getBrands(),
                sortAndFilterModel -> {
                    if (sortFilterCallback != null) {
                        sortFilterCallback.onFilterChanged(sortAndFilterModel);
                    }
                    updateAppliedFiltersUI();
                }
        );
    }
    
    /**
     * Update keyword display
     */
    public void updateKeywordDisplay(String keyword) {
        binding.resultKeyword.setText(keyword);
        binding.resultKeywordLoading.setText(keyword);
    }
    
    /**
     * Set searching state
     */
    public void setSearching(boolean isSearching) {
        if (isSearching == (binding.resultLoadingPage.getVisibility() == View.VISIBLE)) return;

        if (isSearching) {
            binding.resultLoadingPage.setVisibility(View.VISIBLE);
        } else {
            binding.resultLoadingPage.setVisibility(View.GONE);
            binding.resultLoadingPage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.lay_off));
        }

        binding.resultSwipeRefresh.setRefreshing(isSearching);
    }
    
    /**
     * Set location loading state
     */
    public void setLocating(boolean isLocating) {
        if (isLocating) {
            binding.resultMapProgress.setVisibility(View.VISIBLE);
            binding.resultMapProgress.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fragment_in));
        } else {
            binding.resultMapProgress.setVisibility(View.GONE);
            binding.resultMapProgress.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fragment_out));
        }
    }
    
    /**
     * Update results count display
     */
    public void updateResultsCount(int count) {
        totalResultsCount = count;
        if (totalResultsCount > 0) {
            binding.resultCountText.setText(context.getString(R.string.results_found, totalResultsCount));
        } else {
            binding.resultCountText.setText(context.getString(R.string.no_results_found));
        }
    }
    
    /**
     * Update store count display
     */
    public void updateStoreCount(int storeCount) {
        binding.resultStoresCount.setText(context.getString(R.string.stores_nearby, storeCount));
    }
    
    /**
     * Update applied filters UI
     */
    private void updateAppliedFiltersUI() {
        // Count applied filters
        SortAndFilterModel filters = viewModel.getSortAndFilterModel();
        appliedFiltersCount = countAppliedFilters(filters);

        // Update UI
        if (appliedFiltersCount > 0) {
            binding.resultAppliedFilters.setVisibility(View.VISIBLE);
            binding.resultAppliedFilters.setText(context.getString(R.string.filters_applied, appliedFiltersCount));
        } else {
            binding.resultAppliedFilters.setVisibility(View.GONE);
        }
    }
    
    /**
     * Update filters chip display
     */
    private void updateFiltersChip() {
        SortAndFilterModel model = viewModel.getSortAndFilterModel();
        int filterCount = countAppliedFilters(model);
        
        // Update filter chip based on count
        // Implementation depends on specific UI requirements
    }
    
    /**
     * Count applied filters
     */
    private int countAppliedFilters(SortAndFilterModel filters) {
        int count = 0;
        
        if (!filters.getSortBy().equals(SortAndFilterModel.SORT_POPULARITY)) {
            count++;
        }
        if (filters.getMinPrice() > SortAndFilterModel.PRICE_MIN) {
            count++;
        }
        if (filters.getMaxPrice() < SortAndFilterModel.PRICE_MAX) {
            count++;
        }
        if (filters.getCategory() != null && !filters.getCategory().equals(SortAndFilterModel.CATEGORY_ALL)) {
            count++;
        }
        if (filters.getBrand() != null && !filters.getBrand().equals(SortAndFilterModel.BRAND_ALL)) {
            count++;
        }
        
        return count;
    }
    
    /**
     * Animate map header for expanded state
     */
    public void animateMapHeaderExpanded() {
        binding.resultProductMapTitle.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(250)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
    
    /**
     * Animate map header for collapsed state
     */
    public void animateMapHeaderCollapsed() {
        binding.resultProductMapTitle.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(250)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }
    
    // Getters and setters for state
    public boolean isMapViewActive() {
        return isMapViewActive;
    }
    
    public int getTotalResultsCount() {
        return totalResultsCount;
    }
    
    public void setSearchCallback(SearchCallback callback) {
        this.searchCallback = callback;
    }
    
    public void setViewToggleCallback(ViewToggleCallback callback) {
        this.viewToggleCallback = callback;
    }
    
    public void setSortFilterCallback(SortFilterCallback callback) {
        this.sortFilterCallback = callback;
    }
}
