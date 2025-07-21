package fpt.prm392.fe_salehunter.view.fragment.dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.HashSet;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.databinding.FragmentSortAndFilterDialogBinding;
import fpt.prm392.fe_salehunter.model.SortAndFilterModel;
import lombok.Setter;

public class SortAndFilterDialog extends BottomSheetDialogFragment {
    private FragmentSortAndFilterDialogBinding vb;

    @Setter
    private SortAndFilterModel sortAndFilterModel;
    @Setter
    private HashSet<String> categories;
    @Setter
    private HashSet<String> brands;

    @Setter
    SortAndFilterDialog.DialogResultListener dialogResultListener;

    public SortAndFilterDialog() {
        // Required empty public constructor
        sortAndFilterModel = new SortAndFilterModel();
    }

    public interface DialogResultListener {
        void onApply(SortAndFilterModel sortAndFilterModel);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vb = FragmentSortAndFilterDialogBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vb = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        renderData(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vb.sortAndFilterDialogReset.setOnClickListener(button -> {
            sortAndFilterModel = new SortAndFilterModel();
            renderData(true);
        });

        vb.sortAndFilterDialogApplyFilter.setOnClickListener(button -> {
            updateSortAndFilterModel();
            dialogResultListener.onApply(sortAndFilterModel);
            dismiss();
        });

    }

    void renderData(boolean reset) {
        // Debug logging to check data availability
        android.util.Log.d("SortAndFilterDialog", "renderData called - reset: " + reset);
        android.util.Log.d("SortAndFilterDialog", "Categories count: " + (categories != null ? categories.size() : "null"));
        android.util.Log.d("SortAndFilterDialog", "Brands count: " + (brands != null ? brands.size() : "null"));

        // Force visibility again in case it was lost
        android.util.Log.d("SortAndFilterDialog", "Ensuring views are visible in renderData");
        vb.sortAndFilterDialogSortGroup.setVisibility(View.VISIBLE);
        vb.sortAndFilterDialogCategoryGroup.setVisibility(View.VISIBLE);
        vb.sortAndFilterDialogBrandGroup.setVisibility(View.VISIBLE);

        //Sorting List
        android.util.Log.d("SortAndFilterDialog", "Setting sort option: " + sortAndFilterModel.getSortBy());
        switch (sortAndFilterModel.getSortBy()) {

            case SortAndFilterModel.SORT_PRICE_ASC:
                vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_priceAsc);
                break;

            case SortAndFilterModel.SORT_PRICE_DSC:
                vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_priceDsc);
                break;

            case SortAndFilterModel.SORT_RATING:
                vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_rating);
                break;

