package fpt.prm392.fe_salehunter.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.databinding.ActivityScannerBinding;
import fpt.prm392.fe_salehunter.data.local.repository.LocalBarcodeRepository;
import fpt.prm392.fe_salehunter.viewmodel.activity.ScannerViewModel;

public class Scanner extends AppCompatActivity {
    private ActivityScannerBinding vb;
    private ScannerViewModel viewModel;
    private LocalBarcodeRepository localBarcodeRepository;

    public static final int RESULT_TRY_AGAIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        vb = ActivityScannerBinding.inflate(getLayoutInflater());
        View view = vb.getRoot();
        setContentView(view);
        setTheme(R.style.SaleHunter);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        vb.lazer.startAnimation(AnimationUtils.loadAnimation(this,R.anim.scanner_lazer_effect));

        viewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        localBarcodeRepository = new LocalBarcodeRepository(this);

        viewModel.bindCameraProvider(this, vb.cameraPreview.getSurfaceProvider());

        viewModel.getResult().observe(this, result -> {
            showDetectingProductLayout(result);
            lookUpBarcode(result);
        });

        vb.scannerDetectingTryAgain.setOnClickListener(button ->{
            Intent returnIntent = new Intent();
            setResult(Scanner.RESULT_TRY_AGAIN, returnIntent);
            onBackPressed();
        });

    }

    void submitData(String productName){
        String[] arrayOfWords = productName.split(" ");

        StringBuilder shortenedName = new StringBuilder();
        for(int i=0; i< arrayOfWords.length; i++){
            shortenedName.append(arrayOfWords[i]);
            if(i<5) shortenedName.append(" ");
            else break;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", shortenedName.toString());
        setResult(Scanner.RESULT_OK, returnIntent);
        onBackPressed();
    }

    void showDetectingProductLayout(String barcode){
        vb.scannerScanningLayout.setVisibility(View.INVISIBLE);
        vb.scannerDetectingLayout.setVisibility(View.VISIBLE);
        vb.scannerDetectingBarcode.setText(barcode);
        vb.scannerDetectingTryAgain.setVisibility(View.GONE);
    }

    void productNotDetected(String message){
        vb.scannerDetectingBarcode.clearAnimation();
        vb.scannerDetectingImage.clearAnimation();
        vb.scannerDetectingLoading.setVisibility(View.GONE);
        vb.scannerDetectingText.setText(message);
        vb.scannerDetectingText.setTextColor(getResources().getColor(R.color.accent));
        vb.scannerDetectingTryAgain.setVisibility(View.VISIBLE);
        vb.scannerDetectingTryAgain.startAnimation(AnimationUtils.loadAnimation(this,R.anim.lay_on));
    }

    void lookUpBarcode(String barcode){
       // First check local cache, then fallback to API
       localBarcodeRepository.lookupProductByBarcode(barcode, new LocalBarcodeRepository.BarcodeCallback<String>() {
           @Override
           public void onSuccess(String productName) {
               runOnUiThread(() -> {
                   if (productName != null && !productName.trim().isEmpty()) {
                       submitData(productName);
                   } else {
                       lookUpBarcodeFromAPI(barcode);
                   }
               });
           }
           
           @Override
           public void onError(Exception error) {
               runOnUiThread(() -> lookUpBarcodeFromAPI(barcode));
           }
       });
    }

    void lookUpBarcodeFromAPI(String barcode){
        lookUpBarcodeAlt(barcode);
    }

    void lookUpBarcodeAlt(String barcode){
        viewModel.barcodeLookupUpcItemDb(barcode).observe(this, response ->{
            if(response.code() == 200) {
                viewModel.removeObserverUpcItemDb(this);

                if(response.body().getItems().size()>0) {
                    String productName = response.body().getItems().get(0).getProductName();
                    
                    // Cache the successful lookup
                    cacheBarcodeLookup(barcode, productName, "upc_item_db");
                    
                    submitData(productName);
                } else lookUpBarcodeAlt2(barcode);

            } else lookUpBarcodeAlt2(barcode);

        });
    }

    void lookUpBarcodeAlt2(String barcode){
        viewModel.barcodeLookupBarcodeMonster(barcode).observe(this, responseAlt ->{
            if(responseAlt.code() == 200) {
                viewModel.removeObserverBarcodeMonster(this);

                if(responseAlt.body().getProductName() != null) {
                    String productName = responseAlt.body().getProductName();
                    
                    // Cache the successful lookup
                    cacheBarcodeLookup(barcode, productName, "barcode_monster");
                    
                    submitData(productName);
                } else productNotDetected(getString(R.string.Product_Not_Detected));

            } else productNotDetected(getString(R.string.You_are_Disconnected));
        });
    }

    /**
     * Cache barcode lookup result for future use
     */
    private void cacheBarcodeLookup(String barcode, String productName, String source) {
        localBarcodeRepository.cacheBarcodeLookup(barcode, productName, null, source, 
            new LocalBarcodeRepository.BarcodeCallback<Long>() {
                @Override
                public void onSuccess(Long id) {
                    // Successfully cached
                }
                
                @Override
                public void onError(Exception error) {
                    // Cache failed, but continue normally
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localBarcodeRepository != null) {
            localBarcodeRepository.shutdown();
        }
        vb = null;
    }
}