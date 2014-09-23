package robots.routes;

import com.google.gson.Gson;
import robots.filters.Resources;
import robots.samples.SampleService;
import robots.utils.Tuples;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 10/03/14 20:45
 */
public class Result {


    public final HttpServletRequest request;
    public final HttpServletResponse response;
    public final Map<String, Object> parameters;
    public Tuples.Triplet<String,String,String> triplet;
    public static final SampleService service = new SampleService();


    public static String json(){
         return "application/json;charset=UTF-8";
    }

    public static String text(){
        return "text/plain;charset=UTF-8";
    }

    public static String html(){
        return "text/html;charset=UTF-8";
    }

    public Result(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.parameters = new HashMap<>();
    }


    private Map<String,Object> joinParam(Map<String,Object> p, Map<String,String[]> map){
        if(p==null || map==null)
            return null;
        for (Map.Entry<String,String[]> entry : map.entrySet()) {
             Object o = entry.getValue();
             p.put(entry.getKey(), o.getClass().isArray() ? ((String[]) o)[0] : (String) o);
        }
        return p;
    }


    public final String getParam(){
        String param = new Gson().toJson(joinParam( parameters ,request.getParameterMap() ));
        System.out.println( param );
        return param;  //use JSON.parse(obj)
    }


    private void writeString(String string, int status , String contentType) {
        try {
            response.setStatus(status);
            response.setContentType(contentType);
            response.getWriter().write(string);
            response.getWriter().close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public final String all(){
        return service.all();
    }
    public final void save(String obj){
        service.save(obj);
    }
    public final String get(String obj){
        return service.get(obj);
    }
    public final void delete(String obj){
        service.delete(obj);
    }
    public final void update(String id, String obj){
        service.update(id, obj);
    }

    public final void ok(String response){
        writeString(response,200,html());
    }

    public final void writeHTML(String response){
        writeString(response,200,html());
    }

    public final void writeError(String response){
        writeString(response,500,text());
    }

    public final void writeText(String response){
        writeString(response,200,text());
    }

    public final void writeJSON(String response){
        writeString(response,200, json());
    }

    public final void redirect(String path){
        try {
            this.response.sendRedirect(path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void status(int status, String response){
        writeString(response, status, html());
    }

    public final void status(int status){
        this.response.setStatus(status);
    }

    public final String template(String template) {

        try {
            return Resources.view(template).asString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "<no data>";
    }

}
