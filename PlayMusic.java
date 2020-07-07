
import java.io.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;

public class PlayMusic implements Runnable,MetaEventListener{
	// for midi
    private int MIDI=1;
    private static final int END_OF_TRACK = 47;
    private Sequencer sequencer;
    private Synthesizer synthesizer;
    private Sequence seq = null;
    private String filename;

    //for wav   
    private int WAV=2;
    private AudioInputStream stream;
    private AudioFormat format = null;
    private SourceDataLine line = null;

    private final static String SOUND_DIR = "Sounds/";
    //1 for midi,2 for wav.
    //Used to differentiate btw mid n wav files. 
    private int type;
    
    public PlayMusic(String fnm,int t){
	type=t;
	filename=SOUND_DIR+fnm;
	if(type==MIDI){
	    initSequencer();
	    loadMidi(filename);
	    play();
	}
	//wav
	else{
            createInput(filename);	    
            createOutput();
            //integer argument used to overload method play().
	    play(1);
	}

    }
     
       //Midi methods.
    private void initSequencer(){
        
        try{
            
            sequencer=MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addMetaEventListener(this);
            

            
            if(!(sequencer instanceof Synthesizer)){
                    
                System.out.println("Linking the sequencer to a synthesizer");
                synthesizer=MidiSystem.getSynthesizer();
                synthesizer.open();
                          
                Receiver synthReceiver = synthesizer.getReceiver();
                Transmitter seqTransmitter = sequencer.getTransmitter();
                seqTransmitter.setReceiver(synthReceiver);
                
            }else
                synthesizer=(Synthesizer)sequencer;            
        }catch(Exception e){}
    }
    
    private void loadMidi(String fnm){
     
        try{
            
            seq=MidiSystem.getSequence(getClass().getResource(fnm));           
        } catch (InvalidMidiDataException | IOException ex) {}        
    }
    
    private void play(){
        
        //load sequence into sequencer and start it playing.
     
        if((sequencer!=null) &&(seq!=null)){
            try{
                sequencer.setSequence(seq);
                //start playing it. start() returns immediately. 
                sequencer.start();
            } catch (InvalidMidiDataException ex) {}
        }
    }
    
    @Override
    public void meta(MetaMessage meta) {
        
        //only interested in end of track meta event.   
        if (meta.getType()==END_OF_TRACK){
            //end? start again.
	    sequencer.stop();
            sequencer.setTickPosition(0);
            sequencer.start();
        }
    }
    
    // Close down the sequencer and synthesizer
    //Close called when game is over.
    public void close(){
        
        if (sequencer != null) {
            
            if(sequencer.isRunning()) sequencer.stop();
            
            sequencer.removeMetaEventListener(this);
            sequencer.close();

            if (synthesizer != null) synthesizer.close();
        }
    }

      //WAV methods.
    private void createInput(String fnm){
    try {
      stream = AudioSystem.getAudioInputStream( new File(fnm) );
      format = stream.getFormat();
      //System.out.println("Audio format: " + format);

      // convert ULAW/ALAW formats to PCM format
      if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
           (format.getEncoding() == AudioFormat.Encoding.ALAW) ) {
        AudioFormat newFormat = 
           new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                format.getSampleRate(),
                                format.getSampleSizeInBits()*2,
                                format.getChannels(),
                                format.getFrameSize()*2,
                                format.getFrameRate(), true);  // big endian
        // update stream and format details
        stream = AudioSystem.getAudioInputStream(newFormat, stream);
        System.out.println("Converted Audio format: " + newFormat);
        format = newFormat;
      }
    }
    catch (UnsupportedAudioFileException e) 
    {  System.out.println( e.getMessage());  
       System.exit(0);
    }
    catch (IOException e) 
    {  System.out.println( e.getMessage());  
       System.exit(0);
    }
  }  


  private void createOutput(){
  // set up the SourceDataLine going to the JVM's mixer  
    try {
      // gather information for line creation
      DataLine.Info info =
            new DataLine.Info(SourceDataLine.class, format);
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Line does not support: " + format);
        System.exit(0);
      }
      // get a line of the required format
      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(format); 
    }
    catch (Exception e)
    {  System.out.println( e.getMessage());  
       System.exit(0);
    }
  }  


  private void play(int wav){
	
    Thread thread=new Thread(this);
    thread.start();

  }
  private boolean running=true;
  @Override
  public void run(){
	running=true;
	while(running){	
		int numRead = 0;
    		byte[] buffer = new byte[line.getBufferSize()];

    		line.start();
    		// read and play chunks of the audio
    		try {
      			int offset;
		 if(!running) break;
     		 while ((numRead = stream.read(buffer, 0, buffer.length)) >= 0) {
        	// System.out.println("read: " + numRead);
        		offset = 0;
		 if(!running) break;
       		 while (offset < numRead){
         	 offset += line.write(buffer, offset, numRead-offset);
                 if(!running) break;}
      		}
    		}
   		 catch (IOException e) {
			  System.out.println( e.getMessage()); 
		}
    		// wait until all data is played, then close the line
    		line.drain();
		stream=null;
        	format=null;
		line=null;
		createInput(filename);
		createOutput();
	}
  }
  public void stop(){
	running=false;
        if(type==MIDI){
		close();
	}
	else{
	   if(line!=null) line.stop();
       if(line!=null) line.close();
	}
  }
}
