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
	public static final int MAX_PARTICLES = 1000;
	public static final double GRAVITY_UPS = 2.0;
	private static final long TIME_PER_TICK = (long) (1000000000.0 / GRAVITY_UPS);
	private static long nextUpdate = -1, startTime = -1;

	private static CLMem pos, color, vel, prePos, postPos;
	private static CLKernel gravityKernel, initKernel, lerpKernel;
	private static CLProgram program;

	public static void create() {
		if (Options.showOpenCLInfo) System.out.println("Open CL Info:");
		device = CLPlatform.getPlatforms(Options.showOpenCLInfo, true).get(0).getDevices(CL10.CL_DEVICE_TYPE_GPU).get(0);

		CLDevice device = CLPlatform.getPlatforms(true, true).get(0).getDevices(CL_DEVICE_TYPE_GPU).get(0);
		try {
			program = new CLProgram(device, new MyFile("/test.cl"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		gravityKernel = program.getKernel("gravity");
		initKernel = program.getKernel("init");
		lerpKernel = program.getKernel("lerp");
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
		prePos = device.allocateMemory(MAX_PARTICLES * Float.SIZE);
		postPos = device.allocateMemory(MAX_PARTICLES * Float.SIZE);
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
		gravityKernel.setArg(index++, prePos);
		gravityKernel.setArg(index++, postPos);
		gravityKernel.setArg(index++, vel);
		gravityKernel.setArg(index++, color);
		gravityKernel.setArg(index++, MAX_PARTICLES);
		gravityKernel.setArg(index++, 0.1f);
		gravityKernel.setArg(index++, 2.0f);
		System.out.println(Timer.getString(gravityKernel.runWithTime(MAX_PARTICLES)));
	}

	public static void cleanUp() {
		pos.free();
		color.free();
		vel.free();
		prePos.free();
		postPos.free();
		device.free();
		
		gravityKernel.free();
		initKernel.free();
		lerpKernel.free();
		program.free();
	}

	private OpenCLManager() {
	}

}
