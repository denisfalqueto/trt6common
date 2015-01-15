package br.jus.trt.lib.common_tests.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

/**
 * Producer de um {@link Logger} para utilização em ambiente de testes.
 *
 * Serão produzidos vários tipos de loggers, mas todos são direcionados para o
 * Log4j2.
 *
 * @author augusto
 *
 */
public class LoggerProducer {

    private Class<?> getInjectionClass(InjectionPoint ip) {
        return ip.getMember().getDeclaringClass();
    }

    @Produces
    @ForTest
    public Logger createLog4j2Logger(InjectionPoint caller) {
    	try {
    		Class<?> injectionClass = getInjectionClass(caller);
//			Logger logger = LogManager.getFormatterLogger(injectionClass);
//    		return logger;
    		
    		return new Logger() {
				
				@Override
				public void warn(Marker marker, String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Marker marker, String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Marker marker, Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Marker marker, Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Marker marker, String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Marker marker, Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Marker marker, Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void warn(Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Marker marker, String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Marker marker, String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Marker marker, Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Marker marker, Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Marker marker, String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Marker marker, Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Marker marker, Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void trace(Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public <T extends Throwable> T throwing(Level level, T t) {
					// TODO Auto-generated method stub
					return t;
				}
				
				@Override
				public <T extends Throwable> T throwing(T t) {
					// TODO Auto-generated method stub
					return t;
				}
				
				@Override
				public void printf(Level level, Marker marker, String format,
						Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void printf(Level level, String format, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Marker marker, String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Marker marker, String message,
						Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Marker marker, Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Marker marker, Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Marker marker, String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Marker marker, Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Marker marker, Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void log(Level level, Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isWarnEnabled(Marker marker) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isWarnEnabled() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isTraceEnabled(Marker marker) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isTraceEnabled() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isInfoEnabled(Marker marker) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isInfoEnabled() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isFatalEnabled(Marker marker) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isFatalEnabled() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isErrorEnabled(Marker marker) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isErrorEnabled() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isEnabled(Level level, Marker marker) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isEnabled(Level level) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isDebugEnabled(Marker marker) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isDebugEnabled() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void info(Marker marker, String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Marker marker, String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Marker marker, Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Marker marker, Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Marker marker, String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Marker marker, Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Marker marker, Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void info(Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public String getName() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public MessageFactory getMessageFactory() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Level getLevel() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public void fatal(Marker marker, String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Marker marker, String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Marker marker, Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Marker marker, Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Marker marker, String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Marker marker, Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Marker marker, Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void fatal(Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public <R> R exit(R result) {
					// TODO Auto-generated method stub
					return result;
				}
				
				@Override
				public void exit() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Marker marker, String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Marker marker, String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Marker marker, Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Marker marker, Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Marker marker, String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Marker marker, Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Marker marker, Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void error(Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void entry(Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void entry() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Marker marker, String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Marker marker, String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Marker marker, Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Marker marker, Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(String message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(String message, Object... params) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Object message, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Message msg, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Marker marker, String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Marker marker, Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Marker marker, Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(String message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Object message) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void debug(Message msg) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void catching(Level level, Throwable t) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void catching(Throwable t) {
					// TODO Auto-generated method stub
					
				}
			};
    	} catch (Throwable e) {
    		e.printStackTrace(); 
    		throw e;
    	} finally {
    		System.out.println();
    	}
    }

}
