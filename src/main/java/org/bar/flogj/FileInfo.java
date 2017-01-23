package org.bar.flogj;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

public class FileInfo {
    private String filePath;
    private OutputStream stream;
    private long length;
    private Date date = null;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public OutputStream getStream() {
        return stream;
    }

    public long getLength() {
        return length;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setStream(OutputStream stream) {
        this.stream = stream;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void addLength(long length) {
        this.length += length;
    }

    boolean write(String message) {
        try {
            String line = message + "\n";
            if (this.stream != null) {
                byte[] bytes = line.getBytes();
                this.stream.write(bytes);
                this.length += bytes.length;
            } else {
                System.out.println(this.filePath + " stream is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * close file
     * @return
     */
    boolean close() {
        if (stream != null) {
            try {
                stream.flush();
                stream.close();
                stream = null;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
     * close rename and create file
     * @return
     */
    boolean reopen() {
        try {
            // open file and save file info
            File file = new File(this.filePath);
            this.stream = new BufferedOutputStream(new FileOutputStream(file, true));
            this.length = file.length();
            this.date = new Date();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    void flush() {
        try {
            this.stream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    FileInfo(String filePath, OutputStream stream, long length) {
        this.date = new Date();
        this.filePath = filePath;
        this.stream = stream;
        this.length = length;
    }
}
