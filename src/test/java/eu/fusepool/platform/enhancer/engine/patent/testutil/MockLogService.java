/**
 * 
 */
package eu.fusepool.platform.enhancer.engine.patent.testutil;

import org.apache.log4j.Logger;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * @author giorgio
 *
 */
public class MockLogService implements LogService {

	
	private static Logger log = Logger.getLogger(MockLogService.class) ;
	
	/* (non-Javadoc)
	 * @see org.osgi.service.log.LogService#log(int, java.lang.String)
	 */
	@Override
	public void log(int level, String message) {
		switch (level) {
		case LogService.LOG_DEBUG:
			log.debug(message) ;
			break ;
		case LogService.LOG_WARNING:
			log.warn(message) ;
			break;
		case LogService.LOG_INFO:
			log.info(message) ;
			break;
		case LogService.LOG_ERROR:
			log.error(message) ;
			break;
		default:
			break;
		} 

	}

	/* (non-Javadoc)
	 * @see org.osgi.service.log.LogService#log(int, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(int level, String message, Throwable exception) {
		switch (level) {
		case LogService.LOG_DEBUG:
			log.debug(message, exception) ;
			break ;
		case LogService.LOG_WARNING:
			log.warn(message, exception) ;
			break;
		case LogService.LOG_INFO:
			log.info(message, exception) ;
			break;
		case LogService.LOG_ERROR:
			log.error(message, exception) ;
			break;
		default:
			break;
		} 

	}

	/* (non-Javadoc)
	 * @see org.osgi.service.log.LogService#log(org.osgi.framework.ServiceReference, int, java.lang.String)
	 */
	@Override
	public void log(ServiceReference sr, int level, String message) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.osgi.service.log.LogService#log(org.osgi.framework.ServiceReference, int, java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(ServiceReference sr, int level, String message,
			Throwable exception) {
		// TODO Auto-generated method stub

	}

}
