package com.example.ecommerce.repository;

import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;

import java.util.ArrayList;

public class CartRepository implements ICartRepository{

    ICartDao cartDao;

    public CartRepository(ICartDao cartDao) {
        this.cartDao = cartDao;
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
        return new Cart(totalItems,cartSubTotalPrice,cartItems,cartTotalTax);
    }

    @Override
    public void addProductToCart(int productId) throws Exception {
        ArrayList<CartItem> cartItems = cartDao.getCartItems();
        boolean isProductInCart = false;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId() == productId) {
                cartDao.updateCartItem(productId, cartItem.getQuantity() + 1);
                isProductInCart = true;
                break;
            }
        }
        if (!isProductInCart) {
            cartDao.createCartItem(productId);
        }
    }

    @Override
    public void removeProductFromCart(int productId) throws Exception {
        cartDao.deleteCartItem(productId);
    }

    @Override
    public void clearCart() throws Exception {
        cartDao.clearCart();
    }
}
