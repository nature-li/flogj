package org.bar.flogj;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    static private Logger logger = new Logger();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private Object env = Env.develop;
    private Level level = Level.all;
    private Writer writer = null;
    private Controller controller = null;

    static public Logger getLogger() {
        return logger;
    }

    private String now() {
        Date date = new Date();
        return format.format(date);
    }

    private String position() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        String where = stackTraceElements[3].getFileName();
        where += ":" + stackTraceElements[3].getLineNumber();
        where += ":" + stackTraceElements[3].getClassName();
        where += "." + stackTraceElements[3].getMethodName();
        return where;
    }

    private String joinMessage(Level level, String pvId, Keyword keyword, Object content) {
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
        env = environment;
        writer = new Writer(target, fileName, maxFileSize, maxFileCount);
        controller = new Controller(writer);

        if (!controller.start()) {
            System.out.println("logger start controller failed");
            return false;
        }
        return true;
    }

    public void stop() {
        controller.stop();
    }

    public void setLevel(Level lvl) {
        level = lvl;
    }

    public void trace(Object content) {
        if (Level.trace.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.trace, "", Keyword.normal, content);
        log(Level.trace, msg);
    }

    public void debug(Object content) {
        if (Level.debug.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.debug, "", Keyword.normal, content);
        log(Level.debug, msg);
    }

    public void info(Object content) {
        if (Level.info.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.info, "", Keyword.normal, content);
        log(Level.info, msg);
    }

    public void warn(Object content) {
        if (Level.warn.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.warn, "", Keyword.normal, content);
        log(Level.warn, msg);
    }

    public void error(Object content) {
        if (Level.error.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.error, "", Keyword.normal, content);
        log(Level.error, msg);
    }

    public void fatal(Object content) {
        if (Level.fatal.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.fatal, "", Keyword.normal, content);
        log(Level.fatal, msg);
    }

    public void report(Object content) {
        if (Level.report.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.report, "", Keyword.normal, content);
        log(Level.report, msg);
    }

    public void trace(String pvId, Object content) {
        if (Level.trace.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.trace, pvId, Keyword.normal, content);
        log(Level.trace, msg);
    }

    public void debug(String pvId, Object content) {
        if (Level.debug.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.debug, pvId, Keyword.normal, content);
        log(Level.debug, msg);
    }

    public void info(String pvId, Object content) {
        if (Level.info.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.info, pvId, Keyword.normal, content);
        log(Level.info, msg);
    }

    public void warn(String pvId, Object content) {
        if (Level.warn.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.warn, pvId, Keyword.normal, content);
        log(Level.warn, msg);
    }

    public void error(String pvId, Object content) {
        if (Level.error.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.error, pvId, Keyword.normal, content);
        log(Level.error, msg);
    }

    public void fatal(String pvId, Object content) {
        if (Level.fatal.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.fatal, pvId, Keyword.normal, content);
        log(Level.fatal, msg);
    }

    public void report(String pvId, Object content) {
        if (Level.report.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.report, pvId, Keyword.normal, content);
        log(Level.report, msg);
    }

    public void trace(String pvId, Keyword keyword, Object content) {
        if (Level.trace.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.trace, pvId, keyword, content);
        log(Level.trace, msg);
    }

    public void debug(String pvId, Keyword keyword, Object content) {
        if (Level.debug.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.debug, pvId, keyword, content);
        log(Level.debug, msg);
    }

    public void info(String pvId, Keyword keyword, Object content) {
        if (Level.info.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.info, pvId, keyword, content);
        log(Level.info, msg);
    }

    public void warn(String pvId, Keyword keyword, Object content) {
        if (Level.warn.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.warn, pvId, keyword, content);
        log(Level.warn, msg);
    }

    public void error(String pvId, Keyword keyword, Object content) {
        if (Level.error.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.error, pvId, keyword, content);
        log(Level.error, msg);
    }

    public void fatal(String pvId, Keyword keyword, Object content) {
        if (Level.fatal.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.fatal, pvId, keyword, content);
        log(Level.fatal, msg);
    }

    public void report(String pvId, Keyword keyword, Object content) {
        if (Level.report.compareTo(level) < 0) {
            return;
        }
        String msg = joinMessage(Level.report, pvId, keyword, content);
        log(Level.report, msg);
    }
}
