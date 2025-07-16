package fpt.prm392.fe_salehunter.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import fpt.prm392.fe_salehunter.model.BcRecord;

@Database(entities = {BcRecord.class}, version = 2, exportSchema = false)
public abstract class myDataBase extends RoomDatabase {
    public abstract myDao daoAccess() ;

     private static myDataBase db;

    public static myDataBase get(Context context){
        if(db==null){
            db = Room.databaseBuilder(context, myDataBase.class,"bc")
                    // Removed .createFromAsset("bc.db") - creates empty database instead
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return db;
    }
    
}
