package com.dfirago.simplecrud.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dfirago.simplecrud.R;
import com.dfirago.simplecrud.adapters.ProductItemArrayAdapter;
import com.dfirago.simplecrud.dao.ProductItemDAO;
import com.dfirago.simplecrud.dao.impl.ProductItemDAOImpl;
import com.dfirago.simplecrud.domain.ProductItem;

import java.util.List;

public class ProductListActivity extends Activity {

    private static final String LABEL_DELETE = "Delete";
    private static final String LABEL_CANCEL = "Cancel";

    private ProductItemDAO productItemDAO;

    private ProductItemArrayAdapter productItemArrayAdapter;

    private ListView productListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productItemDAO = new ProductItemDAOImpl(getApplicationContext());

        productItemArrayAdapter = new ProductItemArrayAdapter(
                getApplicationContext(), R.layout.item_product_list);

        productListView = (ListView) findViewById(R.id.product_list_view);
        productListView.setAdapter(productItemArrayAdapter);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductItem productItem = (ProductItem) productListView.getItemAtPosition(position);
                Intent intent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
                intent.putExtra(ProductDetailsActivity.PRODUCT_ITEM_ID, productItem.getId());
                startActivityForResult(intent, 1);
            }
        });
        productListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CharSequence[] items = {LABEL_DELETE, LABEL_CANCEL};
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductListActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals(LABEL_DELETE)) {
                            ProductItem productItem = (ProductItem) productListView.getItemAtPosition(position);
                            productItemDAO.delete(productItem);
                            loadProducts();
                        } else if (items[item].equals(LABEL_CANCEL)) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailsActivity.class);
                startActivityForResult(intent, 2);
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        productItemArrayAdapter.clear();
        List<ProductItem> productItems = productItemDAO.list();
        productItemArrayAdapter.addAll(productItems);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadProducts();
    }
}
