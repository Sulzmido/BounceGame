
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

//TileManager gets the current level ArrayList from level class.
//It manipulates the data structure for easy access and manipulation.
//It also handles how the tile is displayed on the screen.

public class TileManager{
    
    //reference to level object
    private Level level;
    private Game game;
    
    //Level data in arraylist data structure.
    private ArrayList tiles;
    
    //level data in 2D array data struct.
    private Tile[][] tilesArray;
    
    //variable used in wraparound.
    private int xHead;
    
    //Tile Map dimensions
    private int tWidth;
    private int tHeight;
    
    //Tile index dimension.
    private int maxRowIndex;
    private int maxColIndex;
    
//starting level.
    private int lvl=1;

    private int noOfLevels=4;
    
    private final int tileSize=50;

//Screen width and height.
    private int Width;
    private int Height;
    
    public TileManager(Level l,Game g,int w,int h){
        
        level=l;
	game=g;
    	Width=w;
	Height=h;  
  
        //get arraylist of tiles for level 1
        tiles=level.getArrayList(lvl);
        
        maxRowIndex=level.getMaxRowNo(lvl);      
        maxColIndex=level.getMaxColNo(lvl);
        
        tWidth=maxRowIndex*tileSize;
        tHeight=maxColIndex*tileSize;
        
        tilesArray=new Tile[maxColIndex][maxRowIndex];
        
        //fill up array with empty tiles.
        for(int x=0;x<maxColIndex;x++){
            for(int y=0;y<maxRowIndex;y++){
                
                tilesArray[x][y]=new Tile("e",y,x);
            }
        }
        
        convertDataStructure();
        
        //System.out.println(tWidth+" "+tHeight);
        System.out.println("Level :"+lvl);
        
        xHead=0;
    }

//used to initialize a new level.
//tm's data is changed.

    public void init(){
	lvl++;	
	if(lvl<=noOfLevels){
	game.initMusic(lvl);
	tiles=null;
	tiles=level.getArrayList(lvl);
	maxRowIndex=level.getMaxRowNo(lvl);      
        maxColIndex=level.getMaxColNo(lvl);
        
        tWidth=maxRowIndex*tileSize;
        tHeight=maxColIndex*tileSize;
        tilesArray=null;
        tilesArray=new Tile[maxColIndex][maxRowIndex];
        
        //fill up array with empty tiles.
        for(int x=0;x<maxColIndex;x++){
            for(int y=0;y<maxRowIndex;y++){
                
                tilesArray[x][y]=new Tile("e",y,x);
            }
        }
        
        convertDataStructure();
        
        //System.out.println(tWidth+" "+tHeight);
        System.out.println("Level :"+lvl);
        
        
        xHead=0; 
	}
	else{
	System.out.println("You have finished this game");
	System.out.println("Thanks for playing brah");
	System.exit(0);
}
    }
    
    
    
    //Converts Arraylist of tiles to a 2D array of tiles
    // for easy accesibility and manipulation.
//also replaces some empty tiles with map data.

    private void convertDataStructure(){
        
        Tile t;
        
        for(int i=0;i<tiles.size();i++){
            
            t=(Tile)tiles.get(i);
            
            int x=t.getX();
            int y=t.getY();
            
            tilesArray[y][x]=t;
        }
        
        
    }

//how fast the map moves in one update.
//value should sync with ballSprite moveSize.    
    private final int moveSize=50;
    
    public void moveLeft(){
        
        xHead=(xHead-moveSize)%tWidth; 
    }
    
    public void moveRight(){
        
        xHead=(xHead+moveSize)%tWidth; 
    }
    
    public void update(){
        
    }
    
    private Color tileColor=Color.black;
    private final Color tileBorder=Color.white;

    private Color winLineColor=Color.green;
    private int winLineOffset=100;    

//draw() implements tileMap wraparound in a cunning way.
//Different from method used in BackgroundManager.

    public void draw(Graphics g){

//draw a colored rect at the end of tile map.

	g.setColor(winLineColor);
	//g.drawLine(tWidth-winLineOffset+xHead,0,tWidth-winLineOffset+xHead,Height);
	g.fillRect(tWidth-winLineOffset+xHead,0,100,Height);
	if(xHead>=0){
		//g.drawLine(-winLineOffset+xHead,0,-winLineOffset+xHead,Height);
		g.fillRect(-winLineOffset+xHead,0,100,Height);
	}
	else{
		 //g.drawLine(tWidth-winLineOffset+xHead+tWidth,0,tWidth-winLineOffset+xHead+tWidth,Height);
		g.fillRect(tWidth-winLineOffset+xHead+tWidth,0,100,Height);
	}

        //draw all tiles.
        for(int y=0;y<maxRowIndex;y++){
            for(int x=0;x<maxColIndex;x++){

                Tile b=tilesArray[x][y];
                //skip drawing an empty tile.
                if(b.getType()==0) continue;
                
		tileColor=b.getColor();
                g.setColor(tileColor);
                
                drawBrick(g,(b.getX()*b.getSize())+xHead,b);
		                
                if(xHead>=0){
		    g.setColor(tileColor);
                    drawBrick(g,((b.getX()*b.getSize())+xHead)-tWidth,b);		    
                }
                else{
		    g.setColor(tileColor);
                    drawBrick(g,((b.getX()*b.getSize())+xHead)+tWidth,b);		   
                }
                
                g.setColor(tileBorder);

                drawBorder(g,(b.getX()*b.getSize())+xHead,b); 
               
                if(xHead>=0){
                    drawBorder(g,((b.getX()*b.getSize())+xHead)-tWidth,b);
                }
                else{
                    drawBorder(g,((b.getX()*b.getSize())+xHead)+tWidth,b);
                }
                
            }
        }

	
    }

