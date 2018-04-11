package org.bar.flogj;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    static private Logger logger = new Logger();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private Object env = Env.develop;
    private Level level = Level.all;
    private Controller controller = null;

    static public Logger getLogger() {
        return logger;
    }

    private String now() {
        Date date = new Date();
        String current = format.format(date);
        current += "000";
        return current;
    }

    private String position() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        String where = stackTraceElements[3].getClassName();
        where += "." + stackTraceElements[3].getMethodName();
        where += "@" + stackTraceElements[3].getFileName();
        where += ":" + stackTraceElements[3].getLineNumber();
        return where;
    }

    private String joinMessage(Level level, String pvId, String keyword, Object content) {
        String message = "";
        message += "[" + now() + "]\u001e";
        message += "[" + level + "]\u001e";
        message += "[" + Thread.currentThread().getId() + "]\u001e";
        message += "[" + position() + "]\u001e";
        message += "[" + env + "]\u001e";
        message += "[" + pvId + "]\u001e";
        message += "[" + keyword + "]\u001e";
        message += "[" + content + "]\u001e";
        return message;
    }

    private void log(Level lvl, String message) {
        controller.put(lvl, message);
    }

    public boolean init(Env environment, String target, String fileName) {
        long maxFileSize = 100 * 1024 * 1024;
        long maxFileCount = -1;
        return this.init(environment, target, fileName, maxFileSize, maxFileCount);
    }

    public boolean init(Env environment, String target, String fileName, long maxFileSize, long maxFileCount) {
        if (!Syslog.init()) {
            System.out.println("logger init syslog failed");
            return false;
        }

        env = environment;
        Writer writer = new Writer(target, fileName, maxFileSize, maxFileCount);
        controller = new Controller(writer);

        if (!controller.start()) {
            String msg = "logger start controller failed";
            System.out.println(msg);
            Syslog.error(msg);
            return false;
        }
        return true;
    }

    public boolean setAsync(boolean async) {
        if (this.controller == null) {
            return false;
        }

        this.controller.setAsync(async);
        return true;
    }

    public void stop() {
        controller.stop();
        Syslog.close();
    }

    public void setLevel(Level lvl) {
        level = lvl;
    }

    public Level getLevel() {
        return level;
    }

    public void trace(Object content) {
        if (Level.trace.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.trace, "", "normal", content);
        log(Level.trace, msg);
    }

    public void debug(Object content) {
        if (Level.debug.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.debug, "", "normal", content);
        log(Level.debug, msg);
    }

    public void info(Object content) {
        if (Level.info.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.info, "", "normal", content);
        log(Level.info, msg);
    }

    public void warn(Object content) {
        if (Level.warn.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.warn, "", "normal", content);
        log(Level.warn, msg);
    }

    public void error(Object content) {
        if (Level.error.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.error, "", "normal", content);
        log(Level.error, msg);
    }

    public void fatal(Object content) {
        if (Level.fatal.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.fatal, "", "normal", content);
        log(Level.fatal, msg);
    }

    public void report(Object content) {
        if (Level.report.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.report, "", "normal", content);
        log(Level.report, msg);
    }

    public void trace(String pvId, Object content) {
        if (Level.trace.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.trace, pvId, "normal", content);
        log(Level.trace, msg);
    }

    public void debug(String pvId, Object content) {
        if (Level.debug.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.debug, pvId, "normal", content);
        log(Level.debug, msg);
    }

    public void info(String pvId, Object content) {
        if (Level.info.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.info, pvId, "normal", content);
        log(Level.info, msg);
    }

    public void warn(String pvId, Object content) {
        if (Level.warn.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.warn, pvId, "normal", content);
        log(Level.warn, msg);
    }

    public void error(String pvId, Object content) {
        if (Level.error.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.error, pvId, "normal", content);
        log(Level.error, msg);
    }

    public void fatal(String pvId, Object content) {
        if (Level.fatal.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.fatal, pvId, "normal", content);
        log(Level.fatal, msg);
    }

    public void report(String pvId, Object content) {
        if (Level.report.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.report, pvId, "normal", content);
        log(Level.report, msg);
    }

    public void trace(String pvId, String keyword, Object content) {
        if (Level.trace.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.trace, pvId, keyword, content);
        log(Level.trace, msg);
    }

    public void debug(String pvId, String keyword, Object content) {
        if (Level.debug.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.debug, pvId, keyword, content);
        log(Level.debug, msg);
    }

    public void info(String pvId, String keyword, Object content) {
        if (Level.info.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.info, pvId, keyword, content);
        log(Level.info, msg);
    }

    public void warn(String pvId, String keyword, Object content) {
        if (Level.warn.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.warn, pvId, keyword, content);
        log(Level.warn, msg);
    }

    public void error(String pvId, String keyword, Object content) {
        if (Level.error.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.error, pvId, keyword, content);
        log(Level.error, msg);
    }

    public void fatal(String pvId, String keyword, Object content) {
        if (Level.fatal.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.fatal, pvId, keyword, content);
        log(Level.fatal, msg);
    }

    public void report(String pvId, String keyword, Object content) {
        if (Level.report.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.report, pvId, keyword, content);
        log(Level.report, msg);
    }
}
