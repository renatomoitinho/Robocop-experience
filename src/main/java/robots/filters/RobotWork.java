package robots.filters;

import com.theoryinpractise.coffeescript.CoffeeScriptCompiler;
import org.apache.log4j.Logger;
import robots.routes.Controllers;
import robots.routes.Result;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 *
 * @author renatomoitinhodias@gmail.com
 * @since 11/03/14 21:34
 */
@WebListener
public class RobotWork implements ServletContextListener {

    Logger log = Logger.getLogger(ServletContextListener.class);

    private static final BlockingQueue<AsyncContext> process = new LinkedBlockingQueue<>();
    private static final ExecutorService singleExecute = Executors.newSingleThreadExecutor();
    private static final CoffeeScriptCompiler coffeeScriptCompiler = new CoffeeScriptCompiler("1.6.1");

    private static ThreadLocal<ScriptEngine> scriptEngineLocal = new ThreadLocal<ScriptEngine>() {

        /**
         * Returns the current thread's "initial value" for this
         * thread-local variable.  This method will be invoked the first
         * time a thread accesses the variable with the {@link #get}
         * method, unless the thread previously invoked the {@link #set}
         * method, in which case the <tt>initialValue</tt> method will not
         * be invoked for the thread.  Normally, this method is invoked at
         * most once per thread, but it may be invoked again in case of
         * subsequent invocations of {@link #remove} followed by {@link #get}.
         * <p/>
         * <p>This implementation simply returns <tt>null</tt>; if the
         * programmer desires thread-local variables to have an initial
         * value other than <tt>null</tt>, <tt>ThreadLocal</tt> must be
         * subclassed, and this method overridden.  Typically, an
         * anonymous inner class will be used.
         *
         * @return the initial value for this thread-local
         */
        @Override
        protected ScriptEngine initialValue() {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine scriptEngine = manager.getEngineByName("jav8");

            try {
                InputStreamReader mustache =  Resources.lib("mustache").asReader();
                InputStreamReader underscore = Resources.lib("underscore").asReader();

                String coffee = Resources.coffee("app").asString();

                String reader =  coffeeScriptCompiler.compile(coffee , "robots", true, CoffeeScriptCompiler.SourceMap.V3, true, false).getJs();

                scriptEngine.eval(mustache);
                scriptEngine.eval(underscore);
                scriptEngine.eval(reader);


            } catch (ScriptException | IOException e) {
                e.printStackTrace();
            }

            System.out.println( "new engine :/ " );
            return scriptEngine;        }


    };


    public static void newWork(AsyncContext c) {
        try {
            process.put(c);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("contextInitialized");

        singleExecute.submit(new Thread(new Runnable() {

            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p/>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {

                log.info("starting....");
                while (true) {
                    // Thread.sleep(2000);
                    AsyncContext context = null;

                    try {
                        context = process.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {

                        ScriptEngine engine = scriptEngineLocal.get();

                        final Invocable inv = (Invocable) engine;
                        final Object obj = engine.get("obj");
                        final HttpServletRequest request = (HttpServletRequest) context.getRequest();
                        final HttpServletResponse response = (HttpServletResponse) context.getResponse();
                        final Result result = (Result) request.getAttribute("result");
                        Controllers method = inv.getInterface(obj, Controllers.class);

                       // inv.

                        log.info("invoiced " + result.triplet.first );
                        method.on(result.triplet.first, result, request, response);

                    }
                    finally {
                        context.complete();
                    }


                }

            }
        }));

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
         log.info("contextDestroyed");
        singleExecute.shutdownNow();
    }
}
