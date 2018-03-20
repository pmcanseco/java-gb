/**
 * Created by Pablo Canseco on 1/27/2018.
 */
class Logger { // extend me for logging facilities.

   private enum Level {
        DEBUG("DEBUG"),
        INFO("INFO"),
        WARN("\u001B[33mWARNING"),
        ERROR("\u001B[31mERROR"),
        FATAL("\u001B[31mFATAL");

        String levelString;
        Level(String s) {
            this.levelString = s;
        }
    }

    private String className = this.getClass().getName();

    private void log(Level level, String msg) {
        System.out.println(level.levelString + " - " + className + " :: " + msg);
    }

    void logDebug(String msg) {
        log(Level.DEBUG, msg);
    }
    void logInfo(String msg) {
        log(Level.INFO, msg);
    }
    void logWarning(String msg) {
        log(Level.WARN, msg);
    }
    void logError(String msg) {
        log(Level.ERROR, msg);
    }
    void logFatal(String msg) {
        log(Level.FATAL, msg);
    }
}
