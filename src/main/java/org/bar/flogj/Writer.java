package org.bar.flogj;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Writer implements Runnable {
    // collector thread and it's lock
    private Thread thread = new Thread(this);
    private Lock lock = new ReentrantLock();
    private Condition quit = lock.newCondition();
    private int flag = 0;

    // log store directory、file_name、file_size、 file_count and file tail
    private String target = null;
    private String fileName = null;
    private long maxFileSize = 0;
    private long maxFileCount = 0;
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private String processTail = ".process.log";
    private String reportTail = ".report.log";

    // being written files
    private HashMap<Level, FileInfo> levelFile = new HashMap<Level, FileInfo>();


    Writer(String target, String fileName, long maxFileSize, long maxFileCount) {
        this.target = target;
        this.fileName = fileName;
        this.maxFileSize = maxFileSize;
        this.maxFileCount = maxFileCount;
    }

    FileInfo createFile(String tail) {
        try {
            // calc file full path
            Path path = Paths.get(target, fileName + tail);
            String filePath = path.toString();

            // open new file
            FileInfo fileInfo = new FileInfo(filePath, this.maxFileSize);
            if (!fileInfo.open()) {
                return null;
            }

            return fileInfo;
        } catch (Exception e) {
            Syslog.error(e);
            return null;
        }
    }

    /**
     * init file stream and start rolling thread
     */
    boolean start() {
        // make sure target exists
        try {
            File directory = new File(this.target);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    Syslog.error("mkdirs failed: " + this.target);
                    return false;
                }
            }
        } catch (Exception e) {
            Syslog.error(e);
            return false;
        }

        // init process stream
        try {
            FileInfo fileInfo = this.createFile(this.processTail);
            if (fileInfo == null) {
                Syslog.error("create process log file failed");
                return false;
            }

            // save file info
            levelFile.put(Level.trace, fileInfo);
            levelFile.put(Level.debug, fileInfo);
            levelFile.put(Level.info, fileInfo);
            levelFile.put(Level.warn, fileInfo);
            levelFile.put(Level.error, fileInfo);
            levelFile.put(Level.fatal, fileInfo);
        } catch (Exception e) {
            Syslog.error(e);
            return false;
        }

        // init report stream
        try {
            FileInfo fileInfo = this.createFile(this.reportTail);
            if (fileInfo == null) {
                Syslog.error("create report log file failed");
                return false;
            }

            // save file info
            levelFile.put(Level.report, fileInfo);
        } catch (Exception e) {
            Syslog.error(e);
            return false;
        }

        // start rolling thread
        this.thread.start();
        return true;
    }

    /**
     * stop rolling thread
     */
    void stop() {
        try {
            // set stop flag
            try {
                this.lock.lock();
                this.flag = 1;
                this.quit.signal();
            } finally {
                this.lock.unlock();
            }

            // flush all writing log file
            for (FileInfo fileInfo : this.levelFile.values()) {
                fileInfo.close();
            }

            // wait collecting thread to wait
            this.thread.join();
        } catch (Exception e) {
            Syslog.error(e);
        }
    }

    /**
     * write data queue to files
     *
     * @param queue
     * @return
     */
    boolean write(Queue<Record> queue) {
        boolean empty = queue.isEmpty();

        while (!queue.isEmpty()) {
            // poll a message
            Record record = queue.poll();

            // get file info
            FileInfo fileInfo = this.levelFile.get(record.getLevel());
            if (fileInfo == null) {
                Syslog.error(record.getLevel() + " fileInfo is null");
                continue;
            }

            // write message
            fileInfo.write(record.getMsg());
        }

        // flush file one by one
        if (!empty) {
            for (FileInfo fileInfo : this.levelFile.values()) {
                fileInfo.flush();
            }
        }
        return true;
    }

    boolean writeFlushRotate(Record record) {
        // get file info
        FileInfo fileInfo = this.levelFile.get(record.getLevel());
        if (fileInfo == null) {
            Syslog.error(record.getLevel() + " fileInfo is null");
            return false;
        }

        // write and flush
        fileInfo.writeFlushRotate(record.getMsg());
        return true;
    }

    void rotate() {
        // rotate file one by one
        for (FileInfo fileInfo : this.levelFile.values()) {
            if (fileInfo.needRotate()) {
                fileInfo.rotate();
            }
        }
    }

    /**
     * clean redundant files
     */
    private void cleanFiles() {
        try {
            // list all process and report rolled files
            File folder = new File(this.target);
            File[] files = folder.listFiles();
            if (files == null) {
                return;
            }

            // assign different files to different list
            List<String> processList = new ArrayList<String>();
            List<String> reportList = new ArrayList<String>();
            String processHeader = this.fileName + this.processTail;
            String reportHeader = this.fileName + this.reportTail;
            for (File file : files) {
                try {
                    String name = file.getName();
                    if (!this.checkDate(name)) {
                        continue;
                    }

                    if (name.startsWith(processHeader)) {
                        processList.add(file.toString());
                    } else if (name.startsWith(reportHeader)) {
                        reportList.add(file.toString());
                    }
                } catch (Exception e) {
                    Syslog.error(e);
                }
            }

            // remove redundant files
            this.remove(processList);
            this.remove(reportList);
        } catch (Exception e) {
            Syslog.error(e);
        }
    }

    /**
     * collecting thread function
     */
    public void run() {
        // do NOT need to delete redundant files
        if (this.maxFileSize < 0) {
            return;
        }

        // unset quit flag
        try {
            this.lock.lock();
            this.flag = 0;
        } finally {
            this.lock.unlock();
        }

        while (true) {
            // clean redundant files
            this.cleanFiles();

            // sleep 5 seconds until quit flag is set
            try {
                this.lock.lock();
                if (this.flag != 1) {
                    try {
                        this.quit.await(5, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        Syslog.error(e);
                    }
                }

                // if quit flag is set quit while loop.
                if (this.flag == 1) {
                    break;
                }
            } finally {
                this.lock.unlock();
            }
        }

        this.cleanFiles();
    }

    /**
     * check test.process.20170123162100123 tail
     *
     * @param name
     * @return boolean
     */
    private boolean checkDate(String name) {
        String[] sections = name.split("\\.");
        if (sections.length < 1) {
            return false;
        }
        String tail = sections[sections.length - 1];
        if (tail.length() < 3) {
            return false;
        }
        tail = tail.substring(0, tail.length() - 3);

        if (tail.length() != 17) {
            return false;
        }

        try {
            Date when = format.parse(tail);
            String str = format.format(when);
            if (tail.equals(str)) {
                return true;
            }
        } catch (ParseException e) {
            Syslog.error(e);
        } catch (Exception e) {
            Syslog.error(e);
        }

        return false;
    }

    /**
     * remove redundant files
     *
     * @param files
     */
    private void remove(List<String> files) {
        if (files.size() > this.maxFileCount) {
            Collections.sort(files);
            while (files.size() > 0 && files.size() > this.maxFileCount) {
                try {
                    String fullPath = files.get(0);
                    files.remove(0);
                    File file = new File(fullPath);
                    if (!file.delete()) {
                        Syslog.error("delete file failed: " + fullPath);
                    }
                } catch (Exception e) {
                    Syslog.error(e);
                }
            }
        }
    }
}
