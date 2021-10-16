package io.ib67.serverutil;

import com.google.gson.*;

import java.lang.reflect.Type;

public abstract class AbstractModuleConfig {
    /* public static class Adapter extends TypeAdapter<AbstractModuleConfig> {
         private static final Gson PURE_GSON = new GsonBuilder().setPrettyPrinting().create();
         @Override
         public void write(JsonWriter out, AbstractModuleConfig value) throws IOException {
             out.beginObject();
             out.name("implClass").value(value.implClass);
             out.name("data").jsonValue(PURE_GSON.toJson(value));
             out.endObject();
         }

         @Override
         @SneakyThrows
         public AbstractModuleConfig read(JsonReader in) throws IOException {
             String implClass=null;
             String data=null;
             a: while(in.hasNext()){
                 switch (in.nextName()){
                     case "implClass":
                         implClass = in.nextString();
                         break a;
                     case "data":
                         break;
                 }
             }
             if(implClass == null){
                 throw new IOException("Not a module config.");
             }
             Class<? extends AbstractModuleConfig> clazz = (Class<? extends AbstractModuleConfig>) Class.forName(implClass);
             class DirtyHack<T> {
                 DirtyHack(Class<T> tClass){}
                 T data;
                 DirtyHack<T> resolve(JsonReader in){
                     System.out.println(new TypeToken<DirtyHack<T>>(){}.getType());
                     return PURE_GSON.fromJson(in,new TypeToken<DirtyHack<T>>(){}.getType());
                 }
             }
             var capture = new DirtyHack<>(clazz);

             return capture.resolve(in).data;
         }
     }*/
    private static final Gson PURE_GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String implClass;

    {
        this.implClass = this.getClass().getName();
    }

    public static class Adapter implements JsonSerializer<AbstractModuleConfig>, JsonDeserializer<AbstractModuleConfig> {

        private static final JsonParser PARSER = new JsonParser();

        @Override
        public AbstractModuleConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var jo = json.getAsJsonObject();
            var implClazz = jo.get("implClass").getAsString();
            try {
                Class<? extends AbstractModuleConfig> cfgClazz = (Class<? extends AbstractModuleConfig>) Class.forName(implClazz);
                //return PURE_GSON.fromJson(jo.get("data").toString(),cfgClazz);
                return PURE_GSON.fromJson(jo, cfgClazz);
            } catch (ClassNotFoundException exception) {
                throw new JsonParseException("Can't find implementation: " + implClazz);
            }
        }

        @Override
        public JsonElement serialize(AbstractModuleConfig src, Type typeOfSrc, JsonSerializationContext context) {
            var jo = new JsonObject();
            //jo.addProperty("implClass",src.implClass);
            //jo.add("data",PURE_GSON.toJsonTree(src));
            return jo;
        }
    }

}
