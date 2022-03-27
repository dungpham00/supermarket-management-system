package vn.com.project.services.employee.model;

import java.util.Calendar;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import domainapp.basics.util.cache.StateHistory;
import vn.com.project.exceptions.DExCode;
import vn.com.project.services.finance.model.OperatingExpense;
import vn.com.project.services.finance.model.PaySheet;
import vn.com.project.utils.DToolkit;

@DClass(schema="project")
public class Salary {
	public static final String S_id = "id";
	public static final String S_month = "month";
	public static final String S_date = "date";
	public static final String S_em = "employee";
	public static final String S_attend = "attend";
	public static final String S_rate = "rate";
	public static final String S_money = "money";
	
	@DAttr(name = S_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of student id
	private static int idCounter = 0;
		  
//	@DAttr(name = S_month, type = Type.Domain, length = 30, optional = false, cid=true)
//	  private Month month;
//	
//	@DAttr(name = S_date, type = Type.Date, length = 15, optional = false)
//	  private Date date;
	
	@DAttr(name = S_em, type = Type.Domain, length = 30, optional = false)
	private Employee employee;
	
	@DAttr(name = S_attend, type = Type.Long, length = 15, optional = false)
	  private Long attend;
	
	@DAttr(name = S_rate, type = Type.Long, length = 15, optional = false)
	  private Long rate;
	
	@DAttr(name = S_money, type = Type.Long, length = 15, auto = true, optional = false, mutable = false, serialisable = false, derivedFrom = {S_attend, S_rate})
	  private Long money;
	
	@DAttr(name = "paySheet", type = Type.Domain, length = 30, optional = false)
	@DAssoc(ascName="paySheet-has-salaries", role = "salaries", ascType = AssocType.One2Many, endType = AssocEndType.Many,
	associate = @Associate(type=PaySheet.class, cardMin=1, cardMax=1))
	private PaySheet paySheet;
	
//	@DAttr(name = "operatingExpense", type = Type.Domain, length = 30, optional = false)
//	@DAssoc(ascName="operatingExpense-has-salaries", role = "salary", ascType = AssocType.One2Many, endType = AssocEndType.Many,
//	associate = @Associate(type=OperatingExpense.class, cardMin=1, cardMax=1))
//	private OperatingExpense operatingExpense;
	
	
//	@DOpt(type=DOpt.Type.ObjectFormConstructor)
//	  @DOpt(type=DOpt.Type.RequiredConstructor)
//	public Salary(@AttrRef("month") Month month, @AttrRef("date") Date date, @AttrRef("employee") Employee em, @AttrRef("attend") Long attend, @AttrRef("rate") Long rate) {
//		this(null, month, date, em, attend, rate, null);
//	}
	
	@DOpt(type=DOpt.Type.RequiredConstructor)
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public Salary(@AttrRef("employee") Employee em, @AttrRef("attend") Long attend, @AttrRef("rate") Long rate, @AttrRef("paySheet") PaySheet paySheet) {
		this(null, em, attend, rate, paySheet, 0L);
	}
	
	
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Salary(@AttrRef("id") String id, @AttrRef("employee") Employee em, @AttrRef("attend") Long attend, @AttrRef("rate") Long rate, @AttrRef("paySheet") PaySheet paySheet, @AttrRef("money") Long money) {
		this.id = nextID(id);
//		this.month = month;
//		this.date = date;
		this.employee = em;
		this.attend = attend;
		this.rate = rate;
		this.paySheet = paySheet;
//		this.operatingExpense = operatingExpense;
		updateMoney();
	
	}
	
//	public void setMonth(Month month) {
//		this.month = month;
//	}
//	
//	public void setDate(Date date) throws ConstraintViolationException {
//	    // additional validation on dob
//	    if (date.before(DToolkit.MIN_DATE)) {
//	      throw new ConstraintViolationException(DExCode.INVALID_DATE, date);
//	    }
//	    
//	    this.date = date;
//	  }
	
	public void setEmployee(Employee em) {
		this.employee = em;
	}
	
	public void setAttend(Long attend) {
		this.attend = attend;
		updateMoney();
	}
	
	public void setRate(Long rate) {
		this.rate = rate;
		updateMoney();
	}
	
	public void updateMoney() {
		money = attend * rate;
	}
	
	public void setPaySheet(PaySheet paySheet) {
		this.paySheet = paySheet;
	}
	
//	public void setOperatingExpense(OperatingExpense operatingExpense) {
//		this.operatingExpense = operatingExpense;
//	}
	
//	public void setMoney(Long money) {
//		this.money = money;
//	}
	
	public String getId() {
		return id;
	}
	
//	public Month getMonth() {
//		return month;
//	}
//	
//	public Date getDate() {
//		return date;
//	}
	
	public Long getAttend() {
		return attend;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public Long getRate() {
		return rate;
	}
	
	public Long getMoney() {
		return money;
	}
	
	public PaySheet getPaySheet() {
		return paySheet;
	}
	
//	public OperatingExpense getOperatingExpense() {
//		return operatingExpense;
//	}
	
	
	
	public String toString() {
		return "";
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
	    Salary other = (Salary) obj;
	    if (id != other.id)
	      return false;
	    return true;
	  }
	
	private String nextID(String id) throws ConstraintViolationException {
	    if (id == null) { // generate a new id
	      if (idCounter == 0) {
	        idCounter = Calendar.getInstance().get(Calendar.YEAR);
	      } else {
	        idCounter++;
	      }
	      return "S" + idCounter;
	    } else {
	      // update id
	      int num;
	      try {
	        num = Integer.parseInt(id.substring(1));
	      } catch (RuntimeException e) {
	        throw new ConstraintViolationException(
	            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] { id });
	      }
	      
	      if (num > idCounter) {
	        idCounter = num;
	      }
	      
	      return id;
	    }
	  }

	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			// TODO: update this for the correct attribute if there are more than one auto
			// attributes of this class

			String maxId = (String) maxVal;

			try {
				int maxIdNum = Integer.parseInt(maxId.substring(1));

				if (maxIdNum > idCounter) // extra check
					idCounter = maxIdNum;

			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { maxId });
			}
		}
	}
	  
//	  public int compareTo(Object o) {
//		    if (o == null || (!(o instanceof Salary)))
//		      return -1;
//
//		    Salary e = (Salary) o;
//
//		    return this.employee.getId().compareTo(e.employee.getId());
//		  }
}
