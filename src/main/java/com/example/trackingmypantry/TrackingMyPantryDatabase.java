package com.example.trackingmypantry;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ProductEntity.class}, version = 1, exportSchema = false)
public abstract class TrackingMyPantryDatabase extends RoomDatabase {

    public abstract ProductDAO productDAO();

    private static volatile TrackingMyPantryDatabase INSTANCE;
    private static final int nTHREADS = 4;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(nTHREADS);
    //first time the database get created, delete all rows
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        //delete all elements from db
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                ProductDAO dao = INSTANCE.productDAO();
                dao.deleteAll();
            });
        }
    };

    //build the database
    static TrackingMyPantryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TrackingMyPantryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TrackingMyPantryDatabase.class, "product_database")
                            .addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

}


