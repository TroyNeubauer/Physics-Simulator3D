package planet;
import java.util.*;

import org.lwjgl.opengl.GL11;

import com.troyberry.math.Maths;
import com.troyberry.opengl.util.*;

import main.PSGameSettings;
import sphere.Sphere;
import world.World;

public class AtmosphereRenderer
{
  private AtmosphereShader shader;
  private LodManager lod;
  
  public AtmosphereRenderer(ICamera camera)
  {
    this.shader = new AtmosphereShader();
    this.lod = new LodManager();
    this.lod.add(0, 200.0F * PSGameSettings.ATMOSPHERE_QUALITY);
    this.lod.add(1, 75.0F * PSGameSettings.ATMOSPHERE_QUALITY);
    this.lod.add(2, 0.0F);
  }
  
  public void render(World world)
  {
    Map<Integer, List<Planet>> list = new HashMap();
    double distance;
    int currentLOD;
    for (Sphere sphere : world.getObjects()) {
      if ((sphere != null) && 
        ((sphere instanceof Planet)))
      {
        distance = Maths.getDistanceBetweenPoints(world.getCamera().getPosition(), sphere.position) / sphere.scale;
        if(distance > PSGameSettings.ATMOSPHERE_RENDER_DISTANCE)continue;
        currentLOD = this.lod.get((float)distance);
        List<Planet> batch = (List)list.get(Integer.valueOf(currentLOD));
        if (batch != null)
        {
          batch.add((Planet)sphere);
        }
        else
        {
          List<Planet> newBatch = new ArrayList();
          newBatch.add((Planet)sphere);
          list.put(Integer.valueOf(currentLOD), newBatch);
        }
      }
    }
    ICamera camera = world.getCamera();
    prepare(camera);
    for (Integer lod : list.keySet())
    {
      sphere.SphereRenderer.vaos[lod.intValue()].bind(new int[] { 0 });
      for (Planet planet : list.get(lod)) {
        if (planet.atmosphereDensity != 0.0F)
        {
          double distanceFromCenter = Maths.getDistanceBetweenPoints(planet.position, camera.getPosition());
          
          double distanceFromSurface = distanceFromCenter - planet.scale;
          
          double distanceFromAtmosphere = distanceFromSurface - planet.atmosphereThickness;
          
          float bottomLayerStart = planet.scale;
          
          float topLayerEnd = bottomLayerStart + planet.atmosphereThickness;
          
          float atmosphereThickness = topLayerEnd - bottomLayerStart;
          float maxLayers = planet.atmosphereThickness * PSGameSettings.ATMOSPHERE_QUALITY;
          int layerCount = (int)Maths.clamp(1.0D, maxLayers, planet.atmosphereThickness * 75.0F / distanceFromAtmosphere);
          if (distanceFromAtmosphere < 0.0D) {
            layerCount = (int)maxLayers;
          }
          float totalAlpha = planet.atmosphereDensity / 100.0F;
          
          float atmosphereCenter = (topLayerEnd - planet.scale) / 2.0F;
          
          float eachLayerThickness = atmosphereThickness / layerCount;
          float eachLayerAlpha = totalAlpha / maxLayers;
          float cameraLayer = Maths.lerpSafe(0.0F, maxLayers, (float)distanceFromSurface / maxLayers);
          this.shader.alpha.loadFloat(eachLayerAlpha);
          this.shader.color.loadVec3(planet.atmosphereColor);
          float distFactor = 0.0F;
          for (int i = 0; i <= (int)cameraLayer; i++)
          {
            distFactor = i / cameraLayer;
            this.shader.transformationMatrix.loadMatrix(planet.getTransformationMatrix(Maths.lerpSafe(bottomLayerStart, topLayerEnd, distFactor)));
            GL11.glDrawElements(4, sphere.SphereRenderer.vaos[lod.intValue()].getIndexCount(), 5125, 0L);
          }
        }
      }
      sphere.SphereRenderer.vaos[lod.intValue()].unbind(new int[] { 0 });
    }
    finish();
  }
  
  public void cleanUp()
  {
    this.shader.cleanUp();
  }
  
  private void prepare(ICamera camera)
  {
    this.shader.start();
    this.shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
    this.shader.viewMatrix.loadMatrix(camera.getViewMatrix());
    GlUtil.enableAdditiveBlending();
    GlUtil.enableDepthTesting();
    GlUtil.cullBackFaces(false);
  }
  
  private void finish()
  {
    this.shader.stop();
  }
}
