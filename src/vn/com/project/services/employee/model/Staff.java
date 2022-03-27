package vn.com.project.services.employee.model;

import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import vn.com.project.services.sale.model.ProductShelf;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;

@DClass(schema="project")
public class Staff extends Employee {
	
	@DAttr(name = "manager", type = Type.Domain, length = 30, optional = false)
	@DAssoc(ascName = "manager-has-staffs", role = "staffs", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Manager.class, cardMin = 1, cardMax = 1))
	private Manager manager;
	
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public Staff(@AttrRef("name") String name, 
			@AttrRef("gender") Gender gender, 
			@AttrRef("dob") Date dob, 
			@AttrRef("address") Address address, 
			@AttrRef("specificAddress") String specificAddress, 
			@AttrRef("email") String email, 
			@AttrRef("phone") String phone, 
			@AttrRef("department") Department department,
			@AttrRef("manager") Manager manager) {
		this(null, name, gender, dob, address, specificAddress, email, phone, department, manager);
	}
	
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Staff(String id, String name, Gender gender, Date dob, Address address, String specificAddress, String email, String phone, Department department, Manager manager) 
	throws ConstraintViolationException {
		super(id, name, gender, dob, address, specificAddress, email, phone, department);
		this.manager = manager;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

}
