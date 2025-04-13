package server.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс, управляющий логированием
 */
public class MyLogger {
    private static final Logger logger = LogManager.getLogger("GlobalLogger");

    public static void info(String msg) {
        logger.info(msg);
    }
}
