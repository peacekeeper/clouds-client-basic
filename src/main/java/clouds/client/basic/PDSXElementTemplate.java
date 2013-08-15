package clouds.client.basic;



public class PDSXElementTemplate  {

	public PDSXElementTemplate(String name , String label , boolean required, String dtype , String caption){
		this.name = name;
		this.label = label;
		this.required = required;
		this.dtype = dtype;
		this.caption = caption;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDtype() {
		return dtype;
	}
	public void setDtype(String dtype) {
		this.dtype = dtype;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	private String name, label, dtype, caption;
	boolean required;
	

}
