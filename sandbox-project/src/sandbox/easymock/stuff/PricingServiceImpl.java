package sandbox.easymock.stuff;

import java.math.BigDecimal;

public class PricingServiceImpl implements PricingService {
	private DataAccess dataAccess;

	public void setDataAccess(DataAccess dataAccess) {
		this.dataAccess = dataAccess;
	}

	public BigDecimal getPrice(String sku) throws SkuNotFoundException {
		BigDecimal price = dataAccess.getPriceBySku(sku);
		if (price == null) {
			throw new SkuNotFoundException("SKU not found.");
		}
		return price;
	}
}
