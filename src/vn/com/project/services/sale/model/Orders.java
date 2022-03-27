package vn.com.project.services.sale.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

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
import vn.com.project.exceptions.DExCode;
import vn.com.project.services.employee.model.Employee;
import vn.com.project.services.employee.model.Staff;
import vn.com.project.services.finance.model.Revenue;
import vn.com.project.services.product.model.Product;
import vn.com.project.utils.DToolkit;

@DClass(schema = "project")
public class Orders {
	private static final String O_id = "id";
	private static final String O_paymentDate = "paymentDate";
	private static final String O_cashier = "cashier";
	private static final String O_subTotal = "subTotal";
	private static final String O_coupon = "coupon";
	private static final String O_discount = "discount";
	private static final String O_grandTotal = "grandTotal";

	@DAttr(name = O_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of employee id
	private static int idCounter = 0;

	@DAttr(name = O_paymentDate, type = Type.Date, length = 15, optional = false)
	private Date paymentDate;

	@DAttr(name = O_cashier, type = Type.Domain, length = 30, optional = false)
	private Staff cashier;
	
	@DAttr(name = "revenue", type = Type.Domain, length = 6, optional = false)
	@DAssoc(ascName = "revenue-has-orders", role = "orders", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Revenue.class, cardMin = 1, cardMax = 1), dependsOn = true)
	public Revenue revenue;
	

	@DAttr(name = O_subTotal, type = Type.Long, optional = true, auto = true, serialisable = true, mutable = false)
	private Long subTotal;

	@DAttr(name = O_coupon, type = Type.String, optional = true, auto = true, serialisable = true, mutable = false)
	private String coupon;

	@DAttr(name = O_discount, type = Type.Long, optional = true, auto = true, serialisable = true, mutable = false)
	private Long discount;

	@DAttr(name = O_grandTotal, type = Type.Long, optional = true, auto = true, serialisable = true, mutable = false)
	private Long grandTotal;

	@DAttr(name = "orderLines", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = OrderLine.class))
	@DAssoc(ascName = "orders-has-orderLines", role = "orders", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = OrderLine.class, cardMin = 0, cardMax = 30))
	private Collection<OrderLine> orderLines;

	// derived
	private int sCount;

	// from object form: Employee is not included
//		@DOpt(type = DOpt.Type.ObjectFormConstructor)
//		@DOpt(type = DOpt.Type.RequiredConstructor)
//		public ReceivedProduct(@AttrRef("impDate") Date impDate) {
//			this(null, impDate);
//		}

	@DOpt(type = DOpt.Type.RequiredConstructor)
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Orders(@AttrRef("paymentDate") Date paymentDate, @AttrRef("cashier") Staff cashier, @AttrRef("revenue") Revenue revenue) {
		this(null, paymentDate, cashier, revenue, 0L, null, 0L, 0L);
	}

	// based constructor (used by others)
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Orders(@AttrRef("id") String id, @AttrRef("paymentDate") Date paymentDate,
			@AttrRef("cashier") Staff cashier, @AttrRef("revenue") Revenue revenue, @AttrRef("subTotal") Long subTotal, @AttrRef("coupon") String coupon, @AttrRef("discount") Long discount, @AttrRef("grandTotal") Long grandTotal) {
		this.id = nextID(id);
		this.paymentDate = paymentDate;
		this.cashier = cashier;
		this.revenue = revenue;
		
		if (subTotal != null) {
			this.subTotal = subTotal;
		} else {
			this.subTotal = 0L;
		}
		
		if (grandTotal != null) {
			this.grandTotal = grandTotal;
		} else {
			this.grandTotal = 0L;
		}
		
		if (subTotal >= 1000000) {
			this.coupon = getCoupon();
		} else {
			this.coupon = null;
		}
		
		if (subTotal >= 1000000) {
			this.discount = getDiscount();
		} else {
			this.discount = 0L;
		}
		
		orderLines = new ArrayList<>();
		sCount = 0;

//			totalMoney = 0L;
	}

