package com.example.ecommerce.ui.create_product;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.App;
import com.example.ecommerce.MainActivity;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.IProductRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
            productNameError.postValue("Looks like you forgot to add a product name");
            return false;
        } else {
            productNameError.postValue(null);
            return true;
        }
    }

    public boolean validateProductDescription(String productDescription) {
        if (productDescription == null || productDescription.isEmpty()) {
            productDescriptionError.postValue("Looks like you forgot to add a product description");
            return false;
        } else {
            productDescriptionError.postValue(null);
            return true;
        }
    }

    public boolean validateProductPrice(Double productPrice) {
        if (productPrice == null || productPrice <= 0) {
            productPriceError.postValue("Looks like you forgot to add a product price");
            return false;
        } else {
            productPriceError.postValue(null);
            return true;
        }
    }

    public boolean validateProductCost(Double productCost, Double productPrice) {
        if (productCost == null || productCost < 0) {
            productCostError.postValue("Looks like you forgot to add a product cost");
            return false;
        } else if (productCost > productPrice) {
            productCostError.postValue("Please enter a cost lower than the price");
            return false;
        } else {
            productCostError.postValue(null);
            return true;
        }
    }

    public boolean validateProductDiscount(Double productDiscount) {
        if (productDiscount == null || productDiscount < 0) {
            productDiscountError.postValue("Please enter a valid discount, or 0 if no discount is available");
            return false;
        } else {
            productDiscountError.postValue(null);
            return true;
        }
    }

    public boolean validateProductQuantity(Integer productQuantity) {
        if (productQuantity == null || productQuantity < 0) {
            productQuantityError.postValue("Please enter a valid quantity, or 0 if no quantity is available");
            return false;
        } else {
            productQuantityError.postValue(null);
            return true;
        }
    }

    public boolean validateProductBrand(String productBrand) {
        if (productBrand == null || productBrand.isEmpty()) {
            productBrandError.postValue("Looks like you forgot to add a product brand");
            return false;
        } else {
            productBrandError.postValue(null);
            return true;
        }
    }

    public boolean validateProductCategory(String productCategory) {
        if (productCategory == null || productCategory.isEmpty()) {
            productCategoryError.postValue("Looks like you forgot to add a product category");
            return false;
        } else {
            productCategoryError.postValue(null);
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
            // Get the input stream from the selected image URI
            InputStream inputStream = App.appModule.provideAppContext().getContentResolver().openInputStream(imageUri);

            // Define the directory and file name where the image will be saved
            File directory = new File(App.appModule.provideAppContext().getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs(); // Create directory if it doesn't exist
            }

            File file = new File(directory, "product_image_" + System.currentTimeMillis() + ".jpg");

            // Save the image using FileOutputStream
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            // Update the product image field with the file path
            applyUpdateToTheProduct("productImage", file.getAbsolutePath());
            Log.d(TAG, "Image saved to: " + file.getAbsolutePath());

        } catch (IOException e) {
            Log.e(TAG, "Failed to save image", e);
        }
    }

    public void deleteImage() {
        try{
            String imagePath = product.getValue().getProductImage();
            if(imagePath != null && !imagePath.isEmpty()){
                File file = new File(imagePath);
                if(file.exists()){
                    file.delete();
                    applyUpdateToTheProduct("productImage", null);
                    Log.d(TAG, "Image deleted successfully");
                }
            }
        } catch (Exception e){
            Log.e(TAG, "Failed to delete image", e);
        }
    }

    public void createProduct(OnProductCreatedCallback callback) {
        if (validateProduct()) {
            try {
                repository.createProduct(product.getValue());
                // Reset the product to create a new one
                product.setValue(new Product.ProductBuilder().buildProduct());
                callback.onSuccessfulProductCreation();
            } catch (Exception e) {
                callback.onFailedProductCreation();
                Log.e(TAG, "Error creating product", e);
            }
        }
    }

    public void updateProduct(OnProductUpdatedCallback callback) {
        if (validateProduct()) {
            try {
                repository.updateProduct(product.getValue());
                callback.onProductUpdateSuccess();
            } catch (Exception e) {
                callback.onProductUpdateFailure();
                Log.e(TAG, "Error updating product", e);
            }
        }
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

}
