package robots.routes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 07/03/14 00:02
 */
public interface Controllers {
     void on(String method, Result result, HttpServletRequest request, HttpServletResponse response);
}
