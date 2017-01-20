package org.bar.flogj;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class MtFilter extends Filter {
    boolean acceptOnMatch = false;
    int levelMin;
    int levelMax;

    @Override public int decide(LoggingEvent event) {
        int inputLevel = event.getLevel().toInt();
        if (inputLevel >= levelMin && inputLevel <= levelMax) {
            return Filter.ACCEPT;
        }
        return Filter.DENY;
    }

    public int getLevelMax() {
        return levelMax;
    }

    public int getLevelMin() {
        return levelMin;
    }

    public boolean getAcceptOnMatch() {
        return acceptOnMatch;
    }

    public void setLevelMax(int levelMax) {
        this.levelMax = levelMax;
    }

    public void setLevelMin(int levelMin) {
        this.levelMin = levelMin;
    }

    public void setAcceptOnMatch(boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }
}
