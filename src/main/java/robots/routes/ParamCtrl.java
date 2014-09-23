package robots.routes;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 06/03/14 22:48
 */
public interface ParamCtrl {
    /**
     * wether the uri matches this uri
     */
    boolean matches(String uri);

    void fillIntoRequest(String uri, Map<String, Object> request);

}
