package planet;


import com.troyberry.math.Vector3f;
import com.troyberry.opengl.util.GLColorUtil;

import sphere.Sphere;

public class Planet
  extends Sphere
{
  public float atmosphereThickness;
  public Vector3f atmosphereColor = GLColorUtil.BLUE;
  public float atmosphereDensity;
  
  public Planet(Vector3f position, Vector3f velocity, Vector3f color, float scale, float mass, float atmosphereThickness, float atmosphereDensity)
  {
    super(position, velocity, color, scale, mass);
    this.atmosphereThickness = (atmosphereThickness * scale);
    this.atmosphereDensity = atmosphereDensity;
  }
}
