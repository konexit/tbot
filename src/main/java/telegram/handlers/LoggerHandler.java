package telegram.handlers;

import java.io.IOException;
import java.util.Date;
import java.util.logging.*;


public class LoggerHandler extends Formatter{

    private static final Logger logger = Logger.getLogger(LoggerHandler.class.getName());

    public void confLog(){
        Handler consoleHandler;
        Handler fileHandler;
        try{
            consoleHandler = new ConsoleHandler();
            fileHandler = new FileHandler("dispatcherTelegram.log", 50000, 1, true);
            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            consoleHandler.setFormatter(new LoggerHandler());
            fileHandler.setFormatter(new LoggerHandler());
            consoleHandler.setLevel(Level.ALL);
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.INFO);
        }catch(IOException exception){
            logger.log(Level.SEVERE, "Error occur in FileHandler.", exception);
        }
    }

    @Override
    public String format(LogRecord record) {
        return new Date(record.getMillis()) + " " + record.getLevel() + "  " + record.getSourceClassName() + " - " + record.getMessage() + "\n";
    }


}
