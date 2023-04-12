package telegram;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import telegram.config.GeneralData;
import telegram.handlers.GetUpdatesHandler;
import telegram.scheduled.Scheduled;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args)  {
        GeneralData generalData = GeneralData.getInstance();
        generalData.config();
        try {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(generalData.getSchedulerThreadCount());
            HttpServer server = HttpServer.create(new InetSocketAddress(generalData.getDomain(), generalData.getServerPort()), 0);
            server.createContext("/getUpdates/", new GetUpdatesHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            System.out.println("Start PROD version " + generalData.getDomain() + " on " + generalData.getServerPort());
            Scheduled.getInstance().startJobs();
        } catch (Exception e) {
            logger.fatal("Cannot start server EXCEPTION: " + e.getMessage());
        }
    }
}