package com.general.io;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * адаптер для дат.
 */
public class DateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-MM-dd"
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
