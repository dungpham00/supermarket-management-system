package vn.com.project.services.employee.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;

@DClass(schema="project")
public class Manager extends Employee {
	
	@DAttr(name="description", type = Type.String, length = 50, optional = false)
	private String description;

	@DAttr(name="staffs",type=Type.Collection,optional = false,
		      serialisable=false,filter=@Select(clazz=Staff.class))
		  @DAssoc(ascName="manager-has-staffs",role="manager",
		      ascType=AssocType.One2Many,endType=AssocEndType.One,
		    associate=@Associate(type=Staff.class,cardMin=0,cardMax=30))
	private Collection<Staff> staffs;
	
	 // derived
	  private int sCount;
	
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public Manager(@AttrRef("name") String name, 
			@AttrRef("gender") Gender gender, 
			@AttrRef("dob") Date dob, 
			@AttrRef("address") Address address, 
			@AttrRef("specificAddress") String specificAddress, 
			@AttrRef("email") String email, 
			@AttrRef("phone") String phone, 
			@AttrRef("department") Department department,
			@AttrRef("description") String description) {
		this(null, name, gender, dob, address, specificAddress, email, phone, department, description);
	}
	
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Manager(String id, String name, Gender gender, Date dob, Address address, String specificAddress, String email, String phone, Department department, String description) 
	throws ConstraintViolationException {
		super(id, name, gender, dob, address, specificAddress, email, phone, department);
		this.description = description;
		
		staffs = new ArrayList<>();
		sCount = 0;
	}
	
	//setter method 
	 public void setDescription(String description) {
	   this.description = description;
	 }
	 
	 // getter method
	 public String getDescription() {
	   return description;
	 }
	
	public Collection<Staff> getStaffs() {
		return staffs;
	}
	
	@DOpt(type=DOpt.Type.LinkAdder)
	  public boolean addStaff(Collection<Staff> staffs) {
	    // do nothing
	    return false;
	  }
	  
	  
	  @DOpt(type=DOpt.Type.LinkAdder)
	  public boolean addStaff(Staff e) {
		  if (!staffs.contains(e)) {
			  staffs.add(e);
		  }
		  return false;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkAdderNew)
	  public boolean addNewStaff(Staff e) {
		  staffs.add(e);
		  
		  sCount++;
		  
		  return false;
	  }
	  

	  
	  @DOpt(type=DOpt.Type.LinkAdderNew)
	  public boolean addNewStaff(Collection<Staff> ems) {
		  staffs.addAll(ems);
		  sCount+=ems.size();
		  return false;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkRemover)
	  public boolean removeStaff(Staff e) {
		  boolean removed = staffs.remove(e);
		  
		  if (removed) {
			  sCount--;
		  }
		  return false;
	  }
	  
	  public void setStaff(Collection<Staff> em) {
		  this.staffs = em;
		  sCount = em.size();
	  }
	  
	  
	  @DOpt(type=DOpt.Type.LinkCountGetter)
	  public Integer getSCount() {
		  return sCount;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkCountSetter)
	  public void setSCount( int count) {
		  sCount = count;
	  }
}

