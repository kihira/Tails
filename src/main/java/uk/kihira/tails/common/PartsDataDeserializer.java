package uk.kihira.tails.common;

import com.google.gson.*;

import java.lang.reflect.Type;

class PartsDataDeserializer implements JsonDeserializer<PartsData> {
    @Override
    public PartsData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Load old data if exists
        if (json.getAsJsonObject().has("partInfos")) {
            PartsData partsData = new PartsData();
            for (JsonElement e : json.getAsJsonObject().get("partInfos").getAsJsonArray()) {
                PartInfo info = context.deserialize(e, PartInfo.class);
                partsData.partInfoMap.put(info.partType, info);
            }

            Tails.logger.info("Loading old parts data");
            return partsData;
        }

        // Default serializer. Not the most efficent but works for now
        return new Gson().fromJson(json, typeOfT);
    }
}
