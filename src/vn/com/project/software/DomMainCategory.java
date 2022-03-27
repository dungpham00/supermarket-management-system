package vn.com.project.software;

import domainapp.basics.exceptions.DataSourceException;
import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.project.services.product.model.Category;

public class DomMainCategory {
	public static void main(String[] args) {
		DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();

		// this should be run subsequent times
		sw.init();

		// create classes
		try {
			sw.addClass(Category.class);

			// get objects
			sw.loadAndPrintObjects(Category.class);

			// create some objects
			createCategory(sw);

			// check that a new object is in the object pool
			sw.printObjectPool(Category.class);

			// check that object is in the database by printing data in the database
			sw.printObjectDB(Category.class);

		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @effects
	 * 
	 */
	private static void createCategory(DomSoftware sw) throws DataSourceException {
//		Category obj = new Category(2, "Drink");
		sw.addObject(Category.class, new Category(2, "Drink"));
	}
}
