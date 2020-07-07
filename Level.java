
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

//Class level provides arraylist for the tiles in a level
//with their type and index passed for each tile.

//Loads all levels.

//Warning: The .txt file should contain enough chars in at least
//one row to make the mapWidth longer than the screenWidth or 
//bugs will come up ! (due to implementation :=) ).	

public class Level{
    
    //Directory of level files
    private final String LVL_DIR="Levels/";
    
    //Array of arraylist of tiles for each level.
    private ArrayList tile[];
    
    //Total no of levels.
    private int noOfLevels=4; 
    
    public Level(){
     
        tile=new ArrayList[noOfLevels];
        
        for(int i=0;i<noOfLevels;i++){
            
            initLevels("Level"+String.valueOf(i+1)+".txt",i);
        }
                
    }
    
    public ArrayList getArrayList(int level){
        return tile[level-1];
    }
    
   
    private int maxRowNo[]=new int[noOfLevels]; 
    private int maxColNo[]=new int[noOfLevels];
    
    //getting a level's maxRow or maxCol Number in index form.
    public int getMaxRowNo(int level){
        return maxRowNo[level-1];
    }
    
    public int getMaxColNo(int level){
        return maxColNo[level-1];
    }
    
    //methods used for reading in all the levels
    //stores each level Arraylist data,maxRowNo and maxColNo data
    //in arrays.
    public void initLevels(String fnm,int arrayIndex){
        
        tile[arrayIndex]=new ArrayList(); 
        
        fnm=LVL_DIR+fnm;
        try{
            
            BufferedReader br=new BufferedReader(new FileReader(fnm));
 
            String line;
            char ch;
            Tile b;
            int bline=0;
            
            int rowNo=0;
            
            while((line = br.readLine()) != null) {                
                
                //if (line.length() == 0)  continue;
                if (line.startsWith("//")) continue;
             
                maxColNo[arrayIndex]++;
                rowNo=line.length();
                for(int x=0; x < line.length(); x++){
                    
                    ch = line.charAt(x);
                    
                    switch (ch){
                        
                        case ' ':
                            b=new Tile("e",x,bline);
                        break;
                        case 's':
                            b=new Tile("s",x,bline);
                        break;
                        case 'r':
                            b=new Tile("r",x,bline);
                        break;
                        case 'b':
                            b=new Tile("b",x,bline);
                        break;
                        case 'g':
                            b=new Tile("g",x,bline);
                        break;
                        case 'y':
                            b=new Tile("y",x,bline);
                        break;
			case 'o':
                            b=new Tile("o",x,bline);
                        break;
                        default:
                            b=new Tile("s",x,bline);
                        break;
                    }
                    
                    tile[arrayIndex].add(b);
                    
                }
                
                maxRowNo[arrayIndex]= maxRowNo[arrayIndex]<rowNo? rowNo:maxRowNo[arrayIndex];
                bline++;
            }   
            
        }catch(Exception e){
            System.out.println("An Error Occured While Reading Level Data "+e.toString());
            System.exit(0);
        }
    }
}
