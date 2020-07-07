
//Class used to define each tile.

import java.awt.Color;

public class Tile{
    
    //x,y represent tile indices.
    private int x;
    private int y;
    
    //type of tile
    private String type;
    
    private Color color;
    
    private final int tileSize=50;
    
    public Tile(String type,int x,int y){
        
        this.x=x;
        this.y=y;
        this.type=type;
        setColor(type);
        
    }
    
    //Getters and Setters.
    
    private void setColor(String type){
    
    	if(type.compareTo("s")==0) color=Color.black;
        if(type.compareTo("r")==0) color=Color.red;
        if(type.compareTo("y")==0) color=Color.yellow;
        if(type.compareTo("b")==0) color=Color.blue;
        if(type.compareTo("g")==0) color=Color.green;
	if(type.compareTo("o")==0) color=Color.orange;
	}
	
    public Color getColor(){
        return color;
    }

    public String getStringType(){
	return type;
    }    

    public int getType(){
        
        if(type.compareTo("e")==0){
            return 0;  
        }
        else return 1;
    }
    
    public int getY(){
        return y;
    }
    
    public int getX(){
        return x;
    }
    
    public int getSize(){
        return tileSize;
    }
}
