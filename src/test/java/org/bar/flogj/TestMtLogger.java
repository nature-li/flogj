package org.bar.flogj;

import org.junit.Test;

public class TestMtLogger {
    private static MtLogger logger = new MtLogger(TestMtLogger.class, MtEnv.develop);

    @Test
    public void testTrace() {
        logger.trace("this is a trace message");
        logger.trace("a_pvid","this is a trace message");
        logger.trace("a_pvid", MtKeyWord.business, "this is a trace message");
    }

    @Test
    public void testDebug() {
        logger.debug("this is a debug message");
        logger.debug("a_pvid","this is a debug message");
        logger.debug("a_pvid", MtKeyWord.business, "this is a debug message");
    }
    @Test
    public void testInfo() {
        logger.info("this is a info message");
        logger.info("a_pvid","this is a info message");
        logger.info("a_pvid", MtKeyWord.business, "this is a info message");
    }
    @Test
    public void testWarn() {
        logger.warn("this is a warn message");
        logger.warn("a_pvid","this is a warn message");
        logger.warn("a_pvid", MtKeyWord.business, "this is a warn message");
    }
    @Test
    public void testError() {
        logger.error("this is a error message");
        logger.error("a_pvid","this is a error message");
        logger.error("a_pvid", MtKeyWord.business, "this is a error message");
    }
    @Test
    public void testFatal() {
        logger.fatal("this is a fatal message");
        logger.fatal("a_pvid","this is a fatal message");
        logger.fatal("a_pvid", MtKeyWord.business, "this is a fatal message");
    }
    @Test
    public void testReport() {
        logger.report("this is a report message");
        logger.report("a_pvid","this is a report message");
        logger.report("a_pvid", MtKeyWord.business, "this is a report message");
    }

}
