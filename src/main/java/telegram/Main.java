package telegram;

import com.sun.net.httpserver.HttpServer;
import telegram.config.GeneralData;
import telegram.handlers.GetUpdatesHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args)  {
        GeneralData.getInstance().config();
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("10.10.5.173", 8081), 0);
            server.createContext("/getUpdates/", new GetUpdatesHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            System.out.println("Start PROD version 10.10.5.173 on 8081");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}