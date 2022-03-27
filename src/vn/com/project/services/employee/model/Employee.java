package vn.com.project.services.employee.model;

import java.util.Calendar;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Format;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.util.Tuple;
import vn.com.project.services.employee.model.Address;
import vn.com.project.services.employee.model.Gender;
import vn.com.project.services.employee.reports.EmployeeByAddressJoinReport;
import vn.com.project.services.employee.reports.EmployeeByDepartmentJoinReport;
import vn.com.project.services.employee.model.Employee;
import vn.com.project.exceptions.DExCode;
import vn.com.project.utils.DToolkit;

@DClass(schema="project")
public abstract class Employee {
	public static final String E_name = "name";
	public static final String E_gender = "gender";
	public static final String E_id = "id";
	public static final String E_dob = "dob";
	public static final String E_address = "address";
	public static final String E_specificAddress = "specificAddress";
	public static final String E_email = "email";
	public static final String E_phone = "phone";
	public static final String E_role = "role";
	public static final String E_department = "department";
	public static final String E_rptEmployeeByAddress = "rptEmployeeByAddress";
	public static final String E_rptEmployeeByDepartment = "rptEmployeeByDepartment";

	
	//attributes of employees
	@DAttr(name = E_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	//static variable to keep track of employee id
	private static int idCounter = 0;
	
	@DAttr(name = E_name, type = Type.String, length = 25, optional = false, cid = true)
	private String name;
	
	@DAttr(name = E_gender, type = Type.Domain, length = 10, optional = false)
	private Gender gender;
	
	@DAttr(name = E_dob, type = Type.Date, length = 15, optional = false, format=Format.Date)
	private Date dob;
	
	@DAttr(name = E_address, type = Type.Domain, length = 20, optional = false)
	@DAssoc(ascName="address-has-employees", role = "employee", ascType = AssocType.One2Many, endType = AssocEndType.Many,
	associate = @Associate(type=Address.class, cardMin=1, cardMax=1))
	private Address address;
	
	@DAttr(name = E_specificAddress, type = Type.String, length = 30, optional= false)
	private String specificAddress;
	
	@DAttr(name = E_email, type = Type.String, length = 25, optional = false)
	private String email;
	
	@DAttr(name = E_phone, type = Type.String, length = 11, optional = false)
	private String phone;
	
	@DAttr(name = E_department, type = Type.Domain, length = 30, optional = false)
	@DAssoc(ascName="department-has-employees", role = "employee", ascType = AssocType.One2Many, endType = AssocEndType.Many,
	associate = @Associate(type=Department.class, cardMin=1, cardMax=1))
	private Department department;
	
	// v5.0: to realise link to report
	  @DAttr(name=E_rptEmployeeByAddress,type=Type.Domain, serialisable=false, 
	      // IMPORTANT: set virtual=true to exclude this attribute from the object state
	      // (avoiding the view having to load this attribute's value from data source)
	      virtual=true)
	  private EmployeeByAddressJoinReport rptEmployeeByAddress;
	  
	// v5.0: to realise link to report
		  @DAttr(name=E_rptEmployeeByDepartment,type=Type.Domain, serialisable=false, 
		      // IMPORTANT: set virtual=true to exclude this attribute from the object state
		      // (avoiding the view having to load this attribute's value from data source)
		      virtual=true)
		  private EmployeeByDepartmentJoinReport rptEmployeeByDepartment;
	
	// constructor methods
	// for creating in the application
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public Employee(@AttrRef("name") String name,
			@AttrRef("gender") Gender gender,
			@AttrRef("dob") Date dob,
			@AttrRef("address") Address address,
			@AttrRef("specificAddress")  String specificAddress,
			@AttrRef("email") String email,
			@AttrRef("phone") String phone,
			@AttrRef("department") Department department) {
		this(null, name, gender, dob, address, specificAddress, email, phone, department);
	}
	
	// a shared constructor that is invoked by other constructors
	  @DOpt(type=DOpt.Type.DataSourceConstructor)
	  public Employee(@AttrRef("id") String id, 
	      @AttrRef("name") String name, @AttrRef("gender") Gender gender,
	      @AttrRef("dob") Date dob, @AttrRef("address") Address address,
	      @AttrRef("specificAddress")  String specificAddress,
	      @AttrRef("email") String email,
	      @AttrRef("phone") String phone,
	      @AttrRef("department") Department department) 
	  throws ConstraintViolationException {
	    // generate an id
	    this.id = nextID(id);

	    // assign other values
	    this.name = name;
	    this.gender = gender;
	    this.dob = dob;
	    this.address = address;
	    this.specificAddress = specificAddress;
	    this.email = email;
	    this.phone = phone;
	    this.department = department;
	  }
	  
	// setter methods
	  public void setName(String name) {
	    this.name = name;
	  }

	  public void setDob(Date dob) throws ConstraintViolationException {
	    // additional validation on dob
	    if (dob.before(DToolkit.MIN_DOB)) {
	      throw new ConstraintViolationException(DExCode.INVALID_DOB, dob);
	    }
	    
	    this.dob = dob;
	  }

	  public void setGender(Gender gender) {
	    this.gender = gender;
	  }
	  
	  public void setAddress(Address address) {
	    this.address = address;
	  }
	  

	  // v2.7.3
	  public void setNewAddress(Address address) {
	    // change this invocation if need to perform other tasks (e.g. updating value of a derived attribtes)
	    setAddress(address);
	  }
	  
	  public void setSpecificAddress(String specificAddress) {
		    this.specificAddress = specificAddress;
	  }
	  
	  public void setEmail(String email) throws ConstraintViolationException {
	    if (email.indexOf("@") < 0) {
	      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
	          new Object[] {"'" + email + "' (does not have '@') "});
	    }
	    this.email = email;
	  }
	  
	  
	  public void setPhone(String phone) {
		    this.phone = phone;
	  }
	  
