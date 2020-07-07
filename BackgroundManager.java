
import java.awt.Graphics;
import java.awt.image.BufferedImage;

//Handles the background wraparound JFrame.

public class BackgroundManager{

    private ImageManager im;

    //background image.
    private BufferedImage bImg;
    
    //background image width and height
    private int imWidth;
    private int imHeight;
    
    //screen width and height
    private int Width;
    private int Height;
    
    //variable used to implement wraparound.
    private int xHead;
    
    public BackgroundManager(ImageManager i,int w,int h){
        
        im=i;
        Width=w;
        Height=h;
        
        bImg=im.getImage("bg");
        imWidth=bImg.getWidth();
        imHeight=bImg.getHeight();
        
        xHead=0;
    }

//called during level transversal.
    public void init(){
	xHead=0;
    }

//how fast the background image moves.
    private final int moveSize=30;
    
    public void moveLeft(){
        
        xHead=(xHead-moveSize)%imWidth; 
    }
    
    public void moveRight(){
        
        xHead=(xHead+moveSize)%imWidth; 
    }
    
    public void update(){
        //Nothing update yet.
               
    }
    
    //draw background depending on the value of xHead.
//wrap around work given to draw().
    public void draw(Graphics g){

        if(xHead==0){
            //Head
            disp(g,bImg,0,Width,0,Width);
        }
        
        else if(xHead>0 && xHead<Width){
            
            //Head
            disp(g,bImg,xHead,Width,0,Width-xHead);
            //tail
            disp(g,bImg,0,xHead,imWidth-xHead,imWidth);
            
        }
        else if(xHead >= Width){   
            //tail.
            disp(g,bImg,0,Width,imWidth-xHead,imWidth-xHead+Width);
        }
        else if((xHead<0)&&(xHead>=Width-imWidth)){
            disp(g,bImg,0,Width,-xHead,Width-xHead);  // im body
        }
        else if(xHead<Width-imWidth){
            // draw im tail at (0,0) and im head at (width+xImHead,0)
            disp(g,bImg,0,imWidth+xHead,-xHead,imWidth);  // im tail   
            disp(g,bImg,imWidth+xHead,Width,0,Width-imWidth-xHead);  // im head
        }
                  
    }
    
    private void disp(Graphics g,BufferedImage im,int scrX1,int scrX2,int imX1,int imX2){
        
        g.drawImage(im,scrX1,0,scrX2,Height,imX1,0,imX2,Height,null);
    }
    
}