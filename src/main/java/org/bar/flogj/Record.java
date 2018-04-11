package org.bar.flogj;

class Record {
    private Level level;
    private String msg;

    Record(Level level, String msg) {
        this.level = level;
        this.msg = msg;
    }

    String getMsg() {
        return msg;
    }

    void setMsg(String msg) {
        this.msg = msg;
    }

    Level getLevel() {
        return level;
    }

    void setLevel(Level level) {
        this.level = level;
    }
}
