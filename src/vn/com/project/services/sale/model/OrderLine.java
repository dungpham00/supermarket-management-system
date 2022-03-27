package vn.com.project.services.sale.model;

import java.util.Calendar;

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
import vn.com.project.services.warehouse.model.ProductOut;

@DClass(schema="project")
public class OrderLine {
	private static final String SP_id = "id";
	private static final String SP_total="total";
	private static final String SP_price="price";
	private static final String SP_quantity="quantity";
	
	@DAttr(name = SP_id, id = true, type = Type.String, auto = true, length = 7, mutable = false, optional = false)
	private String id;
	//static variable to keep track of employee id
	private static int idCounter = 0;
	
	@DAttr(name = "productsOnShelf", type = Type.Domain, length = 30, optional = false)
	  @DAssoc(ascName = "productsOnShelf-has-orderLines", role = "orderLines", 
	    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	    associate = @Associate(type = ProductOnShelf.class, cardMin = 1, cardMax = 1), dependsOn = true)
	  public ProductOnShelf productsOnShelf;
	
	@DAttr(name = "orders", type = Type.Domain, length = 6, optional = false)
	  @DAssoc(ascName = "orders-has-orderLines", role = "orderLines", 
	    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	    associate = @Associate(type = Orders.class, cardMin = 1, cardMax = 1), dependsOn = true)
	  public Orders orders;
	
	@DAttr(name = SP_price, type = Type.Long, optional = false, length = 15, auto = true, mutable = false)
	private Long price;
	
	@DAttr(name = SP_quantity, type = Type.Long, optional = false, length = 15)
	private Long quantity;
	
	@DAttr(name = SP_total, type = Type.Long,auto= true, optional = false,mutable= false,serialisable= false, length = 15,
			derivedFrom= {SP_price, SP_quantity})
	private Long total;
	
	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public OrderLine(@AttrRef("productsOnShelf") ProductOnShelf productsOnShelf,
			@AttrRef("orders") Orders orders,
			@AttrRef("quantity") Long quantity) {
		this(null, productsOnShelf, orders, 0L, quantity);
	}
	
	public OrderLine(@AttrRef("id") String id,
			@AttrRef("productsOnShelf") ProductOnShelf productsOnShelf,
			@AttrRef("orders") Orders orders,
			@AttrRef("price") Long price,
			@AttrRef("quantity") Long quantity
			) throws ConstraintViolationException {
		this.id = nextID(id);
	    this.productsOnShelf = productsOnShelf;
	    this.orders = orders;
	    this.price = getPrice();
	    this.quantity = quantity;
	    updateTotal(); 
	}
	
	@DOpt(type=DOpt.Type.DerivedAttributeUpdater)
	  @AttrRef(value=SP_total)
	  public void updateTotal() {
	    // updates both final mark and final grade
	    total = price*quantity;      
	    
	  }
	
	public void setProductsOnShelf(ProductOnShelf productsOnShelf) {
		  this.productsOnShelf = productsOnShelf;
	  }
	  
	  public void setOrders(Orders orders) {
		  this.orders = orders;
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
	  
	  public ProductOnShelf getProductsOnShelf() {
		  return productsOnShelf;
	  }
	  
	  public Orders getOrders() {
		  return orders;
	  }
	  
	  public Long getPrice() {
		  return productsOnShelf.getPrice();
	  }
	  
	  public Long getQuantity() {
		  return quantity;
	  }
	  
	  public Long getTotal() {
		  return total;
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
	    OrderLine other = (OrderLine) obj;
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
				return "SP" + stringIdCounter;
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
