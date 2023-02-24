package telegram;

import com.sun.net.httpserver.HttpServer;
import telegram.handlers.GetUpdatesHandler;
import telegram.handlers.LoggerHandler;
import telegram.handlers.SendMessageHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static void main(String[] args)  {
        GeneralData generalData = GeneralData.getInstance();
        generalData.config();
        new LoggerHandler().confLog();

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("10.10.5.173", 8081), 0);
            server.createContext("/getUpdates/", new GetUpdatesHandler());
            server.createContext("/sendMessage/", new SendMessageHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            System.out.println(" Server started on port 10.10.5.173");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}