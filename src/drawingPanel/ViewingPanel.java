package drawingPanel;
import components.Button;
import components.*;
import components.Label;
import components.TextField;
import graphics.*;
import panels.*;
import materialData.MaterialData;
import math.Mat;
import math.Vect;
import math.util;
import fem.Model;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.*;

import com.sun.j3d.utils.universe.*;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.image.TextureLoader;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class ViewingPanel extends JPanel implements ActionListener {
	public ButtonIcon bShowAxes,bShowMesh,bShowField,bDeform,bAnimation,bShot;
	public Button bLoadMesh, bClear,bNavL, bNavR,bChangeBackground,bRotate,bApplyVscale, 
	bNavU ,bNavD,bNavZoomIn,bNavZoomOut,bFindValue,bColorChooser;
	private Button bInfo,bRefresh;
	public ButtonIcon bDefaultView,bFullScreen;
	public JComboBox  stressDist;
	public TextField tfVectorScale,tfX1,tfX2;;
	private JPanel  regButtonPanel ,southPanel, centerPanel,colBar,eastPanel;
	public FindValue fv = new FindValue();
	private JFrame messageFrame,fsFrame;
	private TextField tfNumbRegs;
	private Button[] regButton,regEditButton;
	private JColorChooser cch=new JColorChooser();
	private EditReg editReg=new EditReg();
	private EditLight editLight;
	public int  zoom = 0,nLoadedMesh;
	private BranchGroup group;
	private TransformGroup univGroup,femGroup;
	private Cartesian cartesian;
	private Cartesian2D cartesian2D;
	public SurfFacets[] surfFacets;
	private SimpleUniverse universe;
	public Color[] regionMatColor;
	public Color[] regEdgeColor;
	public double[] faceTransp;
	public double[] edgeTransp;
	private double spaceTransparency = .85;
	public MaterialData matData;
	public Canvas3D canvas;
	public int nChosenRegion=0, nBoundary = 6;
	public int decimal = 3, numberOfElements, numberOfRegions;
	public double  scaleFactor,vScale0=1,vScale,vScalefact=1,moveStep0,moveStep,Vmin,Vmax,rng=0;
	private Vect camEye,camEye0=new Vect(-.2,-1,1), target,target0=new Vect(3),upVect,upVect0=new Vect(0,0,1);
	public boolean meshDrawn = false,meshLoaded,axesShown,meshShown,fieldShown,runMotor;
	public boolean[] setRegion;
	
	public String bunFilePath, dataFilePath, fluxFilePath, fluxFilePath1,
	eddyFilePath, elastDataFilePath, vPotFilePath,elType;
	ColorBar cBar=new ColorBar();
	private JScrollPane messageScrollPane;
	public JTextArea messageArea;
	public int fieldMode,defMode,dim=3;
	public JProgressBar progressBar;
	private Thread progressThread;
	private Background background;
	ImageComponent2D image;
	private int  width,height,backgroundMode=0,unitIndex=1,nfs;
	private MouseRotate mouseRotate;
	private MouseTranslate mouseTranslate;
	private double[] spaceBoundary=new double[6];
	public TextField[] tfc=new TextField[2];
	private String cBarTitle;
	public boolean divert=false,vectFieldSet;
	public JPanel drwpNorth ;
	public JSlider jslider; 
	private AmbientLight lightAmb;
	private DirectionalLight[] light;


	public ViewingPanel() {
		
		// Constructing the drawing panel north ===================== begin
		
		 drwpNorth = new JPanel(new FlowLayout(0, 10, 10));
		width=1000;
		height=800;
		for(int i=0;i<spaceBoundary.length;i++)
			if(i%2==0)
			spaceBoundary[i]=-1000;
			else
				spaceBoundary[i]=1000;
				
		tfc[0]=new TextField("");
		tfc[1]=new TextField("");
		
		this.bDeform = new ButtonIcon();
		this.bDeform.setPreferredSize(new Dimension(30, 30));
		this.bDeform.setImageIcon("deform.jpg","derom");
		
		this.bAnimation = new ButtonIcon();
		this.bAnimation.setImageIcon("animation.jpg","animate results");
		this.bAnimation.setPreferredSize(new Dimension(30, 30));
		
		this.bShot = new ButtonIcon();
		this.bShot.setImageIcon("capture.jpg","screen shot");
		this.bShot.setPreferredSize(new Dimension(30, 30));

		this.bDefaultView = new ButtonIcon();
		this.bDefaultView.setPreferredSize(new Dimension(30, 30));
		this.bDefaultView.setImageIcon("defView.jpg","Default View");
		
		this.bClear = new Button();
		this.bClear.setPreferredSize(new Dimension(30, 30));
		this.bClear.setImageIcon("clear.jpg", "Add block");
		
		
		this.bChangeBackground = new Button();
		this.bChangeBackground.setPreferredSize(new Dimension(30, 30));
		this.bChangeBackground.setImageIcon("cbkg.jpg","Change Background Color");
		
		this.bRotate = new Button();
		this.bRotate.setPreferredSize(new Dimension(30, 30));
		this.bRotate.setImageIcon("rotate.jpg","Rotate Rotor");

		this.bRefresh = new Button();
		this.bRefresh.setPreferredSize(new Dimension(30, 30));
		this.bRefresh.setImageIcon("refresh.jpg","Refresh");

		this.bLoadMesh = new Button();
		this.bLoadMesh.setPreferredSize(new Dimension(30, 30));
		this.bLoadMesh.setImageIcon("loadModel.jpg","Load Mesh");

		this.bInfo = new Button();
		this.bInfo.setPreferredSize(new Dimension(30, 30));
		this.bInfo.setImageIcon("info.jpg","info");

		this.bNavL = new Button();	
		this.bNavU = new Button();
		this.bNavR = new Button();
		this.bNavD = new Button();
		this.bNavZoomIn = new Button();
		this.bNavZoomOut = new Button();
		this.bNavL.setImageIcon("arrowLeft.jpg","left");
		this.bNavR.setImageIcon("arrowRight.jpg","right");
		this.bNavU.setImageIcon("arrowUp.jpg","up");
		this.bNavD.setImageIcon("arrowDown.jpg","down");
		this.bNavZoomIn.setImageIcon("zoomIn.jpg","Zoom In");
		this.bNavZoomOut.setImageIcon("zoomOut.jpg","Zoom In");
		

		this.bNavL.setMargin(new Insets(0,0,0,0));  
		this.bNavR.setMargin(new Insets(0,0,0,0));  
		this.bNavD.setMargin(new Insets(0,0,0,0));  
		this.bNavU.setMargin(new Insets(0,0,0,0));  
		this.bNavZoomIn.setMargin(new Insets(0,0,0,0));  
		this.bNavZoomOut.setMargin(new Insets(0,0,0,0));  

		
		this.bNavL.setPreferredSize(new Dimension(18, 18));
		this.bNavR.setPreferredSize(new Dimension(18, 18));
		this.bNavU.setPreferredSize(new Dimension(18, 18));
		this.bNavD.setPreferredSize(new Dimension(18, 18));
		this.bNavZoomIn.setPreferredSize(new Dimension(18, 18));
		this.bNavZoomOut.setPreferredSize(new Dimension(18, 18));
		
		this.bNavL.addActionListener(this);
		this.bNavR.addActionListener(this);
		this.bNavU.addActionListener(this);
		this.bNavD.addActionListener(this);
		this.bNavZoomIn.addActionListener(this);
		this.bNavZoomOut.addActionListener(this);
		this.bChangeBackground.addActionListener(this);
		

		this.bFullScreen = new ButtonIcon();
		this.bFullScreen.setPreferredSize(new Dimension(30, 30));
		this.bFullScreen.setImageIcon("maxmin.jpg","Full Screen");
		
		
		this.bShowAxes = new ButtonIcon();
		this.bShowAxes.setPreferredSize(new Dimension(30, 30));
		this.bShowAxes.setImageIcon("axes.jpg","Show/Hide axes");
		
		this.bShowMesh = new ButtonIcon();
		this.bShowMesh.setPreferredSize(new Dimension(30, 30));
		this.bShowMesh.setImageIcon("discretize.jpg","Show/Hide Mesh");
		
		this.bShowField = new ButtonIcon();
		this.bShowField.setPreferredSize(new Dimension(30, 30));
		this.bShowField.setImageIcon("field.jpg","Show/Hide Field");
		
		

		this.tfVectorScale = new TextField("1.0");
		this.tfVectorScale.setPreferredSize(new Dimension(55, 25));
		
		this.tfX1 = new TextField("1.0");
		this.tfX1.setPreferredSize(new Dimension(45, 25));
		this.tfX2 = new TextField("1.0");
		this.tfX2.setPreferredSize(new Dimension(55, 25));
		this.bColorChooser = new Button();
		this.bColorChooser.setMargin(new Insets(0,0,0,0));  
		this.bColorChooser.setPreferredSize(new Dimension(26, 26));
		
		this.bColorChooser.setImageIcon("colorPicker.jpg","adjust light");
		
		
		jslider = new JSlider(JSlider.HORIZONTAL,
	            1, 100, 2);
		jslider.setPreferredSize(new Dimension(100, 30) );
		jslider.setMajorTickSpacing(20);
		jslider.setValue(50);
		jslider.setPaintTicks(true);
		///  message frame ====
		this.messageArea = new JTextArea();
		this.messageArea.setBackground(Color.white);

		this.messageArea.setFont(new Font("Arial", 0, 14));
		this.messageArea.setEditable(false);

		this.messageScrollPane = new JScrollPane(this.messageArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.messageScrollPane.setPreferredSize(new Dimension(500, 125));
		this.messageScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 15,10));

		this.messageFrame = new JFrame(" Analysis Progress");
		this.messageFrame.setLocation(50, 550);
		this.messageFrame.setPreferredSize(new Dimension(600, 400));
		this.messageFrame.add(this.messageScrollPane);
		
		
		String[] unitOption = {"\u03C3"+"xx", "\u03C3"+"yy",  "\u03C3"+"zz","\u03C3"+"rr", "\u03C3"+"tt","\u03C3"+"meis"};
		this.stressDist = new JComboBox(unitOption);
			this.stressDist.setSelectedIndex(5);
		this.stressDist.setEnabled(true);
		
		
		
		
		// fullscreen frame========
		this.fsFrame=new JFrame();
		this.fsFrame.setSize(1500, 1200);
		//=========================
	

		//==== Navigation panel 
		Panel navPanel=new Panel(new GridLayout(2,3));
		navPanel.add(bNavL);
		navPanel.add(bNavZoomIn);
		navPanel.add(bNavR);
		navPanel.add(bNavD);
		navPanel.add(bNavZoomOut);
		navPanel.add(bNavU);
	
		drwpNorth.add(this.bDefaultView);
		drwpNorth.add(this.bAnimation);
		drwpNorth.add(this.bDeform);
		drwpNorth.add(this.bLoadMesh);
		drwpNorth.add(this.bClear);
		drwpNorth.add(this.bRefresh);
		drwpNorth.add(this.bChangeBackground);
		drwpNorth.add(this.bInfo);
		drwpNorth.add(this.bFullScreen);
		drwpNorth.add(this.bShowAxes);
		drwpNorth.add(this.bShowMesh);
		drwpNorth.add(this.bShowField);
		drwpNorth.add(navPanel);
		drwpNorth.add(this.bRotate);
		drwpNorth.add(this.tfX1);
		drwpNorth.add(jslider);
		drwpNorth.add(this.tfVectorScale);
		drwpNorth.add(this.bShot);
		drwpNorth.add(this.bColorChooser);

	
		//==========================
				
		// Constructing Constructing the drawing panel north  ===================== end
		
		
		
		// Constructing Constructing the drawing panel West  ===================== begin
		
		 JTabbedPane drwpWest = new JTabbedPane();
		 drwpWest.setFont(new Font("Arial", 1, 12));

		 
			this.tfNumbRegs = new TextField("0");
			
			this.regButtonPanel = new JPanel(new  FlowLayout(0, 1, 10));
			this.regButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			this.regButtonPanel.setPreferredSize(new Dimension(130, 3000));
			Label lbNumbRegs = new Label("Number of Regions");
			lbNumbRegs.setFont(new Font("Times New Roman", 1, 12));
			this.tfNumbRegs.setPreferredSize(new Dimension(60, 25));
			this.tfNumbRegs.setEditable(false);

			this.regButtonPanel.add(lbNumbRegs);
			this.regButtonPanel.add(this.tfNumbRegs);
			
			drwpWest.addTab("Regions",regButtonPanel);
			
			JScrollPane westScrollPane = new JScrollPane(drwpWest,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Constructing Constructing the drawing panel West  ===================== end



		// Constructing Constructing the drawing panel East  ===================== begin

		eastPanel= new JPanel(new FlowLayout(0, 1, 10));
			//eastPanel.setBorder(BorderFactory.createLineBorder(Color.black));
			eastPanel.setPreferredSize(new Dimension(100, 1000));
			eastPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			this.bApplyVscale = new Button("Apply");
			
			
			
		//
			eastPanel.add(this.tfX2);
			eastPanel.add(this.bApplyVscale);

		// Constructing Constructing the drawing panel East  ===================== end
	
		// Constructing SimpleUniverse ===================== begin
		
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		this.canvas = new Canvas3D(config);

		centerPanel = new JPanel(new GridLayout(1, 1));
		centerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		centerPanel.add(this.canvas);
	

		this.universe = new SimpleUniverse(this.canvas);
	
		
		group = new BranchGroup();
		group.setCapability(BranchGroup.ALLOW_DETACH);  
	//	group.setCapability(Group.ALLOW_CHILDREN_WRITE);  
		
		this.background = new Background();
		background.setColor(new Color3f(new Color(250,250,255)));
		/*TextureLoader loader = new TextureLoader(main.Main.class.getResource("sky2.jpg"), this );
		this.image = loader.getScaledImage((int)(1.4*this.width), (int)(1.1*this.height));
		this.background.setImage(this.image);*/
		BoundingSphere sphere = new BoundingSphere(new Point3d(0, 0, 0), 1000);
		this.background.setApplicationBounds(sphere);
		this.group.addChild(this.background);
	
			
			
			
			
		
		// Constructing SimpleUniverse ===================== end
		
					
		this.bunFilePath = System.getProperty("user.dir") + "//bun.txt";
		this.fluxFilePath = System.getProperty("user.dir") + "//flux.txt";
		this.fluxFilePath1 = System.getProperty("user.dir") + "//flux";
		this.eddyFilePath = System.getProperty("user.dir") + "//eddy.txt";
		this.vPotFilePath = System.getProperty("user.dir") + "//vPot.txt";
		this.dataFilePath = System.getProperty("user.dir") + "//data.txt";
		this.elastDataFilePath = System.getProperty("user.dir") + "//elastData.txt";



		JScrollPane southScrollPane = new JScrollPane(this.southPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		


		setLayout(new BorderLayout());
		add("North", drwpNorth);
		add("South", southScrollPane);
		add("East", eastPanel);
		add("West", westScrollPane);
		add("Center", centerPanel);

		this.bDefaultView.addActionListener(this);
		this.bClear.addActionListener(this);
		this.bInfo.addActionListener(this);
		this.bRefresh.addActionListener(this);
		this.bShowMesh.addActionListener(this);
		this.bShowField.addActionListener(this);
		this.bShowAxes.addActionListener(this);
		this.bShot.addActionListener(this);
		this.bColorChooser.addActionListener(this);
		
		this.bLoadMesh.addActionListener(this);
		
		this.bFullScreen.addActionListener(this);

		this.editReg.bApply.addActionListener(this);

		
		camEye=camEye0.deepCopy();
		target=target0.deepCopy();
		upVect=upVect0.deepCopy();
		newViewer();
		getScene();
		
		this.editLight.bApply.addActionListener(this);
		
		this.universe.addBranchGraph(this.group);
		
						

	}

	public void getScene() {

		this.group.setCapability(BranchGroup.ALLOW_DETACH);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		this.group.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				1000);

		mouseRotate = new MouseRotate();

		 mouseTranslate = new MouseTranslate();
		mouseTranslate.setFactor(.001,.001);

		this.univGroup = new TransformGroup();
		this.univGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.univGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		mouseRotate.setTransformGroup(this.univGroup);
		mouseTranslate.setTransformGroup(this.univGroup);

		this.univGroup.addChild(mouseRotate);
		this.univGroup.addChild(mouseTranslate);

		mouseRotate.setSchedulingBounds(bounds);
		mouseTranslate.setSchedulingBounds(bounds);

		this.canvas.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int steps = e.getWheelRotation();
				ViewingPanel.this.zoom -= steps;
				camEye=camEye.add(target.sub(camEye).times(.1*steps));
				newViewer();
			}
		});

		View view = this.universe.getViewer().getView();            
		// far way objects are not shown
		view.setBackClipDistance(100.0d);
		// very close objects are also clipped
		view.setFrontClipDistance(0.001d);             
	//	this.blockGroup = new TransformGroup();
	//	this.univGroup.addChild(this.blockGroup);
		this.cartesian = new Cartesian(this.spaceBoundary, Color.red, Color.green.darker(),
				Color.blue);
		
	//	this.univGroup.addChild(this.cartesian);
		this.femGroup = new TransformGroup();

		
		addLights();

		this.group.addChild(this.univGroup);


	}


	public void newViewer() {

		
		Transform3D lookAt = new Transform3D();
		lookAt.lookAt(new P3d(camEye), new P3d(target),
				new Vector3d(upVect.el[0],upVect.el[1],upVect.el[2]));

		lookAt.invert();
		this.universe.getViewingPlatform().getViewPlatformTransform()
		.setTransform(lookAt);

	}

	

	public void resetView() {

		this.univGroup.setTransform(new Transform3D());
		camEye=camEye0.deepCopy();
		target=target0;
		this.zoom = 0;
		newViewer();
	}

	public void fullScreen() {
		if(nfs%2==0){
			centerPanel.remove(canvas);
			fsFrame.add(canvas);
			fsFrame.setVisible(true);
		}
		else{		
			fsFrame.remove(canvas);	
			fsFrame.setVisible(false);
			centerPanel.add(canvas);

		}
		nfs++;
		repaint();

	}

	public void addLights() {
		
		int N=6;
		
		light=new DirectionalLight[N];
		
		editLight=new EditLight(N+1);
		
		
		for(int i=0;i<N;i++)
			 light[i] = new DirectionalLight(editLight.color[i],	editLight.direction[i]);


	 
	 lightAmb=new AmbientLight(editLight.color[N]);


	 
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);

		lightAmb.setInfluencingBounds(bounds);
		
		for(int i=0;i<light.length;i++){
		
		light[i].setCapability(DirectionalLight.ALLOW_COLOR_WRITE);
		light[i].setCapability(DirectionalLight.ALLOW_DIRECTION_WRITE);
		light[i].setInfluencingBounds(bounds);
		
		univGroup.addChild(light[i]);	 

		}

		
		lightAmb.setCapability(DirectionalLight.ALLOW_COLOR_WRITE);
				
	
		univGroup.addChild(lightAmb);	 
	 
	 
		}
	
	

	public void actionPerformed(ActionEvent e) {

	if (e.getSource() == this.bDefaultView) {


			resetView();

		}

		else if (e.getSource() == this.bFullScreen) {

			fullScreen();

		}


		else if (e.getSource() == this.bInfo) {
			this.messageFrame.setVisible(true);
			this.messageFrame.pack();

		}

		else if (e.getSource() == this.bFindValue) {


			this.fv.setLocation(getX() + 800, getY() +400);
			this.fv.setVisible(true);



		}

		else if (e.getSource() == this.bRefresh) {
		
	/*	if(fieldMode<4)
				this.showVectField(true);
			else*/
			
			//	this.showMesh(true);
				this.refreshRegions();


		}
	
		else if (e.getSource() == this.bLoadMesh) {
			
			
			}
		else if (e.getSource() == this.bShot) {
			
			takeShot();
			}
			
		else if (e.getSource() == this.bChangeBackground)
			changeBackground();
		
		else if (e.getSource() == this.bShowAxes){
			
			
			axesShown=!axesShown;
			showAxes(axesShown);
		

		}
	
		else if (e.getSource() == this.bShowMesh){
			setMeshVisible(meshShown);
			meshShown=!meshShown;
		
			

		}
	
		else if (e.getSource() == this.bShowField){
		
			fieldShown=!fieldShown;
			showVectField(fieldShown);


		}
		else if (e.getSource() == this.bColorChooser){
		
		//	tempColor=cch.showDialog(new JFrame(), "Pick a Color",Color.red);
			//this.regionMatColor[this.nChosenRegion]=tempColor;
			Point p=this.bColorChooser.getLocationOnScreen();
			this.editLight.setLocation(p.x+5,p.y+10);
			this.editLight.setVisible(true);

		}
	
		else if (e.getSource() == this.bClear){
			clearStuff();
		}
	
		else if (e.getSource() == this.bNavL)
			moveLeft();
		else if (e.getSource() == this.bNavR)
			moveRight();
		else if (e.getSource() == this.bNavU)
			moveUp();
		else if (e.getSource() == this.bNavD)
			moveDown();
		else if (e.getSource() == this.bNavZoomIn)
			zoomIn();
		else if (e.getSource() == this.bNavZoomOut)
			zoomOut();
		else if (e.getSource() == this.editLight.bApply){
			editLight.setColors();
			for(int i=0;i<editLight.color.length-1;i++){
				light[i].setColor(this.editLight.color[i]);
				light[i].setDirection(this.editLight.direction[i]);
				
			}
			
			int L=light.length;
				this.lightAmb.setColor(this.editLight.color[L]);
		}
	
	
		else if (e.getSource() == this.editReg.bApply){
			
			int ir=this.nChosenRegion;
			if(editReg.faceColor!=null)
			this.regionMatColor[ir]=editReg.faceColor;
			
			if(editReg.edgeColor!=null)
			this.regEdgeColor[ir]=editReg.edgeColor;
			this.surfFacets[ir].showRegEdge=!editReg.hideEdge;
			this.surfFacets[ir].showRegFace=!editReg.hideFace;
			
			this.faceTransp[ir]=Double.parseDouble(editReg.tfFaceTransp.getText());
			this.edgeTransp[ir]=Double.parseDouble(editReg.tfEdgeTransp.getText());

			
			this.surfFacets[ir].showRegion=this.surfFacets[ir].showRegFace||this.surfFacets[ir].showRegEdge;
			
			
			if(this.surfFacets[ir].showRegFace)
			regButton[ir].setBackground(this.regionMatColor[ir]);
				else
					regButton[ir].setBackground(Color.white);
			
			
				regButton[ir].setBorderPainted(this.surfFacets[ir].showRegEdge);
				
			refreshRegions();
			
		}
		else{

			for( int i=1;i<=this.numberOfRegions;i++){

				if(e.getSource() == this.regButton[i]) {

						this.nChosenRegion =i;
						this.surfFacets[i].switchVisibility();
						
						if(this.surfFacets[i].showRegion){
							this.faceTransp[i]=0;
						this.edgeTransp[i]=0;
						
						
					
						regButton[this.nChosenRegion].setBackground(this.regionMatColor[this.nChosenRegion]);

						}
					
						else
							regButton[this.nChosenRegion].setBackground(Color.white);
						
						refreshRegions();

					
						 this.editReg.setVisible(false);
						 
						 				
					break;

				}
				else 	if(e.getSource() == this.regEditButton[i]) {

					this.nChosenRegion =i;

					this.editReg.faceColor=this.regionMatColor[i];
					 this.editReg.edgeColor=this.regEdgeColor[i];
					 
					 editReg.hideFace=!this.surfFacets[i].showRegFace;
					 editReg.hideEdge=!this.surfFacets[i].showRegEdge;
		
					 editReg.fcch.setSelected(editReg.hideFace);
					 editReg.edch.setSelected(editReg.hideEdge);
							 
					 editReg.regch.setSelected(!this.surfFacets[i].showRegion);
					 
						editReg.tfFaceTransp.setText(Double.toString(this.faceTransp[i]));
						editReg.tfEdgeTransp.setText(Double.toString(this.edgeTransp[i]));
						

				
					Point p=regEditButton[i].getLocationOnScreen();
					 this.editReg.setLocation(p.x+5,p.y+10);
					 
					 this.editReg.setVisible(true);
			
				break;

			}
			}
		
		
		}
	}




	public void addRegButton(Model model,int ir) {
		String name=model.region[ir].getName();
		
		this.regButton[ir] = new Button(ir+". "+name,Button.LEFT);
		this.regButton[ir].setName(Integer.toString(ir));
		if(this.setRegion[ir]){
				this.regButton[ir].setBackground(this.regionMatColor[ir]);
		}
		this.regButton[ir].addActionListener(this);
		this.regButton[ir].setPreferredSize(new Dimension(85, 20));
		
		regEditButton[ir]=new Button();
		regEditButton[ir].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		regEditButton[ir].setText(">");
		
		
		this.regEditButton[ir].addActionListener(this);
		this.regEditButton[ir].setPreferredSize(new Dimension(15, 20));
		
		this.regButton[ir].setMargin(new Insets(1,1,1,1));  
		this.regButtonPanel.add(this.regButton[ir]);
		this.regButtonPanel.add(regEditButton[ir]);
		this.regButtonPanel.updateUI();
		this.tfNumbRegs.setText(Integer.toString(ir));

	}


	public void changeBackground() {
		this.backgroundMode++;
		this.group.detach();

		int mode=this.backgroundMode%5;

		if (mode==1) {

			TextureLoader loader = new TextureLoader(main.Main.class.getResource("sky.jpg"), this );
			ImageComponent2D image = loader.getScaledImage((int)(1.5*this.width), (int)(1.5*this.height));
			this.background.setImage(image);

		}
		if (mode==2) {
			TextureLoader loader = new TextureLoader(main.Main.class.getResource("black.jpg"), this );
			ImageComponent2D image = loader.getScaledImage((int)(1.5*this.width), (int)(1.5*this.height));
			this.background.setImage(image);
			

		}
		else if (mode==3)
		{
			TextureLoader loader = new TextureLoader(main.Main.class.getResource("white.jpg"), this );
			ImageComponent2D image = loader.getScaledImage((int)(1.5*this.width), (int)(1.5*this.height));
			this.background.setImage(image);
		}
		else if (mode==4)
		{
			// cch=new JColorChooser();
			Color color=cch.showDialog(new JFrame(), "Pick a Color",Color.blue);
			background.setImage(null);
			background.setColor(new Color3f(color));
			background.setCapability(Background.ALLOW_COLOR_WRITE);
			BoundingSphere sphere = new BoundingSphere(new Point3d(0,0,0), 100000);
			background.setApplicationBounds(sphere);



		}
		else if (mode==0)
		{
			TextureLoader loader = new TextureLoader(main.Main.class.getResource("sky2.jpg"), this );
			this.image = loader.getScaledImage((int)(1.5*this.width), (int)(1.5*this.height));
			this.background.setImage(this.image);

		}

		this.universe.addBranchGraph(this.group);



	}

	public void moveLeft(){
		camEye.el[0]+=moveStep;
		target.el[0]+=moveStep;
		newViewer();
	}
	public void moveRight(){
		camEye.el[0]-=moveStep;
		target.el[0]-=moveStep;
		newViewer();
	}
	public void moveUp(){
		camEye.el[1]-=moveStep;
		target.el[1]-=moveStep;
		newViewer();
	}
	public void moveDown(){

		camEye.el[1]+=moveStep;
		target.el[1]+=moveStep;
		newViewer();

	}
	public void zoomIn(){

		
		camEye.el[2]-=moveStep;
		newViewer();

	}
	public void zoomOut(){
		camEye.el[2]+=moveStep;
		newViewer();

	}



	public void setMesh(Model model) {
		if(model.dim==3)
			model.setEdge();
		
		tfc[0]=new TextField("");
		tfc[1]=new TextField("");
		
		this.dim=model.dim;
		model.defScale=Double.parseDouble(this.tfVectorScale.getText());
		clearRegButtons();
		this.meshLoaded = true;
		this.numberOfRegions = model.numberOfRegions;
		
		 matData= new MaterialData(this.numberOfRegions);
		regButton=new Button[this.numberOfRegions+1];
		regEditButton=new Button[this.numberOfRegions+1];
		
		setRegion=new boolean[this.numberOfRegions+1];
	
		for (int ir = 1; ir <= this.numberOfRegions; ir++)
		/*	if(ir==1)*/ setRegion[ir]=true;
		
		this.regionMatColor=new Color[this.numberOfRegions+1];
		this.regEdgeColor=new Color[this.numberOfRegions+1];
		this.faceTransp=new double[this.numberOfRegions+1];
		this.edgeTransp=new double[this.numberOfRegions+1];

	
		model.setSliceBounds();


		for (int ir = 1; ir <= this.numberOfRegions; ir++){
			
			if(!setRegion[ir]) continue;
			int cc=model.region[ir].getColorCode();
			int bb=model.region[ir].getColorBrightness();
			this.regionMatColor[ir] = this.matData.matColor(ir,model.region[ir].getMaterial(),cc,bb);
			this.regEdgeColor[ir]=this.regEdgeColor[ir];

		
			addRegButton(model,ir);

		}

		this.surfFacets = new SurfFacets[this.numberOfRegions + 1];

		for (int ir = 1; ir <= this.numberOfRegions; ir++) {

			if(!this.setRegion[ir]) continue;
		
			int cc=model.region[ir].getColorCode();
			int bb=model.region[ir].getColorBrightness();
			
			this.surfFacets[ir] = new SurfFacets(model, ir,this.matData.matColor(ir,model.region[ir].getMaterial(),cc,bb),this.spaceTransparency);
	
		}



		for (int ir = 1; ir <= this.numberOfRegions; ir++){
			if(model.region[ir].getMaterial().startsWith("air")){
				this.surfFacets[ir].showRegion=false;
				regButton[ir].setBackground(Color.white);
				
			}
			
			}
			
		


		double dm=new Vect ((model.spaceBoundary[1]-model.spaceBoundary[0]),(model.spaceBoundary[3]-
				model.spaceBoundary[2])).max();
		mouseTranslate.setFactor(.001*dm,.001*dm);
		
		mouseRotate.setFactor(.005);


		if(model.dim==2) {
			
		
			Vect[] ends=new Vect[4];
	
			double sx=1.2;
			double sy=1.2;
			ends[0]=new Vect(-sx*model.getRmax(),0);
			ends[1]=new Vect(sx*model.getRmax(),0);
			ends[2]=new Vect(0,-sy*model.getRmax());
			ends[3]=new Vect(0,sy*model.getRmax());
			
			this.group.detach();
			this.univGroup.removeChild(this.cartesian);
			this.univGroup.removeChild(this.cartesian2D);
			this.cartesian2D = new Cartesian2D(ends, Color.red, Color.green.darker());
		//	this.univGroup.addChild(this.cartesian2D);
			this.universe.addBranchGraph(this.group);

			if(model.spaceBoundary==null) camEye0.el[2]=1;
			else 
				camEye0.el[2]=2*model.spaceBoundary[1];

			camEye0.el[0]=0.0;
			camEye0.el[1]=0.0;
			target0=new Vect(0,0,0);
			upVect0.el[0]=0;  upVect0.el[1]=1;upVect0.el[2]=0;
			camEye=camEye0.deepCopy();
			target=target0.deepCopy();
			upVect=upVect0.deepCopy();
			//mouseRotate.setFactor(0);
			//newViewer();

			resetView();
		}
		

		else
		{
			if(cartesian2D!=null && this.cartesian2D.getParent()!=null){
			this.group.detach();
			this.univGroup.removeChild(this.cartesian2D);
			this.univGroup.addChild(this.cartesian);
			this.universe.addBranchGraph(this.group);
			}
			
			zoom=-(int)(1.0/model.spaceBoundary[5]);
			
			target0=new Vect ((model.spaceBoundary[0]+model.spaceBoundary[1])/2,(model.spaceBoundary[2]+
					model.spaceBoundary[3])/2,(model.spaceBoundary[4]+model.spaceBoundary[5])/2);

		resetView();
		}

		moveStep0=model.maxDim*.005;	
		moveStep=moveStep0;
		
		
		
		addRegions();
		


	}
	
	public void addRegions() {

		this.group.detach();
		
		this.univGroup.removeChild(this.femGroup);
	//	this.femGroup.removeAllChildren();


		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
				
		
				
				if(this.surfFacets[ir]==null) continue;
				
				this.surfFacets[ir].setFaceColor(regionMatColor[ir]);
				
				this.surfFacets[ir].setFaceTransparency(faceTransp[ir]);
				
				if(this.regEdgeColor[ir]==null)
				this.regEdgeColor[ir]=regionMatColor[ir].darker();
				this.surfFacets[ir].setEdgeColor(regEdgeColor[ir],edgeTransp[ir]);
				
				if(!this.surfFacets[ir].showRegion) {
					this.surfFacets[ir].showRegEdge=false;
					this.surfFacets[ir].showRegFace=false;
				}
				
				this.femGroup.addChild(this.surfFacets[ir]);

				
			}
		
		this.univGroup.addChild(this.femGroup);

		this.universe.addBranchGraph(this.group);
		
		refreshRegions();
		
	}
	
	public void setMeshVisible(boolean b){
		for (int ir = 1; ir <=this.numberOfRegions; ir++)
			this.surfFacets[ir].setVisible(b);
	}
	
	
	public void refreshRegions() {

		
		for (int ir = 1; ir <=this.numberOfRegions; ir++) {

			if(this.surfFacets[ir]==null) continue;
			
				
				this.surfFacets[ir].setFaceColor(regionMatColor[ir]);
				
				this.surfFacets[ir].setFaceTransparency(faceTransp[ir]);
				
				if(this.regEdgeColor[ir]==null)
				this.regEdgeColor[ir]=regionMatColor[ir].darker();
				
				this.surfFacets[ir].setEdgeColor(regEdgeColor[ir],edgeTransp[ir]);										
			
				this.surfFacets[ir].render();

				
			}

	}
	
	

	public void paintNodalScalar(Model model) {
		
	
		this.fieldMode=5;

		this.Vmax=model.nodalScalarMax;
		this.Vmin=model.nodalScalarMin;

		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
		//	if(ir!=1) continue;

			this.surfFacets[ir].setEdgeColor(Color.black,.8);
			this.surfFacets[ir].paintNodalScalar(model);	

		}



			setColorBar("Stress (MPa)",this.Vmin,this.Vmax);

	}


	public void setColorBar(String title,double min, double max){
		{
			
		cBar.setEnds(min, max);

			if(colBar!=null)
				eastPanel.remove(colBar);

			colBar=this.cBar.getColorBarPn(min,max,title,21);

			eastPanel.add(colBar);
			eastPanel.updateUI();

		}
	}


	public void clearStuff() {
		
		this.group.detach();
		this.univGroup.removeChild(this.femGroup);
		this.femGroup = new TransformGroup();
		
		this.nLoadedMesh=0;
		
		this.fieldMode=0;
		resetView();
		this.meshDrawn = false;
		this.meshLoaded = false;
		this.universe.addBranchGraph(this.group);

		clearRegButtons();
		
		
		this.progressThread = null;
		this.bShowMesh.doClick();

	}
	
	
	public void clearRegButtons() {
			this.regButtonPanel.removeAll();
		this.tfNumbRegs.setText(Integer.toString(0));
		this.regButtonPanel.updateUI();
		this.numberOfRegions = 0;
		if(colBar!=null)
			regButtonPanel.remove(colBar);

	}


	public void rescale(Model model) {

	
		this.vScale=Double.parseDouble(this.tfVectorScale.getText());
		
		if(this.vScale<=0){
			
			String msg = "Scale factor must be positive.";

			JOptionPane.showMessageDialog(null, msg, " ",
					JOptionPane.ERROR_MESSAGE); 
			return;
			
		}
		

		double e1=Double.parseDouble(this.cBar.tfc[0].getText());
		double e2=Double.parseDouble(this.cBar.tfc[1].getText());
		if(e1>0 || e2>=0){
			Vmin=e1;
			Vmax=e2;
			setColorBar(cBarTitle,this.Vmin, this.Vmax);
		}
		
		if(fieldMode<=4){
		
			scaleVectField(model);

			
		}else{
		
		
		scaleNodalScalar(model);
		}

		
			
	}



	public void setVectField(Model model,int mode) {
		
		fieldMode=mode;
	
		setVminVmax(model);
				
		this.numberOfRegions = model.numberOfRegions;
		
		cBar.setEnds(Vmin, Vmax);
		
		int arrowMode=0;
		if(dim==2) arrowMode=1;
		else if(dim==3 && fieldMode==3)  arrowMode=4;
		else arrowMode=3;

		//arrowMode=4;

		
		this.numberOfRegions = model.numberOfRegions;

		this.group.detach();
		for (int ir = 1; ir <=this.numberOfRegions; ir++) {
				
			if(this.surfFacets[ir]==null) continue;
			
			this.surfFacets[ir].setVectField(model,this.cBar,this.fieldMode,arrowMode);
		
		}
			
		this.universe.addBranchGraph(this.group);
		
		setSlider();
		showVectField(true);
		scaleVectField(model);
		addVectColorBar(true);
	}
	
	
	public void setVminVmax(Model model){
	
		int mode=fieldMode;
	
		if(mode==0){
			this.Vmin=model.FreluctMin;
			this.Vmax=model.FreluctMax;
		}
		else if(mode==1){
				this.Vmin=model.FmsMin;
				this.Vmax=model.FmsMax;
			}
		
		else if(mode==2){
			//this.Vmin=model.uMin;

			this.Vmax=model.uMax;
		}
		
		else if(mode==3){
		}
		else if(mode==4){
			this.Vmin=model.Bmin;
			this.Vmax=model.Bmax;
		}
	}
	
	
	public void showVectField(boolean b)
	{
		for (int ir = 1; ir <=this.numberOfRegions; ir++){

			if(!this.setRegion[ir]) continue;
			
			this.surfFacets[ir].showVectField=this.surfFacets[ir].showRegion;
			
			this.surfFacets[ir].showVectField=b;
			this.surfFacets[ir].showRegFace=false;
			
			//this.surfFacets[ir].refreshField();
			this.surfFacets[ir].setEdgeTransparency(.5);
			this.surfFacets[ir].render();
		}
		
		
	}

		
	
	public void addVectColorBar(boolean b) {


		DecimalFormat formatter=new DecimalFormat("0.00E00");
		
		
		this.tfVectorScale.setText(formatter.format(this.vScale));

		if(fieldMode==2){
			cBarTitle="Displacem. (m)";
			
		}
		else if(fieldMode==3){
			cBarTitle="Reluct. Force (N)";
		}
		else if(fieldMode==4){
			cBarTitle="Flux (T)";
		}
		else if(fieldMode==8){
			cBarTitle="surf. Force (N)";
		}


		if(b) setColorBar(cBarTitle,this.Vmin, this.Vmax);
	}
	
