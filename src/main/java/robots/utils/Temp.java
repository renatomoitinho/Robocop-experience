package robots.utils;

import com.google.gson.JsonObject;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 13/03/14 10:57
 */
public final class Temp {

    private Temp(){}

    private static final ConcurrentMap<String,Map<String,JsonObject>> cache =
            new ConcurrentLinkedHashMap.Builder<String,Map<String,JsonObject>>()
            .maximumWeightedCapacity(1000)
            .build();

     public static Schema create(String name){
         Map<String,JsonObject> map = new HashMap<>();
         cache.put(name, map);
         return new SchemaImpl(map);
     }


    public static class SchemaImpl implements Schema{

        final Map<String,JsonObject> fields;
        final AtomicInteger count = new AtomicInteger();

        public SchemaImpl(Map<String,JsonObject> fields) {
            this.fields = fields;
        }

        @Override
        public JsonObject get(String id) {
            JsonObject resp;
            return (resp = fields.get(id))!=null ? resp : new JsonObject() ;
        }

        @Override
        public void delete(String id) {
              fields.remove(id);
        }

        @Override
        public void insert(JsonObject object) {
            String id = Integer.toHexString(count.incrementAndGet());
            System.out.println("generate id ->" + id );
            object.addProperty("id", id);
            fields.put(id, object);
        }

        @Override
        public void update(String id, JsonObject object) {
            if(fields.containsKey(id))
                fields.put(id, object);
        }

        @Override
        public JsonObject[] all() {
            return fields.values().toArray(new JsonObject[fields.size()]);
        }
    }


    public static interface Schema{
       void delete(String id);
       void insert(JsonObject object);
       void update(String id, JsonObject object);
       JsonObject get(String id);
       JsonObject[] all();

    }


}
