kernel void gravity(global float4* prePos, global float4* postPos, global float4* velocities, 
	global float4* colors, const int size, const float add, float mass) {
	
	const int itemId = get_global_id(0);
	if(itemId < size) {
		float4 pos = postPos[itemId];
		float4 vel = velocities[itemId];
				
		float4 otherPos, deltaVel;
		float gravity, dist, distSquared;
		for(int i = 0; i < size; i++) {
			
				otherPos = postPos[i];
				deltaVel = (float4) (otherPos.x - pos.x, otherPos.y - pos.y, otherPos.z - pos.z, 0.0f);
				distSquared = pow(deltaVel.x, 2.0f) + pow(deltaVel.y, 2.0f) + pow(deltaVel.z, 2.0f);
				dist = sqrt(distSquared);
				gravity = (mass * mass) / distSquared;
				
				deltaVel /= dist;
				
				deltaVel *= gravity;
				
				if(i != itemId) {
					vel += deltaVel;
				}
			
		}
		prePos[itemId] = pos;
		postPos[itemId] = pos + vel * add;
		velocities[itemId] = vel;
	}
}

kernel void lerp(global float4* positions, global float4* prePos, global float4* postPos, const float value) {
	const int itemId = get_global_id(0);
	positions[itemId] = prePos[itemId] + value * (postPos[itemId] - prePos[itemId]);
	
}

kernel void init(global float4* positions, global float4* prePos, global float4* postPos) {
	const int itemId = get_global_id(0);
	float4 pos = positions[itemId];
	prePos[itemId] = pos;
	postPos[itemId] = pos;
}
