package vn.com.project.services.warehouse.model;

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
import vn.com.project.services.product.model.Product;
import vn.com.project.services.sale.model.ProductOnShelf;

@DClass(schema = "project")
public class ProductOut {
	private static final String R_id = "id";
//	private static final String R_total="total";
	private static final String R_price = "price";
	private static final String R_quantity = "quantity";

	@DAttr(name = R_id, id = true, type = Type.String, auto = true, length = 7, mutable = false, optional = false)
	private String id;
	// static variable to keep track of employee id
	private static int idCounter = 0;

//	@DAttr(name = "warehouse", type = Type.Domain, length = 30, optional = false)
//	  @DAssoc(ascName = "warehouse-has-productOuts", role = "productOuts", 
//	    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
//	    associate = @Associate(type = Product.class, cardMin = 1, cardMax = 1), dependsOn = true)
//	  public Warehouse warehouse;

	@DAttr(name = "product", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "product-has-productOuts", role = "productOuts", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Product.class, cardMin = 1, cardMax = 1), dependsOn = true)
	public Product product;
	
	

//	@DAttr(name = "warehouse", type = Type.Domain, length = 6, optional = false)
//	@DAssoc(ascName = "warehouse-has-productOuts", role = "productOuts", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Warehouse.class, cardMin = 1, cardMax = 1), dependsOn = true)
//	public Warehouse warehouse;

//	@DAttr(name = "shelf", type = Type.Domain, length = 30, optional = false)
//	@DAssoc(ascName = "shelf-has-productOuts", role = "productOuts", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Shelf.class, cardMin = 1, cardMax = 1))
//	private Shelf shelf;

//	@DAttr(name = R_price, type = Type.Long, optional = false, length = 15, auto = true, mutable = false)
//	private Long price;

	@DAttr(name = R_quantity, type = Type.Long, optional = false, length = 15)
	private Long quantity;

	@DAttr(name = "productsOnShelf", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "productsOnShelf-has-productOuts", role = "productOuts", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = ProductOnShelf.class, cardMin = 1, cardMax = 1), dependsOn = true)
	public ProductOnShelf productsOnShelf;

//	@DAttr(name = R_total, type = Type.Long,auto= true, optional = false,mutable= false,serialisable= false, length = 15,
//			derivedFrom= {R_price,R_quantity})
//	private Long total;

//	private StateHistory<String, Object> stateHist;

	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public ProductOut(@AttrRef("product") Product product, 
			@AttrRef("quantity") Long quantity,
			@AttrRef("productsOnShelf") ProductOnShelf productsOnShelf) {
		this(null, product, quantity, productsOnShelf);
	}

	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public ProductOut(@AttrRef("id") String id, @AttrRef("product") Product product,
			@AttrRef("quantity") Long quantity, @AttrRef("productsOnShelf") ProductOnShelf productsOnShelf) throws ConstraintViolationException {
		this.id = nextID(id);
		this.product = product;
//		this.productName = getProductName();
//		this.warehouse = warehouse;
//		this.shelf = shelf;
//		this.price = getPrice();
		this.quantity = quantity;
		this.productsOnShelf = productsOnShelf;

//	    stateHist = new StateHistory<>();
//	    updateTotal(); 
	}

//	@DOpt(type=DOpt.Type.DerivedAttributeUpdater)
//	  @AttrRef(value=R_total)
//	  public void updateTotal() {
//	    // updates both final mark and final grade
//	    total = price*quantity;      
//	    
//	  }

//	public void setWarehouse(Warehouse warehouse) {
//		this.warehouse = warehouse;
//	}

	public ProductOnShelf getProductsOnShelf() {
		return productsOnShelf;
	}

	public void setProductsOnShelf(ProductOnShelf productsOnShelf) {
		this.productsOnShelf = productsOnShelf;
	}

	public void setProduct(Product product) {
		this.product = product;
	}


	  public void setQuantity(Long quantity) {
		  this.quantity = quantity;
	  }

//	  public void setTotal(Long total) {
//		  this.total = total;
//	  }
	  

	public String getId() {
		return id;
	}

//	public Warehouse getWarehouse() {
//		return warehouse;
//	}

	public Product getProduct() {
		return product;
	}

	public Long getQuantity() {
		return quantity;
	}

//	  public Long getTotal() {
//		  return total;
//	  }

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
		ProductOut other = (ProductOut) obj;
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
			return "EP" + stringIdCounter;
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

	@Override
	public String toString() {
		return id + ", " ;
	}
}

