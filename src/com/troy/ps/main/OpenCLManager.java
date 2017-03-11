package com.troy.ps.main;

import static org.lwjgl.opencl.CL10.*;

import java.io.*;

import org.lwjgl.opencl.*;

import com.troy.ps.world.*;
import com.troyberry.logging.*;
import com.troyberry.math.*;
import com.troyberry.opencl.*;
import com.troyberry.opengl.mesh.*;
import com.troyberry.util.*;
import com.troyberry.util.data.*;

public class OpenCLManager {

	private static CLDevice device;
	public static final int MAX_PARTICLES = 50000;
	public static final double GRAVITY_UPS = 10.0;
	private static final long TIME_PER_TICK = (long) (1000000000.0 / GRAVITY_UPS);
	private static long nextUpdate = -1, startTime = -1;
	public static final int ATTRACTORS = 10;
	
	private static CLMem pos, color, vel, prePos, postPos, attractors;
	private static CLKernel gravityKernel, initKernel, lerpKernel, advencedGravity;
	private static CLProgram program, gravityTest;
	private static float mass = 50;

	public static void create() {
		if (Options.showOpenCLInfo) System.out.println("Open CL Info:");
		device = CLPlatform.getPlatforms(Options.showOpenCLInfo, true).get(0).getDevices(CL10.CL_DEVICE_TYPE_GPU).get(0);

		CLDevice device = CLPlatform.getPlatforms(true, true).get(0).getDevices(CL_DEVICE_TYPE_GPU).get(0);
		try {
			program = new CLProgram(device, new MyFile("/test.cl"));
			gravityTest = new CLProgram(device, new MyFile("/advancedgravity.cl"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		gravityKernel = program.getKernel("gravity");
		initKernel = program.getKernel("init");
		lerpKernel = program.getKernel("lerp");
		advencedGravity = gravityTest.getKernel("gravity");
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

	public static void setMem(Vbo posVbo, Vbo colorVbo, float[] velocities) {
		pos = device.getFromGLBuffer(CL_MEM_READ_WRITE, posVbo);
		color = device.getFromGLBuffer(CL_MEM_READ_WRITE, colorVbo);
		vel = device.allocateMemory(MyBufferUtils.createFloatBuffer(velocities));
		prePos = device.allocateMemory(MAX_PARTICLES * Float.BYTES);
		postPos = device.allocateMemory(MAX_PARTICLES * Float.BYTES);
		int[] attr = new int[MAX_PARTICLES * ATTRACTORS];
		for(int i = 0; i < MAX_PARTICLES; i++) {
			for(int j = 0; j < ATTRACTORS; j++) {
				attr[i * ATTRACTORS + j] = j;
			}
		}
		attractors = device.allocateMemory(MyBufferUtils.createIntBuffer(attr));
	}

	public static void update() {
		if (nextUpdate == -1) nextUpdate = System.nanoTime();
		while (System.nanoTime() > nextUpdate) {
			nextUpdate += TIME_PER_TICK;
			gravity();
			startTime = System.nanoTime();
		}
		float value = Maths.normalize(startTime, nextUpdate, System.nanoTime());
		lerp(value);
	}
	
	public static void forceUpdate() {
		gravity();
		startTime = System.nanoTime();
		nextUpdate = startTime + TIME_PER_TICK;
	}

	private static void gravity() {
		int index = 0;
		advencedGravity.setArg(index++, prePos);
		advencedGravity.setArg(index++, postPos);
		advencedGravity.setArg(index++, vel);
		advencedGravity.setArg(index++, color);
		advencedGravity.setArg(index++, attractors);
		advencedGravity.setArg(index++, ATTRACTORS);
		advencedGravity.setArg(index++, MAX_PARTICLES);
		advencedGravity.setArg(index++, 0.1f);
		advencedGravity.setArg(index++, mass);
		System.out.println(Timer.getString(advencedGravity.runWithTime(MAX_PARTICLES)));
	}

	public static void cleanUp() {
		pos.free();
		color.free();
		vel.free();
		prePos.free();
		postPos.free();
		attractors.free();
		
		gravityKernel.free();
		initKernel.free();
		lerpKernel.free();
		program.free();
		device.free();
	}

	private OpenCLManager() {
	}

}
