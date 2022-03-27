package vn.com.project.services.finance.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import vn.com.project.exceptions.DExCode;
import vn.com.project.services.employee.model.Employee;
import vn.com.project.services.employee.model.Manager;
import vn.com.project.services.employee.model.Month;
import vn.com.project.services.employee.model.Salary;
import vn.com.project.utils.DToolkit;

@DClass(schema="project")
public class PaySheet {
	
	private static final String PS_id = "id";
	private static final String PS_month = "month";
	private static final String PS_payDate = "payDate";
	
	
	@DAttr(name = PS_id, id = true, type = Type.String, auto = true, length = 7, mutable = false, optional = false)
	private String id;
	// static variable to keep track of student id
	private static int idCounter = 0;
	
	@DAttr(name = PS_month, type = Type.Domain, length = 30, optional = false, cid=true)
	private Month month;
	
	@DAttr(name = PS_payDate, type = Type.Date, length = 15, optional = false)
	private Date payDate;
	
	@DAttr(name = "manager", type = Type.Domain, length = 50, optional = false)
	private Manager manager;
	
	@DAttr(name = "totalSalary", type = Type.Long, length = 15, optional = false, auto = true, mutable = false)
	private Long totalSalary;
	
//	@DAttr(name = "paySheet", type = Type.Domain, length = 30, optional = false)
//	@DAssoc(ascName="paySheet-has-salaries", role = "salary", ascType = AssocType.One2Many, endType = AssocEndType.Many,
//	associate = @Associate(type=PaySheet.class, cardMin=1, cardMax=1))
//	private PaySheet paySheet;
	
	@DAttr(name="salaries",type=Type.Collection,optional = false,
		      serialisable=false,filter=@Select(clazz=Salary.class))
		  @DAssoc(ascName="paySheet-has-salaries",role="paysheet",
		      ascType=AssocType.One2Many,endType=AssocEndType.One,
		    associate=@Associate(type=Salary.class,cardMin=0,cardMax=30))
		  private Collection<Salary> salaries;  

		  // derived
		  private int sCount;

	
	@DOpt(type=DOpt.Type.RequiredConstructor)
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public PaySheet(@AttrRef("month") Month month, @AttrRef("payDate") Date payDate, @AttrRef("manager") Manager manager) {
		this(null, month, payDate, manager, 0L);
	}
	
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public PaySheet(@AttrRef("id") String id, @AttrRef("month") Month month, @AttrRef("payDate") Date payDate, @AttrRef("manager") Manager manager, @AttrRef("totalSalary") Long totalSalary) throws ConstraintViolationException {
		this.id = nextID(id);
		this.month = month;
		this.payDate = payDate;
		this.manager = manager;
		if(totalSalary != null)
			this.totalSalary = totalSalary;
		else 
			this.totalSalary = 0L;
		
		salaries = new ArrayList<>();
		sCount = 0;
	}
	
	public void setMonth(Month month) {
		this.month = month;
	}
	
	public void setPayDate(Date payDate) throws ConstraintViolationException {
	    // additional validation on dob
	    if (payDate.before(DToolkit.MIN_DATE)) {
	      throw new ConstraintViolationException(DExCode.INVALID_DATE, payDate);
	    }
	    
	    this.payDate = payDate;
	  }
	
	public void setManager(Manager manager) {
		this.manager = manager;
	}
	
	public String getId() {
		return id;
	}
	
	public Month getMonth() {
		return month;
	}
	
	public Date getPayDate() {
		return payDate;
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public Long getTotalSalary() {
		return totalSalary;
	}
	
	// ---------------------------SOLD PRODUCT----------------------------
	public Collection<Salary> getSalaries() {
	    return salaries;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkAdder)
	  public boolean addSalary(Salary s) {
		  if (!salaries.contains(s)) {
			  salaries.add(s);
//			  qty -= s.getQuantity();
		  }
		  return true;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkAdderNew)
	  public boolean addNewSalary(Salary s) {
		  salaries.add(s);
		  
		  sCount++;
		  
		  totalSalary += s.getMoney();
		  return true;
	  }
	  

	  
	  @DOpt(type=DOpt.Type.LinkAdderNew)
	  public boolean addNewSalary(Collection<Salary> salary) {
		  salaries.addAll(salary);
		  sCount+=salary.size();
		  for (Salary s : salary) {
			  totalSalary += s.getMoney();
		  }
		  return true;
	  }
	  
	  @DOpt(type = DOpt.Type.LinkAdder)
		public boolean addSalary(Collection<Salary> salary) {
			for (Salary s : salary) {
				if (!salaries.contains(s)) {
					salaries.add(s);
//					qty -= s.getQuantity();
				}
			}

			// no other attributes changed
			return true;
		}
	  
	  @DOpt(type=DOpt.Type.LinkRemover)
	  public boolean removeSalary(Salary s) {
		  boolean removed = salaries.remove(s);
		  
		  if (removed) {
			  sCount--;
			  totalSalary -= s.getMoney();
			  return true;
		  }
		  return false;
	  }
	  
	  public boolean setSalary(Collection<Salary> salary) {
		  this.salaries = salary;
		  sCount = salary.size();
		  
		  for (Salary s : salary) {
		  totalSalary -= s.getMoney();
		  }
		  return true;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkCountGetter)
	  public Integer getSCount() {
		  return sCount;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkCountSetter)
	  public void setSCount( int count) {
		  sCount = count;
	  }

	
	public String toString() {
		return id + ", " + month;
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
	    PaySheet other = (PaySheet) obj;
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
				return "PS" + stringIdCounter;
			} else {
				// update id
				int num;
				try {
					num = Integer.parseInt(id.substring(2));
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
	        int maxIdNum = Integer.parseInt(maxId.substring(2));
	        
	        if (maxIdNum > idCounter) // extra check
	          idCounter = maxIdNum;
	        
	      } catch (RuntimeException e) {
	        throw new ConstraintViolationException(
	            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxId});
	      }
	    }
	  }
	

}
