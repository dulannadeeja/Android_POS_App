package com.example.ecommerce.features.products.create_product;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.App;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.IProductRepository;
import com.example.ecommerce.utils.FileHelper;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateProductViewModel extends ViewModel {
    String TAG = "CreateProductViewModel";
    IProductRepository repository;

    private MutableLiveData<Product> product;
    private MutableLiveData<String> productNameError = new MutableLiveData<>();
    private MutableLiveData<String> productDescriptionError = new MutableLiveData<>();
    private MutableLiveData<String> productPriceError = new MutableLiveData<>();
    private MutableLiveData<String> productCostError = new MutableLiveData<>();
    private MutableLiveData<String> productDiscountError = new MutableLiveData<>();
    private MutableLiveData<String> productQuantityError = new MutableLiveData<>();
    private MutableLiveData<String> productBrandError = new MutableLiveData<>();
    private MutableLiveData<String> productCategoryError = new MutableLiveData<>();

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CreateProductViewModel(IProductRepository repository, Product product) {
        this.repository = repository;
        this.product = product == null ? new MutableLiveData<>(new Product.ProductBuilder().buildProduct()) : new MutableLiveData<>(product);
    }

    public boolean validateProduct() {
        boolean isValid = true;
        isValid = validateProductName(product.getValue().getProductName());
        isValid = validateProductDescription(product.getValue().getProductDescription()) && isValid;
        isValid = validateProductPrice(product.getValue().getProductPrice()) && isValid;
        isValid = validateProductCost(product.getValue().getProductCost(), product.getValue().getProductPrice()) && isValid;
        isValid = validateProductDiscount(product.getValue().getProductDiscount()) && isValid;
        isValid = validateProductQuantity(product.getValue().getProductQuantity()) && isValid;
        isValid = validateProductBrand(product.getValue().getProductBrand()) && isValid;
        isValid = validateProductCategory(product.getValue().getProductCategory()) && isValid;
        return isValid;
    }

    public boolean validateProductName(String productName) {
        if (productName == null || productName.isEmpty()) {
            productNameError.setValue("Looks like you forgot to add a product name");
            return false;
        } else {
            productNameError.setValue(null);
            return true;
        }
    }

    public boolean validateProductDescription(String productDescription) {
        if (productDescription == null || productDescription.isEmpty()) {
            productDescriptionError.setValue("Looks like you forgot to add a product description");
            return false;
        } else {
            productDescriptionError.setValue(null);
            return true;
        }
    }

    public boolean validateProductPrice(Double productPrice) {
        if (productPrice == null || productPrice <= 0) {
            productPriceError.setValue("Looks like you forgot to add a product price");
            return false;
        } else {
            productPriceError.setValue(null);
            return true;
        }
    }

    public boolean validateProductCost(Double productCost, Double productPrice) {
        if (productCost == null || productCost < 0) {
            productCostError.setValue("Looks like you forgot to add a product cost");
            return false;
        } else if (productCost > productPrice) {
            productCostError.setValue("Please enter a cost lower than the price");
            return false;
        } else {
            productCostError.setValue(null);
            return true;
        }
    }

    public boolean validateProductDiscount(Double productDiscount) {
        if (productDiscount == null || productDiscount < 0) {
            productDiscountError.setValue("Please enter a valid discount, or 0 if no discount is available");
            return false;
        } else {
            productDiscountError.setValue(null);
            return true;
        }
    }

    public boolean validateProductQuantity(Integer productQuantity) {
        if (productQuantity == null || productQuantity < 0) {
            productQuantityError.setValue("Please enter a valid quantity, or 0 if no quantity is available");
            return false;
        } else {
            productQuantityError.setValue(null);
            return true;
        }
    }

    public boolean validateProductBrand(String productBrand) {
        if (productBrand == null || productBrand.isEmpty()) {
            productBrandError.setValue("Looks like you forgot to add a product brand");
            return false;
        } else {
            productBrandError.setValue(null);
            return true;
        }
    }

    public boolean validateProductCategory(String productCategory) {
        if (productCategory == null || productCategory.isEmpty()) {
            productCategoryError.setValue("Looks like you forgot to add a product category");
            return false;
        } else {
            productCategoryError.setValue(null);
            return true;
        }
    }

    public <T> void applyUpdateToTheProduct(String field, T value) {
        Product currentProduct = product.getValue();
        if (currentProduct == null) return; // Handle null product scenario

        switch (field) {
            case "productName":
                if (value instanceof String) {
                    currentProduct.setProductName((String) value);
                    validateProductName((String) value);
                } else {
                    throw new IllegalArgumentException("Invalid type for productName");
                }
                break;
            case "productDescription":
                if (value instanceof String) {
                    currentProduct.setProductDescription((String) value);
                    validateProductDescription((String) value);
                } else {
                    throw new IllegalArgumentException("Invalid type for productDescription");
                }
                break;
            case "productPrice":
                if (value instanceof Double) {
                    currentProduct.setProductPrice((Double) value);
                    validateProductPrice((Double) value);
                } else {
                    throw new IllegalArgumentException("Invalid type for productPrice");
                }
                break;
            case "productCost":
                if (value instanceof Double) {
                    currentProduct.setProductCost((Double) value);
                    validateProductCost((Double) value, currentProduct.getProductPrice());
                } else {
                    throw new IllegalArgumentException("Invalid type for productCost");
                }
                break;
            case "productDiscount":
                if (value instanceof Double) {
                    currentProduct.setProductDiscount((Double) value);
                    validateProductDiscount((Double) value);
                } else {
                    throw new IllegalArgumentException("Invalid type for productDiscount");
                }
                break;
            case "productQuantity":
                if (value instanceof Integer) {
                    currentProduct.setProductQuantity((Integer) value);
                    validateProductQuantity((Integer) value);
                } else {
                    throw new IllegalArgumentException("Invalid type for productQuantity");
                }
                break;
            case "productBrand":
                if (value instanceof String) {
                    currentProduct.setProductBrand((String) value);
                    validateProductBrand((String) value);
                } else {
                    throw new IllegalArgumentException("Invalid type for productBrand");
                }
                break;
            case "productCategory":
                if (value instanceof String) {
                    currentProduct.setProductCategory((String) value);
                    validateProductCategory((String) value);
                } else {
                    throw new IllegalArgumentException("Invalid type for productCategory");
                }
                break;
            case "productImage":
                if (value instanceof String || value == null) {
                    currentProduct.setProductImage((String) value);
                } else {
                    throw new IllegalArgumentException("Invalid type for productImage");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + field);
        }

        // Notify observers of the updated product
        product.setValue(currentProduct);
    }

    public void saveImage(Uri imageUri) {
        try {
            String filePath = FileHelper.saveImage(imageUri, "product_image", App.appModule.provideAppContext());

            // Update the product image field with the file path
            applyUpdateToTheProduct("productImage", filePath);

        } catch (Exception e) {
            Log.e(TAG, "Failed to save image", e);
        }
    }

    public void deleteImage() {
        try {
            String imagePath = product.getValue().getProductImage();
            FileHelper.deleteImage(imagePath, "product_image");
        } catch (Exception e) {
            Log.e(TAG, "Failed to delete image", e);
        }
    }

    public void createProduct(OnProductCreatedCallback callback) {
        isLoading.setValue(true);
        try {
            if (validateProduct()) {
                compositeDisposable.add(
                        repository.createProduct(product.getValue())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    isLoading.setValue(false);
                                    callback.onSuccessfulProductCreation();
                                    // Reset the product to create a new one
                                    product.setValue(new Product.ProductBuilder().buildProduct());
                                }, throwable -> {
                                    isLoading.setValue(false);
                                    callback.onFailedProductCreation();
                                    Log.e(TAG, "Error creating product", throwable);
                                }));
            }
        } catch (Exception e) {
            isLoading.setValue(false);
            callback.onFailedProductCreation();
            Log.e(TAG, "Error creating product", e);
        }
    }

    public void updateProduct(OnProductUpdatedCallback callback) {
        isLoading.setValue(true);
        try{
            if (validateProduct()) {
                    compositeDisposable.add(
                            repository.updateProduct(product.getValue())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        callback.onProductUpdateSuccess();
                                        isLoading.setValue(false);
                                    }, throwable -> {
                                        callback.onProductUpdateFailure();
                                        Log.e(TAG, "Error updating product", throwable);
                                        isLoading.setValue(false);
                                    })
                    );
            }
        } catch (Exception e) {
            callback.onProductUpdateFailure();
            Log.e(TAG, "Error updating product", e);
            isLoading.setValue(false);
        }
    }

    public void clearErrors() {
        productNameError.setValue(null);
        productDescriptionError.setValue(null);
        productPriceError.setValue(null);
        productCostError.setValue(null);
        productDiscountError.setValue(null);
        productQuantityError.setValue(null);
        productBrandError.setValue(null);
        productCategoryError.setValue(null);
    }

    public MutableLiveData<String> getProductNameError() {
        return productNameError;
    }

    public MutableLiveData<String> getProductDescriptionError() {
        return productDescriptionError;
    }

    public MutableLiveData<String> getProductPriceError() {
        return productPriceError;
    }

    public MutableLiveData<String> getProductCostError() {
        return productCostError;
    }

    public MutableLiveData<String> getProductDiscountError() {
        return productDiscountError;
    }

    public MutableLiveData<String> getProductQuantityError() {
        return productQuantityError;
    }

    public MutableLiveData<String> getProductBrandError() {
        return productBrandError;
    }

    public MutableLiveData<String> getProductCategoryError() {
        return productCategoryError;
    }

    public MutableLiveData<Product> getProduct() {
        return product;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

}
