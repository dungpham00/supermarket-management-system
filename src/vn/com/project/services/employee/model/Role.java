package vn.com.project.services.employee.model;

import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;

public enum Role {
	Staff,
	Manager,
	//Others
	;

	@DAttr(name="name", type = Type.String, id = true, length = 10)
	public String getName() {
		return name();
	}
}
