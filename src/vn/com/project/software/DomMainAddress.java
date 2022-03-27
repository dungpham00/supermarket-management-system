package vn.com.project.software;

import domainapp.basics.exceptions.DataSourceException;
import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.project.services.employee.model.Address;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DomMainAddress {
  
  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
    
    // this should be run subsequent times
    sw.init();
    
    // create classes
    try {
      sw.addClass(Address.class);
      
      // get objects
      sw.loadAndPrintObjects(Address.class);

      // create some objects
      createCity(sw);
      
      // check that a new object is in the object pool
      sw.printObjectPool(Address.class);

      // check that object is in the database by printing data in the database
      sw.printObjectDB(Address.class);
      
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }

  /**
   * @effects 
   * 
   */
  private static void createCity(DomSoftware sw) throws DataSourceException {
    Address obj = new
//    City(6, "Hoa Binh");
//      City(5, "Thai Nguyen");
//    City(4, "Hue");
//      City(3, "Danang");
//      City(2, "HCM");
      Address(1, "Hanoi");
      sw.addObject(Address.class, obj);    
  }
}
