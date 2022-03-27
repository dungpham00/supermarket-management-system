package vn.com.project.software;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.query.Expression.Op;
import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.project.services.employee.model.Address;
import vn.com.project.services.employee.model.Gender;
import vn.com.project.services.employee.model.Role;
import vn.com.project.services.employee.model.Employee;
import vn.com.project.services.employee.model.Department;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DomMainEmployees {
  
  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
    
    // this should be run subsequent times
    sw.init();
    
    try {
      // register a domain model fragment concerning Student
      Class[] domFrag = {
          Employee.class
      };
      sw.addClasses(domFrag);
      
      // create some Student objects
      createEmployee(sw);
      
      // read object:
//      querySimple(sw, Student.class, Student.A_id, Op.EQ, "S2020");
      
    //  queryEmployees(sw);
      
      // display the domain model and its instances
//      boolean displayFqn = false;
//      sw.printDomainModel(displayFqn);
      
      // check that a new object is in the object pool
//      sw.printObjectPool(Student.class);

      // check that object is in the database by printing data in the database
      sw.printObjectDB(Employee.class);
      
      // update object:
//      updateObject(sw, "S2020");
      
      // delete object:
//      deleteObject(sw, "S2020");
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }
  
  
  /**
   * @return 
   * @effects 
   * 
   */
  private static <T> Collection<T> querySimple(DomSoftware sw, Class<T> cls, 
      String attribName, Op op, String val) throws NotPossibleException, DataSourceException {
    
    Collection<T> objects = sw.retrieveObjects(cls, attribName, op, val);
    sw.printObjects(cls, objects);
    return objects;
  }
  

  /**
   * @effects 
   * 
   */
  private static void createEmployee(DomSoftware sw) throws NotFoundException, DataSourceException {
    // get a city object
    Address address = sw.retrieveObjectById(Address.class, 2);
    // create a Student
    Date dob;
    Department department = sw.retrieveObjectById(Department.class, 1);
    
    //dob = Toolkit.getDateZeroTime(1, 1, 1970);
    
    // another method of creating Date
    dob = createDateFromString("1/1/1970");
    
//    sw.addObject(Employee.class,
//        new Employee("Duc Le", 
//            Gender.Male, 
//            dob, 
//            address,
//            "Me Linh, Dong Hung",
//            "duc@gmail.com",
//            "037415886",
//            department,
//            Role.Manager)
//        );    
  }

  /**
   * @effects 
   *  return a Date object whose string representation is <tt>dateStr</tt>.
   *  If dateStr is invalid
   *    return null
   */
  private static Date createDateFromString(String dateStr) {
    DateFormat dformat = new SimpleDateFormat("dd/MM/yyyy");
    Date dt = null;
    try {
      dt = dformat.parse(dateStr);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    
    return dt;
  }

  /**
   * @effects 
   * 
   */
  private static void updateObject(DomSoftware sw, Object id) throws NotFoundException, DataSourceException {
    Employee s = sw.retrieveObjectById(Employee.class, id);
    if (s != null) {
      System.out.printf("Updating object%n%s%n", s);
      sw.updateObject(Employee.class, s, 
          new String[] {
              Employee.E_email, Employee.E_address},
          new Object[] {
              "leminhduc@gmail.com",
              sw.retrieveObjectById(Address.class, 2)
          });
      System.out.printf("... after:%n%s%n", s);
    }    
  }

  /**
   * @effects 
   * 
   * @version 
   * @param sw 
   * 
   */
  private static void deleteObject(DomSoftware sw, Object id) throws NotFoundException, DataSourceException {
    Employee e = sw.retrieveObjectById(Employee.class, id);
    if (e != null) {
      System.out.printf("Deleting object%n%s%n", e);
      sw.deleteObject(e, Employee.class);
      sw.printObjectDB(Employee.class);
    }    
  }
}

