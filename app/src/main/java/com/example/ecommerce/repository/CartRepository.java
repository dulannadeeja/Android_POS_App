package com.example.ecommerce.repository;

import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;

import java.util.ArrayList;

public class CartRepository implements ICartRepository{

    private ICartDao cartDao;
    private IProductDao productDao;

    public CartRepository(ICartDao cartDao, IProductDao productDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    @Override
    public Cart getCart() throws Exception {
        ArrayList<CartItem> cartItems = cartDao.getCartItems();
        int totalItems = 0;
        double cartSubTotalPrice = 0;
        double cartTotalTax = 0;

        for (CartItem cartItem : cartItems) {
            totalItems += cartItem.getQuantity();
            cartSubTotalPrice += cartItem.getQuantity() * (cartItem.getPrice() - cartItem.getDiscount());
        }
        return new Cart(totalItems,cartSubTotalPrice,cartItems,cartTotalTax,-1,0);
    }

    @Override
    public void addProductToCart(int productId) throws Exception {
        Boolean isProductInCart = isProductInCart(productId);
        Boolean isProductHasStock = false;
        if (isProductInCart) {
            CartItem cartItem = cartDao.getCartItem(productId);
            isProductHasStock = isProductHasStock(cartItem.getQuantity(),productId);
            if (isProductHasStock) {
                cartDao.updateCartItem(productId,cartItem.getQuantity() + 1);
            }
        } else {
            isProductHasStock = isProductHasStock(0,productId);
            if (isProductHasStock) {
                cartDao.createCartItem(productId);
            }
        }
        if(!isProductHasStock) {
            throw new Exception("Product out of stock");
        }
    }

    public Boolean isProductInCart(int productId) throws Exception {
        ArrayList<CartItem> cartItems = cartDao.getCartItems();
        boolean isProductInCart = false;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId() == productId) {
                isProductInCart = true;
                break;
            }
        }
        return isProductInCart;
    }

    public Boolean isProductHasStock(int qtyInCart,int productId) throws Exception {
        int productStock = productDao.getProductQuantity(productId);
        return productStock > qtyInCart;
    }

    @Override
    public void removeProductFromCart(int productId) throws Exception {
        cartDao.deleteCartItem(productId);
    }

    @Override
    public void decreaseProductQuantity(int productId) throws Exception {
        CartItem cartItem = cartDao.getCartItem(productId);
        if (cartItem.getQuantity() > 1) {
            cartDao.updateCartItem(productId, cartItem.getQuantity() - 1);
        } else {
            cartDao.deleteCartItem(productId);
        }
    }

    @Override
    public void clearCart() throws Exception {
        cartDao.clearCart();
    }
}
