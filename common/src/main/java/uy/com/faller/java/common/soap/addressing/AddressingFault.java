package uy.com.faller.java.common.soap.addressing;


public class AddressingFault extends Exception {
	
	private static final long serialVersionUID = 1799209957496629184L;
	
	public static final String WSA_NS = "http://www.w3.org/2005/08/addressing";
	
	private String faultCode;
	private String faultString;

	/**
	 * Constructor 
	 */
	public AddressingFault() {}

	/**
	 * Constructor
	 * @param message 
	 */
	public AddressingFault(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * @param cause 
	 */
	public AddressingFault(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor
	 * @param message
	 * @param cause 
	 */
	public AddressingFault(String message, Throwable cause) {
		super(message, cause);
	}

	public String getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	public String getFaultString() {
		return faultString;
	}

	public void setFaultString(String faultString) {
		this.faultString = faultString;
	}
	
}