/*	
	public void setColorBar(String title,double min, double max){
		{
			cBar.setEnds(min, max);

			if(colBar!=null)
				eastPanel.remove(colBar);
			colBar=this.cBar.getColorBarPn(min,max,title,21);
			eastPanel.add(colBar);
			eastPanel.updateUI();

		}
	}
*/
	
	
	public void scaleVectField(Model model){


		for (int ir = 1; ir <=this.numberOfRegions; ir++){
			if(this.surfFacets[ir]==null) continue;

			surfFacets[ir].rescaleVectField(model,cBar, this.vScale);
		}
	}

	
		
public void scaleNodalScalar(Model model){
	
	
		
	for (int ir = 1; ir <=this.numberOfRegions; ir++) {
		if(!this.surfFacets[ir].showRegion) continue;

			this.surfFacets[ir].reScaleNodalScalar(cBar);

		}

			
	}



	public void showAxes(boolean b) {
		this.group.detach();
		this.group.removeChild(this.univGroup);
		if (b){
			if(dim==3){
				
			this.univGroup.addChild(this.cartesian);		
			}
			else
			this.univGroup.addChild(this.cartesian2D);		
		}
		else{
			if(dim==3)
			this.univGroup.removeChild(this.cartesian);
			else
			this.univGroup.removeChild(this.cartesian2D);
		}
		
		this.group.addChild(this.univGroup);
		this.universe.addBranchGraph(this.group);
	}


	public void deformMesh(Model model){

		for(int k=1;k<=model.numberOfRegions;k++){
			if(this.setRegion[k])
				this.surfFacets[k].deformReg(model, this.vScale);
		}
	}
	
	
	public void setSlider(){
		
		DecimalFormat df=new DecimalFormat("0.00E00");
		double sc=1;
			if(this.Vmax>0) sc=1.0/this.Vmax;

		vScale0=sc;
		vScale=sc;

		this.jslider.setMinimum(1);
		this.jslider.setMaximum(100);
		this.jslider.setMajorTickSpacing(5);
		this.jslider.setValue(50);
		this.jslider.setPaintTicks(true);
		//this.vScale=.02*this.vScalefact*this.vScale0*this.jslider.getValue();

		
		this.tfVectorScale.setText(df.format(vScale));

		//vScalefact=Double.parseDouble(this.tfX1.getText());

		
		this.updateUI();
	}

	public void loadMode() {

		this.messageArea.setText("");
		if(divert)
		Console.redirectOutput(this.messageArea);
		/*	this.messageFrame.setVisible(true);
		this.messageFrame.pack();*/
		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setBounds(1060, 820, 100, 20);
		this.progressBar.setForeground(Color.green);
		add(this.progressBar);
		this.progressBar.setVisible(false);

		this.progressThread = new Thread() {

			public void run() {
				ViewingPanel.this.progressBar.setVisible(true);
				int kt = 0;
				while (!ViewingPanel.this.meshLoaded) {
					kt++;
					ViewingPanel.this.progressBar.setValue(10 * (kt % 11));
					ViewingPanel.this.progressBar.repaint();
					try {
						Thread.sleep(500);
					} catch (InterruptedException err) {
					}

				}
				ViewingPanel.this.progressBar.setVisible(false);
			}
		};

		this.progressThread.start();

	}

	
	public void takeShot(){
		
		DateFormat dateFormat = new SimpleDateFormat("mm.ss");
		Date date = new Date();
		String suff=dateFormat.format(date);
		
		String root=System.getProperty("user.dir")+"\\CanvasImages";
		File folder = new File(root);
		if(!folder.exists())
				folder.mkdir();
		
		try {

			Rectangle rec=new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			 rec=new Rectangle(200,200,800,500);
			 BufferedImage bi = new Robot().createScreenCapture( rec);
			 File file=new File(root+"\\shot"+suff+".jpg");
			 ImageIO.write( bi, "bmp",file );
		
		} catch (HeadlessException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (AWTException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		 			
	}


public void stressMode() {
	
	this.drwpNorth.add(this.stressDist);
	this.repaint();


	}

	public void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
	
