package org.bar.flogj;

import org.apache.log4j.Level;

public class MtLevel extends Level {
    public static final int MT_TRACE_INT = Level.DEBUG_INT - 10000;
    public static final int MT_DEBUG_INT = Level.DEBUG_INT;
    public static final int MT_INFO_INT = Level.INFO_INT;
    public static final int MT_WARN_INT = Level.WARN_INT;
    public static final int MT_ERROR_INT = Level.ERROR_INT;
    public static final int MT_FATAL_INT = Level.FATAL_INT;
    public static final int MT_REPORT_INT = Level.FATAL_INT + 10000;

    private static final String MT_TRACE_STR = "trace";
    private static final String MT_DEBUG_STR = "debug";
    private static final String MT_INFO_STR = "info";
    private static final String MT_WARN_STR = "warn";
    private static final String MT_ERROR_STR = "error";
    private static final String MT_FATAL_STR = "fatal";
    private static final String MT_REPORT_STR = "report";

    public static final MtLevel MT_TRACE = new MtLevel(MT_TRACE_INT, MT_TRACE_STR, 7);
    public static final MtLevel MT_DEBUG = new MtLevel(MT_DEBUG_INT, MT_DEBUG_STR, 7);
    public static final MtLevel MT_INFO = new MtLevel(MT_INFO_INT, MT_INFO_STR, 6);
    public static final MtLevel MT_WARN = new MtLevel(MT_WARN_INT, MT_WARN_STR, 4);
    public static final MtLevel MT_ERROR = new MtLevel(MT_ERROR_INT, MT_ERROR_STR, 3);
    public static final MtLevel MT_FATAL = new MtLevel(MT_FATAL_INT, MT_FATAL_STR, 0);
    public static final MtLevel MT_REPORT = new MtLevel(MT_REPORT_INT, MT_REPORT_STR, 0);


    protected MtLevel(int level, String strLevel, int syslogEquiv) {
        super(level, strLevel, syslogEquiv);
    }

    public static Level toLevel(String sArg) {
        return (Level) toLevel(sArg, MtLevel.INFO);
    }

    public static Level toLevel(String sArg, Level defaultValue) {
        if (sArg == null) {
            return defaultValue;
        }
        String stringVal = sArg.toUpperCase();

        if (stringVal.equals(MT_TRACE_STR)) {
            return MtLevel.MT_TRACE;
        } else if (stringVal.equals(MT_DEBUG_STR)) {
            return MtLevel.MT_DEBUG;
        } else if (stringVal.equals(MT_INFO_STR)) {
            return MtLevel.MT_INFO;
        } else if (stringVal.equals(MT_WARN_STR)) {
            return MtLevel.MT_WARN;
        } else if (stringVal.equals(MT_ERROR_STR)) {
            return MtLevel.MT_ERROR;
        } else if (stringVal.equals(MT_FATAL_STR)) {
            return MtLevel.MT_FATAL;
        } else if (stringVal.equals(MT_REPORT_STR)) {
            return MtLevel.MT_REPORT;
        }

        return Level.toLevel(sArg, (Level) defaultValue);
    }

    public static Level toLevel(int i) throws IllegalArgumentException {
        switch (i) {
            case MT_TRACE_INT:
                return MtLevel.MT_TRACE;
            case MT_DEBUG_INT:
                return MtLevel.MT_DEBUG;
            case MT_INFO_INT:
                return MtLevel.MT_INFO;
            case MT_WARN_INT:
                return MtLevel.MT_WARN;
            case MT_ERROR_INT:
                return MtLevel.MT_ERROR;
            case MT_FATAL_INT:
                return MtLevel.MT_FATAL;
            case MT_REPORT_INT:
                return MtLevel.MT_REPORT;
        }

        return Level.toLevel(i);
    }
}
