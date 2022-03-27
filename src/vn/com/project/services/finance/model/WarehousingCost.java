package vn.com.project.services.finance.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import vn.com.project.exceptions.DExCode;
import vn.com.project.services.product.model.Product;
import vn.com.project.services.warehouse.model.ImportConsignment;
import vn.com.project.utils.DToolkit;

public class WarehousingCost {
	
	@DAttr(name = "id", id = true, type = Type.String, auto = true, length = 7, mutable = false, optional = false)
	private String id;
	//static variable to keep track of employee id
	private static int idCounter = 0;
	
	@DAttr(name="startDate", type = Type.Date, optional = false)
	private Date startDate;
	
	@DAttr(name="endDate", type = Type.Date, optional = false)
	private Date endDate;
	
	
	@DAttr(name="total", auto = true, type = Type.Long, mutable = false, serialisable = true)
	private Long total;
	
	@DAttr(name = "importConsignments", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = ImportConsignment.class))
	@DAssoc(ascName = "warehousingCost-has-importConsignments", role = "warehousingCost", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ImportConsignment.class, cardMin = 0, cardMax = 30))
	private Collection<ImportConsignment> importConsignments;
	
	private int iCount;
	
	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public WarehousingCost(@AttrRef("startDate") Date startDate, @AttrRef("endDate") Date endDate) {
		this(null, startDate, endDate, 0L);
	}
	
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public WarehousingCost(@AttrRef("id") String id, @AttrRef("startDate") Date startDate, @AttrRef("endDate") Date endDate, @AttrRef("total") Long total) throws ConstraintViolationException {
		this.id = nextID(id);
		this.startDate = startDate;
		this.endDate = endDate;
		if (total != null) 
			this.total = total;
		else
			this.total = 0L;
		
		importConsignments = new ArrayList<>();
		iCount = 0;
		
	}
	
	public String getId() {
		return id;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Long getTotal() {
		return total;
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
	public void updateImportConsignment(ImportConsignment i) {
//		if(importConsignments.add(i)) {
//			total += i.getTotalMoney();
//		} else if (importConsignments.remove(i)) {
//			total -= i.getTotalMoney();
//		}
		
		total = i.getTotalMoney();
	}
	
	
	public Collection<ImportConsignment> getImportConsignments() {
	    return importConsignments;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkAdder)
	  public boolean addImportConsignment(ImportConsignment i) {
		  if (!importConsignments.contains(i)) {
			  importConsignments.add(i);
		  }
		  return true;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkAdderNew)
	  public boolean addNewImportConsignment(ImportConsignment i) {
		  importConsignments.add(i);
		  
		  iCount++;
		  
		  total += i.getTotalMoney();
		  return true;
	  }
	  

	  @DOpt(type = DOpt.Type.LinkAdder)
		public boolean addImportConsignment(Collection<ImportConsignment> importC) {
		  boolean added = false;
			for (ImportConsignment i : importC) {
				if (!importC.contains(i)) {
					if(!added) added = true;
					importC.add(i);
				}
			}
			return false;
			}
	  
	  @DOpt(type=DOpt.Type.LinkAdderNew)
	  public boolean addNewImportConsignment(Collection<ImportConsignment> ips) {
		  importConsignments.addAll(ips);
		  iCount+=ips.size();
		  
		  for(ImportConsignment i: ips) {
			  total += i.getTotalMoney();			  
			  }
		  return true;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkRemover)
	  public boolean removeImportConsignment(ImportConsignment i) {
		  boolean removed = importConsignments.remove(i);
		  
		  if (removed) {
			  iCount--;
			  total -= i.getTotalMoney();
			  return true;
		  }
		  return false;
	  }
	  
	  public boolean setImportConsignment(Collection<ImportConsignment> ip) {
		  this.importConsignments = ip;
		  iCount = ip.size();
		  for (ImportConsignment i : ip) {
			  total += i.getTotalMoney();
		  }
		  return true;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkCountGetter)
	  public Integer getICount() {
		  return iCount;
	  }
	  
	  @DOpt(type=DOpt.Type.LinkCountSetter)
	  public void setICount( int count) {
		  iCount = count;
	  }
	  
	  
	  @Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WarehousingCost other = (WarehousingCost) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	  
	  private String nextID(String id) throws ConstraintViolationException {
			if (id == null) { // generate a new id
//				if (idCounter == 0) {
//					idCounter = Calendar.getInstance().get(Calendar.YEAR);
//				} else {
//					idCounter++;
//				}
				idCounter++;
				String stringIdCounter = String.format("%05d", idCounter);
				return "WC" + stringIdCounter;
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
	
	
	  
}
