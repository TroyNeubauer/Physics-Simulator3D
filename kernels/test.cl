#pragma OPENCL EXTENSION cl_khr_global_int32_base_atomics : enable
#define CHILD_NODES 8
#if CONFIG_USE_DOUBLE

	#if defined ( cl_khr_fp64 )  // Khronos extension available?
		#pragma OPENCL EXTENSION cl_khr_fp64 : enable
		#define DOUBLE_SUPPORT_AVAILABLE
	#elif defined ( cl_amd_fp64 )  // AMD extension available?
		#pragma OPENCL EXTENSION cl_amd_fp64 : enable
		#define DOUBLE_SUPPORT_AVAILABLE
	#endif

#endif

#if defined (DOUBLE_SUPPORT_AVAILABLE)
	// double
	typedef double real;
	typedef double2 real2;
	typedef double3 real3;
	typedef double4 real4;
	typedef double8 real8;
	typedef double16 real16;
	#define PI 3.14159265358979323846
#else
	// float
	typedef float real;
	typedef float2 real2;
	typedef float3 real3;
	typedef float4 real4;
	typedef float8 real8;
	typedef float16 real16;
	#define PI 3.14159265359f
#endif

INCLUDE:Body
/*struct __attribute__ ((packed)) Node {
	float4 min;
	float4 max;
	int bodyIndex;
	int children[8];};*/
INCLUDE:Node
/*struct __attribute__ ((packed)) Body {
	float4 prePos;
	float4 postPos;
	float4 velocity;
	float mass;
	float radius;};*/

__kernel void printBodies( __global struct Body* bodies, const int offset, const int count) {
	const int id = get_global_id (0);
	if (id < count) {
		int theOne = id + offset;
		printf ("prePos [%f, %f, %f, %f], postPos [%f, %f, %f, %f], mass %f, radius %f\n", bodies[theOne].prePos.x, bodies[theOne].prePos.y, bodies[theOne].prePos.z, bodies[theOne].prePos.w, bodies[theOne].postPos.x, bodies[theOne].postPos.y, bodies[theOne].postPos.z, bodies[theOne].postPos.w, bodies[theOne].mass, bodies[theOne].radius);
	}
}

__kernel void createBodies( __global struct Body* bodies, const int offset, const int count, __global real4* prePos, __global real4* postPos, __global real* mass, __global real* radius) {
	const int id = get_global_id (0);
	if (id < count) {
		int theOne = id + offset;
		bodies[theOne].prePos = prePos[id];
		bodies[theOne].postPos = postPos[id];
		bodies[theOne].mass = mass[id];
		bodies[theOne].radius = radius[id];
	}
}

__kernel void gravity( __global real4* prePos, __global real4* postPos, __global real4* velocities, __global real4* colors, __global real* radii, 
const int size, const real add, const real mass) {

	const int itemId = get_global_id (0);
	if(itemId < size) {
		real4 pos = postPos[itemId];
		real4 vel = velocities[itemId];
		real radius = radii[itemId];
		real4 otherPos, deltaVel;
		real gravity, dist, distSquared;
		for (int i = 0; i < size; i++) {
			otherPos = postPos[i];
			deltaVel = (real4) (otherPos.x - pos.x, otherPos.y - pos.y, otherPos.z - pos.z, 0.0f);
			distSquared = pow (deltaVel.x, 2.0f) + pow(deltaVel.y, 2.0f) + pow(deltaVel.z, 2.0f);
			dist = sqrt (distSquared);
				
			gravity = (mass * mass) / distSquared;
				
			deltaVel /= dist;
				
			deltaVel *= gravity;
				
			if (i != itemId) {
				if ((radius + radii[i]) * 10 < dist) {
					vel += deltaVel;
				}
			}
		}
		prePos[itemId] = pos;
		postPos[itemId] = pos + vel * add;
		velocities[itemId] = vel;
	}
}

__kernel void lerp( __global real4* positions, __global real4* prePos, __global real4* postPos, const real value) {
	const int itemId = get_global_id(0);
	positions[itemId] = prePos[itemId] + value * (postPos[itemId] - prePos[itemId]);
	
}

__kernel void init( __global real4* positions, __global real4* prePos, __global real4* postPos) {
	const int itemId = get_global_id(0);
	real4 pos = positions[itemId];
	prePos[itemId] = pos;
	postPos[itemId] = pos;
}
