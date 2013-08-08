package clouds.client.basic;

import xdi2.core.xri3.XDI3Segment;

public class PDSXElement  implements PersonalCloudEntity {

	public PDSXElement(PDSXEntity parent , PDSXElementTemplate template, String value) {
		
		this.template = template;
		this.value = value;
		this.parentEntity = parent;
		parent.addElement(this);
	}
	
	@Override
	public XDI3Segment getAddress(PersonalCloud pc) {
		return XDI3Segment.create( parentEntity.getAddress(pc) + "<+" + template.getName() + ">&");
	}
	public static String get(PersonalCloud pc, PDSXEntity parent ,String elementName){
		//TODO
		return null;
	}
	protected void save(PersonalCloud pc){
		//TODO
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public String toString(){
		StringBuffer s = new StringBuffer();
		s.append("\t").append("Element Name:" + template.getName()).append("\n");
		s.append("\t").append("Element Value:" + value).append("\n");
		
		
		return s.toString();
	}
	
	private String value;
	private PDSXElementTemplate template;
	private PDSXEntity parentEntity;

}
