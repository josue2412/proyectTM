package com.example.josuerey.helloworld.utilities;

import android.arch.persistence.room.TypeConverter;

import com.example.josuerey.helloworld.domain.movement.Movement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MovementConverter {
    @TypeConverter
    public String fromMovementList(List<Movement> movements) {
        if (movements == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Movement>>() {}.getType();
        String json = gson.toJson(movements, type);
        return json;
    }

    @TypeConverter
    public List<Movement> toMovementList(String movementString) {
        if (movementString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Movement>>() {}.getType();
        List<Movement> countryLangList = gson.fromJson(movementString, type);
        return countryLangList;
    }
}