    //Helper methods.
    private void drawBrick(Graphics g,int xCoord,Tile b){
        g.fillRect(xCoord,Height-((maxColIndex-b.getY())*b.getSize()),b.getSize(),b.getSize());
    }
    private void drawBorder(Graphics g,int xCoord,Tile b){
        g.drawRect(xCoord,Height-((maxColIndex-b.getY())*b.getSize()),b.getSize(),b.getSize());
    }
    
//Drawing the obstacles was given to tm to handle 
//becos of devious tile map wrap around.

    public void drawObs(Graphics g,Point p,BufferedImage im){

	g.drawImage(im,p.x+xHead,p.y,null);
                
        if(xHead>=0){
             g.drawImage(im,p.x+xHead-tWidth,p.y,null);
        }
        else{
            g.drawImage(im,p.x+xHead+tWidth,p.y,null);
        }
    }
    
//how much the ball moves vertically in one update.
//Changing this value will cause bugs if some corresponding 
//variables are not changed in BallSprite class.

    private int vertMoveStep=10;
//Method checks the position of the tile below it.
//returns a pixel value on how far the ball should move 
//depending on how far the tile Below is.

    public int tileBelow(int x,int y){
        //No comments.
	//The implementation was hell enough.
	//HardWire coding , non portable.
        int xIndex=(x/tileSize);
        int yIndex=(Height-y)/tileSize;
        yIndex=maxColIndex-yIndex;
        //System.out.println(xIndex+" "+maxRowIndex);
        Tile t;
        int type;
        int highTile=0;
        
        if(yIndex>maxColIndex || yIndex<0) yIndex=0;
        
        for(int i=yIndex;i<maxColIndex;i++){
            
            t=tilesArray[i][xIndex];
            type=t.getType();
            if(type==0) continue;
            
            highTile=Height-((maxColIndex-i)*tileSize);
            break;
        }
        //System.out.println("highTile: "+highTile);
        if((highTile-tileSize)-y>vertMoveStep){
            return vertMoveStep;
        }
        if((highTile-tileSize)-y==0){
            return 0;
        }
        else return ((highTile-tileSize)-y);
        
    }
    //checks whether ball has tile above it.
//returns a pixel value on how far the ball should move 
//depending on how far the tile Above is.

    public int tileAbove(int x,int y){
    
    	int xIndex=(x/tileSize);
        int yIndex=(Height-y)/tileSize;
        yIndex=maxColIndex-yIndex;
        
        Tile t;
        int type;
        int aboveTile=0;
        
        for(int i=yIndex;i>-1;i--){
            //System.out.println(i);
            t=tilesArray[i][xIndex];
            type=t.getType();
            if(type==0) continue;
            
            if(type==1){
                aboveTile=Height-((maxColIndex-i)*tileSize);
                break;    	
            }
            
        }
                
        if(y-(aboveTile+tileSize)>vertMoveStep) return vertMoveStep;
        if(y-(aboveTile+tileSize)==0) return 0;
        else return (y-(aboveTile+tileSize));
    }
    //method checks whether ball has tile to its left.
//returns a boolean.
    public boolean willHitLeftTile(Point ballPoint){
    
        int xIndex=(ballPoint.x-(tileSize/2))/tileSize;
        int yIndex=(Height-ballPoint.y)/tileSize;
        yIndex=maxColIndex-yIndex;
        
        if(yIndex<0 || yIndex>=maxColIndex) return false;
	if(xIndex<0 || xIndex>=maxRowIndex) return false;
        
        Tile t;
        int type;
        
        //any tile at x-25/50 ,yIndex?
        t=tilesArray[yIndex][xIndex];
        return t.getType() != 0;       
    }
    
//Method to check whether ball has tile in front of it.
//returns a boolean.
    public boolean willHitRightTile(Point ballPoint){
    
        int xIndex=(ballPoint.x+((3*tileSize)/2))/tileSize;
        int yIndex=(Height-ballPoint.y)/tileSize;
        yIndex=maxColIndex-yIndex;
        
        if(yIndex<0 || yIndex>=maxColIndex) return false;
	if(xIndex<0 || xIndex>=maxRowIndex) return false;
	
        
        Tile t;
        int type;
        
        //any tile at x+75/50 ,yIndex?
        t=tilesArray[yIndex][xIndex];
        return t.getType() != 0;         
    }
    
	//getters and setters.
    public Tile[][] get2DArrayStruct(){ return tilesArray; }
    public int getMaxRowIndex(){ return maxRowIndex; }
    public int getMaxColIndex(){ return maxColIndex; }
    public int getTileWidth(){ return tWidth; }
    public void setxHead(int value){ xHead=value; }
    public int getxHead(){ return xHead; }
}
