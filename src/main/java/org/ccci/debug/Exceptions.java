package org.ccci.debug;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Overriding default exception handling to give me some better logging.
 * 
 * @author Matthew.Drees
 * 
 */
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(classDependencies = "javax.faces.context.FacesContext")
@Name("org.jboss.seam.exception.exceptions")
public class Exceptions extends org.jboss.seam.exception.Exceptions {

	private static final LogProvider log = Logging
			.getLogProvider(Exceptions.class);

	@Override
	public void handle(Exception e) throws Exception {
		log.debug("Handling Exception " + e); 

		try {
		    ExceptionContext.getCurrentInstance().recordHandledException(e, "seam exception handler");
	        RecordedExceptions.instance().recordHandledException(e);
			ConversationEndedObserver observer = (ConversationEndedObserver) Component.getInstance(ConversationEndedObserver.class);
			observer.beginExceptionRecovery();
		    super.handle(e);
		} catch (Exception unhandledException) {
			log.error("Unhandled Exception", unhandledException);
			//TODO: is swallowing the right thing?
		}
	}


}
