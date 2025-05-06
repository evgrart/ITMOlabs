//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package server.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
    private static final Logger logger = LogManager.getLogger("GlobalLogger");

    public MyLogger() {
    }

    public static void info(String msg) {
        logger.info(msg);
    }
}
