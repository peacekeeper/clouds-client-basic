package clouds.client.basic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageResult;

public class PDSXEntity implements PersonalCloudEntity {

	public PDSXEntity(String name, String what, String oid) {
		this.objectType = name;
		this.what = what;
		this.elements = new Vector<PDSXElement>();
		this.objectName = oid;

		objectMap.add(this);
	}

	@Override
	public XDI3Segment getAddress(PersonalCloud pc) {
		// TODO Auto-generated method stub
		PDSXEntity entity = PDSXEntity.get(pc, objectType, objectName);
		if ((objectUUID == null || objectUUID.length() == 0 ) && ( entity == null)) {
			objectUUID = UUID.randomUUID().toString();
		}
		else if(entity != null) {
			this.objectUUID = entity.objectUUID;
		}
		return XDI3Segment.create(pc.getCloudNumber().toString() + "[+"
				+ objectType + "]" + "!:uuid:" + objectUUID);
	}

	public XDI3Segment getElementCountAddress(PersonalCloud pc) {
		return XDI3Segment.create(pc.getCloudNumber().toString() + "[+"
				+ objectType + "]<+" + "count" + ">&");
	}

	public String getName() {
		return objectType;
	}

	public void setName(String name) {
		this.objectType = name;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public Vector<PDSXElement> getElements() {
		return elements;
	}

	public void setElements(Vector<PDSXElement> elements) {
		this.elements = elements;
	}

	public int howManyElements() {
		return elements.size();
	}

	public void addElement(PDSXElement element) {
		elements.add(element);
	}

	public String getObjectId() {
		return objectName;
	}

	public void setObjectId(String objectId) {
		this.objectName = objectId;
	}

	public static PDSXEntity get(PersonalCloud pc, String entityType, String oid) {

		XDI3Segment stmt1 = XDI3Segment.create(pc.getCloudNumber().toString()
				+ "[+" + entityType + "]" + "*" + oid);

		MessageResult objectExists = pc.getXDIStmts(stmt1,false);
		if (objectExists.isEmpty()) {
			return null;
		}
		PDSXEntity entity = new PDSXEntity(entityType, "Unknown", oid);
		
		Graph responseGraph = objectExists.getGraph();
		ReadOnlyIterator<Relation> refRelIter = responseGraph.getRootContextNode().getAllRelations();
		while(refRelIter.hasNext()){
			Relation r = refRelIter.next();
			System.out.println(r.getArcXri().toString());
			if(r.getArcXri().toString().equals("$ref")){
				System.out.println(r.getTargetContextNodeXri().toString());
				String UUIDStr = r.getTargetContextNodeXri().toString();
				entity.objectUUID = UUIDStr.substring(new String(pc.getCloudNumber().toString()
				+ "[+" + entityType + "]").length());
				System.out.println(UUIDStr);
			}
		}
		Vector<PDSXElementTemplate> templates = (Vector<PDSXElementTemplate>) (elementTemplates
				.get(entityType));
		for (int i = 0; i < templates.size(); i++) {
			PDSXElementTemplate template = templates.elementAt(i);
			XDI3Segment orderId = XDI3Segment.create(pc.getCloudNumber()
					.toString()
					+ "[+"
					+ entityType
					+ "]"
					+ "*"
					+ oid
					+ "<+"
					+ template.getName() + ">&");

			MessageResult result = pc.getXDIStmts(orderId,true);
			if (result.isEmpty()) {
				continue;
			}
			MemoryGraph response = (MemoryGraph) result.getGraph();

			Literal literalValue = response.getDeepLiteral(XDI3Segment
					.create(pc.getCloudNumber().toString() + "[+" + entityType
							+ "]" + "*" + oid + "<+" + template.getName()
							+ ">&"));
			String value = (literalValue == null) ? "" : literalValue
					.getLiteralData();
			PDSXElement element = new PDSXElement(entity, template, value);

		}
		return entity;

	}

	public static PDSXEntity get(PersonalCloud pc, String entityType,
			String oid, int order) {

		PDSXEntity entity = new PDSXEntity(entityType, "Unknown", oid);
		Vector<PDSXElementTemplate> templates = (Vector<PDSXElementTemplate>) (elementTemplates
				.get(entityType));
		for (int i = 0; i < templates.size(); i++) {
			PDSXElementTemplate template = templates.elementAt(i);
			XDI3Segment orderId = XDI3Segment.create(pc.getCloudNumber()
					.toString()
					+ "[+"
					+ entityType
					+ "]"
					+ "#"
					+ order
					+ "<+"
					+ template.getName() + ">&");

			MessageResult result = pc.getXDIStmts(orderId,true);
			MemoryGraph response = (MemoryGraph) result.getGraph();
			Literal literalValue = response.getDeepLiteral(XDI3Segment
					.create(pc.getCloudNumber().toString() + "[+" + entityType
							+ "]" + "#" + order + "<+" + template.getName()
							+ ">&"));
			String value = (literalValue == null) ? "" : literalValue
					.getLiteralData();
			PDSXElement element = new PDSXElement(entity, template, value);

		}
		return entity;
	}

	public static Vector<PDSXEntity> getAll(PersonalCloud pc, String entityType) {

		return null;
	}

	public void save(PersonalCloud pc) {
		ArrayList<XDI3Statement> XDIStmts = new ArrayList<XDI3Statement>();

		Iterator<PDSXElement> iter = elements.iterator();
		while (iter.hasNext()) {
			PDSXElement attr = iter.next();
			if (attr.getValue() != null) {
				XDI3Statement stmt = XDI3Statement.create(attr.getAddress(pc)
						.toString() + "/&/\"" + attr.getValue() + "\"");
				XDIStmts.add(stmt);
			}

		}
		XDI3Statement entityCount = XDI3Statement
				.create(getElementCountAddress(pc) + "/&/\"" + objectMap.size()
						+ "\"");
		XDIStmts.add(entityCount);

		XDI3Segment orderId = XDI3Segment.create(pc.getCloudNumber().toString()
				+ "[+" + objectType + "]" + "#" + order);

		MessageResult result = pc.getXDIStmts(orderId,true);
		if (result.isEmpty()) {

			XDI3Statement orderStmt = XDI3Statement.create(pc.getCloudNumber()
					.toString()
					+ "[+"
					+ objectType
					+ "]"
					+ "#"
					+ order
					+ "/$ref/" + this.getAddress(pc));
			XDIStmts.add(orderStmt);
		}
		
		XDI3Segment getNameStmt = XDI3Segment.create(pc.getCloudNumber().toString()
				+ "[+" + objectType + "]" + "*" + objectName);

		MessageResult objectExists = pc.getXDIStmts(getNameStmt,true);
		if (objectExists.isEmpty()) {
			XDI3Statement nameRefStmt = XDI3Statement.create(pc.getCloudNumber()
					.toString()
					+ "[+"
					+ objectType
					+ "]"
					+ "*"
					+ objectName
					+ "/$ref/" + this.getAddress(pc));
			XDIStmts.add(nameRefStmt);
		}
		

		pc.setXDIStmts(XDIStmts);

		// TODO
	}

	public static void addTemplate(String entityName,
			PDSXElementTemplate template) {
		Vector<PDSXElementTemplate> templates = (Vector<PDSXElementTemplate>) (elementTemplates
				.get(entityName));
		if (templates == null) {
			templates = new Vector<PDSXElementTemplate>();
			elementTemplates.put(entityName, templates);
		}
		templates.add(template);
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Type:" + objectType).append("\n");
		s.append("What:" + what).append("\n");
		s.append("Id:" + objectName).append("\n");
		s.append("# of elements:" + elements.size()).append("\n");

		for (int i = 0; i < elements.size(); i++) {
			PDSXElement element = (PDSXElement) elements.get(i);
			s.append(element.toString()).append("\n");
		}
		return s.toString();
	}

	private String objectType = "";
	private String what = "";
	private Vector<PDSXElement> elements = null;
	private String objectName = "";
	private static Vector<PDSXEntity> objectMap = new Vector<PDSXEntity>();
	private static Hashtable<String, Vector<PDSXElementTemplate>> elementTemplates = new Hashtable<String, Vector<PDSXElementTemplate>>();
	private int order;
	private String objectUUID;
}
