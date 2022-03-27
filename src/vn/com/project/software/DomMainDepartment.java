package vn.com.project.software;

import domainapp.basics.exceptions.DataSourceException;
import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.project.services.employee.model.Department;

public class DomMainDepartment {
	public static void main(String[] args) {
	    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
	    
	    // this should be run subsequent times
	    sw.init();
	    
	    // create classes
	    try {
	      sw.addClass(Department.class);
	      
	      // get objects
	      sw.loadAndPrintObjects(Department.class);

	      // create some objects
	      createDepartment(sw);
	      
	      // check that a new object is in the object pool
	      sw.printObjectPool(Department.class);

	      // check that object is in the database by printing data in the database
	      sw.printObjectDB(Department.class);
	      
	    } catch (DataSourceException e) {
	      e.printStackTrace();
	    }
	  }

	  /**
	   * @effects 
	   * 
	   */
	  private static void createDepartment(DomSoftware sw) throws DataSourceException {
//	    Department obj = new
//	    City(6, "Hoa Binh");
//	      City(5, "Thai Nguyen");
//	    City(4, "Hue");
//	      City(3, "Danang");
//	      City(2, "HCM");
//	      Department(1, "Sale", "Pham Thu Dung");
//	      sw.addObject(Department.class, obj);   
		  
//		  sw.addObject(Department.class, new Department(1, "Sale", "Pham Thu Dung"));
	  }
}
