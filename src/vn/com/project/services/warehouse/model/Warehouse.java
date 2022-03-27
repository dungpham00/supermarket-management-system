package vn.com.project.services.warehouse.model;

import java.util.ArrayList;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import vn.com.project.services.product.model.Product;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;

@DClass(schema = "project")
public class Warehouse {
	@DAttr(name = "id", id = true, type = Type.String, length = 1, mutable = false, optional = false)
	private String id;

	@DAttr(name = "products", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = Product.class))
	@DAssoc(ascName = "warehouse-has-products", role = "warehouse", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Product.class, cardMin = 0, cardMax = 30))
	private Collection<Product> products;

	private int pCount;

//	@DAttr(name = "receivedProducts", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = ReceivedProduct.class))
//	@DAssoc(ascName = "warehouse-has-receivedProducts", role = "warehouse", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ReceivedProduct.class, cardMin = 0, cardMax = 30))
//	private Collection<ReceivedProduct> receivedProducts;
//
//	private int rCount;
//
//	@DAttr(name = "exportProducts", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = ExportProduct.class))
//	@DAssoc(ascName = "warehouse-has-exportProducts", role = "warehouse", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ExportProduct.class, cardMin = 0, cardMax = 30))
//	private Collection<ExportProduct> exportProducts;
//
//	private int eCount;

	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Warehouse(@AttrRef("id") String id) throws ConstraintViolationException {
		this.id = id;
		products = new ArrayList<>();
		pCount = 0;

//		receivedProducts = new ArrayList<>();
//		rCount = 0;
//
//		exportProducts = new ArrayList<>();
//		eCount = 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

// ------------------PRODUCT----------------------

	public Collection<Product> getProducts() {
		return products;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addProduct(Product p) {
		if (!products.contains(p)) {
			products.add(p);
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewProduct(Product p) {
		products.add(p);
		pCount++;

		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addProduct(Collection<Product> pr) {
		for (Product p : pr) {
			if (!products.contains(p)) {
				products.add(p);
			}
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewProduct(Collection<Product> prs) {
		products.addAll(prs);
		pCount += prs.size();

		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	public boolean removeProduct(Product p) {
		boolean removed = products.remove(p);

		if (removed) {
			pCount--;
			return true;
		}
		return false;
	}

	public boolean setProduct(Collection<Product> pr) {
		this.products = pr;
		pCount = pr.size();

		return true;
	}

	@DOpt(type = DOpt.Type.LinkCountGetter)
	public Integer getPCount() {
		return pCount;
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setPCount(int count) {
		pCount = count;
	}

// ------------------EXPORT PRODUCT----------------------

//	public Collection<ExportProduct> getExportProducts() {
//		return exportProducts;
//	}
//
//	@DOpt(type = DOpt.Type.LinkAdder)
//	public boolean addExportProduct(ExportProduct e) {
//		if (!exportProducts.contains(e)) {
//			exportProducts.add(e);
//		}
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkAdderNew)
//	public boolean addNewExportProduct(ExportProduct e) {
//		exportProducts.add(e);
//		eCount++;
//
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkAdder)
//	public boolean addExportProduct(Collection<ExportProduct> ep) {
//		for (ExportProduct e : ep) {
//			if (!exportProducts.contains(e)) {
//				exportProducts.add(e);
//			}
//		}
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkAdderNew)
//	public boolean addNewExportProduct(Collection<ExportProduct> eps) {
//		exportProducts.addAll(eps);
//		eCount += eps.size();
//
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkRemover)
//	public boolean removeExportProduct(ExportProduct e) {
//		boolean removed = exportProducts.remove(e);
//
//		if (removed) {
//			eCount--;
//			return true;
//		}
//		return false;
//	}
//
//	public boolean setExportProduct(Collection<ExportProduct> ep) {
//		this.exportProducts = ep;
//		eCount = ep.size();
//
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkCountGetter)
//	public Integer getECount() {
//		return eCount;
//	}
//
//	@DOpt(type = DOpt.Type.LinkCountSetter)
//	public void setECount(int count) {
//		eCount = count;
//	}
//	// ------------------RECEIVED PRODUCT----------------------
//
//	public Collection<ReceivedProduct> getReceivedProducts() {
//		return receivedProducts;
//	}
//
//	@DOpt(type = DOpt.Type.LinkAdder)
//	public boolean addReceivedProduct(ReceivedProduct r) {
//		if (!receivedProducts.contains(r)) {
//			receivedProducts.add(r);
//		}
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkAdderNew)
//	public boolean addNewReceivedProduct(ReceivedProduct r) {
//		receivedProducts.add(r);
//		rCount++;
//
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkAdder)
//	public boolean addReceivedProduct(Collection<ReceivedProduct> rp) {
//		for (ReceivedProduct r : rp) {
//			if (!receivedProducts.contains(r)) {
//				receivedProducts.add(r);
//			}
//		}
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkAdderNew)
//	public boolean addNewReceivedProduct(Collection<ReceivedProduct> rps) {
//		receivedProducts.addAll(rps);
//		rCount += rps.size();
//
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkRemover)
//	public boolean removeReceivedProduct(ReceivedProduct r) {
//		boolean removed = receivedProducts.remove(r);
//
//		if (removed) {
//			rCount--;
//			return true;
//		}
//		return false;
//	}
//
//	public boolean setReceivedProduct(Collection<ReceivedProduct> rp) {
//		this.receivedProducts = rp;
//		rCount = rp.size();
//
//		return true;
//	}
//
//	@DOpt(type = DOpt.Type.LinkCountGetter)
//	public Integer getRCount() {
//		return rCount;
//	}
//
//	@DOpt(type = DOpt.Type.LinkCountSetter)
//	public void setRCount(int count) {
//		rCount = count;
//	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Warehouse other = (Warehouse) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//
//	private String nextID(String id) throws ConstraintViolationException {
//		if (id == null) { // generate a new id
//			if (idCounter == 0) {
//				idCounter = Calendar.getInstance().get(Calendar.YEAR);
//			} else {
//				idCounter++;
//			}
//			return "W" + idCounter;
//		} else {
//			// update id
//			int num;
//			try {
//				num = Integer.parseInt(id.substring(1));
//			} catch (RuntimeException e) {
//				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
//						new Object[] { id });
//			}
//
//			if (num > idCounter) {
//				idCounter = num;
//			}
//
//			return id;
//		}
//	}
//
//	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
//	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
//			throws ConstraintViolationException {
//
//		if (minVal != null && maxVal != null) {
//			// TODO: update this for the correct attribute if there are more than one auto
//			// attributes of this class
//			if (attrib.name().equals("id")) {
//				String maxId = (String) maxVal;
//
//				try {
//					int maxIdNum = Integer.parseInt(maxId.substring(1));
//
//					if (maxIdNum > idCounter) // extra check
//						idCounter = maxIdNum;
//
//				} catch (RuntimeException e) {
//					throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
//							new Object[] { maxId });
//				}
//			}
//		}
//	}

	@Override
	public String toString() {
		return "Warehouse(" + id + ")";
	}
}
