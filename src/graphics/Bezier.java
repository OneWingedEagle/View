package graphics;
//This applet draws a Bezier curve after user gives the control points
//The Bezier curve is redrawn when user clicks on and moves on of the
//control points
import java.awt.*;
import java.applet.Applet;
public class Bezier extends Applet {
	Point[] coordlist;	//the control points
	int numpoints;	//number of control points
	Image offscreenImg;	//used for double buffering
	Graphics offscreenG;
	double t;	//the time interval
	double k = .025;	//time step value for drawing curve
	int moveflag = 5;	//flag to notify if user is moving a point
	Button restart;
	boolean poly = true;	//flag to draw control polygon and points
	Button polygon;
	public void init() {
	//Create coordinate list
	coordlist = new Point[4];
	numpoints = 0;
	//Create offscreen buffer
	offscreenImg = createImage(size().width,size().height);
	offscreenG = offscreenImg.getGraphics();
	//add buttons to screen
	restart = new Button("Restart");
	add(restart);
	polygon = new Button("Polygon");
	add(polygon);
	}
	//this method is called internally by repaint()
	public void update(Graphics g) {
	paint(g);
	}
	public void paint(Graphics g) {
	//prepare screen
	setBackground(Color.white);
	offscreenG.setColor(Color.black);
	offscreenG.clearRect(0,0,size().width,size().height);
	//Draw control points and polygon
	if(poly) {
	for(int i=0;i<numpoints;i++) {
	offscreenG.fillOval(coordlist[i].x-2,
	coordlist[i].y-2,4,4);
	if(numpoints>1 && i<(numpoints-1))
	
	offscreenG.drawLine(coordlist[i].x,coordlist[i].y,
	coordlist[i+1].x,coordlist[i+1].y);
	}
	}
	//Calculate and draw bezier curve
	if(numpoints == 4) {
	double x1,x2,y1,y2;
	x1 = coordlist[0].x;
	y1 = coordlist[0].y;
	for(t=k;t<=1+k;t+=k){
	//use Berstein polynomials
	x2=(coordlist[0].x+t*(-coordlist[0].x*3+t*(3*coordlist[0].x-
	coordlist[0].x*t)))+t*(3*coordlist[1].x+t*(-6*coordlist[1].x+
	coordlist[1].x*3*t))+t*t*(coordlist[2].x*3-coordlist[2].x*3*t)+
	coordlist[3].x*t*t*t;
	y2=(coordlist[0].y+t*(-coordlist[0].y*3+t*(3*coordlist[0].y-
	coordlist[0].y*t)))+t*(3*coordlist[1].y+t*(-6*coordlist[1].y+
	coordlist[1].y*3*t))+t*t*(coordlist[2].y*3-coordlist[2].y*3*t)+
	coordlist[3].y*t*t*t;
	//draw curve
	offscreenG.drawLine((int)x1,(int)y1,(int)x2,(int)y2);
	x1 = x2;
	y1 = y2;
	}
	}
	g.drawImage(offscreenImg,0,0,this);

}
//check if user presses either button
public boolean action(Event e, Object o) {
	if (e.target == restart) {
	//start all over with no points
	numpoints = 0;
	repaint();
	poly = true;
	return true;
	}
	else if(e.target == polygon) {
	if(poly)
	poly = false;
	else
	poly = true;
	repaint();
	return true;
	}
	return false;

}
public boolean mouseDown(Event evt, int x, int y) {
	//Store point in array
	Point point = new Point(x,y);
	//if there are less than four points, add another one
	if(numpoints < 4) {
	coordlist[numpoints] = point;
	numpoints++;
	repaint();
	}
	//otherwise, check if user is trying to click on old point
	else
	for(int i=0;i<numpoints;i++)
	for(int j=-2;j<3;j++)
	for(int l=-2;l<3;l++)
	if(point.equals(new Point(coordlist[i].x+j,
	coordlist[i].y+l)))
	moveflag = i;
	return true;
	}
	public boolean mouseDrag(Event evt, int x, int y) {
	//check if user is trying to drag an old point
	if(moveflag < numpoints) {
	coordlist[moveflag].move(x,y);
	repaint();
	}
	return true;
	}
	public boolean mouseUp(Event evt, int x, int y) {
	moveflag = 5;
	return true;
	}	

}
	
