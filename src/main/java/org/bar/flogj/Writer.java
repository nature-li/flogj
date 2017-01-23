package org.bar.flogj;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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

    // tool for calc day of month
    private Calendar cal = Calendar.getInstance();

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

            // open the file
            File file = new File(filePath);
            OutputStream stream = new BufferedOutputStream(new FileOutputStream(file, true));

            // save file info
            FileInfo fileInfo = new FileInfo(filePath, stream, file.length());
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
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
                directory.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // init process stream
        try {
            FileInfo fileInfo = this.createFile(this.processTail);
            if (fileInfo == null) {
                System.out.println("create process log file failed");
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
            e.printStackTrace();
            return false;
        }

        // init report stream
        try {
            FileInfo fileInfo = this.createFile(this.reportTail);
            if (fileInfo == null) {
                System.out.println("create process log file failed");
                return false;
            }

            // save file info
            levelFile.put(Level.report, fileInfo);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    /**
     * write data queue to files
     *
     * @param queue
     * @return
     */
    boolean write(Queue<Record> queue) {
        while (!queue.isEmpty()) {
            // poll a message
            Record record = queue.poll();

            // get file info
            FileInfo fileInfo = this.levelFile.get(record.getLevel());
            if (fileInfo == null) {
                System.out.println(record.getLevel() + " fileInfo is null");
                continue;
            }

            // write message
            fileInfo.write(record.getMsg());
        }

        // rolling file
        HashSet<FileInfo> fileSet = new HashSet<FileInfo>();
        for (FileInfo fileInfo : this.levelFile.values()) {
            fileSet.add(fileInfo);
        }

        // get day of today
        Date now = new Date();
        this.cal.setTime(now);
        int today = this.cal.get(Calendar.DAY_OF_MONTH);
        for (FileInfo fileInfo : fileSet) {
            // get file create day
            this.cal.setTime(fileInfo.getDate());
            int when = this.cal.get(Calendar.DAY_OF_MONTH);

            // if when != today rolling file
            boolean rolling = false;
            if (when != today && fileInfo.getLength() > 0) {
                rolling = true;
            }

            // if file size is big enough rolling file
            if (fileInfo.getLength() > this.maxFileSize) {
                rolling = true;
            }

            // roll file
            if (rolling) {
                // close writing file
                fileInfo.close();

                // rename file
                rename(fileInfo);

                // reopen file
                fileInfo.reopen();
            } else {
                fileInfo.flush();
            }
        }
        return true;
    }

    /**
     * rename file
     *
     * @param fileInfo
     */
    private void rename(FileInfo fileInfo) {
        File oldPath = new File(fileInfo.getFilePath());
        String now = format.format(new Date());
        File newPath = new File(fileInfo.getFilePath() + "." + now);
        oldPath.renameTo(newPath);
    }

    /**
     * clean redundant files
     */
    private void cleanFiles() {
        try {
            // list all process and report rolled files
            File folder = new File(this.target);
            File[] files = folder.listFiles();

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
                    e.printStackTrace();
                }
            }

            // remove redundant files
            this.remove(processList);
            this.remove(reportList);
        } catch (Exception e) {
            e.printStackTrace();
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
                        e.printStackTrace();
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

        try {
            Date when = format.parse(tail);
            String str = format.format(when);
            if (tail.equals(str)) {
                return true;
            }
        } catch (ParseException e) {
            //            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
