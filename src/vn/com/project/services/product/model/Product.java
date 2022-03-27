package vn.com.project.services.product.model;

import java.util.ArrayList;
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
import vn.com.project.services.sale.model.ProductOnShelf;
import vn.com.project.services.warehouse.model.ProductOut;
import vn.com.project.services.warehouse.model.ReceivedProduct;
import vn.com.project.services.warehouse.model.Warehouse;
import vn.com.project.utils.DToolkit;

public class Product {
	public static final String P_name = "name";
	public static final String P_id = "id";
	public static final String P_price = "price";
	public static final String P_category = "category";
	public static final String P_exp = "exp";
	public static final String P_weight = "weight";
	public static final String P_color = "color";
	public static final String P_desc = "description";
	public static final String P_quantity = "quantity";
	public static final String P_bill = "bill";
	public static final String P_qty = "qty";

	// attributes of importProducts
	@DAttr(name = P_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of product id
	private static int idCounter = 0;

	@DAttr(name = P_name, type = Type.String, length = 30, optional = false, cid = true)
	private String name;

	@DAttr(name = P_color, type = Type.Domain, length = 10, optional = true)
	private Color color;

	@DAttr(name = P_weight, type = Type.String, length = 10, optional = false)
	private String weight;

	@DAttr(name = P_desc, type = Type.String, length = 50, optional = false)
	private String description;

//	@DAttr(name = P_price, type = Type.Long, length = 15, optional = false)
//	private Long price;

	@DAttr(name = P_category, type = Type.Domain, length = 30, optional = false)
	@DAssoc(ascName = "category-has-products", role = "product", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Category.class, cardMin = 1, cardMax = 1))
	private Category category;

	@DAttr(name = P_exp, type = Type.Date, length = 15, optional = false)
	private Date exp;

	@DAttr(name = P_qty, type = Type.Long, length = 10, auto = true, mutable = false, serialisable = true)
	private Long qty;

	@DAttr(name = "receivedProducts", type = Type.Collection, optional = true, serialisable = false, filter = @Select(clazz = ReceivedProduct.class))
	@DAssoc(ascName = "product-has-receivedProducts", role = "product", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ReceivedProduct.class, cardMin = 0, cardMax = 30))
	private Collection<ReceivedProduct> receivedProducts;

	private int receiveCount;

//	@DAttr(name="exportProducts",type=Type.Collection,optional = true,
//		      serialisable=false,filter=@Select(clazz=ExportProduct.class))
//		  @DAssoc(ascName="product-has-exportProducts",role="product",
//		      ascType=AssocType.One2Many,endType=AssocEndType.One,
//		    associate=@Associate(type=ExportProduct.class,cardMin=0,cardMax=30))
//	private Collection<ExportProduct> exportProducts;
//	
//	private int exportCount;

	@DAttr(name = "productOuts", type = Type.Collection, optional = true, serialisable = false, filter = @Select(clazz = ProductOut.class))
	@DAssoc(ascName = "product-has-productOuts", role = "product", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ProductOut.class, cardMin = 0, cardMax = 30))
	private Collection<ProductOut> productOuts;
	
	private int eCount;

	@DAttr(name = "warehouse", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "warehouse-has-products", role = "products", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Warehouse.class, cardMin = 1, cardMax = 1), dependsOn = true)
	public Warehouse warehouse;

	// constructor methods
	// for creating in the application
