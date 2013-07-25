package clouds.client.basic;

import java.util.Hashtable;

import xdi2.core.xri3.XDI3Segment;

public class PCAttributeCollection implements PersonalCloudEntity {
	
	String name = null;
	Hashtable<String , PCAttribute> attributes = new Hashtable<String , PCAttribute>();
	
	public PCAttributeCollection(String name){
		this.name = name;
	}
	public void setAttribute(PCAttribute attr){
		attributes.put(attr.getName(), attr);
	}

	public PCAttribute getAttribute(String name){
		return attributes.get(name);
	}
	
	public void deleteAttribute(String attrName){
		attributes.remove(attrName);
	}
	@Override
	public XDI3Segment getAddress(PersonalCloud pc) {
		return XDI3Segment.create( pc.getCloudNumber().toString() + "+" + name.replace('.', '+'));
	}

	public Hashtable<String , PCAttribute> getAttributeMap(){
		return attributes;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
