import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import robots.routes.Controllers;
import robots.routes.Routes;
import robots.samples.SampleService;

import javax.script.ScriptEngineManager;
import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.getProperty;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 11/03/14 19:13
 */
public final class Main {




        public static void main(String[] args){

        try{

            config();

            System.out.println( getProperty("robots.coffee.compiler"));

            String webappDirLocation = getProperty("robots.webapp");
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(Integer.valueOf(getProperty("robots.port")));

            StandardContext ctx = (StandardContext) tomcat.addWebapp("/",
                    new File(webappDirLocation).getAbsolutePath());

            WebResourceRoot resources = new StandardRoot(ctx);
            ctx.setResources(resources);

            tomcat.start();
            welcome();
            tomcat.getServer().await();

        }catch (ServletException | LifecycleException  e){

           e.printStackTrace();
           System.exit(1);

        } catch (IOException e) {

            System.err.println("move webapp and conf/robots.properties for directory executable jar :/ ");
        }
    }

    /*
    * loads configurations
    * */

    public static ClassLoader currentClassLoader(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = Class.class.getClassLoader();
        }
        return classLoader;
    }

    public static void welcome() throws IOException {
        InputStream inputStream =  currentClassLoader().getResourceAsStream("welcome");
        byte[] print = new byte[inputStream.available()];
        inputStream.read(print);
        inputStream.close();

        System.out.println( new String(print));
    }

    public static void putInSystem(InputStream inputStream) throws IOException {
        Properties props = new Properties();
        props.load(inputStream);

        for(Map.Entry<Object, Object> entry : props.entrySet()){
            System.setProperty((String)entry.getKey(), (String)entry.getValue());
        }
    }

    public static void config() throws IOException {
        loadproperties();
        Routes.readConfs();
    }


    public static void loadproperties() throws IOException {

        File jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()); //run out
        if(!jar.isDirectory()){
            String dirjar = jar.getAbsolutePath().replace(jar.getName(),"");
            putInSystem( new FileInputStream(Paths.get(dirjar).resolve("../conf/robots.properties").toFile()));
            System.setProperty("robots.webapp", Paths.get(dirjar).resolve("../webapp").toUri().getPath());
            return;
        }

        InputStream inputStream = currentClassLoader().getResourceAsStream("robots.properties");
        putInSystem(inputStream);
    }

 }
