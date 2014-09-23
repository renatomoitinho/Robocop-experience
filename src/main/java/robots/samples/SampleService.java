package robots.samples;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import robots.routes.Service;
import robots.utils.Temp;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 13/03/14 11:47
 */
public class SampleService implements Service {

    Logger log = Logger.getLogger(SampleService.class);

    final Temp.Schema schema;
    final Gson gson;

    public SampleService(){
        schema = Temp.create("users");
        gson = new Gson();
    }

    public String get(String obj){
        return schema.get( obj ).toString();
    }

    public void delete(String obj){
        schema.delete(obj);
    }

    public void update(String key, String obj){
        schema.update(key, toObj(obj));
    }

    public void save(String obj){
        log.info("save ..." + obj );
        schema.insert( toObj(obj) );
    }

    public JsonObject toObj(String obj){
      return  gson.fromJson(obj , JsonObject.class);
    }

    public String all(){
        String all = gson.toJson (schema.all());
        log.info("all ..." + all);

        return all;
    }

}
