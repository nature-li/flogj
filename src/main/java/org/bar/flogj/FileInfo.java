package org.bar.flogj;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class FileInfo {
    // date tool
    private SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private Calendar cal = Calendar.getInstance();

    // set once and then never been changed
    private String filePath;
    private long maxLength;

    // dynamic set
    private OutputStream stream;
    private long curLength;
    private Date lastRotate;
    private boolean occurError;
    private boolean closed;
    private boolean needFlush;

    FileInfo(String filePath, long maxLength) {
        this.filePath = filePath;
        this.maxLength = maxLength;

        this.clear();
    }

    void clear() {
        this.stream = null;
        this.curLength = 0;
        this.lastRotate = new Date();
        this.closed = true;
        this.occurError = false;
        this.needFlush = false;
    }

    void reset(OutputStream stream, long curLength, long lastRotate) {
        this.clear();
        this.stream = stream;
        this.curLength = curLength;
        this.lastRotate = new Date(lastRotate);
        this.closed = false;
    }

    boolean open() {
        try {
            if (!this.closed) {
                return true;
            }

            // open file
            File file = new File(this.filePath);

            // open new file
            OutputStream stream = new BufferedOutputStream(new FileOutputStream(file, true));

            // reset
            long curLength = file.length();
            this.reset(stream, curLength, file.lastModified());
        } catch (Exception e) {
            occurError = true;
            Syslog.error(e);
            return false;
        }

        return true;
    }

    void close() {
        try {
            if (stream == null) {
                return;
            }

            if (this.closed) {
                return;
            }

            stream.flush();
            stream.close();

            this.clear();
        } catch (Exception e) {
            occurError = true;
            Syslog.error(e);
        }
    }

    void write(String message) {
        try {
            if (this.stream == null) {
                Syslog.error(this.filePath + " stream is null");
                occurError = true;
                return;
            }

            if (this.closed) {
                occurError = true;
                Syslog.error(this.filePath + " has been closed");
                return;
            }

            // write string to file
            String line = message + "\n";
            byte[] bytes = line.getBytes();
            this.stream.write(bytes);

            // increase length of file
            this.curLength += bytes.length;
            this.needFlush = true;

            // rotate if needed
            if (this.curLength >= this.maxLength) {
                this.rotate();
            }
        } catch (Exception e) {
            this.occurError = true;
            Syslog.error(e);
        }
    }

    void writeFlushRotate(String message) {
        try {
            if (this.stream == null) {
                Syslog.error(this.filePath + " stream is null");
                occurError = true;
                return;
            }

            if (this.closed) {
                occurError = true;
                Syslog.error(this.filePath + " has been closed");
                return;
            }

            // write string to file
            String line = message + "\n";
            byte[] bytes = line.getBytes();
            this.stream.write(bytes);

            // increase length of file
            this.curLength += bytes.length;

            // flush
            this.flush();

            // rotate if needed
            if (this.needRotate()) {
                this.rotate();
            }
        } catch (Exception e) {
            this.occurError = true;
            Syslog.error(e);
        }
    }

    void flush() {
        try {
            if (!needFlush) {
                return;
            }
            this.needFlush = false;

            this.stream.flush();
        } catch (Exception e) {
            this.occurError = true;
            Syslog.error(e);
        }
    }

    private void rename() {
        // old file
        File oldPath = new File(this.filePath);

        // new file
        String now = fmt.format(new Date());
        now += "000";
        File newPath = new File(this.filePath + "." + now);

        // rename old file to new file
        boolean ret = oldPath.renameTo(newPath);
        if (!ret) {
            Syslog.error("rename file[" + oldPath.getAbsolutePath() + "] to [" + newPath.getAbsolutePath() + "] failed");
        }
    }

    private void delete() {
        File oldPath = new File(this.filePath);
        if (!oldPath.delete()) {
            Syslog.error("delete file failed: " + this.filePath);
        }
    }

    void rotate() {
        if (this.curLength > 0) {
            this.close();
            this.rename();
        } else {
            this.close();
            this.delete();
        }

        this.open();
    }

    boolean needRotate() {
        // if file size is big enough
        if (this.curLength >= this.maxLength) {
            return true;
        }

        // if error occurs
        if (this.occurError) {
            return true;
        }

        // if midnight
        Date now = new Date();
        this.cal.setTime(now);
        int today = this.cal.get(Calendar.DAY_OF_MONTH);

        this.cal.setTime(this.lastRotate);
        int when = this.cal.get(Calendar.DAY_OF_MONTH);
        return (today != when);
    }
}
