package com.example.trackingmypantry;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProductDAO {

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insert(ProductEntity product);

        @Delete
        int remove(ProductEntity product);

        @Query("DELETE FROM product_table")
        void deleteAll();

        @Query("UPDATE product_table SET category = :category WHERE id = :id")
        void addCategory(String id, String category);

        @Query("UPDATE product_table SET quantity = :quantity WHERE id = :id")
        void addQuantity(String id, String quantity);

        @Query("UPDATE product_table SET endDate = :endDate WHERE id = :id")
        void addEndDate(String id, String endDate);

        @Query("SELECT * FROM product_table ORDER BY product ASC")
        LiveData<List<ProductEntity>> getListOfProducts();
}
