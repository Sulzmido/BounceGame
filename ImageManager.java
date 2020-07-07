
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

//Loads the background image depending on type
//Loads an array of ball Images

public class ImageManager{
    
    GraphicsConfiguration gc;

    //background image
    private BufferedImage bim;

    private int noBallImages=4;
    private int noStarImages=4;

    //ball and star images
    private BufferedImage[] ball;
    private BufferedImage[] star;
    
    private String bgImType=".jpg";
    
    public ImageManager(){

        ball=new BufferedImage[noBallImages];
	star=new BufferedImage[noStarImages];

        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        gc=ge.getDefaultScreenDevice().getDefaultConfiguration();
       
        bim=loadImage("Images/bg"+bgImType);

        for(int i=0;i<noBallImages;i++){
           ball[i]=loadImage("Images/ball"+String.valueOf(i+1)+".gif");
        }
	for(int i=0;i<noStarImages;i++){
	   star[i]=loadImage("Images/star"+String.valueOf(i+1)+".gif");
	}
    }
    
    //loads an image compatible with underlying graphics device.
    public BufferedImage loadImage(String fnm){
        
        try {
            BufferedImage im=ImageIO.read(getClass().getResource(fnm));
            
            int transparency=im.getColorModel().getTransparency();
            
            BufferedImage copy=gc.createCompatibleImage(im.getWidth(),im.getHeight(),transparency);
            
            Graphics2D g2d=copy.createGraphics();
            g2d.drawImage(im,0,0,null);
            g2d.dispose();
            return copy;
            
        } catch (IOException ex) {return null;}
        
    }   
    
    //Getters.
    
    public BufferedImage getImage(String name){
        
        if(name.compareTo("bg")==0){
            return bim;
        }
        
        
        else return null;
    }
    public BufferedImage getAnimationImage(String name,int number){

        if(name.compareTo("ball")==0){
            return ball[number];
        }
	if(name.compareTo("star")==0){
            return star[number];
        }
        else return null;
    }
}
