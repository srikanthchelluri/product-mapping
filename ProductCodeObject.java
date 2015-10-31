public class ProductCodeObject {

	private String productNumber;
	private String ibCode;

	//CONSTRUCTOR
	public ProductCodeObject(String productNumberInput, String ibCodeInput) {
		productNumber = productNumberInput;
		ibCode = ibCodeInput;
	}

	//ACCESSORS
	public String getProductNumber() {
		return productNumber;
	}

	public String getIbCode() {
		return ibCode;
	}

	//MODIFERS
	public void setProductNumber(String productNumberInput) {
		productNumber = productNumberInput;
	}

	public void setIbCode(String ibCodeInput) {
		ibCode = ibCodeInput;
	}

	//TOSTRING
	@Override
	public String toString() {
		return productNumber + ", " + ibCode;
	}

}