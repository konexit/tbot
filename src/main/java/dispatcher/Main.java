package dispatcher;

import com.sun.net.httpserver.HttpServer;
import dispatcher.config.BuildAPP;
import dispatcher.scheduled.services.AddJobHandler;
import dispatcher.scheduled.services.DeleteJobHandler;
import dispatcher.scheduled.services.InfoJobsHandler;
import dispatcher.swagger.SwaggerHandler;
import dispatcher.system.services.StaticFileHandler;
import dispatcher.telegramAPI.handlers.GetUpdatesHandler;
import dispatcher.telegramAPI.handlers.SendMessageHandler;
import dispatcher.telegramAPI.services.TelegramBotStatusWebHook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        BuildAPP buildAPP = BuildAPP.getInstance();

        if (!buildAPP.init()) {
            System.out.println("Dispatcher could not start. Check the logs to find the problem");
            return;
        }

        try {
            ThreadPoolExecutor threadPoolExecutor =
                    (ThreadPoolExecutor) Executors.newFixedThreadPool(
                            (Integer) buildAPP.systemAPI.getPropertiesByKey("serverThreadCount"));
            HttpServer server = HttpServer.create(new InetSocketAddress(
                    (String) buildAPP.systemAPI.getPropertiesByKey("domain"),
                    (Integer) buildAPP.systemAPI.getPropertiesByKey("serverPort")), 0);

            server.createContext("/getUpdates/", new GetUpdatesHandler());
            server.createContext("/sendMessageTelegram/", new SendMessageHandler());

            server.createContext("/telegramBotStatusWebHook/", new TelegramBotStatusWebHook());
            server.createContext("/addJob/", new AddJobHandler());
            server.createContext("/infoJobs/", new InfoJobsHandler());
            server.createContext("/deleteJob/", new DeleteJobHandler());


            server.createContext("/docs/", new SwaggerHandler());
            server.createContext("/", new StaticFileHandler("/resources/",
                    System.getProperty("user.dir") + File.separator, ""));

            server.setExecutor(threadPoolExecutor);
            server.start();
            System.out.println("Start Dispatcher Server");

        } catch (Exception e) {
            logger.fatal("Cannot start server EXCEPTION: " + e.getMessage());
        }
    }
}