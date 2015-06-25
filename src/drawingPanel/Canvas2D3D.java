package drawingPanel;

import java.awt.Color;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;


	public class Canvas2D3D extends Canvas3D
	{
	public Canvas2D3D (GraphicsConfiguration config)
	{
	super(config);
	
	}
	
/*public void postRender()
{
J3DGraphics2D draw;
draw = this.getGraphics2D();
draw.setColor(Color.red);
draw.drawLine(0,0,500,400);
draw.flush(true);
}*/

public void postRender(String s, int i, int j)
{
	J3DGraphics2D draw;
	draw = this.getGraphics2D();
	draw.setColor(Color.red);
	draw.drawLine(0,0,500,400);
	draw.drawString(s,i,j);
	draw.flush(true);
}


}
