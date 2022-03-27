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
import vn.com.project.services.warehouse.model.ProductOut;

@DClass(schema="project")
public class ProductShelf {
	@DAttr(name = "id", id = true, type = Type.String, auto = true, length = 7, mutable = false, optional = false)
	private String id;
	
	private static int idCounter = 0;
	
	@DAttr(name = "name", type = Type.String, length = 25, optional = false)
	private String name;
	
	@DAttr(name = "productsOnShelf", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = ProductOnShelf.class))
	@DAssoc(ascName = "shelf-has-productsOnShelf", role = "shelf", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = ProductOnShelf.class, cardMin = 0, cardMax = 30))
	private Collection<ProductOnShelf> productsOnShelf;
	
	private int oCount;
	
	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public ProductShelf(@AttrRef("name") String name) {
		this(null, name);
	}
	
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public ProductShelf(@AttrRef("id") String id, @AttrRef("name") String name) {
		this.id = nextID(id);
		this.name = name;
		productsOnShelf = new ArrayList<>();
		oCount = 0;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Collection<ProductOnShelf> getProductsOnShelf() {
		return productsOnShelf;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addProductOnShelf(ProductOnShelf e) {
		if (!productsOnShelf.contains(e)) {
			productsOnShelf.add(e);
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewProductOnShelf(ProductOnShelf e) {
		productsOnShelf.add(e);
		oCount++;
		
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addProductOnShelf(Collection<ProductOnShelf> ep) {
		for (ProductOnShelf e : ep) {
			if (!productsOnShelf.contains(e)) {
				productsOnShelf.add(e);
			}
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewProductOnShelf(Collection<ProductOnShelf> eps) {
		productsOnShelf.addAll(eps);
		oCount += eps.size();

		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	public boolean removeProductOnShelf(ProductOnShelf e) {
		boolean removed = productsOnShelf.remove(e);

		if (removed) {
			oCount--;
			return true;
		}
		return false;
	}

	public boolean setProductOnShelf(Collection<ProductOnShelf> ep) {
		this.productsOnShelf = ep;
		oCount = ep.size();

		return true;
	}

	@DOpt(type = DOpt.Type.LinkCountGetter)
	public Integer getOCount() {
		return oCount;
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setOCount(int count) {
		oCount = count;
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
	    ProductShelf other = (ProductShelf) obj;
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
				return "SH" + stringIdCounter;
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
		  return "Shelf(" + id + ")";
	  }
}
