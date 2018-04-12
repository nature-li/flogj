package org.bar.flogj;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.io.PrintWriter;
import java.io.StringWriter;

class Syslog {
    interface CLibrary extends Library {
        void openlog(final Memory ident, int option, int facility);
        void syslog(int priority, final String format, final String message);
        void closelog();
    }

    static private long lastRecordTime = 0;
    static private int counterPerHour = 0;
    static private CLibrary instance = null;
    static private Memory ident = null;

    static boolean init() {
        try {
            boolean syslogExist = false;
            if (Platform.isLinux() || Platform.isMac() || Platform.isFreeBSD() || Platform.isOpenBSD() || Platform.isSolaris()) {
                syslogExist = true;
            }

            if (!syslogExist) {
                return false;
            }

            instance = (CLibrary) Native.loadLibrary(Platform.isWindows() ? "msvcrt" : "c", CLibrary.class);

            if (ident == null) {
                ident = new Memory(128);
                ident.clear();
                ident.setString(0, "mt_log", false);
            }

            final int LOG_PID = 0x01;
            final int LOG_CONS = 0x02;
            final int LOG_NDELAY = 0x08;
            final int LOG_PERROR = 0x20;
            final int LOG_USER = (1 << 3);

            instance.openlog(ident, LOG_PID | LOG_CONS | LOG_NDELAY | LOG_PERROR, LOG_USER);

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    static void close() {
        if (instance != null) {
            instance.closelog();
        }
    }

    static boolean error(String msg) {
        try {
            if (!shouldLog()) {
                return false;
            }

            final int LOG_ERR = 3;
            instance.syslog(LOG_ERR, "%s", msg);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    static boolean error(Exception ex) {
        try {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            String msg = errors.toString();
            error(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static private boolean shouldLog() {
        final int maxRecordOneHour = 10;
        final int milSecsInOneHour = 3600 * 1000;

        // get now time
        long now = System.currentTimeMillis();

        // reset counter every one hour
        if (now - lastRecordTime >= milSecsInOneHour) {
            lastRecordTime = now;
            counterPerHour = 0;
        }

        // write only if counter < 10 in one hour
        if (counterPerHour >= maxRecordOneHour) {
            return false;
        }

        counterPerHour++;
        return true;
    }
}
