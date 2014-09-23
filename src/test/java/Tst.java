import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 22/03/14 17:05
 */
public class Tst {

    @Test void tt(){
        int i = 1;

        System.out.println( Integer.valueOf( String.valueOf(i) , 16) );
    }

}
