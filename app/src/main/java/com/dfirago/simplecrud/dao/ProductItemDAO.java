package com.dfirago.simplecrud.dao;

import com.dfirago.simplecrud.domain.ProductItem;

import java.util.List;

/**
 * Created by dmfi on 18/02/2017.
 */

public interface ProductItemDAO {

    List<ProductItem> list();

    ProductItem find(Long id);

    ProductItem save(ProductItem item);

    void delete(ProductItem item);
}
