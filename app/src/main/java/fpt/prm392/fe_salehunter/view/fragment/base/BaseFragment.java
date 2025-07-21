package fpt.prm392.fe_salehunter.view.fragment.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.viewbinding.ViewBinding;

import fpt.prm392.fe_salehunter.view.activity.MainActivity;

/**
 * Base fragment class that provides common functionality and lifecycle management
 * Following the project's established patterns for fragment structure
 */
public abstract class BaseFragment<VB extends ViewBinding, VM extends ViewModel> extends Fragment {

    protected VB binding;
    protected VM viewModel;
    protected NavController navController;
    protected MainActivity mainActivity;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFragmentCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = createViewBinding(inflater, container);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Prevent re-initialization if view model already exists
        if (viewModel != null) return;

        // Initialize main activity reference
        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
        }

        // Set title
        setActivityTitle();

        // Initialize NavController with delay to ensure proper setup
        new Handler().post(() -> {
            if (mainActivity != null) {
                navController = mainActivity.getAppNavController();
            }
        });

        // Initialize view model
        initializeViewModel();

        // Process arguments
        processArguments(getArguments());

        // Setup UI components
        setupUI();

        // Load initial data
        loadInitialData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        onFragmentDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        setActivityTitle();
        onFragmentResume();
    }

    /**
     * Abstract methods to be implemented by subclasses
     */
    protected abstract VB createViewBinding(@NonNull LayoutInflater inflater, ViewGroup container);
    protected abstract Class<VM> getViewModelClass();
    protected abstract void setupUI();
    protected abstract String getFragmentTitle();

    /**
     * Optional methods that can be overridden by subclasses
     */
    protected void onFragmentCreate(Bundle savedInstanceState) {
        // Override in subclass if needed
    }

    protected void onFragmentDestroy() {
        // Override in subclass if needed
    }

    protected void onFragmentResume() {
        // Override in subclass if needed
    }

    protected void processArguments(Bundle arguments) {
        // Override in subclass to process fragment arguments
    }

    protected void loadInitialData() {
        // Override in subclass to load initial data
    }

    /**
     * Initialize view model using ViewModelProvider
     */
    private void initializeViewModel() {
        if (getViewModelClass() != null) {
            viewModel = new ViewModelProvider(this).get(getViewModelClass());
        }
    }

    /**
     * Set activity title
     */
    private void setActivityTitle() {
        if (mainActivity != null && getFragmentTitle() != null) {
            mainActivity.setTitle(getFragmentTitle());
        }
    }

    /**
     * Utility method to handle back navigation
     */
    protected void navigateBack() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    /**
     * Utility method to check if activity is valid
     */
    protected boolean isActivityValid() {
        return getActivity() != null && !getActivity().isFinishing() && isAdded();
    }

    /**
     * Safe context getter
     */
    protected android.content.Context getSafeContext() {
        return isActivityValid() ? getContext() : null;
    }
}
