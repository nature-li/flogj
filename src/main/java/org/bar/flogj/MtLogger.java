package org.bar.flogj;

import org.apache.log4j.Logger;

public class MtLogger {
    static String FQCN = MtLogger.class.getName();
    private Logger logger_ = null;
    private MtEnv env_ = MtEnv.develop;

    public MtLogger(String name, MtEnv env) {
        this.logger_ = Logger.getLogger(name);
        this.env_ = env;
    }

    public MtLogger(Class clazz, MtEnv env) {
        this(clazz.getName(), env);
    }

    private String get_message(String pvid, MtKeyWord keyword, Object msg) {
        String message = env_ + "]\u001e[" + pvid + "]\u001e[" + keyword + "]\u001e[" + msg;
        return message;
    }


    public void trace(Object msg) {
        String pvid = "";
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_TRACE, message, null);
    }

    public void debug(String msg) {
        String pvid = "";
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_DEBUG, message, null);
    }

    public void info(String msg) {
        String pvid = "";
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_INFO, message, null);
    }

    public void warn(String msg) {
        String pvid = "";
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_WARN, message, null);
    }

    public void error(String msg) {
        String pvid = "";
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_ERROR, message, null);
    }

    public void fatal(String msg) {
        String pvid = "";
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_FATAL, message, null);
    }

    public void report(String msg) {
        String pvid = "";
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_REPORT, message, null);
    }

    public void trace(String pvid, String msg) {
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_TRACE, message, null);
    }

    public void debug(String pvid, String msg) {
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_DEBUG, message, null);
    }

    public void info(String pvid, String msg) {
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_INFO, message, null);
    }

    public void warn(String pvid, String msg) {
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_WARN, message, null);
    }

    public void error(String pvid, String msg) {
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_ERROR, message, null);
    }

    public void fatal(String pvid, String msg) {
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_FATAL, message, null);
    }

    public void report(String pvid, String msg) {
        MtKeyWord key_word = MtKeyWord.normal;
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_REPORT, message, null);
    }

    public void trace(String pvid, MtKeyWord key_word, String msg) {
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_TRACE, message, null);
    }

    public void debug(String pvid, MtKeyWord key_word, String msg) {
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_DEBUG, message, null);
    }

    public void info(String pvid, MtKeyWord key_word, String msg) {
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_INFO, message, null);
    }

    public void warn(String pvid, MtKeyWord key_word, String msg) {
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_WARN, message, null);
    }

    public void error(String pvid, MtKeyWord key_word, String msg) {
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_ERROR, message, null);
    }

    public void fatal(String pvid, MtKeyWord key_word, String msg) {
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_FATAL, message, null);
    }

    public void report(String pvid, MtKeyWord key_word, String msg) {
        String message = get_message(pvid, key_word, msg);
        this.logger_.log(FQCN, MtLevel.MT_REPORT, message, null);
    }
}
