package robots.routes;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import robots.samples.SampleService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 13/03/14 11:47
 */
public final class Services {

    private static final ConcurrentMap<String, Object> cache =
            new ConcurrentLinkedHashMap.Builder<String,Object>()
                    .maximumWeightedCapacity(1000)
                    .build();

    static {
        cache.put("sampleService", new SampleService());
    }

    public <T extends Service> T of(Class<T> tClass){
        return null ;//(T) cache.get(tClass.getSimpleName());
    }

}
