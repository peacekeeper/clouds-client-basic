package clouds.client.basic;

import xdi2.core.xri3.XDI3Segment;

public class PCAttribute implements PersonalCloudEntity {

	String name = null;
	String value = null;	
	PCAttributeCollection container = null;
	
	public PCAttribute(String name , PCAttributeCollection container){
		this.name = name;
		this.container = container;
		this.container.setAttribute(this);
	}
	public PCAttribute(String name , String value , PCAttributeCollection container){
		this.name = name;
		this.value = value;
		this.container = container;
		this.container.setAttribute(this);
	}
	@Override
	public XDI3Segment getAddress(PersonalCloud pc) {
		return XDI3Segment.create( container.getAddress(pc) + "<+" + name + ">&");
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PCAttributeCollection getContainer() {
		return container;
	}
	public void setContainer(PCAttributeCollection container) {
		this.container = container;
	}

}
