package vn.com.project.services.warehouse.model;

import domainapp.basics.model.meta.DAttr;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import vn.com.project.services.product.model.Product;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;

@DClass(schema = "project")
public class ReceivedProduct {

	private static final String I_id = "id";
	private static final String I_total = "total";
	private static final String I_price = "price";
	private static final String I_quantity = "quantity";

	@DAttr(name = I_id, id = true, type = Type.String, auto = true, length = 7, mutable = false, optional = false)
	private String id;
	// static variable to keep track of employee id
	private static int idCounter = 0;

	@DAttr(name = "product", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "product-has-receivedProducts", role = "receivedProducts", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Product.class, cardMin = 1, cardMax = 1), dependsOn = true)
	public Product product;

//	@DAttr(name = "warehouse", type = Type.Domain, length = 30, optional = false)
//	@DAssoc(ascName = "warehouse-has-receivedProducts", role = "receivedProducts", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Warehouse.class, cardMin = 1, cardMax = 1), dependsOn = true)
//	public Warehouse warehouse;

	@DAttr(name = "importConsignment", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "importConsignment-has-receivedProducts", role = "receivedProducts", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = ImportConsignment.class, cardMin = 1, cardMax = 1), dependsOn = true)
	public ImportConsignment importConsignment;

	@DAttr(name = I_price, type = Type.Long, optional = false, length = 15)
	private Long price;

	@DAttr(name = I_quantity, type = Type.Long, optional = false, length = 15)
	private Long quantity;

	@DAttr(name = I_total, type = Type.Long, auto = true, optional = false, mutable = false, serialisable = false, length = 15, derivedFrom = {
			I_price, I_quantity })
	private Long total;

//	private StateHistory<String, Object> stateHist;

	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public ReceivedProduct(@AttrRef("product") Product product,
			@AttrRef("importConsignment") ImportConsignment importConsignment, @AttrRef("price") Long price, @AttrRef("quantity") Long quantity) {
		this(null, product, importConsignment, price, quantity);
	}

	public ReceivedProduct(@AttrRef("id") String id, @AttrRef("product") Product product,
			@AttrRef("importConsignment") ImportConsignment importConsignment, @AttrRef("price") Long price,
			@AttrRef("quantity") Long quantity) throws ConstraintViolationException {
		this.id = nextID(id);
		this.product = product;
//		this.warehouse = warehouse;
		this.importConsignment = importConsignment;
		this.price = price;
		this.quantity = quantity;

//	    stateHist = new StateHistory<>();
		updateTotal();
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@DOpt(type = DOpt.Type.DerivedAttributeUpdater)
	@AttrRef(value = I_total)
	public void updateTotal() {
		// updates both final mark and final grade
		total = price * quantity;

	}

//	public void setWarehouse(Warehouse warehouse) {
//		this.warehouse = warehouse;
//	}

	public void setImportConsignment(ImportConsignment importConsignment) {
		this.importConsignment = importConsignment;
	}

	public void setPrice(Long price) {
		this.price = price;
		updateTotal();
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
		updateTotal();
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public String getId() {
		return id;
	}

//	public Warehouse getWarehouse() {
//		return warehouse;
//	}

	public ImportConsignment getImportConsignment() {
		return importConsignment;
	}

	public Long getPrice() {
		return price;
	}

	public Long getQuantity() {
		return quantity;
	}

	public Long getTotal() {
		return total;
	}

//	  public Long getTotal(boolean cached) throws IllegalStateException {
//		  if (cached) {
//			  Object val = stateHist.get(I_total);
//			  
//			  if (val == null) 
//				  throw new IllegalStateException("ReceivedProduct.getTotal: cached value is null"); 
//				  
//			  return (Long) val;
//			  } else {
//				  if(total != null)
//					  return total;
//				  else
//					  return 0L;
//		  }
//	  }

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
		ReceivedProduct other = (ReceivedProduct) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private String nextID(String id) throws ConstraintViolationException {
		    if (id == null) { 
//		      if (idCounter == 0) {
//		        idCounter = Calendar.getInstance().get(Calendar.YEAR);
//		      } else {
		idCounter++;
		String stringIdCounter = String.format("%05d", idCounter);
		return "RP" + stringIdCounter;
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

	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			// TODO: update this for the correct attribute if there are more than one auto
			// attributes of this class
			if (attrib.name().equals("id")) {
				String maxId = (String) maxVal;

				try {
					int maxIdNum = Integer.parseInt(maxId.substring(2));

					if (maxIdNum > idCounter) // extra check
						idCounter = maxIdNum;

				} catch (RuntimeException e) {
					throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
							new Object[] { maxId });
				}
			}
			
		}
	}

	@Override
	public String toString() {
		return id + ", " + price;
	}
}
