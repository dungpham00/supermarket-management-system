package vn.com.project.services.employee.model;

import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;

public enum Month {
	January,
	February,
	March,
	April,
	May,
	June,
	July,
	August,
	September,
	October,
	November,
	December;
	
	@DAttr(name="name", type = Type.String, id = true, length = 10)
	public String getName() {
		return name();
	}
}
