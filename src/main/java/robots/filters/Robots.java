package robots.filters;

import org.apache.log4j.Logger;
import robots.routes.DefParamCtrl;
import robots.routes.Methods;
import robots.routes.ParamCtrl;
import robots.routes.Result;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 11/03/14 21:33
 */
@WebFilter(urlPatterns = "/*" , asyncSupported = true)
public class Robots implements Filter {

    Logger log = Logger.getLogger(Robots.class);
    ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        log.info("init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException(
                    "robocop aren't supported.");
        }

        final HttpServletRequest baseRequest = (HttpServletRequest) request;
        final HttpServletResponse baseResponse = (HttpServletResponse) response;

        if (isRequestingStaticFile(baseRequest)){
            deferProcessingToContainer(filterChain, baseRequest, baseResponse);
        } else{

            Methods methods = Methods.self(baseRequest.getMethod());

            if(methods!=null)
            {
                String[] keys = methods.mapped().keySet().toArray(new String[ methods.mapped().size()]);

                for(int i =0; i < keys.length;i++){
                    ParamCtrl paramCtrl = DefParamCtrl.of(keys[i]);
                    if(paramCtrl.matches(baseRequest.getRequestURI())){

                        log.info("found url :)" + baseRequest.getRequestURI() );

                        Result result = new Result(baseRequest, baseResponse);
                        result.triplet = methods.mapped().get(keys[i]);

                        paramCtrl.fillIntoRequest(baseRequest.getRequestURI(), result.parameters);
                        request.setAttribute("result", result);
                        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
                        RobotWork.newWork(request.startAsync());
                        return;
                    }
                }
            }
        }


       // filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        log.info("destroy");

    }

    public boolean isRequestingStaticFile(HttpServletRequest request) throws MalformedURLException {
        URL resourceUrl = servletContext.getResource(uriRelativeToContextRoot(request));
        return resourceUrl != null && isAFile(resourceUrl);
    }

    private String uriRelativeToContextRoot(HttpServletRequest request) {
        String uri = request.getRequestURI().substring(request.getContextPath().length());
        return removeQueryStringAndJSessionId(uri);
    }

    private String removeQueryStringAndJSessionId(String uri) {
        return uri.replaceAll("[\\?;].+", "");
    }

    private boolean isAFile(URL resourceUrl) {
        return !resourceUrl.toString().endsWith("/");
    }

    public void deferProcessingToContainer(FilterChain filterChain, HttpServletRequest request,
                                           HttpServletResponse response) throws IOException, ServletException {
        log.info (MessageFormat.format("Deferring request to container: {0} ", request.getRequestURI()));
        filterChain.doFilter(request, response);
    }
}
