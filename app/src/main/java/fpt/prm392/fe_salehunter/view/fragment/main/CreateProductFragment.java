package fpt.prm392.fe_salehunter.view.fragment.main;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.databinding.FragmentCreateProductBinding;
import fpt.prm392.fe_salehunter.model.request.CreateProductRequestModel;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.product.ProductModel;
import fpt.prm392.fe_salehunter.util.DialogsProvider;
import fpt.prm392.fe_salehunter.util.ImageEncoder;
import fpt.prm392.fe_salehunter.util.TextFieldValidator;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import fpt.prm392.fe_salehunter.view.activity.MainActivity;
import fpt.prm392.fe_salehunter.viewmodel.fragment.main.CreateProductViewModel;

public class CreateProductFragment extends Fragment {
    private static final String TAG = "CreateProductFragment";
    private FragmentCreateProductBinding vb;
    private CreateProductViewModel viewModel;
    private NavController navController;

    // Image handling
    private Uri image1, image2, image3;
    private Uri currentPhotoUri;
    private int currentImageSlot = 0; // Track which image slot is being updated
    
    // Activity result launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    // Fragment state
    private int action = 0;
    public static final String ACTION_KEY = "action";
    public static final int ACTION_CREATE_PRODUCT = 0;
    public static final int ACTION_EDIT_PRODUCT = 1;

    private ProductModel productData;
    public static final String PRODUCT_DATA_KEY = "productData";
    private long storeId;
    public static final String STORE_ID_KEY = "storeId";

    private Double productPrice = 0.0;

