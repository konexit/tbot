package telegram;

import com.sun.net.httpserver.HttpServer;
import telegram.config.GeneralData;
import telegram.handlers.GetUpdatesHandler;
import telegram.scheduled.Scheduled;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {


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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}