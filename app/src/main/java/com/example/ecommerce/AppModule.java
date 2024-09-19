package com.example.ecommerce;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.CustomerDao;
import com.example.ecommerce.dao.DiscountDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.ICustomerDao;
import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.dao.IOrderDao;
import com.example.ecommerce.dao.IPaymentDao;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.dao.OrderDao;
import com.example.ecommerce.dao.PaymentDao;
import com.example.ecommerce.dao.ProductDao;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.DiscountRepository;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.ICustomerRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.repository.IPaymentRepository;
import com.example.ecommerce.repository.IProductRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.ui.cart.CartViewModelFactory;
import com.example.ecommerce.ui.checkout.CheckoutViewModelFactory;
import com.example.ecommerce.ui.customers.CustomerViewModelFactory;
import com.example.ecommerce.ui.customers.create_customer.CreateCustomerViewModelFactory;
import com.example.ecommerce.ui.discount.DiscountViewModelFactory;
import com.example.ecommerce.ui.products.ProductsViewModelFactory;
import com.example.ecommerce.utils.DatabaseHelper;

import kotlin.jvm.Synchronized;

interface IAppModule {
    Context provideAppContext();

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

    IPaymentDao providePaymentDao();

    IPaymentRepository providePaymentRepository();

    IOrderDao provideOrderDao();

    IOrderRepository provideOrderRepository();

    CheckoutViewModelFactory provideCheckoutViewModelFactory();

    ICustomerDao provideCustomerDao();

    ICustomerRepository provideCustomerRepository();

    CreateCustomerViewModelFactory provideCreateCustomerViewModelFactory();

    SharedPreferences provideCustomerSharedPreferences();

    SharedPreferences provideDiscountSharedPreferences();

    CustomerViewModelFactory provideCustomerViewModelFactory();
}

public class AppModule implements IAppModule {
    private final Application application;
    private final Context appContext;
    private final DatabaseHelper databaseHelper;
    private final IProductDao productDao;
    private final IProductRepository productRepository;
    private final ProductsViewModelFactory productsViewModelFactory;
    private final ICartDao cartDao;
    private final ICartRepository cartRepository;
    private final CartViewModelFactory cartViewModelFactory;
    private final IDiscountDao discountDao;
    private final IDiscountRepository discountRepository;
    private final DiscountViewModelFactory discountViewModelFactory;
    private final IPaymentDao paymentDao;
    private final IPaymentRepository paymentRepository;
    private final IOrderDao orderDao;
    private final IOrderRepository orderRepository;
    private final CheckoutViewModelFactory checkoutViewModelFactory;
    private final ICustomerDao customerDao;
    private final ICustomerRepository customerRepository;
    private final CreateCustomerViewModelFactory createCustomerViewModelFactory;
    private final SharedPreferences customerSharedPreferences;
    private final SharedPreferences discountSharedPreferences;
    private final CustomerViewModelFactory customerViewModelFactory;

    public AppModule(Context appContext) {
        this.appContext = appContext;
        this.application = (Application) appContext.getApplicationContext();
        customerSharedPreferences = appContext.getSharedPreferences("CustomerSharedPreferences", MODE_PRIVATE);
        discountSharedPreferences = appContext.getSharedPreferences("DiscountSharedPreferences", MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(appContext);
        productDao = new ProductDao(databaseHelper);
        productRepository = new ProductRepository(productDao);
        discountDao = new DiscountDao(databaseHelper);
        discountRepository = new DiscountRepository(discountDao);
        cartDao = new CartDao(databaseHelper);
        cartRepository = new CartRepository(cartDao,productDao);
        paymentDao = new PaymentDao(databaseHelper);
        orderDao = new OrderDao(databaseHelper);
        paymentRepository = new PaymentRepository(paymentDao);
        orderRepository = new OrderRepository(orderDao);
        customerDao = new CustomerDao(databaseHelper);
        discountViewModelFactory = new DiscountViewModelFactory(discountRepository);
        customerRepository = new CustomerRepository(customerDao, customerSharedPreferences, orderDao);
        productsViewModelFactory = new ProductsViewModelFactory(productRepository, customerRepository);
        createCustomerViewModelFactory = new CreateCustomerViewModelFactory(customerRepository);
        cartViewModelFactory = new CartViewModelFactory(cartRepository, discountRepository,orderRepository);
        checkoutViewModelFactory = new CheckoutViewModelFactory(cartRepository, paymentRepository, orderRepository);
        customerViewModelFactory = new CustomerViewModelFactory(application,customerRepository);
    }

    @Override
    public DatabaseHelper provideDatabaseHelper() {
        return databaseHelper;
    }

    @Override
    public IProductDao provideProductDao() {
        return productDao;
    }

    @Override
    public IProductRepository provideProductRepository() {
        return productRepository;
    }

    @Override
    public ProductsViewModelFactory provideProductsViewModelFactory() {
        return productsViewModelFactory;
    }

    @Override
    public ICartDao provideCartDao() {
        return cartDao;
    }

    @Override
    public ICartRepository provideCartRepository() {
        return cartRepository;
    }

    @Override
    public CartViewModelFactory provideCartViewModelFactory() {
        return cartViewModelFactory;
    }

    @Override
    public IDiscountDao provideDiscountDao() {
        return discountDao;
    }

    @Override
    public IDiscountRepository provideDiscountRepository() {
        return discountRepository;
    }

    @Override
    public DiscountViewModelFactory provideDiscountViewModelFactory() {
        return discountViewModelFactory;
    }

    @Override
    public IPaymentDao providePaymentDao() {
        return this.paymentDao;
    }

    @Override
    public IPaymentRepository providePaymentRepository() {
        return this.paymentRepository;
    }

    @Override
    public IOrderDao provideOrderDao() {
        return this.orderDao;
    }

    @Override
    public IOrderRepository provideOrderRepository() {
        return this.orderRepository;
    }

    @Override
    public CheckoutViewModelFactory provideCheckoutViewModelFactory() {
        return this.checkoutViewModelFactory;
    }

    @Override
    public ICustomerDao provideCustomerDao() {
        return this.customerDao;
    }

    @Override
    public ICustomerRepository provideCustomerRepository() {
        return this.customerRepository;
    }

    @Override
    public CreateCustomerViewModelFactory provideCreateCustomerViewModelFactory() {
        return this.createCustomerViewModelFactory;
    }

    @Override
    public SharedPreferences provideCustomerSharedPreferences() {
        return customerSharedPreferences;
    }

    @Override
    public SharedPreferences provideDiscountSharedPreferences() {
        return discountSharedPreferences;
    }

    @Override
    public CustomerViewModelFactory provideCustomerViewModelFactory() {
        return customerViewModelFactory;
    }

    @Override
    public Context provideAppContext() {
        return appContext;
    }

}