    public CreateProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
                }
            }
        );

        // Camera launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && currentPhotoUri != null) {
                    setImageToSlot(currentImageSlot, currentPhotoUri);
                    currentPhotoUri = null;
                }
            }
        );

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        setImageToSlot(currentImageSlot, selectedImageUri);
                    }
                }
            }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vb = FragmentCreateProductBinding.inflate(inflater, container, false);
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
        ((MainActivity) getActivity()).setTitle(getString(R.string.product));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            action = getArguments().getInt(ACTION_KEY);
            if (action == ACTION_EDIT_PRODUCT)
                productData = new Gson().fromJson(getArguments().getString(PRODUCT_DATA_KEY), ProductModel.class);

            storeId = getArguments().getLong(STORE_ID_KEY);
        }

        viewModel = new ViewModelProvider(this).get(CreateProductViewModel.class);

        new Handler().post(() -> {
            navController = ((MainActivity) getActivity()).getAppNavController();
        });

        vb.createProductScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                double scrollViewHeight = vb.createProductScroll.getChildAt(0).getBottom() - vb.createProductScroll.getHeight();
                double scrollPosition = (scrollY / scrollViewHeight) * 100;
                vb.createProductPhaseProgress.setProgress((int) scrollPosition);
            }
        });

        // Setup image container click listeners
        setupImageClickListeners();

        vb.createProductSaleSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vb.createProductSalePercent.setText(progress + "%");
                if (progress < 100)
                    vb.createProductDiscountedPrice.setText((productPrice - (productPrice * progress / 100)) + "");
                else vb.createProductDiscountedPrice.setText(R.string.free);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        vb.createProductPrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    productPrice = Double.parseDouble(editable.toString());

                    if (productPrice > 0) vb.createProductPrice.setError(null);
                    else {
                        vb.createProductPrice.setError(getString(R.string.Not_a_valid_price));
                        productPrice = 1.0;
                    }
                } else {
                    vb.createProductPrice.setError(null);
                    vb.createProductDiscountedPrice.setText("0");
                }

                vb.createProductDiscountedPrice.setText((productPrice - (productPrice * vb.createProductSaleSlider.getProgress() / 100) + ""));

            }
        });


        vb.createProductName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0) {
                    if (TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.PRODUCTNAME_MIN, TextFieldValidator.PRODUCTNAME_MAX))
                        vb.createProductName.setError(getString(R.string.Product_Name_length_should_be_between) + TextFieldValidator.PRODUCTNAME_MIN + " & " + TextFieldValidator.PRODUCTNAME_MAX);
                    else if (TextFieldValidator.isValidProductName(editable.toString()))
                        vb.createProductName.setError(null);
                    else vb.createProductName.setError(getString(R.string.Not_valid_name));
                } else vb.createProductName.setError(null);

            }
        });

        vb.createProductCategory.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.CATEGORY_MIN, TextFieldValidator.CATEGORY_MAX))
                    vb.createProductCategory.setError(getString(R.string.Category_length_should_be_between) + TextFieldValidator.CATEGORY_MIN + " & " + TextFieldValidator.CATEGORY_MAX);
                else vb.createProductCategory.setError(null);

            }
        });

        vb.createProductBrand.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.BRAND_MIN, TextFieldValidator.BRAND_MAX))
                    vb.createProductBrand.setError(getString(R.string.Brand_length_should_be_between) + TextFieldValidator.BRAND_MIN + " & " + TextFieldValidator.BRAND_MAX);
                else vb.createProductBrand.setError(null);

            }
        });

        vb.createProductDescription.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString().length() > 0 && TextFieldValidator.outLengthRange(editable.toString(), TextFieldValidator.DESCRIPTION_MIN, TextFieldValidator.DESCRIPTION_MAX))
                    vb.createProductDescription.setError(getString(R.string.Description_length_should_be_between) + TextFieldValidator.DESCRIPTION_MIN + " & " + TextFieldValidator.DESCRIPTION_MAX);
                else vb.createProductDescription.setError(null);

            }
        });

        if (action == ACTION_EDIT_PRODUCT) {
            vb.createProductSubmitButton.setText(R.string.Update_Product);
            vb.createProductSubmitButton.setOnClickListener(button -> {
                if (isDataValid()) updateProduct();
            });

            vb.createProductDeleteProduct.setOnClickListener(button -> {

                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
                materialAlertDialogBuilder.setTitle(R.string.Delete_Product);
                materialAlertDialogBuilder.setMessage(R.string.Are_you_sure_that_you_want_to_delete_this_product);
                materialAlertDialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProduct();
                    }
                });

                materialAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                materialAlertDialogBuilder.show();
            });

            renderProductData();
        } else {
            vb.createProductSubmitButton.setText(R.string.Create_Product);
            vb.createProductSubmitButton.setOnClickListener(button -> {
                if (isDataValid()) createProduct();
            });
        }
    }

    boolean isDataValid() {
        boolean validData = true;

        if (vb.createProductDescription.getError() != null || vb.createProductDescription.getEditText().getText().length() == 0) {
            vb.createProductDescription.requestFocus();
            vb.createProductDescription.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (action != ACTION_EDIT_PRODUCT) {
            if (vb.createProductBrand.getError() != null || vb.createProductBrand.getEditText().getText().length() == 0) {
                vb.createProductBrand.requestFocus();
                vb.createProductBrand.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                validData = false;
            }

            if (vb.createProductCategory.getError() != null || vb.createProductCategory.getEditText().getText().length() == 0) {
                vb.createProductCategory.requestFocus();
                vb.createProductCategory.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                validData = false;
            }

            // Check for at least one image (primary image is required)
            if (image1 == null) {
                vb.createProductScroll.smoothScrollTo(0, vb.createProductImageContainer1.getTop());
                vb.createProductImageContainer1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
                Toast.makeText(getContext(), "At least one product image is required", Toast.LENGTH_SHORT).show();
                validData = false;
            }
        }

        if (vb.createProductPrice.getError() != null || vb.createProductPrice.getEditText().getText().length() == 0) {
            vb.createProductPrice.requestFocus();
            vb.createProductPrice.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        if (vb.createProductName.getError() != null || vb.createProductName.getEditText().getText().length() == 0) {
            vb.createProductName.requestFocus();
            vb.createProductName.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fieldmissing));
            validData = false;
        }

        return validData;
    }

    void createProduct() {
        DialogsProvider.get(getActivity()).setLoading(true);

        ArrayList<String> images = new ArrayList<>();
        
        // Add primary image (required)
        if (image1 != null) {
            images.add(ImageEncoder.get().encode(getContext(), image1));
        }
        
        // Add secondary images (optional)
        if (image2 != null) {
            images.add(ImageEncoder.get().encode(getContext(), image2));
        }
        
        if (image3 != null) {
            images.add(ImageEncoder.get().encode(getContext(), image3));
        }

        var requestModel = CreateProductRequestModel.builder()
                .name(vb.createProductName.getEditText().getText().toString())
                .price(Double.parseDouble(vb.createProductPrice.getEditText().getText().toString()))
                .salePercent(vb.createProductSaleSlider.getProgress())
                .category(vb.createProductCategory.getEditText().getText().toString())
                .brand(vb.createProductBrand.getEditText().getText().toString())
                .description(vb.createProductDescription.getEditText().getText().toString())
                .images(images)
                .build();

        viewModel.createProduct(requestModel).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_CREATION:
                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", storeId);
                    navController.navigate(R.id.action_createProductFragment_to_dashboardFragment, bundle);
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    UserAccountManager.signOut(getActivity(), true);
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error), getString(R.string.Code) + response.code());
            }

            viewModel.removeObserverCreateProduct(getViewLifecycleOwner());
        });

    }

    void updateProduct() {
        DialogsProvider.get(getActivity()).setLoading(true);
        var requestModel = CreateProductRequestModel.builder()
                .name(vb.createProductName.getEditText().getText().toString())
                .price(Double.parseDouble(vb.createProductPrice.getEditText().getText().toString()))
                .salePercent(vb.createProductSaleSlider.getProgress())
                .description(vb.createProductDescription.getEditText().getText().toString())
                .build();

        viewModel.updateProduct(
                productData.getId(),
                requestModel
        ).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_OPERATION:
                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", productData.getStoreId());
                    navController.navigate(R.id.action_createProductFragment_to_dashboardFragment, bundle);
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    UserAccountManager.signOut(getActivity(), true);
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error), getString(R.string.Code) + response.code());
            }

            viewModel.removeObserverUpdateProduct(getViewLifecycleOwner());
        });
    }

    void deleteProduct() {
        DialogsProvider.get(getActivity()).setLoading(true);

        viewModel.deleteProduct(storeId, productData.getId()).observe(getViewLifecycleOwner(), response -> {
            DialogsProvider.get(getActivity()).setLoading(false);

            switch (response.code()) {
                case BaseResponseModel.SUCCESSFUL_DELETED | BaseResponseModel.SUCCESSFUL_OPERATION:
                    Bundle bundle = new Bundle();
                    bundle.putLong("storeId", productData.getStoreId());
                    navController.navigate(R.id.action_createProductFragment_to_dashboardFragment, bundle);
                    break;

                case BaseResponseModel.FAILED_AUTH:
                    UserAccountManager.signOut(getActivity(), true);
                    break;

                default:
                    DialogsProvider.get(getActivity()).messageDialog(getString(R.string.Server_Error), getString(R.string.Code) + response.code());
            }

            viewModel.removeObserverDeleteProduct(getViewLifecycleOwner());
        });
    }

    void renderProductData() {
        vb.createProductName.getEditText().setText(productData.getName());
        vb.createProductPrice.getEditText().setText(productData.getCurrentPrice() + "");
        vb.createProductSaleSlider.setProgress(productData.getSalePercent());
        vb.createProductDescription.getEditText().setText(productData.getDescription());

        vb.createProductPhaseLayout.setVisibility(View.GONE);

        vb.createProductCategory.setVisibility(View.GONE);
        vb.createProductBrand.setVisibility(View.GONE);
        vb.createProductProductImagesTitle.setVisibility(View.GONE);
        vb.createProductImageContainer1.setVisibility(View.GONE);
        vb.createProductSecondaryImagesContainer.setVisibility(View.GONE);

        vb.createProductDeleteProduct.setVisibility(View.VISIBLE);
    }

    /**
     * Setup click listeners for all image containers and their action buttons
     */
    private void setupImageClickListeners() {
        // Primary image container (required)
        vb.createProductImageContainer1.setOnClickListener(v -> showImageSelectionDialog(1));
        
        // Secondary image containers (optional)
        vb.createProductImageContainer2.setOnClickListener(v -> showImageSelectionDialog(2));
        vb.createProductImageContainer3.setOnClickListener(v -> showImageSelectionDialog(3));

        // Primary image action buttons
        vb.createProductImage1Camera.setOnClickListener(v -> {
            currentImageSlot = 1;
            checkCameraPermissionAndOpenCamera();
        });
        vb.createProductImage1Gallery.setOnClickListener(v -> {
            currentImageSlot = 1;
            openGallery();
        });

        // Remove buttons
        vb.createProductImage1Remove.setOnClickListener(v -> removeImage(1));
        vb.createProductImage2Remove.setOnClickListener(v -> removeImage(2));
        vb.createProductImage3Remove.setOnClickListener(v -> removeImage(3));
    }

    /**
     * Show dialog to choose between camera and gallery for secondary images
     */
    private void showImageSelectionDialog(int imageSlot) {
        currentImageSlot = imageSlot;
        
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.camera_or_gallery);
        
        String[] options = {getString(R.string.take_photo), getString(R.string.choose_from_gallery)};
        
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermissionAndOpenCamera();
            } else {
                openGallery();
            }
        });
        
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    /**
     * Check camera permission and open camera if granted
     */
    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) 
            == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Open camera to take a photo
     */
    private void openCamera() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    currentPhotoUri = FileProvider.getUriForFile(
                        requireContext(),
                        "fpt.prm392.fe_salehunter.fileprovider",
                        photoFile
                    );
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                    cameraLauncher.launch(cameraIntent);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error creating image file", e);
            Toast.makeText(getContext(), "Error opening camera", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open gallery to select an image
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }

    /**
     * Create a temporary image file for camera capture
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "PRODUCT_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Set image to the specified slot and update UI
     */
    private void setImageToSlot(int slot, Uri imageUri) {
        try {
            switch (slot) {
                case 1:
                    image1 = imageUri;
                    loadImageWithGlide(imageUri, vb.createProductImage1);
                    updateImageContainer1UI(true);
                    break;
                case 2:
                    image2 = imageUri;
                    loadImageWithGlide(imageUri, vb.createProductImage2);
                    vb.createProductImage2Remove.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    image3 = imageUri;
                    loadImageWithGlide(imageUri, vb.createProductImage3);
                    vb.createProductImage3Remove.setVisibility(View.VISIBLE);
                    break;
            }
            Toast.makeText(getContext(), "Image added successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error setting image to slot " + slot, e);
            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load image using Glide with proper error handling
     */
    private void loadImageWithGlide(Uri imageUri, android.widget.ImageView imageView) {
        Glide.with(this)
            .load(imageUri)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade(300))
            .error(R.drawable.ic_add_photo_primary)
            .into(imageView);
    }

    /**
     * Update primary image container UI based on whether image is set
     */
    private void updateImageContainer1UI(boolean hasImage) {
        if (hasImage) {
            vb.createProductImage1Label.setVisibility(View.GONE);
            vb.createProductImage1Actions.setVisibility(View.VISIBLE);
            vb.createProductImage1Remove.setVisibility(View.VISIBLE);
        } else {
            vb.createProductImage1Label.setVisibility(View.VISIBLE);
            vb.createProductImage1Actions.setVisibility(View.GONE);
            vb.createProductImage1Remove.setVisibility(View.GONE);
            vb.createProductImage1.setImageResource(R.drawable.ic_add_photo_primary);
        }
    }

    /**
     * Remove image from specified slot
     */
    private void removeImage(int slot) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Remove Image");
        builder.setMessage("Are you sure you want to remove this image?");
        
        builder.setPositiveButton("Remove", (dialog, which) -> {
            switch (slot) {
                case 1:
                    image1 = null;
                    updateImageContainer1UI(false);
                    break;
                case 2:
                    image2 = null;
                    vb.createProductImage2.setImageResource(R.drawable.ic_add_photo_secondary);
                    vb.createProductImage2Remove.setVisibility(View.GONE);
                    break;
                case 3:
                    image3 = null;
                    vb.createProductImage3.setImageResource(R.drawable.ic_add_photo_secondary);
                    vb.createProductImage3Remove.setVisibility(View.GONE);
                    break;
            }
            Toast.makeText(getContext(), "Image removed", Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}

