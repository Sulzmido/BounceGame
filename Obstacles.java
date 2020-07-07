
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;

public class Obstacles{

	private TileManager tm; 
	private ImageManager im;

	private Tile[][] tilesArray;

	private int maxColIndex;
	private int maxRowIndex;
	//screen height.
	private int Height;

//array holds star images .
	private BufferedImage obsImage[];

	public Obstacles(TileManager t,int h,ImageManager i){

		tm=t;
		im=i;

		obsImage=new BufferedImage[4];
		for(int j=0;j<4;j++){
		    obsImage[j]=im.getAnimationImage("star",j);
		}
		Height=h;
		tilesArray=tm.get2DArrayStruct();
		maxColIndex=tm.getMaxColIndex();
		maxRowIndex=tm.getMaxRowIndex();
		tempObs=new ArrayList();
		initObstacles();
	}
	
	private int noOfObstacles=0;

//tempObs is temporary storage for obstcles before
//obsPoint array is iniialized.
	private ArrayList tempObs;
	private Point[] obsPoint;
	private void initObstacles(){
		Tile t;
		//only use obstacles tile at the last row to initialize obstacles.
		for(int j=0;j<maxRowIndex;j++){
			t=tilesArray[maxColIndex-1][j];
			if(t.getStringType().compareTo("o")==0){
				tempObs.add(t);
				noOfObstacles++;
			}
		}
		obsPoint=new Point[noOfObstacles];
		for(int i=0;i<noOfObstacles;i++){
			obsPoint[i]=new Point();
			t=(Tile)tempObs.get(i);
			obsPoint[i].x=t.getX()*t.getSize();
			
			obsPoint[i].y=new Random().nextInt(Height);
			//System.out.println(obsPoint[i].x+" : "+obsPoint[i].y);			
		}
		
	}
//called during level transversal.
	public void init(){
		noOfObstacles=0;
		tilesArray=tm.get2DArrayStruct();
		maxColIndex=tm.getMaxColIndex();
		maxRowIndex=tm.getMaxRowIndex();
		tempObs=null;
		tempObs=new ArrayList();
		obsPoint=null;
		initObstacles();
	}	
	
//speed of obstacles.
	private int vertSpeed=3;
//variable used to go through array of images.
	private int imIndex=0;

//update() changes the obs pics and makes the obs move vertically.
	public void update(){

		imIndex=(imIndex+1)%4;
		for(int i=0;i<noOfObstacles;i++){
			obsPoint[i].y=(obsPoint[i].y+vertSpeed)%Height;
		}

	}
// draw handles by TileManager due to some need tm vars.
	public void draw(Graphics g){
		for(int i=0;i<noOfObstacles;i++){
			tm.drawObs(g,obsPoint[i],obsImage[imIndex]);
		}
	}

//get all rectangles bounding the images. Used to implement collision.
	public Rectangle[] getObsRects(){
		Rectangle[] obsRects=new Rectangle[noOfObstacles];
		for(int i=0;i<noOfObstacles;i++){
			obsRects[i]=new Rectangle(obsPoint[i].x,obsPoint[i].y,50,50);
	
		}
		return obsRects;
	}


}
