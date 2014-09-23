package robots.filters;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import robots.utils.Tuples;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 12/03/14 20:08
 */
public final class Resources {

    enum Src{
        script("assets.scripts"),
        view("assets.views"),
        lib("assets.libs"),
        conf("conf");

        private static final EnumMap<Src,Tuples.Pair<String,Path>> enumMap = new EnumMap<>(Src.class);

        public String name;
        Src(String name){ this.name = name;}

        public Tuples.Pair<String,Path> get(){
          return enumMap.get(this);
        }

        public static void configurePath(Path path){
            for (Src src : values()){
                enumMap.put(src, Tuples.of(src.name , path.resolve(src.name).toAbsolutePath() ));
            }
        }
    }


    static final Logger log = Logger.getLogger(Resources.class);

    static final ConcurrentMap<String, AS> cache = new ConcurrentLinkedHashMap.Builder<String, AS>()
            .maximumWeightedCapacity(1000)
            .build();

    static {

        File jar = new File(Resources.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        Path usrDir = Paths.get(System.getProperty("user.dir"));

        if(Files.isDirectory(usrDir)){
           Path dir = jar.isDirectory() ? usrDir.resolve("src/main") :usrDir ;
           Src.configurePath(dir);
        }
    }


    private static AS checkAndPutcache(String filename, Path path) throws FileNotFoundException {
        if(cache.containsKey(filename)){
            log.info("cacheable");
            return cache.get(filename);
        }
        Path currentPath;
        File file = (currentPath = path).toFile();
        if(file.exists()){
            AS as = new ASImpl(currentPath);
            cache.put(filename, as);
            return as;
        }

        throw new FileNotFoundException( path + " not found in repository :/ , <<no use extensions>>");
    }


    public static AS script(String filename) throws FileNotFoundException {
        return checkAndPutcache("js:"+filename, Src.script.get().second.resolve("js/".concat(filename).concat(".js")));
    }
    public static AS coffee(String filename) throws FileNotFoundException {
        return checkAndPutcache("coffee:"+filename, Src.script.get().second.resolve("coffees/".concat(filename).concat(".coffee")));
    }

    public static AS view(String filename) throws FileNotFoundException {
       return checkAndPutcache(filename, Src.view.get().second.resolve((filename).concat(".html")));
    }

    public static AS lib(String filename) throws FileNotFoundException{
        return checkAndPutcache(filename, Src.lib.get().second.resolve((filename).concat(".js")));
    }

    public static AS conf(String filename) throws FileNotFoundException{
        return checkAndPutcache(filename, Src.conf.get().second.resolve((filename)));
    }



    //inner class

    public static final class ASImpl implements AS{


        final Path path;
        public ASImpl(Path path) {
            this.path = path;
        }

        @Override
        public InputStream asStream() {
            try {
                return new FileInputStream(asFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public InputStreamReader asReader() {
            try {
                return new FileReader(asFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Path asPath() {
            return path;
        }

        @Override
        public File asFile() {
           return asPath().toFile();
        }

        @Override
        public String asString() {
            try {
                return IOUtils.toString(asStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static interface AS{
        InputStream asStream();
        InputStreamReader asReader();
        Path asPath();
        File asFile();
        String asString();
    }
}
