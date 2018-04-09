/**
 * Created by Pablo Canseco on 1/27/2018.
 */
class Logger { // extend me for logging facilities.

   public enum Level {
        DEBUG("DEBUG"),
        INFO("\u001B[36mINFO"),
        WARN("\u001B[33mWARNING"),
        ERROR("\u001B[31mERROR"),
        FATAL("\u001B[31;1mFATAL");

        String levelString;
        Level(String s) {
            this.levelString = s;
        }
    }

    Logger(String name) {
       this.className = name;
    }
    Logger(String name, Level level) {
        this(name);
        this.level = level.ordinal();
    }

    private String className;
    private int level = Level.DEBUG.ordinal(); // global log level

    private void log(Level level, String msg) {
        if (level.ordinal() >= this.level) {
            String noColor = "\u001B[0m";
            System.out.println(level.levelString + " - " + className + " :: " + msg + noColor);
        }
    }

    void debug(String msg) {
        log(Level.DEBUG, msg);
    }
    void info(String msg) {
        log(Level.INFO, msg);
    }
    void warning(String msg) {
        log(Level.WARN, msg);
    }
    void error(String msg) {
        log(Level.ERROR, msg);
    }
    void fatal(String msg) {
        log(Level.FATAL, msg);
    }
}
