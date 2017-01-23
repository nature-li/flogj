package org.bar.flogj;

class Record {
    private Level level;
    private String msg;

    Record(Level level, String msg) {
        this.level = level;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