//	private void computeDiscount() {
//		discount = (long) Math.round(subTotal * Long.parseLong(coupon.substring(0, 1)));
//	}

//	private void computeGrandTotal() {
//		grandTotal = subTotal - discount;
//	}

	public Revenue getRevenue() {
		return revenue;
	}

	public void setRevenue(Revenue revenue) {
		this.revenue = revenue;
	}

	public String getCoupon() {
		return coupon;
	}

//	public void setCoupon(String coupon, boolean computeDiscount) {
//		this.coupon = coupon;
//		if (computeDiscount)
//			computeDiscount();
//	}

	public Long getDiscount() {
		return discount;
	}

//	public void setDiscount(String discount) {
//		this.discount = discount;
//	}

	public Long getGrandTotal() {
		return grandTotal;
	}

	public Long getSubTotal() {
		return subTotal;
	}

	public void setPaymentDate(Date paymentDate) throws ConstraintViolationException {
		// additional validation on exp
		if (paymentDate.before(DToolkit.MIN_EXP)) {
			throw new ConstraintViolationException(DExCode.INVALID_EXP, paymentDate);
		}

		this.paymentDate = paymentDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getId() {
		return id;
	}

	public Collection<OrderLine> getOrderLines() {
		return orderLines;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addSoldProduct(OrderLine s) {
		if (!orderLines.contains(s)) {
			orderLines.add(s);
		}
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSoldProduct(OrderLine s) {
		orderLines.add(s);

		sCount++;

		subTotal += s.getTotal();
		if (subTotal >= 1000000) {
			coupon = "2%";
			discount = (long) (subTotal * 0.02);
			grandTotal = subTotal - discount;
		} else {
			grandTotal = subTotal;
		}
		
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
			subTotal += s.getTotal();
			if (subTotal >= 1000000) {
				coupon = "2%";
				discount = (long) (subTotal * 0.02);
				grandTotal = subTotal - discount;
			} else {
				grandTotal = subTotal;
			}
		}
		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	public boolean removeSoldProduct(OrderLine s) {
		boolean removed = orderLines.remove(s);

		if (removed) {
			sCount--;
			subTotal -= s.getTotal();
			if (subTotal >= 1000000) {
				coupon = "2%";
				discount = (long) (subTotal * 0.02);
				grandTotal = subTotal - discount;
			} else {
				coupon = null;
				discount = 0L;
				grandTotal = subTotal;
			}
			return true;
		}
		return false;
	}

//		  public boolean updateImportConsignment(ExportProduct i) {
//			  long totalPrice = totalMoney * exportCount;
//			  
//			  long oldTotalPrice = i.getTotal(true);
//			  
//			  long diff = i.getTotal(true) - oldTotalPrice;
//			  
//			  totalPrice += diff;
//			  
//			  return true;
//		  }
//		  

	public boolean setSoldProduct(Collection<OrderLine> sps) {
		this.orderLines = sps;
		sCount = sps.size();

		for (OrderLine s : sps) {
			subTotal += s.getTotal();
			if (subTotal >= 1000000) {
				coupon = "2%";
				discount = (long) (subTotal * 0.02);
				grandTotal = subTotal - discount;
			} else {
				grandTotal = subTotal;
			}
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

//		  public void computeTotalMoney() {
//			  if(exportCount > 0) {
//				  long totalM = 0;
//				  for (ImportConsignment i : importConsignments) {
//					  totalM += i.getTotal();
//				  }
//				  totalMoney = totalM;
//			  } else {
//				  totalMoney = 0L;
//			  }
//		  }

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
		Orders other = (Orders) obj;
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
			return "B" + stringIdCounter;
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

	@Override
	public String toString() {
		return id + ", " + paymentDate + subTotal + coupon + discount + grandTotal;
	}

	public Staff getCashier() {
		return cashier;
	}

	public void setCashier(Staff cashier) {
		this.cashier = cashier;
	}
}