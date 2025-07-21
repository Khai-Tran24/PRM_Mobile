package fpt.prm392.fe_salehunter.viewmodel.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import fpt.prm392.fe_salehunter.data.Repository;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.request.CreateStoreRequestModel;
import fpt.prm392.fe_salehunter.model.store.StoreModel;
import fpt.prm392.fe_salehunter.util.UserAccountManager;
import retrofit2.Response;

public class CreateStoreViewModel extends AndroidViewModel {
    private final Repository repository;
    private final String token;
    private LiveData<Response<BaseResponseModel<StoreModel>>> createStoreObserver;
    private LiveData<Response<BaseResponseModel<Object>>> updateStoreObserver;

    public CreateStoreViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository();
        token = UserAccountManager.getToken(application, UserAccountManager.TOKEN_TYPE_BEARER);
    }

    public LiveData<Response<BaseResponseModel<StoreModel>>> createStore(CreateStoreRequestModel requestModel) {
        createStoreObserver = repository.createStore(token, requestModel);
        return createStoreObserver;
    }

    public void removeObserverCreateStore(LifecycleOwner lifecycleOwner) {
        createStoreObserver.removeObservers(lifecycleOwner);
    }

    public LiveData<Response<BaseResponseModel<Object>>> updateStore(long storeId, String logoBase64, String name, String category, String address, Double latitude, Double longitude, String description, String phone, boolean hasWhatsapp, String websiteLink, String facebookUsername, String InstagramUsername) {
        CreateStoreRequestModel createStoreRequestModel = new CreateStoreRequestModel();
        createStoreRequestModel.setLogoBase64(logoBase64);
        createStoreRequestModel.setName(name);
        createStoreRequestModel.setCategory(category);
        createStoreRequestModel.setAddress(address);
        createStoreRequestModel.setLatitude(latitude);
        createStoreRequestModel.setLongitude(longitude);
        createStoreRequestModel.setDescription(description);
        if (!phone.isEmpty()) createStoreRequestModel.setPhone(phone);
        if (hasWhatsapp) createStoreRequestModel.setWhatsappPhoneNumber(phone);
        if (!websiteLink.isEmpty()) createStoreRequestModel.setWebsiteLink(websiteLink);
        if (!facebookUsername.isEmpty())
            createStoreRequestModel.setFacebookLink("https://www.facebook.com/" + facebookUsername + "/");
        if (!InstagramUsername.isEmpty())
            createStoreRequestModel.setInstagramLink("https://www.instagram.com/" + InstagramUsername + "/");

        updateStoreObserver = repository.updateStore(token, storeId, createStoreRequestModel);
        return updateStoreObserver;
    }

    public void removeObserverUpdateStore(LifecycleOwner lifecycleOwner) {
        updateStoreObserver.removeObservers(lifecycleOwner);
    }

}
