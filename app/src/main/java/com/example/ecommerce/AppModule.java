package com.example.ecommerce;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.ecommerce.dao.ICustomerDao;
import com.example.ecommerce.dao.IOrderDao;
import com.example.ecommerce.dao.IPaymentDao;
import com.example.ecommerce.dao.OrderDao;
import com.example.ecommerce.dao.PaymentDao;
import com.example.ecommerce.features.order.OrderViewModelFactory;
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
import com.example.ecommerce.features.cart.CartViewModelFactory;
import com.example.ecommerce.features.checkout.CheckoutViewModelFactory;
import com.example.ecommerce.features.customers.CustomerViewModelFactory;
import com.example.ecommerce.features.customers.create_customer.CreateCustomerViewModelFactory;
import com.example.ecommerce.features.discount.DiscountViewModelFactory;
import com.example.ecommerce.features.products.ProductsViewModelFactory;
import com.example.ecommerce.utils.DatabaseHelper;
import com.example.ecommerce.utils.RoomDBHelper;

interface IAppModule {
    Context provideAppContext();
    DatabaseHelper provideDatabaseHelper();
    IProductRepository provideProductRepository();
    ProductsViewModelFactory provideProductsViewModelFactory();
    CartViewModelFactory provideCartViewModelFactory();
    DiscountViewModelFactory provideDiscountViewModelFactory();
    IOrderRepository provideOrderRepository();
    CheckoutViewModelFactory provideCheckoutViewModelFactory();
    ICustomerRepository provideCustomerRepository();
    CreateCustomerViewModelFactory provideCreateCustomerViewModelFactory();
    CustomerViewModelFactory provideCustomerViewModelFactory();
    OrderViewModelFactory provideOrderViewModelFactory();
}

public class AppModule implements IAppModule {
    private final Application application;
    private final Context appContext;
    private final DatabaseHelper databaseHelper;
    private final RoomDBHelper roomDatabase;
    private final IProductRepository productRepository;
    private final ProductsViewModelFactory productsViewModelFactory;
    private final ICartRepository cartRepository;
    private final CartViewModelFactory cartViewModelFactory;
    private final IDiscountRepository discountRepository;
    private final DiscountViewModelFactory discountViewModelFactory;
    private final IPaymentDao paymentDao;
    private final IPaymentRepository paymentRepository;
    private final IOrderDao orderDao;
    private final IOrderRepository orderRepository;
    private final CheckoutViewModelFactory checkoutViewModelFactory;
    private final ICustomerRepository customerRepository;
    private final CreateCustomerViewModelFactory createCustomerViewModelFactory;
    private final SharedPreferences customerSharedPreferences;
    private final SharedPreferences discountSharedPreferences;
    private final SharedPreferences cartSharedPreferences;
    private final CustomerViewModelFactory customerViewModelFactory;
    private final OrderViewModelFactory orderViewModelFactory;

    public AppModule(Context appContext) {
        this.appContext = appContext;
        this.application = (Application) appContext.getApplicationContext();
        customerSharedPreferences = appContext.getSharedPreferences("CustomerSharedPreferences", MODE_PRIVATE);
        discountSharedPreferences = appContext.getSharedPreferences("DiscountSharedPreferences", MODE_PRIVATE);
        cartSharedPreferences = appContext.getSharedPreferences("CartSharedPreferences", MODE_PRIVATE);
        databaseHelper = new DatabaseHelper(appContext);
        roomDatabase = RoomDBHelper.getDatabase(appContext);
        productRepository = new ProductRepository(roomDatabase);
        discountRepository = new DiscountRepository(roomDatabase, discountSharedPreferences);
        cartRepository = new CartRepository(roomDatabase, cartSharedPreferences);
        paymentDao = new PaymentDao(databaseHelper);
        orderDao = new OrderDao(databaseHelper);
        paymentRepository = new PaymentRepository(paymentDao);
        orderRepository = new OrderRepository(orderDao);
        discountViewModelFactory = new DiscountViewModelFactory(discountRepository);
        customerRepository = new CustomerRepository(roomDatabase, customerSharedPreferences, orderDao);
        productsViewModelFactory = new ProductsViewModelFactory(productRepository, customerRepository);
        createCustomerViewModelFactory = new CreateCustomerViewModelFactory(customerRepository);
        cartViewModelFactory = new CartViewModelFactory(cartRepository, discountRepository, productRepository);
        checkoutViewModelFactory = new CheckoutViewModelFactory(paymentRepository, orderRepository);
        customerViewModelFactory = new CustomerViewModelFactory(application,customerRepository);
        orderViewModelFactory = new OrderViewModelFactory(orderRepository);
    }

    @Override
    public DatabaseHelper provideDatabaseHelper() {
        return databaseHelper;
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
    public CartViewModelFactory provideCartViewModelFactory() {
        return cartViewModelFactory;
    }

    @Override
    public DiscountViewModelFactory provideDiscountViewModelFactory() {
        return discountViewModelFactory;
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
    public ICustomerRepository provideCustomerRepository() {
        return this.customerRepository;
    }

    @Override
    public CreateCustomerViewModelFactory provideCreateCustomerViewModelFactory() {
        return this.createCustomerViewModelFactory;
    }

    @Override
    public CustomerViewModelFactory provideCustomerViewModelFactory() {
        return customerViewModelFactory;
    }

    @Override
    public Context provideAppContext() {
        return appContext;
    }

    @Override
    public OrderViewModelFactory provideOrderViewModelFactory() {
        return orderViewModelFactory;
    }

}
