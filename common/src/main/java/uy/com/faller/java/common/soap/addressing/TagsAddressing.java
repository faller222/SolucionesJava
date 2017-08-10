package uy.com.faller.java.common.soap.addressing;

public enum TagsAddressing {

	MESSAGE_ID("MessageID"),
	RELATES_TO("RelatesTo"), 
	REPLY_TO("ReplyTo"),
	FROM("From"),
	FAULT_TO("FaultTo"),
	TO("To"),
	ACTION("Action"),
	REFERENCE_PARAMETERS("ReferenceParameters");
	
	private final String value;


	private TagsAddressing(String value) {
		this.value = value;
	}


	public String getValue() {
		return value;
	}

}
