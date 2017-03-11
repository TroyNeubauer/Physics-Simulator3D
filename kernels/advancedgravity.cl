kernel void gravity(global float4* prePos, global float4* postPos, global float4* velocities, 
	global float4* colors, global int* attractors, const int attractorCount, const int size, const float add, float mass) {
	
	const int itemId = get_global_id(0);
	if(itemId < size) {
		float4 pos = postPos[itemId];
		float4 vel = velocities[itemId];
		float radius = cbrt(mass);
		float4 otherPos, deltaVel;
		float gravity, dist, distSquared;
		int i;
		for(int j = 0; j < attractorCount; j++) {
			i = attractors[j * attractorCount + j];
			otherPos = postPos[i];
			deltaVel = (float4) (otherPos.x - pos.x, otherPos.y - pos.y, otherPos.z - pos.z, 0.0f);
			distSquared = pow(deltaVel.x, 2.0f) + pow(deltaVel.y, 2.0f) + pow(deltaVel.z, 2.0f);
			dist = sqrt(distSquared);
				
			gravity = (mass * mass) / distSquared;
				
			deltaVel /= dist;
				
			deltaVel *= gravity;
				
			if(i != itemId) {
				if((2.0f * radius) < dist){
					vel += deltaVel;
				}
			}
			
		}
		prePos[itemId] = pos;
		postPos[itemId] = pos + vel * add;
		velocities[itemId] = vel;
	}
}