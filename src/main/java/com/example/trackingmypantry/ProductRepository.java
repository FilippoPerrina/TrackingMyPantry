package com.example.trackingmypantry;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductRepository {

    private final ProductDAO mProductDao;
    private final LiveData<List<ProductEntity>> mListOfProducts;

    ProductRepository (Application app) {
        TrackingMyPantryDatabase db = TrackingMyPantryDatabase.getDatabase(app);
        mProductDao = db.productDAO();
        mListOfProducts = mProductDao.getListOfProducts();
    }

        //liveData queries are by default executed on a different thread
        LiveData<List<ProductEntity>> getListOfProducts() { return mListOfProducts; }

        //the other queries are explicitly executed on a different thread
        void insert(ProductEntity product){
            TrackingMyPantryDatabase.databaseWriteExecutor.execute(() -> mProductDao.insert(product));
        }

        //not executed in the pool because we need to access to the number of rows removed
        int remove(ProductEntity product) throws InterruptedException {
            final AtomicInteger fcount = new AtomicInteger();
            Thread t = new Thread(() -> {
                int num = mProductDao.remove(product);
                fcount.set(num);
            });
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            t.join();
            return fcount.get();
        }

        void addCategory(String id, String category) {
            TrackingMyPantryDatabase.databaseWriteExecutor.execute(() -> mProductDao.addCategory(id, category));
        }

        void addQuantity(String id, String quantity) {
            TrackingMyPantryDatabase.databaseWriteExecutor.execute(() -> mProductDao.addQuantity(id, quantity));
        }

        void addEndDate(String id, String endDate) {
            TrackingMyPantryDatabase.databaseWriteExecutor.execute(() -> mProductDao.addEndDate(id, endDate));
        }

}
