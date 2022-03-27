package vn.com.project.services.product.model;

import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;

public enum Color {
	Yellow,
	Orange,
	Black,
	Brown,
	Red,
	Pink,
	Green,
	Blue,
	Gray,
	White;
	
	@DAttr(name="name", type = Type.String, id = true, length = 10)
	public String getName() {
		return name();
	}
}
