package com.example.trackingmypantry;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnItemClick {

    private ProductViewModel mProductViewModel;
    private ProductAdapter productAdapter;
    private boolean firstFilter = true;
    private boolean firstSearch = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String accessToken = getIntent().getStringExtra("accessToken");
        TextView textViewFilter = findViewById(R.id.textViewFilterActive);

        //list adapter for the recycler view that shows the products
        RecyclerView recyclerView = findViewById(R.id.recyclerViewListProducts);
        productAdapter = new ProductAdapter(new ProductAdapter.ProductsDiff(), this);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //providing viewModel to the activity
        mProductViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        //when the list of products change check if the list is different and show the new list
        mProductViewModel.getListOfProducts().observe(this, products -> {
                    firstSearch = true;
                    firstFilter = true;
                    textViewFilter.setVisibility(View.INVISIBLE);
                    productAdapter.submitList(products);
                });

        FloatingActionButton buttonAddItem = findViewById(R.id.buttonAddItem);
        buttonAddItem.setOnClickListener(v -> {
            Intent intentToAddItemActivity = new Intent(this,AddItemActivity.class);
            intentToAddItemActivity.putExtra("accessToken",accessToken);
            registrationActivityLauncher.launch(intentToAddItemActivity);
        });

        FloatingActionButton buttonRemoveItem = findViewById(R.id.buttonRemoveItem);
        buttonRemoveItem.setOnClickListener(v -> {
            Intent intentToRemoveActivity = new Intent(this, RemoveItemActivity.class);
            intentToRemoveActivity.putExtra("accessToken", accessToken);
            registrationActivityLauncher.launch(intentToRemoveActivity);
        });
    }

    //when a product is added, selected or removed by the user, register the product and call the viewModel
    ActivityResultLauncher<Intent> registrationActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //in all three cases RESULT_OK is returned, check the content of the bundle and react to it
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    if (data.hasExtra("productSelected")) {
                        Product productSelected = data.getParcelableExtra("productSelected");
                        ProductEntity product = new ProductEntity(productSelected.id,
                                productSelected.name,
                                productSelected.description,
                                productSelected.barcode);
                        mProductViewModel.insertProduct(product);
                    } else if (data.hasExtra("productAdded")){
                        Product productAdded = data.getParcelableExtra("productAdded");
                        ProductEntity product = new ProductEntity(productAdded.id,
                                productAdded.name,
                                productAdded.description,
                                productAdded.barcode);
                        mProductViewModel.insertProduct(product);
                    } else {
                        ProductEntity product = new ProductEntity(null,
                                data.getStringExtra("removeName"),
                                null ,
                                null);
                        try {
                            //check how many rows are removed and notify the user
                            int rowRemoved = mProductViewModel.removeProduct(product);
                            if (rowRemoved >= 1) {
                                Toast.makeText(this, data.getStringExtra("removeName")
                                                + " rimosso dalla dispensa.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Spiacente, "+
                                                data.getStringExtra("removeName") +
                                                " non trovato nella dispensa.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_tmp, menu);
        TextView textViewFilter = findViewById(R.id.textViewFilterActive);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchMenu = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        searchMenu.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchMenu.setIconifiedByDefault(false);
        searchMenu.setSubmitButtonEnabled(false);

        searchMenu.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //check if the list is updated and pass the string in the searchView to the adapter.
            //When the list is received show the list.
            @Override
            public boolean onQueryTextChange(String newText) {
                if (firstSearch) {
                    productAdapter.setmList(productAdapter.getCurrentList());
                }
                List<ProductEntity> list = productAdapter.filterSearch(newText);
                productAdapter.submitList(list);
                firstSearch = false;
                return false;
            }
        });

        MenuItem filterMenu =  menu.findItem(R.id.menuFilter);
        filterMenu.setOnMenuItemClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            FilterFragment filterFragment = new FilterFragment();
            filterFragment.show(fm,"showFilter");

            //get the filter values and pass it to handleFilter
            fm.setFragmentResultListener("applyFilter", this, (requestKey, result) -> {
                String filterCategory = result.getString("selectedCategory");
                String filterEndDate = result.getString("selectedEndDate");
                String filterQuantityFrom = result.getString("quantityTextFrom");
                String filterQuantityTo = result.getString("quantityTextTo");
                try {
                    handleFilter(filterCategory, filterEndDate, filterQuantityFrom, filterQuantityTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
            //Update the list
            fm.setFragmentResultListener("removeFilter", this, (requestKey, result) -> {
                        firstSearch = true;
                        firstFilter = true;
                        textViewFilter.setVisibility(View.INVISIBLE);
                        mProductViewModel.getListOfProducts().observe(this, products -> productAdapter.submitList(products));
                    });

            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }

    //check if a fragment is active, show the alert to exit the application
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Vuoi uscire dall'applicazione?").setCancelable(true);
            alertDialogBuilder.setPositiveButton("Sì", (dialog, which) ->
                    MainActivity.this.finish());
            alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    //show the details of the product clicked
    @Override
    public void onClick(ProductEntity current) {
        Bundle bundle = new Bundle();

        //pass the values of the actual product to the filterFragment
        Product productDetails = new Product(
                current.getId(),
                current.getProduct(),
                current.getDescription(),
                current.getBarcode()
        );
        bundle.putParcelable("productDetails", productDetails);
        bundle.putString("category", current.getCategory());
        bundle.putString("quantity", current.getQuantity());
        bundle.putString("endDate", current.getEndDate());
        FragmentManager fm = getSupportFragmentManager();
        ShowDetailsFragmentMain showDetailsFragment = new ShowDetailsFragmentMain();
        showDetailsFragment.setArguments(bundle);
        showDetailsFragment.show(fm,"showDetails");

        //notify the viewModel when the user changes the product details
        fm.setFragmentResultListener("requestKeyCategory", this, (requestKey, result) ->
                mProductViewModel.addCategory(current.getId(), result.getString("categorySelected")));
        fm.setFragmentResultListener("requestKeyQuantity", this, (requestKey, result) ->
                mProductViewModel.addQuantity(current.getId(), result.getString("quantitySelected")));
        fm.setFragmentResultListener("requestKeyDate", this, (requestKey, result) ->
                mProductViewModel.addEndDate(current.getId(), result.getString("endDateSelected")));
    }

    //filter the list with the values passed by the user
    public void handleFilter(String filterCategory, String filterEndDate, String filterQuantityFrom,
                             String filterQuantityTo) throws ParseException {
        TextView textViewFilter = findViewById(R.id.textViewFilterActive);
        //get the current Date to
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = sdf.format(currentTime);

        //convert the string to the actual date
        if (filterEndDate != null) {
            filterEndDate = convertDate(filterEndDate);
        }

        //check if the list is updated
        if (firstFilter) {
            productAdapter.setmList(productAdapter.getCurrentList());
        }

        //check which values are passed by the user and pass it to the filtering function in the
        //adapter. Show the filtered list and the active filters.
        if (filterCategory != null && filterEndDate != null && !filterQuantityFrom.matches("")
                && !filterQuantityTo.matches("")) {
            List<ProductEntity> list = productAdapter.filterCategoryEndDateQuantity(filterCategory,
                    todayDate, filterEndDate, filterQuantityFrom, filterQuantityTo);
            productAdapter.submitList(list);
            firstFilter = false;
            textViewFilter.setText("Categoria: " + filterCategory + ", Data di scadenza: " +
                    filterEndDate + ", Quantità: " + filterQuantityFrom + "-" + filterQuantityTo);
            textViewFilter.setVisibility(View.VISIBLE);
        } else if (filterCategory != null && filterEndDate != null) {
            List<ProductEntity> list = productAdapter.filterCategoryEndDate(filterCategory,
                    todayDate, filterEndDate);
            productAdapter.submitList(list);
            firstFilter = false;
            textViewFilter.setText("Categoria: " + filterCategory + ", Data di scadenza: " +
                    filterEndDate );
            textViewFilter.setVisibility(View.VISIBLE);
        } else if (filterCategory != null && !filterQuantityFrom.matches("")
                && !filterQuantityTo.matches("")) {
            List<ProductEntity> list = productAdapter.filterCategoryQuantity(filterCategory,
                    filterQuantityFrom, filterQuantityTo);
            productAdapter.submitList(list);
            firstFilter = false;
            textViewFilter.setText("Categoria: " + filterCategory + ", Quantità: " +
                    filterQuantityFrom + "-" + filterQuantityTo);
            textViewFilter.setVisibility(View.VISIBLE);
        } else if (filterEndDate != null && !filterQuantityFrom.matches("") &&
                !filterQuantityTo.matches("")) {
            List<ProductEntity> list = productAdapter.filterEndDateQuantity(todayDate, filterEndDate,
                    filterQuantityFrom, filterQuantityTo);
            productAdapter.submitList(list);
            firstFilter = false;
            textViewFilter.setText("Data di scadenza: " + filterEndDate + ", Quantità: " +
                    filterQuantityFrom + "-" + filterQuantityTo);
            textViewFilter.setVisibility(View.VISIBLE);
        } else if (filterCategory != null) {
            List<ProductEntity> list = productAdapter.filterCategory(filterCategory);
            productAdapter.submitList(list);
            firstFilter = false;
            textViewFilter.setText("Categoria: " + filterCategory);
            textViewFilter.setVisibility(View.VISIBLE);
        } else if (filterEndDate != null) {
            List<ProductEntity> list = productAdapter.filterEndDate(todayDate, filterEndDate);
            productAdapter.submitList(list);
            firstFilter = false;
            textViewFilter.setText("Data di scadenza: " + filterEndDate);
            textViewFilter.setVisibility(View.VISIBLE);
        } else if (!filterQuantityFrom.matches("") && !filterQuantityTo.matches("")) {
            List<ProductEntity> list = productAdapter.filterQuantity(filterQuantityFrom,
                    filterQuantityTo);
            productAdapter.submitList(list);
            firstFilter = false;
            textViewFilter.setText("Quantità: " + filterQuantityFrom + "-" + filterQuantityTo);
            textViewFilter.setVisibility(View.VISIBLE);
        } else {
            Log.d("Filter","NoFilterSelected");
        }
    }

    //convert the option for the endDate filter in the corresponding date
    private String convertDate(String filterEndDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date currentTime = Calendar.getInstance().getTime();

        Calendar calendarOneWeek = Calendar.getInstance();
        calendarOneWeek.add(Calendar.WEEK_OF_MONTH,1);
        Date dateOneWeek = calendarOneWeek.getTime();

        Calendar calendarOneMonth = Calendar.getInstance();
        calendarOneMonth.add(Calendar.MONTH,1);
        Date dateOneMonth = calendarOneMonth.getTime();

        String returnFilter;
        if (filterEndDate.equals("Oggi")) {
            returnFilter = sdf.format(currentTime);
        } else if (filterEndDate.equals("Questa settimana")) {
            returnFilter = sdf.format(dateOneWeek);
        } else {
            returnFilter = sdf.format(dateOneMonth);
        }
        return returnFilter;
    }

}
