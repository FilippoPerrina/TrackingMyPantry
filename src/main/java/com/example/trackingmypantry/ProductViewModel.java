package com.example.trackingmypantry;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
// the viewModel controls only the recyclerView in the MainActivity
public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository mRepository;
    private final LiveData<List<ProductEntity>> mListOfProducts;


    public ProductViewModel(Application application) {
        super(application);
        mRepository = new ProductRepository(application);
        mListOfProducts = mRepository.getListOfProducts();
    }

    public LiveData<List<ProductEntity>> getListOfProducts() {
        return mListOfProducts;
    }

    public void insertProduct(ProductEntity product) {
        this.mRepository.insert(product);
    }

    public int removeProduct(ProductEntity product) throws InterruptedException {
        return this.mRepository.remove(product);
    }

    public void addCategory(String id, String category) { this.mRepository.addCategory(id, category); }

    public void addQuantity(String id, String quantity) { this.mRepository.addQuantity(id, quantity); }

    public void addEndDate(String id, String endDate) { this.mRepository.addEndDate(id, endDate); }

}
