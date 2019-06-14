package com.troy.ps.cltypes;

import java.nio.*;
import java.util.*;

import com.troyberry.opencl.*;
import com.troyberry.util.data.*;

public class CLTypes {

	public static final NodeType NODE = new NodeType();
	public static final BodyType BODY = new BodyType();
	
	private static final List<CLType> types = new ArrayList<CLType>();
	static {
		types.add(NODE);
		types.add(BODY);
		System.out.println(NODE.getDefinition());
		System.out.println(BODY.getDefinition());
	}
	
	public static List<CLType> getTypes() {
		return types;
	}

	private CLTypes() {
	}

}
