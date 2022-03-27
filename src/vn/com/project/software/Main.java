package vn.com.project.software;

import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;

import vn.com.project.services.employee.model.Address;
import vn.com.project.services.employee.model.Department;
import vn.com.project.services.employee.model.Employee;
import vn.com.project.services.employee.model.Manager;
import vn.com.project.services.employee.model.Salary;
import vn.com.project.services.employee.model.Staff;
import vn.com.project.services.employee.reports.EmployeeByAddressJoinReport;
import vn.com.project.services.employee.reports.EmployeeByDepartmentJoinReport;
import vn.com.project.services.finance.model.OperatingExpense;
import vn.com.project.services.finance.model.PaySheet;
import vn.com.project.services.finance.model.Profit;
import vn.com.project.services.finance.model.Revenue;
import vn.com.project.services.finance.model.WarehousingCost;
import vn.com.project.services.product.model.Category;
import vn.com.project.services.product.model.Product;
import vn.com.project.services.sale.model.Orders;
import vn.com.project.services.sale.model.ProductOnShelf;
import vn.com.project.services.sale.model.ProductShelf;
import vn.com.project.services.sale.model.OrderLine;
import vn.com.project.services.warehouse.model.ProductOut;
import vn.com.project.services.warehouse.model.ImportConsignment;
import vn.com.project.services.warehouse.model.ReceivedProduct;
import vn.com.project.services.warehouse.model.Warehouse;


/**
 * @overview 
 *  Create and run a UI-based {@link DomSoftware} for a pre-defined model.  
 *  
 * @author dmle
 */
public class Main {
  
  // 1. initialise the model
  static final Class[] model = {
		  Address.class,
		  Department.class,
      Employee.class, 
      Manager.class,
      Staff.class,
      Warehouse.class,
      Category.class,
      Product.class,
      ImportConsignment.class,
      ReceivedProduct.class,
      ProductOut.class,
      ProductShelf.class,
      ProductOnShelf.class,
      Orders.class,
      OrderLine.class,
      PaySheet.class,
      Salary.class,
      OperatingExpense.class,
      WarehousingCost.class,
      Revenue.class,
      Profit.class,
      EmployeeByAddressJoinReport.class,
      EmployeeByDepartmentJoinReport.class
      };
  
  /**
   * @effects 
   *  create and run a UI-based {@link DomSoftware} for a pre-defined model. 
   */
  public static void main(String[] args){
    // 2. create UI software
    DomSoftware sw = SoftwareFactory.createUIDomSoftware();
    
    // 3. run
    // create in memory configuration
    System.setProperty("domainapp.setup.SerialiseConfiguration", "false");
    
    // 3. run it
    try {
      sw.run(model);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }   
  }

}