            case SortAndFilterModel.SORT_NEWEST:
                vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_updateDateNewest);
                break;

            case SortAndFilterModel.SORT_BEST_DEAL:
                vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_bestDeal);
                break;

            case SortAndFilterModel.SORT_NEAREST_STORE:
                vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_nearestStore);
                break;

            default:
                vb.sortAndFilterDialogSortGroup.check(R.id.sortAndFilterDialog_sort_popularity);
        }

        //Price Section
        android.util.Log.d("SortAndFilterDialog", "Setting price fields");
        if (sortAndFilterModel.getMinPrice() > SortAndFilterModel.PRICE_MIN)
            vb.sortAndFilterDialogMinPrice.setText(String.valueOf(sortAndFilterModel.getMinPrice()));
        else vb.sortAndFilterDialogMinPrice.setText("");
        if (sortAndFilterModel.getMaxPrice() < SortAndFilterModel.PRICE_MAX)
            vb.sortAndFilterDialogMaxPrice.setText(String.valueOf(sortAndFilterModel.getMaxPrice()));
        else vb.sortAndFilterDialogMaxPrice.setText("");

        vb.sortAndFilterDialogMinPrice.clearFocus();
        vb.sortAndFilterDialogMaxPrice.clearFocus();

        //Category Group - Clear existing chips except "All Categories"
        if (reset) {
            vb.sortAndFilterDialogCategoryGroup.removeViews(1, Math.max(0, vb.sortAndFilterDialogCategoryGroup.getChildCount() - 1));
            vb.sortAndFilterDialogChipAllCategories.setChecked(true);
        }

        if (categories != null) {
            for (String category : categories) {
                android.util.Log.d("SortAndFilterDialog", "Adding category: " + category);
                // Create chip using the layout inflater instead of ChipDrawable.createFromAttributes
                Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.item_filter_chip, vb.sortAndFilterDialogCategoryGroup, false);
                chip.setText(category);
                chip.setCheckable(true);
                chip.setCheckedIconVisible(true);

                vb.sortAndFilterDialogCategoryGroup.addView(chip);

                if (category.equals(sortAndFilterModel.getCategory())) chip.setChecked(true);
            }
        }

        //Brand Group - Clear existing chips except "All Brands"
        if (reset) {
            vb.sortAndFilterDialogBrandGroup.removeViews(1, Math.max(0, vb.sortAndFilterDialogBrandGroup.getChildCount() - 1));
            vb.sortAndFilterDialogChipAllBrands.setChecked(true);
        }

        if (brands != null) {
            for (String brand : brands) {
                android.util.Log.d("SortAndFilterDialog", "Adding brand: " + brand);
                // Create chip using the layout inflater instead of ChipDrawable.createFromAttributes
                Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.item_filter_chip, vb.sortAndFilterDialogBrandGroup, false);
                chip.setText(brand);
                chip.setCheckable(true);
                chip.setCheckedIconVisible(true);

                vb.sortAndFilterDialogBrandGroup.addView(chip);

                if (brand.equals(sortAndFilterModel.getBrand())) chip.setChecked(true);
            }
        }

        // Force a layout refresh after all content is added
        android.util.Log.d("SortAndFilterDialog", "Requesting layout refresh");
        vb.getRoot().post(() -> {
            vb.getRoot().requestLayout();
            vb.getRoot().invalidate();
        });
    }

    void updateSortAndFilterModel() {
        int sortById = vb.sortAndFilterDialogSortGroup.getCheckedRadioButtonId();
        if (sortById == R.id.sortAndFilterDialog_sort_popularity) {
            sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_POPULARITY);
        } else if (sortById == R.id.sortAndFilterDialog_sort_priceAsc) {
            sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_PRICE_ASC);
        } else if (sortById == R.id.sortAndFilterDialog_sort_priceDsc) {
            sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_PRICE_DSC);
        } else if (sortById == R.id.sortAndFilterDialog_sort_rating) {
            sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_RATING);
        } else if (sortById == R.id.sortAndFilterDialog_sort_updateDateNewest) {
            sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_NEWEST);
        } else if (sortById == R.id.sortAndFilterDialog_sort_bestDeal) {
            sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_BEST_DEAL);
        } else if (sortById == R.id.sortAndFilterDialog_sort_nearestStore) {
            sortAndFilterModel.setSortBy(SortAndFilterModel.SORT_NEAREST_STORE);
        }

        //Price
        if (vb.sortAndFilterDialogMinPrice.getText().toString().isEmpty())
            sortAndFilterModel.setMinPrice(SortAndFilterModel.PRICE_MIN);
        else
            sortAndFilterModel.setMinPrice(Long.parseLong(vb.sortAndFilterDialogMinPrice.getText().toString()));

        if (vb.sortAndFilterDialogMaxPrice.getText().toString().isEmpty())
            sortAndFilterModel.setMaxPrice(SortAndFilterModel.PRICE_MAX);
        else
            sortAndFilterModel.setMaxPrice(Long.parseLong(vb.sortAndFilterDialogMaxPrice.getText().toString()));

        //Category
        int checkedCategoryId = vb.sortAndFilterDialogCategoryGroup.getCheckedChipId();
        if (checkedCategoryId != View.NO_ID) {
            Chip categoryChip = vb.sortAndFilterDialogCategoryGroup.findViewById(checkedCategoryId);
            if (categoryChip != null) {
                if (categoryChip.getText().equals(vb.sortAndFilterDialogChipAllCategories.getText()))
                    sortAndFilterModel.setCategory(SortAndFilterModel.CATEGORY_ALL);
                else sortAndFilterModel.setCategory(categoryChip.getText().toString());
            }
        }

        //Brand
        int checkedBrandId = vb.sortAndFilterDialogBrandGroup.getCheckedChipId();
        if (checkedBrandId != View.NO_ID) {
            Chip brandChip = vb.sortAndFilterDialogBrandGroup.findViewById(checkedBrandId);
            if (brandChip != null) {
                if (brandChip.getText().equals(vb.sortAndFilterDialogChipAllBrands.getText()))
                    sortAndFilterModel.setBrand(SortAndFilterModel.BRAND_ALL);
                else sortAndFilterModel.setBrand(brandChip.getText().toString());
            }
        }
    }
}