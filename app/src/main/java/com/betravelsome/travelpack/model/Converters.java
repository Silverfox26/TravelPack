package com.betravelsome.travelpack.model;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Converters {
    /**
     * Converts a JSON Place object string to a Place object.
     *
     * @param value The encoded Place object string.
     * @return The converted Place object.
     */
    @TypeConverter
    public static Place fromString(String value) {
        Type placeType = new TypeToken<Place>() {
        }.getType();
        return new Gson().fromJson(value, placeType);
    }

    /**
     * Converts a Place object to a JSON string representation.
     *
     * @param place The Place object to be converted.
     * @return The converted JSON String.
     */
    @TypeConverter
    public static String fromPlace(Place place) {
        Gson gson = new Gson();
        return gson.toJson(place);
    }
}
