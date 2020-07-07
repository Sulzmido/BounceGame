
import java.awt.Color;
import java.awt.Font;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

//Game uses full screen exclusive mode with 2 screen buffers.

public class Game extends JFrame implements Runnable{
    
    BufferStrategy bStr;
    GraphicsDevice gd;
    
    //Width and Height of Screen.
    private int Width;
    private int Height;
    
    //Game entities
    private ImageManager im;
    private BackgroundManager bm;
    private BallSprite bs;
    private Level level;
    private TileManager tm;
    private Obstacles obs;
    private PlayMusic pm;

//initial ball health.
    private int ballHealth=10;
    
    public Game(){
        
        super("BounceGame");
       
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        gd=ge.getDefaultScreenDevice();
        
        setUndecorated(true);
        setIgnoreRepaint(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        setFullScreen();
        setBufferStrategy();    
      
        setUpMusic();

        //initialize game entities
        im=new ImageManager();
        bm=new BackgroundManager(im,Width,Height);
        level=new Level();
        tm=new TileManager(level,this,Width,Height);
	obs=new Obstacles(tm,Height,im);
        bs=new BallSprite(im,tm,bm,this,obs);
        
        //set up keyboard input.
        setUpKey();
        setUpMouse();
        
        //starts the animation thread
        startThread();
    }
    
    private void setFullScreen(){
        
        //activates FSEM
        gd.setFullScreenWindow(this);
                
        DisplayMode dm=gd.getDisplayMode();
        System.out.println(dm.getWidth()+" "+ dm.getHeight()+" "+ dm.getBitDepth()+" "+dm.getRefreshRate());
        
        //gd.setDisplayMode(new DisplayMode(800,600,32,60));
        
        //System.out.println(getBounds().width);        
        //System.out.println(getBounds().height);
        
        Width=getBounds().width;
        Height=getBounds().height;
    }
    
    private void setBufferStrategy(){
        
        /*
            Buffer strategy with two buffers
            {uses flipping, or accelerated blitting(copying), or unaccelerated blitting}
            The use of invokeAndWait() is to avoid a possible deadlock
            with the event dispatcher thread. Should be fixed in J2SE 1.5  
        */
        
        try{
        EventQueue.invokeAndWait(new Runnable(){
            @Override
            public void run(){
                createBufferStrategy(2);
            }            
        });
        }catch(Exception e){}
        
        //becos createBufferStrategy is asynchronous and we want
        //bs to get a correct value
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {}
        
        bStr=getBufferStrategy();
    }

            //Midi 1, Wav 2.
//Array needs to be edited each time a new level is added
    //array for music file
    private String[] music={"Sound4.mid","Sound2.mid","Sound1.mid","Sound3.mid"};
    //array for the type of music.
    private int[] type={1,1,1,1};

    private void setUpMusic(){
	pm=new PlayMusic(music[0],type[0]);
    }
    
    private void setUpKey(){
        
        addKeyListener(new KeyAdapter(){
        
            @Override
            public void keyPressed(KeyEvent e){
                if(!gameOver){
                int keycode=e.getKeyCode();


                if(keycode==KeyEvent.VK_LEFT){

                    //willHitLeftTile checks for collision if ball moves left
                    //using the ball's point.
                    if(!tm.willHitLeftTile(bs.getBallPoint())){

                        bs.moveLeft();
                        bm.moveRight();
                        tm.moveRight();
                    }

                }

                if(keycode==KeyEvent.VK_RIGHT){

                    //willHitRightTile checks for collision if ball moves right.
                    //using the ball's point.
                    if(!tm.willHitRightTile(bs.getBallPoint())){

                        bs.moveRight();
                        bm.moveLeft();
                        tm.moveLeft();
                    }
                }
                if(keycode==KeyEvent.VK_UP){

                    bs.moveUp();                        
                }
		}
            }
        });
        
    }
    
    private void setUpMouse(){
        
        addMouseListener(new MouseAdapter(){
            
            @Override
            public void mouseClicked(MouseEvent e){
                //Point p=e.getLocationOnScreen();
                //test rough exit 
                //if(p.x>1300 && p.y>0){
                    //restoreFullScreen();
                    System.out.println("Restored");
		    System.exit(0);
                //}
            }
        });
    }
    
    private void startThread() {
        Thread thread=new Thread(this);
        thread.start();
    }
    
// "big method" for level transversal.
    public void initNewLevel(){
    	
    	System.out.println("Init new Level");
		bm.init();
//tm calls game.initMusic() so it tm can pass game lvl value.
		tm.init();
		bs.init();
		obs.init();
	
	}
    public void initMusic(int level){

	level=level-1;
	pm.stop();
	pm=null;
	pm=new PlayMusic(music[level],type[level]);
    }
    private boolean levelChanging=false;
    
//Animation loop, no FPS or UPS setting due to laziness.
    @Override
    public void run() {
        
        while(true){
           
            gameUpdate();
            gameRender();
            paintScreen();
        	
           
        }
    }
    
    public void gameUpdate(){
        if(!gameOver){
        //update background
        bm.update();
        //update tiles
        tm.update();
	//update obstacles
	obs.update();
        //update ball sprite
        bs.update();
	}
	
    }
    
    private boolean gameOver=false;
    Graphics gScr;
    public void gameRender(){
        
        gScr=bStr.getDrawGraphics();
        
        gScr.setColor(Color.white);
        gScr.fillRect(0,0,Width,Height);
        
        bm.draw(gScr); 
        tm.draw(gScr);
	obs.draw(gScr);
        bs.draw(gScr);
	
	drawStats(gScr);
	if(gameOver) drawGameOver(gScr);        

        gScr.dispose();
        
    }

    private Font font=new Font(Font.SANS_SERIF,Font.BOLD,24);
    private Color statsColor;
    private void drawStats(Graphics g){

	if(ballHealth>6) statsColor=Color.green;
        else if(ballHealth<7 && ballHealth>3) statsColor=Color.yellow;
	else if(ballHealth<=3) statsColor=Color.red;
	else statsColor=Color.white;

	g.setFont(font);
	g.setColor(statsColor);
	g.drawString("Ball Health :"+ballHealth,10,30);
    }

    private void drawGameOver(Graphics g){

	g.setFont(font);
	g.setColor(Color.red);
	g.drawString("Game Over",320,240);
    }
    
    public void paintScreen(){
        
        bStr.show();
    }
    
    //method contains codes that should be run at the end of the animation in FSEM
    //disposes the window and sets the screeen back to its default state.    
    private void restoreFullScreen(){
        
        Window w=gd.getFullScreenWindow();
        w.dispose();
        gd.setFullScreenWindow(null);
    }

    public void reduceBallHealth(){ ballHealth--; }
    public void increaseBallHealth(){ ballHealth++; }
    public int getBallHealth(){ return ballHealth; }
    public void gameOver(){ gameOver=true; }
 
    public static void main(String[] args){
        new Game();
    }
    
     //@Override
    //public void windowClosing(WindowEvent e) {}
    // @Override
    //public void windowActivated(WindowEvent e) {}
 //@Override
  //  public void windowDeactivated(WindowEvent e) {}
 //@Override
   // public void windowClosed(WindowEvent e) {
//System.out.println("Window Closed");
  //      pm.stop();
	//restoreFullScreen();
	//}
//@Override
  // public void windowDeiconified(WindowEvent e) {}
 //@Override
   // public void windowIconified(WindowEvent e) {}
 //@Override
   // public void windowOpened(WindowEvent e) {}
	
}
