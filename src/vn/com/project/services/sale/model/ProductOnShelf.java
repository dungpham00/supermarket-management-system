package vn.com.project.services.sale.model;

import java.util.ArrayList;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import vn.com.project.services.product.model.Product;
import vn.com.project.services.warehouse.model.ProductOut;

@DClass(schema = "project")
public class ProductOnShelf {
	public static final String O_id = "id";
	
	@DAttr(name = O_id, id = true, type = Type.String, auto = true, length = 7, mutable = false, optional = false)
	private String id;
	// static variable to keep track of employee id
	private static int idCounter = 0;

	@DAttr(name = "product", type = Type.Domain, length = 6, optional = false)
	public Product product;
	
	@DAttr(name="productName", type = Type.String, length=50, optional = false, auto = true, mutable = false, cid = true)
	private String productName;

	@DAttr(name = "shelf", type = Type.Domain, length = 30, optional = false)
	@DAssoc(ascName = "shelf-has-productsOnShelf", role = "productsOnShelf", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = ProductShelf.class, cardMin = 1, cardMax = 1))
	private ProductShelf shelf;

	@DAttr(name = "price", type = Type.Long, optional = false, length = 15)
	private Long price;

	@DAttr(name = "qty", type = Type.Long, optional = false, length = 15, auto = true, mutable = false, serialisable = true)
	private Long qty;

	@DAttr(name = "productOuts", type = Type.Collection, optional = true, serialisable = false, filter = @Select(clazz = ProductOut.class))
	@DAssoc(ascName = "productsOnShelf-has-productOuts", role = "productsOnShelf", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ProductOut.class, cardMin = 0, cardMax = 30))
	private Collection<ProductOut> productOuts;

	private int eCount;

	@DAttr(name = "orderLines", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = OrderLine.class))
	@DAssoc(ascName = "productsOnShelf-has-orderLines", role = "productsOnShelf", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = OrderLine.class, cardMin = 0, cardMax = 30))
	private Collection<OrderLine> orderLines;

	private int sCount;

	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public ProductOnShelf(@AttrRef("product") Product product, @AttrRef("shelf") ProductShelf shelf,
			@AttrRef("price") Long price) {
		this(null, product, null, shelf, price, 0L);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public ProductOnShelf(@AttrRef("id") String id, @AttrRef("product") Product product, @AttrRef("productName") String productName, @AttrRef("shelf") ProductShelf shelf,
			@AttrRef("price") Long price, @AttrRef("qty") Long qty) throws ConstraintViolationException {
		// generate an id
		this.id = nextID(id);

		// assign other values
		this.product = product;
		this.productName = getProductName();
		this.shelf = shelf;
		this.price = price;
		if (qty != null)
			this.qty = qty;
		else
			this.qty = 0L;
		productOuts = new ArrayList<>();
		eCount = 0;
		orderLines = new ArrayList<>();
		sCount = 0;
	}

	public String getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public ProductShelf getShelf() {
		return shelf;
	}

	public Long getPrice() {
		return price;
	}

	public Long getQty() {
		return qty;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setShelf(ProductShelf shelf) {
		this.shelf = shelf;
	}

	public void setPrice(Long price) {
		this.price = price;
	}
	
	public String getProductName() {
		return product.getName();
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	// -----------------------------EXPORT PRODUCT---------------------------

	public Collection<ProductOut> getProductOuts() {
		return productOuts;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addProductOut(ProductOut e) {
		if (!productOuts.contains(e)) {
			productOuts.add(e);
//						  qty -= s.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewProductOut(ProductOut e) {
		productOuts.add(e);

		eCount++;

		qty += e.getQuantity();
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewProductOut(Collection<ProductOut> eps) {
		productOuts.addAll(eps);
		eCount += eps.size();
		for (ProductOut e : eps) {
			qty += e.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addProductOut(Collection<ProductOut> ep) {
		for (ProductOut e : ep) {
			if (!productOuts.contains(e)) {
				productOuts.add(e);
//								qty -= s.getQuantity();
			}
		}

		// no other attributes changed
		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	public boolean removeProductOut(ProductOut e) {
		boolean removed = productOuts.remove(e);

		if (removed) {
			eCount--;
			qty -= e.getQuantity();
			return true;
		}
		return false;
	}

	public boolean setProductOut(Collection<ProductOut> ep) {
		this.productOuts = ep;
		eCount = ep.size();

		for (ProductOut e : ep) {
			qty += e.getQuantity();
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
	
	// -----------------------------SOLD PRODUCT---------------------------
	
	public Collection<OrderLine> getOrderLines() {
		return orderLines;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addSoldProduct(OrderLine s) {
		if (!orderLines.contains(s)) {
			orderLines.add(s);
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSoldProduct(OrderLine s) {
		orderLines.add(s);

		sCount++;

		qty -= s.getQuantity();

		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addSoldProduct(Collection<OrderLine> sps) {
		boolean added = false;
		for (OrderLine s : sps) {
			if (!sps.contains(s)) {
				if (!added)
					added = true;
				sps.add(s);
			}
		}
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSoldProduct(Collection<OrderLine> sps) {
		orderLines.addAll(sps);
		sCount += sps.size();

		for (OrderLine s : orderLines) {
			qty -= s.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	public boolean removeSoldProduct(OrderLine s) {
		boolean removed = orderLines.remove(s);

		if (removed) {
			sCount--;
			qty += s.getQuantity();
			return true;
		}
		return false;
	}  

	public boolean setSoldProduct(Collection<OrderLine> sps) {
		this.orderLines = sps;
		sCount = sps.size();

		for (OrderLine s : sps) {
			qty -= s.getQuantity();
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkCountGetter)
	public Integer getSCount() {
		return sCount;
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setSCount(int count) {
		sCount = count;
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
		ProductOnShelf other = (ProductOnShelf) obj;
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
			return "OS" + stringIdCounter;
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
		return "ProductOnShelf(" + id + ")";
	}
}
