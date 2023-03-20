package telegram;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import telegram.config.GeneralData;
import telegram.handlers.GetUpdatesHandler;
//import telegram.scheduled.Scheduled;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

//import telegram.handlers.SendMessageHandler;

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args)  {
        GeneralData.getInstance().config();
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("10.10.5.173", 8081), 0);
            server.createContext("/getUpdates/", new GetUpdatesHandler());
//            server.createContext("/sendMessage/", new SendMessageHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            for (int i = 0; i < 1000; i++) {
                logger.error(i + " Test error");
                logger.info(i + " Test info");
                logger.debug(i + " Test debug");
            }
//            Scheduled.startScheduled();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}