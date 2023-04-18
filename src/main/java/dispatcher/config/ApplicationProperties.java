package dispatcher.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static ApplicationProperties generalData;
    private ApplicationProperties() {}
    public static synchronized ApplicationProperties getInstance() {
        if (generalData == null) generalData = new ApplicationProperties();
        return generalData;
    }

    private final Logger logger = LogManager.getLogger();

    public Properties getApplicationProperties() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(System.getProperty("user.dir") + File.separator  + "application.properties")) {
            prop.load(input);
        } catch (Exception ex) {
            logger.fatal("Not found file application.properties");
        }
        return prop;
    }
}