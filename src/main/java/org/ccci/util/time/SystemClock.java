package org.ccci.util.time;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.joda.time.DateTime;

@Name("clock")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class SystemClock extends Clock
{

	@Override
	public DateTime currentDateTime() {
		return new DateTime();
	}

}
