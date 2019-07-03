package ch.bfh.sd.five.todo.helper;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonConverter {

    public static String getJson(Object object) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .create();
        return gson.toJson(object);
    }

    public static <T> T getObject(String json, Class<T> classOfT) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();
        return (T) gson.fromJson(json, classOfT);
    }

    public static <T> String getJsonFromList(List<T> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .create();
        Type listOfTestObject = new TypeToken<List<T>>() {}.getType();
        return(gson.toJson(list, listOfTestObject));
    }

    public static <T> ArrayList<T> getObjectList(String json, Class<T> classOfT) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();
        Type listOfTestObject = new TypeToken<List<T>>() {}.getType();
        return (ArrayList<T>) gson.fromJson(json, listOfTestObject);
    }

    static private class LocalDateSerializer implements JsonSerializer<LocalDate> {

        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    static private class LocalDateDeserializer implements JsonDeserializer<LocalDate> {

        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return DateTimeFormatter.ISO_LOCAL_DATE.parse(json.getAsString(), LocalDate::from);
        }
    }
}
