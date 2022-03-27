package vn.com.project.services.finance.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import vn.com.project.exceptions.DExCode;
import vn.com.project.services.sale.model.Orders;
import vn.com.project.utils.DToolkit;

@DClass(schema="project")
public class Revenue {
	@DAttr(name = "id", id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of product id
	private static int idCounter = 0;
	
	@DAttr(name="startDate", type = Type.Date, optional = false)
	private Date startDate;
	
	@DAttr(name="endDate", type = Type.Date, optional = false)
	private Date endDate;
	
	@DAttr(name = "total", type = Type.Long, auto = true, serialisable = true, optional = false, mutable = false)
	private Long total;
	
	@DAttr(name="orders",type=Type.Collection,optional = true,
		      serialisable=false,filter=@Select(clazz=Orders.class))
		  @DAssoc(ascName="revenue-has-orders",role="revenue",
		      ascType=AssocType.One2Many,endType=AssocEndType.One,
		    associate=@Associate(type=Orders.class,cardMin=0,cardMax=30))
	private Collection<Orders> orders;
	
	private int bCount;
	
	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Revenue(@AttrRef("startDate") Date startDate, @AttrRef("endDate") Date endDate) {
		this(null, startDate, endDate, 0L);
	}
	
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Revenue(@AttrRef("id") String id, @AttrRef("startDate") Date startDate, @AttrRef("endDate") Date endDate, @AttrRef("total") Long total) {
		this.id = nextID(id);
		this.startDate = startDate;
		this.endDate = endDate;
		
		if (total != null)
			this.total = total;
		else
			this.total = 0L;
		
		orders = new ArrayList<>();
		bCount = 0;
	}
	
	public String getId() {
		return id;
	}
	
	public Long getTotal() {
		return total;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}	

	public void setStartDate(Date startDate) throws ConstraintViolationException {
		// additional validation on exp
		if (startDate.before(DToolkit.MIN_DATE)) {
			throw new ConstraintViolationException(DExCode.INVALID_DATE, startDate);
		}

		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate) throws ConstraintViolationException {
		// additional validation on exp
		if (endDate.before(DToolkit.MIN_DATE)) {
			throw new ConstraintViolationException(DExCode.INVALID_DATE, endDate);
		}

		this.endDate = endDate;
	}
	
	
	@DOpt(type = DOpt.Type.LinkUpdater)
	public boolean updateBill(Orders b) {
		total = b.getGrandTotal();
		return true;
		
	}
	
	 public Collection<Orders> getOrders() {
		    return orders;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkAdder)
		  public boolean addBill(Orders b) {
			  if (!orders.contains(b)) {
				  orders.add(b);
			  }
			  return true;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkAdderNew)
		  public boolean addNewBill(Orders b) {
			  orders.add(b);
			  
			  bCount++;
			  
			  total += b.getGrandTotal();
			  return true;
		  }
		  

		  @DOpt(type = DOpt.Type.LinkAdder)
			public boolean addBill(Collection<Orders> bill) {
				for (Orders b : bill) {
					if (!orders.contains(b)) {
						orders.add(b);
						//balance += s.getDepositAmount();
					}
				}
				return true;
				}
		  
		  @DOpt(type=DOpt.Type.LinkAdderNew)
		  public boolean addNewBill(Collection<Orders> bill) {
			  orders.addAll(bill);
			  bCount+=bill.size();
			  
			  for(Orders b : bill) {
				  total += b.getGrandTotal();			  }
			  return true;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkRemover)
		  public boolean removeBill(Orders b) {
			  boolean removed = orders.remove(b);
			  
			  if (removed) {
				  bCount--;
				  total -= b.getGrandTotal();
				  return true;
			  }
			  return false;
		  }
		  
		  public boolean setBill(Collection<Orders> bill) {
			  this.orders = bill;
			  bCount = bill.size();
			  for (Orders b : bill) {
				  total += b.getGrandTotal();
			  }
			  return true;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkCountGetter)
		  public Integer getBCount() {
			  return bCount;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkCountSetter)
		  public void setBCount( int count) {
			  bCount = count;
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
				Revenue other = (Revenue) obj;
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
//					if (idCounter == 0) {
//						idCounter = Calendar.getInstance().get(Calendar.YEAR);
//					} else {
//						idCounter++;
//					}
					idCounter++;
					String stringIdCounter = String.format("%05d", idCounter);
					return "R" + stringIdCounter;
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
			    	if (attrib.name().equals("id")) { 
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
			


}
