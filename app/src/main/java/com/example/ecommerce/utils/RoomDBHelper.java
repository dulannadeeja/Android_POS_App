package com.example.ecommerce.utils;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;

@Database(entities = {Product.class, CartItem.class}, version = 2, exportSchema = false)
public abstract class RoomDBHelper extends RoomDatabase {

    public abstract IProductDao productDao();

    public abstract ICartDao cartDao();

    private static volatile RoomDBHelper INSTANCE;

    public static RoomDBHelper getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDBHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RoomDBHelper.class, "ecommerce_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
