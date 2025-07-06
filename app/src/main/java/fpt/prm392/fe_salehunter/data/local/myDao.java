package fpt.prm392.fe_salehunter.data.local;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import fpt.prm392.fe_salehunter.model.BcRecord;

//Data Access object
@Dao
public interface myDao {

    @Query("select * from bc")
    public List<BcRecord> getData();

    @Query("select product from bc WHERE barcode = :productBarcode")
    public String lookUpProduct(String productBarcode);

}
