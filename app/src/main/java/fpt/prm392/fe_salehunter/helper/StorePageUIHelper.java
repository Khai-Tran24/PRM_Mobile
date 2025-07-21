package fpt.prm392.fe_salehunter.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.adapter.ProductsCardAdapter;
import fpt.prm392.fe_salehunter.databinding.FragmentStorePageBinding;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.model.store.StorePageData;
import fpt.prm392.fe_salehunter.model.user.UserModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.StorePageViewModel;

/**
 * Helper class for rendering store page UI elements
 */
public class StorePageUIHelper {

    /**
     * Render store data to UI elements
     */
    public static int renderStoreData(Context context, StorePageViewModel viewModel,
                                      FragmentStorePageBinding binding) {
        StorePageData storePageData = viewModel.getStorePageData();
        int toolbarOffset = -1000;

        binding.storePageStoreName.setText(storePageData.getName());
        binding.storePageStoreNameToolbar.setText(storePageData.getName());

        if (storePageData.getType().equals("online")) {
            setupOnlineStore(context, binding, storePageData);
            toolbarOffset = -500;
        } else {
            setupPhysicalStore(context, binding, storePageData);
        }

        setupStoreActions(context, binding, storePageData);

        return toolbarOffset;
    }

    /**
     * @deprecated Use StorePageDataHelper.renderInitialProducts() instead
     * This method is kept for backward compatibility but should not be used
     * for new product loading since it doesn't support proper pagination
     */
    @Deprecated
    public static boolean renderStoreInitialProductData(
            Context context, StorePageViewModel viewModel,
            FragmentStorePageBinding binding, ProductsCardAdapter adapter
    ) {
        // This method is deprecated - products should be loaded using search API
        // Return true to indicate end of products since we don't load from store data anymore
        binding.storePageNoProducts.setVisibility(View.VISIBLE);
        UserModel currentUser = UserAccountManager.getUser(context);
        if (currentUser != null && viewModel.getStoreId() == currentUser.getStoreId()) {
            binding.storePageNoProductsTitle.setText(R.string.You_dont_have_products);
        }
        return true;
    }

    /**
     * Setup UI for online store
     */
    private static void setupOnlineStore(Context context, FragmentStorePageBinding binding,
                                         StorePageData storeModel) {
        loadStoreImage(context, binding, storeModel.getLogoUrl());

        binding.storePageStoreCategory.setText(R.string.Online_Ecommerce);
        binding.storePageStoreCategoryToolbar.setText(R.string.Online_Ecommerce);

        binding.storePageStoreLocation.setVisibility(View.GONE);
        binding.storePageStorePhone.setVisibility(View.GONE);
    }

    /**
     * Setup UI for physical store
     */
    private static void setupPhysicalStore(Context context, FragmentStorePageBinding binding,
                                           StorePageData storeModel) {
        loadStoreImage(context, binding, storeModel.getLogoUrl());

        binding.storePageStoreCategory.setText(storeModel.getCategory());
        binding.storePageStoreCategoryToolbar.setText(storeModel.getCategory());

        binding.storePageStoreLocation.setText(storeModel.getAddress());
        binding.storePageStorePhone.setText(storeModel.getPhone());

        if (storeModel.getAddress() == null)
            binding.storePageStoreWebsite.setVisibility(View.GONE);
        else {
            binding.storePageStoreLocation.setText(storeModel.getAddress());
            binding.storePageStoreLocation.setOnClickListener(button -> {
                Toast.makeText(context, binding.storePageStoreLocation.getText(), Toast.LENGTH_LONG).show();
            });
        }

        if (storeModel.getPhone() == null)
            binding.storePageStoreWebsite.setVisibility(View.GONE);
        else {
            binding.storePageStorePhone.setText(storeModel.getPhone());
            binding.storePageStorePhone.setOnClickListener(button -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", binding.storePageStorePhone.getText().toString(), null));
                context.startActivity(intent);
            });
        }
    }

    /**
     * Load store logo image
     */
    private static void loadStoreImage(Context context, FragmentStorePageBinding binding,
                                       String logoUrl) {
        // Main logo
        Glide.with(context)
                .load(logoUrl)
                .fitCenter()
                .placeholder(R.drawable.store_placeholder)
                .into(binding.storePageLogo);

        // Toolbar logo
        Glide.with(context)
                .load(logoUrl)
                .fitCenter()
                .placeholder(R.drawable.store_placeholder)
                .into(binding.storePageLogoToolbar);
    }

    /**
     * Setup store action buttons (phone, location, social media)
     */
    private static void setupStoreActions(Context context, FragmentStorePageBinding binding,
                                          StorePageData storeModel) {
        // Phone action
        if (storeModel.getPhone() != null && !storeModel.getPhone().isEmpty()) {
            binding.storePageStorePhone.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + storeModel.getPhone()));
                context.startActivity(phoneIntent);
            });
        }

        // Location action
        if (storeModel.getLatitude() != null && storeModel.getLongitude() != null) {
            binding.storePageStoreLocation.setOnClickListener(v -> {
                String uri = "geo:" + storeModel.getLatitude() + "," + storeModel.getLongitude() +
                        "?q=" + storeModel.getLatitude() + "," + storeModel.getLongitude() +
                        "(" + storeModel.getName() + ")";
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(mapIntent);
            });
        }

        // Social media actions
        setupSocialMediaActions(context, binding, storeModel);
    }

    /**
     * Setup social media action buttons
     */
    private static void setupSocialMediaActions(Context context, FragmentStorePageBinding binding,
                                                StorePageData storeModel) {
        if (storeModel.getFacebookUrl() != null && !storeModel.getFacebookUrl().isEmpty()) {
            binding.storePageStoreFacebook.setOnClickListener(v -> {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeModel.getFacebookUrl()));
                context.startActivity(facebookIntent);
            });
        } else {
            binding.storePageStoreFacebook.setVisibility(View.GONE);
        }

        if (storeModel.getInstagramUrl() != null && !storeModel.getInstagramUrl().isEmpty()) {
            binding.storePageStoreInstagram.setOnClickListener(v -> {
                Intent instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeModel.getInstagramUrl()));
                context.startActivity(instagramIntent);
            });
        } else {
            binding.storePageStoreInstagram.setVisibility(View.GONE);
        }

        if (storeModel.getWebsiteUrl() != null && !storeModel.getWebsiteUrl().isEmpty()) {
            binding.storePageStoreWebsite.setOnClickListener(v -> {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storeModel.getWebsiteUrl()));
                context.startActivity(websiteIntent);
            });
        } else {
            binding.storePageStoreWebsite.setVisibility(View.GONE);
        }

        if (storeModel.getWhatsappPhone() != null && !storeModel.getWhatsappPhone().isEmpty()) {
            binding.storePageStoreWhatsapp.setOnClickListener(v -> {
                Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + storeModel.getWhatsappPhone()));
                context.startActivity(whatsappIntent);
            });
        } else {
            binding.storePageStoreWhatsapp.setVisibility(View.GONE);
        }
    }
}