//	@DOpt(type = DOpt.Type.ObjectFormConstructor)
//	@DOpt(type = DOpt.Type.RequiredConstructor)
//	public Product(@AttrRef("name") String name, @AttrRef("color") Color color, @AttrRef("weight") String weight, @AttrRef("description") String description, @AttrRef("price") Long price, @AttrRef("category") Category category,
//			@AttrRef("exp") Date exp) {
//		this(null, name, color, weight, description, price, category, exp, null);
//	}

	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Product(@AttrRef("name") String name, @AttrRef("color") Color color, @AttrRef("weight") String weight,
			@AttrRef("description") String description,
			@AttrRef("category") Category category, @AttrRef("exp") Date exp,
			@AttrRef("warehouse") Warehouse warehouse) {
		this(null, name, null, weight, description, category, exp, 0L, warehouse);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Product(@AttrRef("id") String id, @AttrRef("name") String name, @AttrRef("color") Color color,
			@AttrRef("weight") String weight, @AttrRef("description") String description,
			@AttrRef("category") Category category, @AttrRef("exp") Date exp, @AttrRef("qty") Long qty,
			@AttrRef("warehouse") Warehouse warehouse) throws ConstraintViolationException {
		// generate an id
		this.id = nextID(id);

		// assign other values
		this.name = name;
		this.color = color;
		this.weight = weight;
		this.description = description;
		this.category = category;
		this.exp = exp;
		if (qty != null)
			this.qty = qty;
		else
			this.qty = 0L;

		receivedProducts = new ArrayList<>();
		receiveCount = 0;
		productOuts = new ArrayList<>();
		eCount = 0;
		this.warehouse = warehouse;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	// setter methods
	public void setName(String name) {
		this.name = name;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public void setPrice(Long price) {
//		this.price = price;
//	}

	public void setCategory(Category category) {
		this.category = category;
	}

	// v2.7.3
	public void setNewCategory(Category category) {
		// change this invocation if need to perform other tasks (e.g. updating value of
		// a derived attribtes)
		setCategory(category);
	}

	public void setExp(Date exp) throws ConstraintViolationException {
		// additional validation on exp
		if (exp.before(DToolkit.MIN_EXP)) {
			throw new ConstraintViolationException(DExCode.INVALID_EXP, exp);
		}

		this.exp = exp;
	}

//	public void setQty(Long qty) {
//		  this.qty = qty;
//	  }

	// getter methods
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public String getWeight() {
		return weight;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	public Date getExp() {
		return exp;
	}

	public Long getQty() {
		return qty;
	}

//	  public void computeQty() {
//		  for (ExportProduct i : importProducts) {
//			  qty += i.getQuantity();
//		  }
//		  
//		  for (SoldProduct e : soldProducts) {
//			  qty -= e.getQuantity();
//		  }
//	  }

//	  public void subtractQty() {
//		  for (ExportProduct e : exportProducts) {
//			  qty -= e.getQuantity();
//		  }
//	  }
//	  
// ------------------RECEIVED PRODUCT----------------------

	public Collection<ReceivedProduct> getReceivedProducts() {
		return receivedProducts;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addReceiveProduct(ReceivedProduct r) {
		if (!receivedProducts.contains(r)) {
			receivedProducts.add(r);
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewReceiveProduct(ReceivedProduct r) {
		receivedProducts.add(r);

		receiveCount++;

		qty += r.getQuantity();
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addReceiveProduct(Collection<ReceivedProduct> rp) {
		for (ReceivedProduct r : rp) {
			if (!receivedProducts.contains(r)) {
				receivedProducts.add(r);
				// balance += s.getDepositAmount();
			}
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewReceiveProduct(Collection<ReceivedProduct> rps) {
		receivedProducts.addAll(rps);
		receiveCount += rps.size();

		for (ReceivedProduct r : rps) {
			qty += r.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	public boolean removeReceiveProduct(ReceivedProduct r) {
		boolean removed = receivedProducts.remove(r);

		if (removed) {
			receiveCount--;
			qty -= r.getQuantity();
			return true;
		}
		return false;
	}

	public boolean setReceiveProduct(Collection<ReceivedProduct> rp) {
		this.receivedProducts = rp;
		receiveCount = rp.size();
		for (ReceivedProduct r : rp) {
			qty += r.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkCountGetter)
	public Integer getReceiveCount() {
		return receiveCount;
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setReceiveCount(int count) {
		receiveCount = count;
	}

// -----------------------------EXPORT PRODUCT---------------------------

	public Collection<ProductOut> getProductOuts() {
		return productOuts;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addExportProduct(ProductOut e) {
		if (!productOuts.contains(e)) {
			productOuts.add(e);
//					  qty -= s.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewExportProduct(ProductOut e) {
		productOuts.add(e);

		eCount++;

		qty -= e.getQuantity();
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewExportProduct(Collection<ProductOut> eps) {
		productOuts.addAll(eps);
		eCount += eps.size();
		for (ProductOut e : eps) {
			qty -= e.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addExportProduct(Collection<ProductOut> ep) {
		for (ProductOut e : ep) {
			if (!productOuts.contains(e)) {
				productOuts.add(e);
//							qty -= s.getQuantity();
			}
		}

		// no other attributes changed
		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	public boolean removeExportProduct(ProductOut e) {
		boolean removed = productOuts.remove(e);

		if (removed) {
			eCount--;
			qty += e.getQuantity();
			return true;
		}
		return false;
	}

	public boolean setExportProduct(Collection<ProductOut> ep) {
		this.productOuts = ep;
		eCount = ep.size();

		for (ProductOut e : ep) {
			qty -= e.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkCountGetter)
	public Integer getECount() {
		return eCount;
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setECount(int count) {
		eCount = count;
	}

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
			return "Product(" + id + "," + name + "," + color + "," + weight + "," + description + ","  + ", "
					+ category + "," + exp + ")";
		else
			return "Product(" + id + ")";
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
		Product other = (Product) obj;
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
			idCounter++;
			String stringIdCounter = String.format("%05d", idCounter);
			return "P" + stringIdCounter;
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
	 * @requires minVal != null /\ maxVal != null
	 * @effects update the auto-generated value of attribute <tt>attrib</tt>,
	 *          specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	 */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			// TODO: update this for the correct attribute if there are more than one auto
			// attributes of this class
			if (attrib.name().equals("id")) {
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
	}

}
