package fpt.prm392.fe_salehunter.viewmodel.activity;

import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

import fpt.prm392.fe_salehunter.R;
import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.barcode.BarcodeMonsterResponseModel;
import fpt.prm392.fe_salehunter.model.barcode.OpenFoodFactsResponseModel;
import fpt.prm392.fe_salehunter.model.barcode.UpcItemDbResponseModel;
import retrofit2.Response;

public class ScannerViewModel extends AndroidViewModel {
    private final Repository repository;
    private final MutableLiveData<String> result;
    private LiveData<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb;
    private LiveData<Response<BarcodeMonsterResponseModel>> barcodeLookupBarcodeMonster;
    private LiveData<Response<OpenFoodFactsResponseModel>> barcodeLookupOpenFoodFacts;

    private ProcessCameraProvider cameraProvider;
    private final ImageAnalysis imageAnalysis;
    private final BarcodeScanner barcodeScanner;

    private final Vibrator vibrator;
    private final MediaPlayer sfx;

    public ScannerViewModel(Application application){
        super(application);
        repository = new Repository();
        result = new MutableLiveData<>();

        vibrator = (Vibrator) application.getSystemService(Context.VIBRATOR_SERVICE);
        sfx = MediaPlayer.create(application, R.raw.scanner_sound);
        sfx.setVolume(0.25f,0.25f);

        try {
            cameraProvider = ProcessCameraProvider.getInstance(application).get();
        }
        catch (Exception e){
            result.setValue("Camera provider not available");
            e.printStackTrace();
        }

        barcodeScanner = BarcodeScanning.getClient(new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_8,Barcode.FORMAT_EAN_13,Barcode.FORMAT_UPC_A,Barcode.FORMAT_UPC_E)
                .build()
        );

        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1920, 1080))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(application),new ImageAnalysis.Analyzer(){
            @androidx.camera.core.ExperimentalGetImage

            @Override
            public void analyze(@NonNull ImageProxy image) {
                Image mediaImage =  image.getImage();

                if (mediaImage != null) {
                    InputImage inputImage = InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());

                    barcodeScanner.process(inputImage)
                            .addOnSuccessListener(barcodes -> {
                                for (Barcode barcode : barcodes) {
                                    String rawValue = barcode.getRawValue();
                                    if(rawValue!=null) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                                        sfx.start();
                                        result.setValue(rawValue);
                                        barcodeScanner.close();
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                //on process start error
                            })
                            .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Barcode>> task) {
                                    image.close();
                                }
                            });

                }

            }

        });

    }

    public MutableLiveData<String> getResult(){return result;}

    public void bindCameraProvider(LifecycleOwner lifecycleOwner, Preview.SurfaceProvider surfaceProvider){
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(surfaceProvider);

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview);
    }

    public LiveData<Response<UpcItemDbResponseModel>> barcodeLookupUpcItemDb(String barcode){
        barcodeLookupUpcItemDb = repository.barcodeLookupUpcItemDb(barcode);
        return barcodeLookupUpcItemDb;
    }

    public LiveData<Response<OpenFoodFactsResponseModel>> barcodeLookupOpenFoodFacts(String barcode){
        barcodeLookupOpenFoodFacts = repository.barcodeLookupOpenFoodFacts(barcode);
        return barcodeLookupOpenFoodFacts;
    }

    public void removeObserverUpcItemDb(LifecycleOwner lifecycleOwner){
        if (barcodeLookupUpcItemDb != null) {
            barcodeLookupUpcItemDb.removeObservers(lifecycleOwner);
        }
    }

    public void removeObserverBarcodeMonster(LifecycleOwner lifecycleOwner){
        if (barcodeLookupBarcodeMonster != null) {
            barcodeLookupBarcodeMonster.removeObservers(lifecycleOwner);
        }
    }

    public void removeObserverOpenFoodFacts(LifecycleOwner lifecycleOwner){
        if (barcodeLookupOpenFoodFacts != null) {
            barcodeLookupOpenFoodFacts.removeObservers(lifecycleOwner);
        }
    }

}
