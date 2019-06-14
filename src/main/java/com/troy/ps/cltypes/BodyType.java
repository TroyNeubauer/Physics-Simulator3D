package com.troy.ps.cltypes;

import com.troyberry.opencl.*;

public class BodyType extends CLType {

	public BodyType() {
		super("Body");
		addField(FieldData.createVectorType("prePos", 4, Float.TYPE));
		addField(FieldData.createVectorType("postPos", 4, Float.TYPE));
		addField(FieldData.createVectorType("velocity", 4, Float.TYPE));
		
		addField(FieldData.createBasicType("mass", Float.TYPE));
		addField(FieldData.createBasicType("radius", Float.TYPE));
	}

}
