package org.bar.flogj;


import java.util.ArrayList;
import java.util.List;

class Test implements Runnable {
    static Logger logger = Logger.getLogger();
    private List<Thread> threads = new ArrayList<Thread>();

    public void run() {
        for (int i = 0; i < 10000; i++) {
            logger.trace("trace message");
            logger.debug("debug message");
            logger.info("info message");
            logger.warn("warn message");
            logger.error("error message");
            logger.fatal("fatal message");
            logger.report("report message");
        }
    }

    public void start() {
        logger.init(Env.develop, "logs", "test", false);

        for (int i = 0; i < 4; i++) {
            this.threads.add(new Thread(this));
        }

        for (int i = 0; i < 4; i++) {
            this.threads.get(i).start();
        }
    }

    public void stop() {
        try {
            for (int i = 0; i < 4; i++) {
                this.threads.get(i).join();
            }

            logger.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Main {
    static public void main(String[] args) throws Exception {
        Test test = new Test();
        test.start();
        test.stop();
    }
}
