package de.ingrid.mdek.quartz.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Base class for background job tests that uses a custom log appender.
 */
public abstract class BaseJobTest {

    private TestAppender testAppender;

    @Plugin(
        name = "TestAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
    public static class TestAppender extends AbstractAppender {

        private final List<LogEvent> events = new CopyOnWriteArrayList<>();

        protected TestAppender(final String name, final Filter filter) {
            super(name, filter, null);
        }

        @PluginFactory
        public static TestAppender createAppender(
                @PluginAttribute("name") final String name,
                @PluginElement("Filter") final Filter filter) {
            return new TestAppender(name, filter);
        }

        @Override
        public void append(final LogEvent event) {
            events.add(event);
        }

        public boolean hasIssues() {
            return this.getEvents(Level.FATAL).size() > 0 || this.getEvents(Level.ERROR).size() > 0 || this.getEvents(Level.WARN).size() > 0;
        }

        public List<LogEvent> getEvents(final Level level) {
            return events.stream().filter(e -> e.getLevel().equals(level)).collect(Collectors.toList());
        }

        public void clear() {
            events.clear();
        }
    }

    /**
     * Setup method *must* be called before each test.
     * @throws Exception
     */
    public void setUp() throws Exception {
        // setup logging
        final Logger jobLogger = LogManager.getLogger(getJobClassUnderTest());
        final LoggerContext lc = (LoggerContext) LogManager.getContext(false);
        lc.reconfigure();
        final Configuration configuration = lc.getConfiguration();
        if (!configuration.getAppenders().containsKey("test")) {
            testAppender = TestAppender.createAppender("test", null);
            testAppender.start();
            final Appender consoleAppender = ConsoleAppender.createDefaultAppenderForLayout(
                    PatternLayout.newBuilder().withPattern("%d{HH:mm:ss.SSS} %-5level %c{-4} - %msg%n").build());
            consoleAppender.start();
            configuration.addAppender(testAppender);
            configuration.addAppender(consoleAppender);
            configuration.addLoggerAppender(lc.getLogger(jobLogger.getName()), testAppender);
            configuration.addLoggerAppender(lc.getLogger(jobLogger.getName()), consoleAppender);
            configuration.getLoggerConfig(jobLogger.getName()).setLevel(Level.DEBUG);
            lc.updateLoggers();
        }

        // check logging setup
        assertTrue(jobLogger.isDebugEnabled());
        jobLogger.debug("Test log entry");
        assertEquals(1, testAppender.getEvents(Level.DEBUG).size());
        testAppender.clear();
    }

    /**
     * Tear down method *must* be called after each test.
     * @throws Exception
     */
    public void tearDown() throws Exception {
        if (testAppender != null) {
            testAppender.clear();
        }
    }

    /**
     * Get the test appender
     * @return TestAppender
     */
    protected TestAppender getTestAppender() {
        return testAppender;
    }

    /**
     * Get the job class that is being tested
     * @return Class<T>
     */
    protected abstract Class<?> getJobClassUnderTest();
}