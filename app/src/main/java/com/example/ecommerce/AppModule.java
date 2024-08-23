package com.example.ecommerce;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.DiscountDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.dao.ProductDao;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.DiscountRepository;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.repository.IProductRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.ui.cart.CartViewModelFactory;
import com.example.ecommerce.ui.discount.DiscountViewModelFactory;
import com.example.ecommerce.ui.products.ProductsViewModel;
import com.example.ecommerce.ui.products.ProductsViewModelFactory;
import com.example.ecommerce.utils.DatabaseHelper;

interface IAppModule{
    DatabaseHelper provideDatabaseHelper();
    IProductDao provideProductDao();
    IProductRepository provideProductRepository();
    ProductsViewModelFactory provideProductsViewModelFactory();
    ICartDao provideCartDao();
    ICartRepository provideCartRepository();
    CartViewModelFactory provideCartViewModelFactory();
    IDiscountDao provideDiscountDao();
    IDiscountRepository provideDiscountRepository();
    DiscountViewModelFactory provideDiscountViewModelFactory();
    ViewModelProvider provideViewModelProvider(ViewModelStoreOwner owner);
    Context provideAppContext();
}

public class AppModule implements IAppModule{
    private Context appContext;
    private DatabaseHelper databaseHelper;
    private IProductDao productDao;
    private IProductRepository productRepository;
    private ProductsViewModelFactory productsViewModelFactory;
    private ICartDao cartDao;
    private ICartRepository cartRepository;
    private CartViewModelFactory cartViewModelFactory;
    private IDiscountDao discountDao;
    private IDiscountRepository discountRepository;
    private DiscountViewModelFactory discountViewModelFactory;

    public AppModule(Context appContext){
        this.appContext = appContext;
        databaseHelper = new DatabaseHelper(appContext);
        productDao = new ProductDao(databaseHelper);
        productRepository = new ProductRepository(productDao);
        productsViewModelFactory = new ProductsViewModelFactory(productRepository);
        discountDao = new DiscountDao(databaseHelper);
        discountRepository = new DiscountRepository(discountDao);
        discountViewModelFactory = new DiscountViewModelFactory(discountRepository);
        cartDao = new CartDao(databaseHelper);
        cartRepository = new CartRepository(cartDao);
        cartViewModelFactory = new CartViewModelFactory(cartRepository, discountRepository);
    }

    @Override
    public DatabaseHelper provideDatabaseHelper(){
        return databaseHelper;
    }

    @Override
    public IProductDao provideProductDao(){
        return productDao;
    }

    @Override
    public IProductRepository provideProductRepository(){
        return productRepository;
    }

    @Override
    public ProductsViewModelFactory provideProductsViewModelFactory(){
        return productsViewModelFactory;
    }

    @Override
    public ICartDao provideCartDao(){
        return cartDao;
    }

    @Override
    public ICartRepository provideCartRepository(){
        return cartRepository;
    }

    @Override
    public CartViewModelFactory provideCartViewModelFactory(){
        return cartViewModelFactory;
    }

    @Override
    public IDiscountDao provideDiscountDao(){
        return discountDao;
    }

    @Override
    public IDiscountRepository provideDiscountRepository(){
        return discountRepository;
    }

    @Override
    public DiscountViewModelFactory provideDiscountViewModelFactory(){
        return discountViewModelFactory;
    }

    @Override
    public ViewModelProvider provideViewModelProvider(ViewModelStoreOwner owner){
        return new ViewModelProvider(owner);
    }

    @Override
    public Context provideAppContext(){
        return appContext;
    }

}
