package helpers;

/**
 * Created by Pablo Canseco on 1/27/2018.
 */
public class Logger { // extend me for logging facilities.

    public enum Level {
        DEBUG("DEBUG"),
        INFO("\u001B[36mINFO "),
        WARN("\u001B[33mWARN "),
        ERROR("\u001B[31mERROR"),
        FATAL("\u001B[31;1mFATAL");

        String levelString;

        Level(String s) {
            this.levelString = s;
        }
    }

    public String noColor = "\u001B[0m";

    public Logger(String name, Level level) {
        this.className = name;
        this.level = level.ordinal();
    }

    private String className;
    private final int level;

    private void log(Level level, String msg) {
        if (level.ordinal() >= this.level) {

            String brightBlack = "\u001B[90m";
            System.out.println(level.levelString
                    + " - " + className + ": " + msg + " "
                    + brightBlack + "("
                    + Thread.currentThread().getStackTrace()[3].getClassName() + ":"
                    + Thread.currentThread().getStackTrace()[3].getLineNumber()
                    + ")" + noColor);
        }
    }

    public void debug(String msg) {
        log(Level.DEBUG, msg);
    }

    public void info(String msg) {
        log(Level.INFO, msg);
    }

    public void warning(String msg) {
        log(Level.WARN, msg);
    }

    public void error(String msg) {
        log(Level.ERROR, msg);
    }

    public void fatal(String msg) {
        log(Level.FATAL, msg);
    }
}
