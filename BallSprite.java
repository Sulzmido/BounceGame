
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

//BallSprite defines the ball.
//Would use methods from TileManager to control its movement.

public class BallSprite{
    
    private ImageManager im;
    private TileManager tm;
    private BackgroundManager bm;
    private Game game;
    private Obstacles obs;
    
//Array holding ball images.
    private BufferedImage[] ball;
    
    private int noOfImages=4;
    
//used to implement state chart.
//could be falling,jumping or still.
    private int state;
    
    public BallSprite(ImageManager m,TileManager t,BackgroundManager b,Game g,Obstacles o){
        
        im=m;
        tm=t;
        bm=b;
	obs=o;
		game=g;        

        ball=new BufferedImage[noOfImages];
        
        for(int i=0;i<noOfImages;i++){
           ball[i]=im.getAnimationImage("ball",i);
        }
        //state falling becos of ball initial world position.
        state=FALLING;
    }

//Called during level transversal.
    public void init(){
		x=700;
		y=0;
		state=FALLING;
    }
    
    //ball states.
    private final int JUMPING=0;
    private final int FALLING=1;
    private final int STILL=2;
    
    //control how many pixels a ball moves with one key press.
    private final int moveSize=50;
    
    public void moveLeft(){
        
        //change animation image using count.
        if(count>0){
            count--;
        }
        if(count==0) count=(noOfImages-1);
        
        x-=moveSize;

    }
    
    public void moveRight(){
        //change animation image using count.
        count=(count+1)%noOfImages;
        x+=moveSize;

    }
    
    //variables used to control a jump.
//how far the ball jumps.
    private final int jumpMax=50;
    int jump;
    
    public void moveUp(){
        
        //ball should only jump if its not already jumping or falling.
    	if(state==STILL){
            
            state=JUMPING;
            jump=0;
        }
      
    }
    //used to control ball animation.
    private int count=0;
    
    //initial ball world position.
    private int x=700;
    private int y=0;
    
    private int counter=0;

    public void update(){

	checkBallCollision();

    	if(x==(tm.getTileWidth()-100)){
// top layer level transversal method.
		game.initNewLevel();
	}
   //counter used bcos FPS is makes it update too fast.

	counter=(counter+1)%Integer.MAX_VALUE;
        if(counter%3==0){
        //this update method was implemented using a statechart.
        if(state==FALLING){
            
            //tileBelow returns an integer depending on how far the ball
            //is from the tile below it.
            int i=tm.tileBelow(x+25,y);
            y+=i;
            //this conditions show the ball has touch the tile below
            if(i<10) state=STILL;
            if(i==0) state=STILL;
        }
		
        if(state==JUMPING){
            //tileAbove returns an integer depending on how far the ball
            //is from the tile above it.
            int i=tm.tileAbove(x+25,y);
            //System.out.println(i);
            
            //this condition indicate a tile is right above the ball
            //therefore it shouldnt move up.
            if(i==0) state=FALLING;
            
            if(state==JUMPING){
                y-=i;
                jump++;
                //this condition indicate the ball just touched a tile above it.
                if(i<10) state=FALLING;
                //ball had reached maximum jump height.
                if(jump==jumpMax) state=FALLING;
            }
        }
		
        if(state==STILL){
            //tileBelow checks whether there is a tile below the ball
            int i=tm.tileBelow(x+25,y);
            //this condition shows there is space below the ball and the ball 
            //can fall.
            if(i>0) state=FALLING;
        }
        }
    }
    
    private void checkBallCollision(){

	Rectangle[] obsRects=obs.getObsRects();
	Rectangle ball=getBallRect();

	for(int i=0;i<obsRects.length;i++){
		if(ball.intersects(obsRects[i])){
			//System.out.println("Collison detected");
			//do more things.

			game.reduceBallHealth();
			if(game.getBallHealth()==0){
			    game.gameOver();
			}
			//take ball back to the start of level.
			else{
			  x=700;
		          y=0;
			  tm.setxHead(0);
			}
			  break;
		}
	}
     }
    
    public void draw(Graphics g){
       //Always draw ball at 700 of screen.
       //Ball doesnt move horizontally but the x variable is changed
       //so as to detect interaction with the tiles.
        g.drawImage(ball[count],700,y,null);
    }
    
    //Getters and Setters
    
    public Point getBallPoint(){
        return new Point(x,y);
    }
    private Rectangle getBallRect(){
	return new Rectangle(x,y,50,50);
}
    
    public int getState(){
        return state;
    }
	
    public void setState(int st){
        state=st;
    }
    
}
