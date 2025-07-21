package fpt.prm392.fe_salehunter.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.Locale;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.util.AppSettingsManager;

public class ProductsCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<ProductModel> productData;
    private final RecyclerView recyclerView;
    private final Context context;

    private final ProductModel loadingCardObject = new ProductModel();
    private final ProductModel noResultCardObject = new ProductModel();

    private final int TYPE_DATA_VIEW_HOLDER = 0;
    private final int TYPE_LOADING_VIEW_HOLDER = 1;
    private final int TYPE_NO_RESULT_VIEW_HOLDER = 2;

    private LastItemReachedListener lastItemReachedListener;
    private ItemInteractionListener itemInteractionListener;

    private boolean noResultsFound = false;
    private boolean hideFavButton = false;

    public ProductsCardAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.productData = new ArrayList<>();
    }
    public interface LastItemReachedListener {
        void onLastItemReached();
    }

    public interface ItemInteractionListener {
        void onProductClicked(long productId, String storeType);

        void onProductAddedToFav(long productId, boolean favChecked);
    }

    public void setLastItemReachedListener(LastItemReachedListener lastItemReachedListener) {
        this.lastItemReachedListener = lastItemReachedListener;
    }

    public void setItemInteractionListener(ItemInteractionListener itemInteractionListener) {
        this.itemInteractionListener = itemInteractionListener;
    }

    //item view inner class
    public static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView brand, name, price, rate, sale;
        ImageView image, store;
        CheckBox favourite;
        ImageView rateIcon;

        public DataViewHolder(View view) {
            super(view);

            brand = view.findViewById(R.id.product_card_brand);
            name = view.findViewById(R.id.product_card_Name);
            price = view.findViewById(R.id.product_card_price);
            rate = view.findViewById(R.id.product_card_rate);
            image = view.findViewById(R.id.product_card_image);
            store = view.findViewById(R.id.product_card_store);
            favourite = view.findViewById(R.id.product_card_favourite);
            rateIcon = view.findViewById(R.id.product_card_rate_icon);
            sale = view.findViewById(R.id.product_card_salePercent);
        }

    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    public static class NoResultViewHolder extends RecyclerView.ViewHolder {
        public NoResultViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (productData.get(position) == loadingCardObject) return TYPE_LOADING_VIEW_HOLDER;
        else if (productData.get(position) == noResultCardObject) return TYPE_NO_RESULT_VIEW_HOLDER;
        else return TYPE_DATA_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_LOADING_VIEW_HOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_loading_layout, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == TYPE_NO_RESULT_VIEW_HOLDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_no_results_layout, parent, false);
            return new NoResultViewHolder(view);
        }

        //Default ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_layout, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (getItemViewType(position) == TYPE_DATA_VIEW_HOLDER) {

            DataViewHolder holder = (DataViewHolder) viewHolder;

            holder.name.setText(productData.get(position).getName());
            holder.brand.setText(productData.get(position).getBrand());
            holder.price.setText(String.format(Locale.ENGLISH, "%.1f %s",productData.get(position).getCurrentPrice(), context.getString(R.string.currency)));
            holder.rate.setText(String.valueOf(productData.get(position).getAverageRating()));
            holder.favourite.setChecked(productData.get(position).isFavorite());
            holder.sale.setText(String.format(Locale.ENGLISH, "%d%s", productData.get(position).getSalePercent(), context.getString(R.string.sale_percent)));

            if (hideFavButton) holder.favourite.setVisibility(View.GONE);
            if (productData.get(position).getSalePercent() == 0) holder.sale.setVisibility(View.GONE);

            if (productData.get(position).getAverageRating() == 0) {
                holder.rate.setVisibility(View.INVISIBLE);
                holder.rateIcon.setVisibility(View.INVISIBLE);
            }

            //Store
            if(isDarkModeEnabled()) holder.store.setImageTintList(ColorStateList.valueOf(Color.WHITE));

            if (productData.get(position).getStoreImageUrl() != null)
                Glide.with(context)
                        .load(productData.get(position).getStoreImageUrl())
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(250))
                        .into(holder.store);

            //Image
            Glide.with(context)
                    .load(productData.get(position).getMainImage())
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(holder.image);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemInteractionListener != null)
                        itemInteractionListener.onProductClicked(productData.get(holder.getAdapterPosition()).getId(), productData.get(holder.getAdapterPosition()).getStoreType());
                }
            });

            holder.favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productData.get(holder.getAdapterPosition()).setFavorite(holder.favourite.isChecked());
                    if (itemInteractionListener != null)
                        itemInteractionListener.onProductAddedToFav(productData.get(holder.getAdapterPosition()).getId(), holder.favourite.isChecked());
                }
            });

        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (noResultsFound) return;
        if (holder.getAdapterPosition() == productData.size() - 1 && lastItemReachedListener != null && !isLoading())
            lastItemReachedListener.onLastItemReached();
    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public void addProduct(ProductModel product) {
        if (noResultsFound) return;

        recyclerView.post(() -> {
            productData.add(product);
            notifyItemInserted(getItemCount());
        });
    }

    //NoPost for nested recyclerviews adapters
    public void addProductNoPost(ProductModel product) {
        if (noResultsFound) return;

        productData.add(product);
        notifyItemInserted(getItemCount());
    }

    public void addProducts(ArrayList<ProductModel> products) {
        if (noResultsFound) return;

        recyclerView.post(() -> {
            productData.addAll(products);
            notifyItemRangeInserted(getItemCount(), products.size());
        });
    }

    //NoPost for nested recyclerviews adapters
    public void addProductsNoPost(ArrayList<ProductModel> products) {
        if (noResultsFound) return;

        productData.addAll(products);
        notifyItemRangeInserted(getItemCount(), products.size());
    }

    public void clearProducts() {
        productData.clear();
        notifyDataSetChanged();
    }

    public boolean isLoading() {
        return productData.contains(loadingCardObject);
    }

    public void setLoading(boolean loading) {
        recyclerView.post(() -> {
            if (isLoading() == loading) return;

            if (loading) {
                productData.add(loadingCardObject);
                notifyItemInserted(getItemCount());
            } else {
                productData.remove(loadingCardObject);
                notifyItemChanged(getItemCount());
            }
        });

    }

    //NoPost for nested recyclerviews adapters
    public void setLoadingNoPost(boolean loading) {
        if (isLoading() == loading) return;

        if (loading) {
            productData.add(loadingCardObject);
            notifyItemInserted(getItemCount());
        } else {
            productData.remove(loadingCardObject);
            notifyItemChanged(getItemCount());
        }
    }

    public void showNoResultsFound() {
        if (productData.contains(noResultCardObject)) return;

        productData.add(noResultCardObject);
        notifyItemInserted(0);
        noResultsFound = true;
    }

    public void setHideFavButton(boolean hide) {
        hideFavButton = hide;
    }

    public boolean isDarkModeEnabled() {
        int currentMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentMode == Configuration.UI_MODE_NIGHT_YES;
    }

    boolean renderDataInLocalLanguage() {
        switch (AppSettingsManager.getLanguageKey(context)) {
            case AppSettingsManager.LANGUAGE_ENGLISH:
                return false;
            case AppSettingsManager.LANGUAGE_ARABIC:
                return true;
            default:
                String systemLanguage = Locale.getDefault().getLanguage();
                if (systemLanguage.equals(AppSettingsManager.LANGUAGE_ARABIC)) return true;
                else return false;
        }
    }

}
