package fpt.prm392.fe_salehunter.model.store;

import com.google.gson.annotations.SerializedName;
import fpt.prm392.fe_salehunter.model.response.BaseResponseModel;
import fpt.prm392.fe_salehunter.model.store.StoreModel;

public class CreateStoreResponseModel extends BaseResponseModel<StoreModel> {
    @SerializedName("store")
    private StoreModel store;

    public StoreModel getStore() {
        return getData() != null ? getData() : store;
    }

    public void setStore(StoreModel store) {
        this.store = store;
        setData(store);
    }
}
