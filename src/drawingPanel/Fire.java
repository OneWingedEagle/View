package drawingPanel;
/*
The ultimate Fire by Andreas Hartl!!!
My first Java program woohoo :)
 */

import java.awt.*;
import java.applet.Applet;
import java.awt.image.*;


final class MyFire extends Canvas
{
	Image im = null;
	MemoryImageSource MemImageSrc = null;
	int backbuffer[] = null;
	int buffer[] = null;
	int pixels[] = null;
	int width = 200;
	int height = 200;

	static int RGB ( int r, int g, int b )
	{
		return ( 255 << 24 ) | ( r << 16) | ( g << 8 ) | b;
	}
	
	public void init ()
	{
		
		backbuffer = new int[width * height];
		buffer = new int[width * height];
		pixels = new int[width * height];
		
		MemImageSrc = new MemoryImageSource ( width, height, pixels, 0, width);
		MemImageSrc.setAnimated ( true );
		im = createImage ( MemImageSrc );
	}	
	
	private void update ()
	{
		int index = 0;
		int color = 0;
			
		System.arraycopy ( buffer, 0, backbuffer, 0, width * height ); 		
			
		for ( int i = 0; i < width; i++ )
			if ( (int)( Math.random () * 2) == 1 )
				backbuffer[(height-2)*width + i] = 255;
	
		for ( int y = 1; y < height-1; y++ )
			for ( int x = 1; x < width-1; x++ )
			{
				index = y * width + x;
				color = backbuffer[index];
				color += backbuffer[index-1];
				color += backbuffer[index+1];
				color += backbuffer[index + width];
				color += backbuffer[index - width];
				color = (int)( (double)color / 5.0 );
				if ( color > 0 )
					color--;
				buffer[index-width] = color; 	
			}

	}
	
	public void Draw ( Graphics g )
	{
		while ( true )
		{
			update ();
			for ( int i = 0; i < width * height; i++ )
				pixels[i] = RGB ( buffer[i], 0, 0 );
	 		MemImageSrc.newPixels ( 0, 0, width, height );
			g.drawImage ( im, 0, 0, null );
			g.drawString ( "Hello World!", 60, 120 );
		}	
	}
}

public class Fire extends Applet 
{
	public void paint(Graphics g) 
	{	
		MyFire Fire = new MyFire ();
		g.setColor (Color.red);
		Fire.init ();
		Fire.Draw (g);	
	}
}
