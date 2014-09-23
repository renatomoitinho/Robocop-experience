package robots.routes;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import robots.utils.Tuples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 09/03/14 17:42
 */
public enum Methods {
    POST,GET,PUT,DELETE;

    static final ConcurrentMap<Methods,Map<String, Tuples.Triplet<String,String, String>>> mapping =
            new ConcurrentLinkedHashMap.Builder<Methods,Map<String, Tuples.Triplet<String,String, String>>>()
            .maximumWeightedCapacity(100)
            .build();

    static {
        for (Methods methods: Methods.values()){
            mapping.put(methods, new HashMap<String, Tuples.Triplet<String,String,String>>());
        }
    }

    public static Methods self(String str){
        return Methods.valueOf(str.toUpperCase());
    }

    public void put(String originalPath, Tuples.Triplet<String,String,String> triplet){
        mapping.get(this).put(originalPath,triplet);
    }

    public Map<String, Tuples.Triplet<String,String,String>> mapped(){
       return mapping.get(this);
    }

}
