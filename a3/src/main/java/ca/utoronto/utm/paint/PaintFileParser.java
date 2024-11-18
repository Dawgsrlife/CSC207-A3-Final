package ca.utoronto.utm.paint;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel; 
	
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart=Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd=Pattern.compile("^EndPaintSaveFile$");

	private Pattern pCircleStart=Pattern.compile("^Circle$");
	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");
	// ADD MORE!!
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}
	
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}

	/**
	 * Parse the specified file
	 * @param fileName
	 * @return
	 */
	public boolean parse(String fileName){
		boolean retVal = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			PaintModel pm = new PaintModel();
			retVal = this.parse(br, pm);
		} catch (FileNotFoundException e) {
			error("File Not Found: "+fileName);
		} finally {
			try { br.close(); } catch (Exception e){};
		}
		return retVal;
	}

	/**
	 * Parse the specified inputStream as a Paint Save File Format file.
	 * @param inputStream
	 * @return
	 */
	public boolean parse(BufferedReader inputStream){
		PaintModel pm = new PaintModel();
		return this.parse(inputStream, pm);
	}

	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";
		
		// During the parse, we will be building one of the 
		// following commands. As we parse the file, we modify 
		// the appropriate command.
		
		CircleCommand circleCommand = null; 
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
	
		try {	
			int state=0; Matcher m; String l;
			
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				this.lineNumber++;
				System.out.println(lineNumber+" "+l+" "+state);
				switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 1: // Looking for the start of a new object or end of the save file
						m=pCircleStart.matcher(l);
						if(m.matches()){
							// ADD CODE!!!
							state=2; 
							break;
						}
						// ADD CODE
				
					case 2:
						// ADD CODE
						break;
					case 3:
						break;
					// ...
					/**
					 * I have around 20+/-5 cases in my FSM. If you have too many
					 * more or less, you are doing something wrong. Too few, and I bet I can find
					 * a bad file that you will say is good. Too many and you are not capturing the right concepts.
					 *
					 * Here are the errors I catch. All of these should be in your code.
					 *
					 	error("Expected Start of Paint Save File");
						error("Expected Start of Shape or End Paint Save File");
						error("Expected Circle color");
						error("Expected Circle filled");
						error("Expected Circle center");
						error("Expected Circle Radius");
						error("Expected End Circle");
						error("Expected Rectangle color");
						error("Expected Rectangle filled");
						error("Expected Rectangle p1");
						error("Expected Rectangle p2");
						error("Expected End Rectangle");
						error("Expected Squiggle color");
						error("Expected Squiggle filled");
						error("Expected Squiggle points");
						error("Expected Squiggle point or end points");
						error("Expected End Squiggle");
						error("Expected Polyline color");
						error("Expected Polyline filled");
						error("Expected Polyline points");
						error("Expected Polyline point or end points");
						error("Expected End Polyline");
						error("Extra content after End of File");
						error("Unexpected end of file");
					 */
				}
			}
		}  catch (Exception e){
			
		}
		return true;
	}
}
