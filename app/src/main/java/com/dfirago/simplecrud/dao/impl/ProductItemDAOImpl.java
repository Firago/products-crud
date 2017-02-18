package com.dfirago.simplecrud.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.dfirago.simplecrud.dao.DatabaseHelper;
import com.dfirago.simplecrud.dao.ProductItemDAO;
import com.dfirago.simplecrud.domain.ProductItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmfi on 18/02/2017.
 */

public class ProductItemDAOImpl implements ProductItemDAO {

    private final DatabaseHelper helper;

    public ProductItemDAOImpl(Context context) {
        helper = new DatabaseHelper(context);
    }

    @Override
    public List<ProductItem> list() {
        List<ProductItem> result = new ArrayList<>();
        Cursor cursor = helper.getReadableDatabase().rawQuery("select * from " + ProductItem.KEY_TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                ProductItem entity = cursorToEntity(cursor);
                result.add(entity);
                cursor.moveToNext();
            }
        }
        return result;
    }

    @Override
    public ProductItem find(Long id) {
        ProductItem result = null;
        String[] whereArgs = new String[]{String.valueOf(id)};
        Cursor cursor = helper.getReadableDatabase()
                .rawQuery("select * from " + ProductItem.KEY_TABLE_NAME
                        + " where " + ProductItem.KEY_ID + " = ?", whereArgs);
        if (cursor.moveToFirst()) {
            result = cursorToEntity(cursor);
        }
        return result;
    }

    @Override
    public ProductItem save(ProductItem productItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductItem.KEY_TITLE, productItem.getTitle());
        contentValues.put(ProductItem.KEY_DESCRIPTION, productItem.getDescription());
        contentValues.put(ProductItem.KEY_IMAGE_URI, productItem.getImageUri());
        SQLiteDatabase db = helper.getWritableDatabase();

        if (productItem.getId() == null) {
            Long id = db.insert(ProductItem.KEY_TABLE_NAME, null, contentValues);
            productItem.setId(id);
        } else {
            String whereClause = ProductItem.KEY_ID + "=?";
            String[] whereArgs = new String[]{
                    String.valueOf(productItem.getId())
            };
            db.update(ProductItem.KEY_TABLE_NAME, contentValues, whereClause, whereArgs);
        }
        return productItem;
    }

    @Override
    public void delete(ProductItem productItem) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = ProductItem.KEY_ID + "=" + productItem.getId();
        db.delete(ProductItem.KEY_TABLE_NAME, whereClause, null);
    }

    @NonNull
    private ProductItem cursorToEntity(Cursor cursor) {
        ProductItem entity = new ProductItem();
        entity.setId(cursor.getLong(0));
        entity.setTitle(cursor.getString(1));
        entity.setDescription(cursor.getString(2));
        entity.setImageUri(cursor.getString(3));
        return entity;
    }
}
