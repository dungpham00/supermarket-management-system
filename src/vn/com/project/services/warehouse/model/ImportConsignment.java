package vn.com.project.services.warehouse.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import vn.com.project.exceptions.DExCode;
import vn.com.project.services.finance.model.Revenue;
import vn.com.project.services.finance.model.WarehousingCost;
import vn.com.project.utils.DToolkit;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;

@DClass(schema="project")
public class ImportConsignment {
	private static final String IC_id = "id";
	private static final String IC_import = "impDate";
	private static final String IC_total="totalMoney";
	@DAttr(name = IC_id, id = true, type = Type.String, auto = true, length = 7, mutable = false, optional = false)
	private String id;
	//static variable to keep track of employee id
	private static int idCounter = 0;
	
	@DAttr(name = IC_import, type = Type.Date, length = 15, optional = false)
	private Date impDate;
	
	@DAttr(name = "warehousingCost", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "warehousingCost-has-importConsignments", role = "importConsignments", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = WarehousingCost.class, cardMin = 1, cardMax = 1), dependsOn = true)
	public WarehousingCost warehousingCost;
	
	@DAttr(name = IC_total, type = Type.Long, optional = true, auto = true, serialisable = true, mutable= false)
	private Long totalMoney;
	
	@DAttr(name="receivedProducts",type=Type.Collection,optional = false,
		      serialisable=false,filter=@Select(clazz=ReceivedProduct.class))
		  @DAssoc(ascName="importConsignment-has-receivedProducts",role="importConsignment",
		      ascType=AssocType.One2Many,endType=AssocEndType.One,
		    associate=@Associate(type=ReceivedProduct.class,cardMin=0,cardMax=30))
	private Collection<ReceivedProduct> receivedProducts;
	
	 // derived
	  private int importCount;
	  

	// from object form: Employee is not included
//		@DOpt(type = DOpt.Type.ObjectFormConstructor)
//		@DOpt(type = DOpt.Type.RequiredConstructor)
//		public ReceivedProduct(@AttrRef("impDate") Date impDate) {
//			this(null, impDate);
//		}
		
		@DOpt(type = DOpt.Type.RequiredConstructor)
		@DOpt(type = DOpt.Type.ObjectFormConstructor)
		public ImportConsignment(@AttrRef("impDate") Date impDate, @AttrRef("warehousingCost") WarehousingCost warehousingCost) {
			this(null, impDate, warehousingCost, 0L);
		}

		// based constructor (used by others)
		@DOpt(type=DOpt.Type.DataSourceConstructor)
		public ImportConsignment(String id, Date impDate, WarehousingCost warehousingCost, Long totalMoney) {
			this.id = nextID(id);
			this.impDate = impDate;
			this.warehousingCost = warehousingCost;
			if (totalMoney != null)
				this.totalMoney = totalMoney;
			else
				this.totalMoney = 0L;
			receivedProducts = new ArrayList<>();
			importCount = 0;
		}
		
		private void computeTotalMoney() {
			for (ReceivedProduct i : receivedProducts) {
				totalMoney += i.getTotal();
			}
		}
		
		public Long getTotalMoney() {
			return totalMoney;
		}
	  
	  public void setImpDate(Date impDate) throws ConstraintViolationException {
			// additional validation on exp
			if (impDate.before(DToolkit.MIN_DATE)) {
				throw new ConstraintViolationException(DExCode.INVALID_DATE, impDate);
			}

			this.impDate = impDate;
		}
	  
	  public Date getImpDate() {
		  return impDate;
	  }
	  
	  public String getId() {
		    return id;
		  }
	  
	  
	  
	  public WarehousingCost getWarehousingCost() {
		return warehousingCost;
	}

	public void setWarehousingCost(WarehousingCost warehousingCost) {
		this.warehousingCost = warehousingCost;
	}

	public Collection<ReceivedProduct> getReceivedProducts() {
		    return receivedProducts;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkAdder)
		  public boolean addReceivedProduct(ReceivedProduct i) {
			  if (!receivedProducts.contains(i)) {
				  receivedProducts.add(i);
			  }
			  return false;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkAdderNew)
		  public boolean addNewReceivedProduct(ReceivedProduct i) {
			  receivedProducts.add(i);
			  
			  importCount++;
			  
			  totalMoney += i.getTotal();
			  return true;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkAdder)
		  public boolean addReceivedProduct(Collection<ReceivedProduct> imports) {
			  boolean added = false;
			  for (ReceivedProduct i : imports) {
				  if(!imports.contains(i)) {
					  if(!added) added = true;
					  imports.add(i);
				  }
			  }
			  return false;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkAdderNew)
		  public boolean addNewReceivedProduct(Collection<ReceivedProduct> ips) {
			  receivedProducts.addAll(ips);
			  importCount+=ips.size();
			  
			  computeTotalMoney();
			  return true;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkRemover)
		  public boolean removeReceivedProduct(ReceivedProduct i) {
			  boolean removed = receivedProducts.remove(i);
			  
			  if (removed) {
				  importCount--;
				  totalMoney -= i.getTotal();
				  return true;
			  }
			  return false;
		  }
		  
//		  public boolean updateImportConsignment(ReceivedProduct i) {
//			  long totalPrice = totalMoney * importCount;
//			  
//			  long oldTotalPrice = i.getTotal(true);
//			  
//			  long diff = i.getTotal(true) - oldTotalPrice;
//			  
//			  totalPrice += diff;
//			  
//			  return true;
//		  }
//		  
		  
		  public boolean setReceivedProduct(Collection<ReceivedProduct> ip) {
			  this.receivedProducts = ip;
			  importCount = ip.size();
			  
			  computeTotalMoney();
			  return true;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkCountGetter)
		  public Integer getImportCount() {
			  return importCount;
		  }
		  
		  @DOpt(type=DOpt.Type.LinkCountSetter)
		  public void setImportCount( int count) {
			  importCount = count;
		  }
		  
		  
		  
//		  public void computeTotalMoney() {
//			  if(importCount > 0) {
//				  long totalM = 0;
//				  for (ImportConsignment i : importConsignments) {
//					  totalM += i.getTotal();
//				  }
//				  totalMoney = totalM;
//			  } else {
//				  totalMoney = 0L;
//			  }
//		  }
	  
		  
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
		    ImportConsignment other = (ImportConsignment) obj;
		    if (id == null) {
		      if (other.id != null)
		        return false;
		    } else if (!id.equals(other.id))
		      return false;
		    return true;
		  }
		  
		  private String nextID(String id) throws ConstraintViolationException {
			    if (id == null) { // generate a new id
			    	idCounter++;
					String stringIdCounter = String.format("%05d", idCounter);
					return "IC" + stringIdCounter;
			    } else {
			      // update id
			      int num;
			      try {
			        num = Integer.parseInt(id.substring(2));
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
		  
		  @Override
		  public String toString() {
		    return id + ", " + impDate + totalMoney;
		  }
}


