package ch.usi.geolocker.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class SpotSerializer implements JsonSerializer<Spot> {
    // Do not include "id" in the serialized JSON
    @Override
    public JsonElement serialize(Spot spot, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("message", new JsonPrimitive(spot.getMessage()));
        jsonObject.add("imageString", new JsonPrimitive(spot.getImageString()));
        jsonObject.add("longitude", new JsonPrimitive(spot.getLongitude()));
        jsonObject.add("latitude", new JsonPrimitive(spot.getLatitude()));
        jsonObject.add("visibilityRangeRadiusInMeters", new JsonPrimitive(spot.getVisibilityRangeRadiusInMeters()));
        jsonObject.add("expirationDateTime", context.serialize(spot.getExpirationDateTime()));
        return jsonObject;
    }
}