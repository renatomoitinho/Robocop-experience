package robots.routes;

import robots.filters.Resources;
import robots.utils.Tuples;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 10/03/14 01:41
 */
public final class Routes {

    public static void processLine(String line){

        if(!line.matches("(.*?#.*?|^\\s*$)")){
            String[] token = line.split("\\s+",3);
            System.out.println(Arrays.toString( token ));
            Methods method = Methods.self(token[0]);
            if(method !=null ){
                method.put(token[1], Tuples.of(token[2], "", ""));
            }
        }
    }

    public static void readConfs() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Resources.conf("routes").asPath(), Charset.defaultCharset());
        for(String line; (line = reader.readLine())!=null; processLine(line)) {}
        reader.close();
    }
}
