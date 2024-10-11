package com.example.ecommerce.model;

import androidx.room.TypeConverter; /**
 * Type converter to store Boolean values as Integer in the database.
 * This is necessary because SQLite does not support the Boolean data type directly.
 */
public class BooleanConverter {

    @TypeConverter
    public static Boolean fromInteger(Integer value) {
        return value != null && value == 1;
    }

    @TypeConverter
    public static Integer toInteger(Boolean value) {
        return value != null && value ? 1 : 0;
    }
}
