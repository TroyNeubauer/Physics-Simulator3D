package com.troy.ps.main;

import static org.lwjgl.opencl.CL10.*;

import java.io.*;
import java.nio.*;
import java.util.*;

import com.troy.ps.cltypes.*;
import com.troy.ps.world.*;
import com.troyberry.logging.Timer;
import com.troyberry.math.*;
import com.troyberry.opencl.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.util.*;
import com.troyberry.util.data.*;

public class OpenCLManager {

	private static CLDevice device;
	private static CLPlatform platform;
	public static final int MAX_PARTICLES = 1000, BODIES = 64;
	public static final double GRAVITY_UPS = 3.0;
	private static final long TIME_PER_TICK = (long) (1000000000.0 / GRAVITY_UPS);
	private static long nextUpdate = -1, startTime = -1;

	private static CLMem pos, color, vel, prePos, postPos, radius, bodies;
	private static CLKernel gravityKernel, initKernel, lerpKernel, printBodiesKernel, createBodiesKernel;
	private static CLProgram program;
	private static float mass = 20;

	public static void create() {
		if (Options.showOpenCLInfo) System.out.println("Open CL Info:");
		List<CLPlatform> ps = CLPlatform.getPlatforms(Options.showOpenCLInfo, true);
		platform = ps.get(0);
		device = platform.getDevices(CL_DEVICE_TYPE_ALL).get(0);
		if (Options.showOpenCLInfo) System.out.println("Using device:" + device.toString());
		
		//for(String s : device.getExtensions())System.out.println("Has Extension: " + s);

		try {
			program = new CLProgram(device, new MyFile("/test.cl"), new File(new File("").getAbsolutePath(), "\\kernels\\"), true, CLTypes.getTypes());
		} catch (CLProgramBuildException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		gravityKernel = program.getKernel("gravity");
		initKernel = program.getKernel("init");
		lerpKernel = program.getKernel("lerp");
		printBodiesKernel = program.getKernel("printBodies");
		createBodiesKernel = program.getKernel("createBodies");
	}
	
	public static void printBodies(int offset, int count) {
		printBodiesKernel.setArg(0, bodies);
		printBodiesKernel.setArg(1, offset);
		printBodiesKernel.setArg(2, count);
		printBodiesKernel.run(count);
	}
	
	public static void createBodies(int offset, int count, float[] prePos, float[] postPos, float[] mass, float[] radius) {
		CLMem prePosMem = device.allocateMemory(TroyBufferUtils.createFloatBuffer(prePos));
		CLMem postPosMem = device.allocateMemory(TroyBufferUtils.createFloatBuffer(postPos));
		CLMem massMem = device.allocateMemory(TroyBufferUtils.createFloatBuffer(mass));
		CLMem radiusMem = device.allocateMemory(TroyBufferUtils.createFloatBuffer(radius));
		int index = 0;
		
		createBodiesKernel.setArg(index++, bodies);
		createBodiesKernel.setArg(index++, offset);
		createBodiesKernel.setArg(index++, count);
		
		createBodiesKernel.setArg(index++, prePosMem);
		createBodiesKernel.setArg(index++, postPosMem);
		createBodiesKernel.setArg(index++, massMem);
		createBodiesKernel.setArg(index++, radiusMem);
		
		createBodiesKernel.run(count);
		
		prePosMem.free();
		postPosMem.free();
		massMem.free();
		radiusMem.free();
	}

	public static void setWorld(World world) {
		int index = 0;
		initKernel.setArg(index++, pos);
		initKernel.setArg(index++, prePos);
		initKernel.setArg(index++, postPos);
		initKernel.run(MAX_PARTICLES);
	}

	public static void lerp(float value) {
		int index = 0;
		lerpKernel.setArg(index++, pos);
		lerpKernel.setArg(index++, prePos);
		lerpKernel.setArg(index++, postPos);
		lerpKernel.setArg(index++, value);
		lerpKernel.run(MAX_PARTICLES);
	}

	public static void setMem(Vbo posVbo, Vbo colorVbo, Vbo radiusVbo, float[] velocities) {
		pos = device.getFromGLBuffer(CL_MEM_READ_WRITE, posVbo);
		color = device.getFromGLBuffer(CL_MEM_READ_WRITE, colorVbo);
		radius = device.getFromGLBuffer(CL_MEM_READ_ONLY, radiusVbo);

		vel = device.allocateMemory(TroyBufferUtils.createFloatBuffer(velocities));

		prePos = device.allocateMemory(MAX_PARTICLES * Float.BYTES);
		postPos = device.allocateMemory(MAX_PARTICLES * Float.BYTES);
		
		bodies = device.allocateMemory(CLTypes.BODY.getBytes() * OpenCLManager.BODIES);
	}

	public static void update() {
		if (nextUpdate == -1) nextUpdate = System.nanoTime();
		device.acquireGLObjects(pos, color, radius);
		int count = 0;
		while (System.nanoTime() > nextUpdate) {
			nextUpdate += TIME_PER_TICK;
			gravity();
			startTime = System.nanoTime();
			count++;
			if (count > 25) break;
		}
		float value = Maths.normalize(startTime, nextUpdate, System.nanoTime());
		lerp(value);
		device.releaseGLObjects(pos, color, radius);
	}

	public static void forceUpdate() {
		gravity();
		nextUpdate = System.nanoTime() + TIME_PER_TICK;
	}

	private static void gravity() {
		int index = 0;
		gravityKernel.setArg(index++, prePos);
		gravityKernel.setArg(index++, postPos);
		gravityKernel.setArg(index++, vel);
		gravityKernel.setArg(index++, color);
		gravityKernel.setArg(index++, radius);
		gravityKernel.setArg(index++, MAX_PARTICLES);
		gravityKernel.setArg(index++, 0.1f);
		gravityKernel.setArg(index++, mass);
		String time = Timer.getString(gravityKernel.runWithTime(MAX_PARTICLES));
		//System.out.println(time);
	}

	public static void cleanUp() {
		pos.free();
		color.free();
		vel.free();
		prePos.free();
		postPos.free();
		bodies.free();
		
		program.free();

		gravityKernel.free();
		initKernel.free();
		lerpKernel.free();
		printBodiesKernel.free();
		createBodiesKernel.free();
		
		device.free();
	}

	private OpenCLManager() {
	}
	
	/**
			OpenCLManager.printBodies(0, OpenCLManager.BODIES);
			float[] prePos = new float[OpenCLManager.BODIES * 4];
			float[] postPos = new float[OpenCLManager.BODIES * 4];
			float[] mass = new float[OpenCLManager.BODIES];
			float[] radius = new float[OpenCLManager.BODIES];
			for(int i = 0; i < prePos.length; i++) prePos[i] = 0.5f;
			
			for(int i = 0; i < mass.length; i++) mass[i] = i * i;
			OpenCLManager.createBodies(0, OpenCLManager.BODIES, prePos, postPos, mass, radius);
			System.out.println("\n\nAfter Change:\n");
			OpenCLManager.printBodies(0, OpenCLManager.BODIES);
		
		}
		loop++;
	 */

}