	  public void setDepartment(Department department) {
		    this.department = department;
		  }
	  
//	  public void setRole(Role role) {
//		    this.role = role;
//	  }

		  // v2.7.3
		  public void setNewDepartment(Department department) {
		    // change this invocation if need to perform other tasks (e.g. updating value of a derived attribtes)
		    setDepartment(department);
		  }
	
	// getter methods
	  public String getId() {
	    return id;
	  }

	  public String getName() {
	    return name;
	  }

	  public Gender getGender() {
	    return gender;
	  }
	  
	  public Date getDob() {
	    return dob;
	  }

	  public Address getAddress() {
	    return address;
	  }
	  
	  public String getSpecificAddress() {
		    return specificAddress;
		  }

	  public String getEmail() {
	    return email;
	  }
	  
	  public String getPhone() {
		    return phone;
		  }

		  public Department getDepartment() {
		    return department;
		  }
		  
//		  public Role getRole() {
//			    return role;
//			  }
	  
	  // override toString
	  /**
	   * @effects returns <code>this.id</code>
	   */
	  @Override
	  public String toString() {
	    return toString(true);
	  }

	  /**
	   * @effects returns <code>Student(id,name,dob,address,email)</code>.
	   */
	  public String toString(boolean full) {
	    if (full)
	      return "Employee(" + id + "," + name + "," + gender + ", " + dob + "," + address + "," + specificAddress + ","
	          + email + "," + phone + ", " + department + ", " +  ")";
	    else
	      return "Employee(" + id + ")";
	  }
	  
	  @Override
	  public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    return result;
	  }

	  @Override
	  public boolean equals(Object obj) {
	    if (this == obj)
	      return true;
	    if (obj == null)
	      return false;
	    if (getClass() != obj.getClass())
	      return false;
	    Employee other = (Employee) obj;
	    if (id == null) {
	      if (other.id != null)
	        return false;
	    } else if (!id.equals(other.id))
	      return false;
	    return true;
	  }

	  // automatically generate the next student id
	  private String nextID(String id) throws ConstraintViolationException {
			if (id == null) { // generate a new id
//				if (idCounter == 0) {
//					idCounter = Calendar.getInstance().get(Calendar.YEAR);
//				} else {
//					idCounter++;
//				}
				idCounter++;
				String stringIdCounter = String.format("%05d", idCounter);
				return "E" + stringIdCounter;
			} else {
				// update id
				int num;
				try {
					num = Integer.parseInt(id.substring(1));
				} catch (RuntimeException e) {
					throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
							new Object[] { id });
				}

				if (num > idCounter) {
					idCounter = num;
				}

				return id;
			}
		}

	  /**
	   * @requires 
	   *  minVal != null /\ maxVal != null
	   * @effects 
	   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	   */
	  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
	  public static void updateAutoGeneratedValue(
	      DAttr attrib,
	      Tuple derivingValue, 
	      Object minVal, 
	      Object maxVal) throws ConstraintViolationException {
	    
	    if (minVal != null && maxVal != null) {
	      //TODO: update this for the correct attribute if there are more than one auto attributes of this class 

	      String maxId = (String) maxVal;
	      
	      try {
	        int maxIdNum = Integer.parseInt(maxId.substring(1));
	        
	        if (maxIdNum > idCounter) // extra check
	          idCounter = maxIdNum;
	        
	      } catch (RuntimeException e) {
	        throw new ConstraintViolationException(
	            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxId});
	      }
	    }
	  }
	


}
