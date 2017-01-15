package planet;


import com.troyberry.opengl.shader.*;
import com.troyberry.util.data.MyFile;

public class AtmosphereShader
  extends ShaderProgram
{
  private static final MyFile VERTEX_SHADER = new MyFile(new String[] { "planet", "atmosphere.vert" });
  private static final MyFile FRAGMENT_SHADER = new MyFile(new String[] { "planet", "atmosphere.frag" });
  protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
  protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
  protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
  protected UniformVec3 color = new UniformVec3("color");
  protected UniformFloat alpha = new UniformFloat("alpha");
  
  public AtmosphereShader()
  {
    super(VERTEX_SHADER, FRAGMENT_SHADER, new String[] { "position" });
    super.storeAllUniformLocations(new Uniform[] { this.projectionMatrix, this.viewMatrix, this.transformationMatrix, this.color, this.alpha });
  }
}
