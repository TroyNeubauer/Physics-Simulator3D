package com.troy.ps.cltypes;

import com.troyberry.opencl.*;
import com.troyberry.util.data.*;

public class NodeType extends CLType {

	public NodeType() {
		super("Node");
		addField(FieldData.createVectorType("min", 4, Float.TYPE));

		addField(FieldData.createVectorType("max", 4, Float.TYPE));
		
		addField(FieldData.createBasicType("bodyIndex", Integer.TYPE));
		addField(FieldData.createArrayType("children", 8, Integer.TYPE));
	}

}
