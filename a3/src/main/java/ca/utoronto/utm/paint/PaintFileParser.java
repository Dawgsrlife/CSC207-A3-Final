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
 */
public class PaintFileParser {
    private int lineNumber = 0; // the current line being parsed
    private String errorMessage = ""; // error encountered during parse
    private PaintModel paintModel;

    /**
     * Below are Patterns used in parsing
     */
    // File Start and End:
    private Pattern pFileStart = Pattern.compile("^PaintSaveFileVersion1.0$");
    private Pattern pFileEnd = Pattern.compile("^EndPaintSaveFile$");

    // Shared Details:
    private String colorNum = "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
    private String coordinate = "\\((-?\\d+),(-?\\d+)\\)";
    private Pattern pColor = Pattern.compile("^color:" + colorNum + "," + colorNum + "," + colorNum + "$");
    private Pattern pFilled = Pattern.compile("^filled:(true|false)$");

    // Circle Block:
    private Pattern pCircleStart = Pattern.compile("^Circle$");
    private Pattern pCircleEnd = Pattern.compile("^EndCircle$");
    private Pattern pCenter = Pattern.compile("^center:" + coordinate + "$");
    private Pattern pRadius = Pattern.compile("^radius:(\\d+)$");

    // Rectangle Block:
    private Pattern pRectangleStart = Pattern.compile("^Rectangle$");
    private Pattern pRectangleEnd = Pattern.compile("^EndRectangle$");
    private Pattern pP1 = Pattern.compile("^p1:" + coordinate + "$");
    private Pattern pP2 = Pattern.compile("^p2:" + coordinate + "$");

    // Shared for Squiggle and Polyline:
    private Pattern pPointsBegin = Pattern.compile("^points$");
    private Pattern pPoint = Pattern.compile("^point:" + coordinate + "$");
    private Pattern pPointsEnd = Pattern.compile("^endpoints$");

    // Squiggle Block:
    private Pattern pSquiggleStart = Pattern.compile("^Squiggle$");
    private Pattern pSquiggleEnd = Pattern.compile("^EndSquiggle$");

    // Polyline Block:
    private Pattern pPolylineStart = Pattern.compile("^Polyline$");
    private Pattern pPolylineEnd = Pattern.compile("^EndPolyline$");

    /**
     * Store an appropriate error message in this, including
     * lineNumber where the error occurred.
     *
     * @param msg
     */
    private void error(String msg) {
        this.errorMessage = "Error in line " + lineNumber + " " + msg;
    }

    /**
     * @return the error message resulting from an unsuccessful parse
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Parse the specified file
     *
     * @param fileName
     * @return
     */
    public boolean parse(String fileName) {
        boolean retVal = false;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            PaintModel pm = new PaintModel();
            retVal = this.parse(br, pm);
        } catch (FileNotFoundException e) {
            error("File Not Found: " + fileName);
        } finally {
            try {
                br.close();
            } catch (Exception e) {
            }
            ;
        }
        return retVal;
    }

    /**
     * Parse the specified inputStream as a Paint Save File Format file.
     *
     * @param inputStream
     * @return
     */
    public boolean parse(BufferedReader inputStream) {
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
     * @param paintModel  the paint model to add the commands to
     * @return whether the complete file was successfully parsed
     */
    public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
        this.paintModel = paintModel;
        this.errorMessage = "";

        // During the parse, we will be building one of the
        // following commands. As we parse the file, we modify
        // the appropriate command.

        CircleCommand circleCommand = null;
        RectangleCommand rectangleCommand = null;
        SquiggleCommand squiggleCommand = null;
        PolylineCommand polylineCommand = null;

        try {
            int state = 0;
            Matcher m;
            String l;

            this.lineNumber = 0;
            while ((l = inputStream.readLine()) != null) {
                if (l.isEmpty()) continue;
                l = l.replaceAll("\\s+", "");
                this.lineNumber++;
                System.out.println(lineNumber + " " + l + " " + state);
                switch (state) {
                    case 0:  // Initial State: looking for file start
                        m = pFileStart.matcher(l);
                        if (m.matches()) {
                            state = 1;
                            break;
                        }
                        error("Expected Start of Paint Save File");
                        return false;
                    case 1:  // Standby State: Looking for the start of a new object or end of the save file
                        m = pCircleStart.matcher(l);
                        if (m.matches()) {
                            // Circle Start
                            state = 2;
                            break;
                        }
                        m = pRectangleStart.matcher(l);
                        if (m.matches()) {
                            // Rectangle Start
                            state = 7;
                            break;
                        }
                        m = pSquiggleStart.matcher(l);
                        if (m.matches()) {
                            // Squiggle Start
                            state = 12;
                            break;
                        }
                        m = pPolylineStart.matcher(l);
                        if (m.matches()) {
                            // Polyline Start
                            state = 17;
                            break;
                        }
                        m = pFileEnd.matcher(l);
                        if (m.matches()) {
                            // End of the file
                            state = 22;
                            break;
                        }
                        error("Expected Start of Shape or End Paint Save File");
                        return false;
                    case 2:
                        // Start Parsing Circle: looking for color
                        m = pColor.matcher(l);
                        if (m.matches()) {
                            state = 3;
                            break;
                        }
                        error("Expected Circle color");
                        return false;
                    case 3:
                        // Parsing Circle: looking for filled
                        m = pFilled.matcher(l);
                        if (m.matches()) {
                            state = 4;
                            break;
                        }
                        error("Expected Circle filled");
                        return false;
                    case 4:
                        // Parsing Circle: looking for center
                        m = pCenter.matcher(l);
                        if (m.matches()) {
                            state = 5;
                            break;
                        }
                        error("Expected Circle center");
                        return false;
                    case 5:
                        // Parsing Circle: looking for radius
                        m = pRadius.matcher(l);
                        if (m.matches()) {
                            state = 6;
                            break;
                        }
                        error("Expected Circle Radius");
                        return false;
                    case 6:
                        // Parsing Circle: looking for EndCircle
                        m = pCircleEnd.matcher(l);
                        if (m.matches()) {
                            state = 1;
                            break;
                        }
                        error("Expected End Circle");
                        return false;
                    case 7:
                        // Start Parsing Rectangle: looking for color
                        m = pColor.matcher(l);
                        if (m.matches()) {
                            state = 8;
                            break;
                        }
                        error("Expected Rectangle color");
                        return false;
                    case 8:
                        // Parsing Rectangle: looking for filled
                        m = pFilled.matcher(l);
                        if (m.matches()) {
                            state = 9;
                            break;
                        }
                        error("Expected Rectangle filled");
                        return false;
                    case 9:
                        // Parsing Rectangle: looking for p1
                        m = pP1.matcher(l);
                        if (m.matches()) {
                            state = 10;
                            break;
                        }
                        error("Expected Rectangle p1");
                        return false;
                    case 10:
                        // Parsing Rectangle: looking for p2
                        m = pP2.matcher(l);
                        if (m.matches()) {
                            state = 11;
                            break;
                        }
                        error("Expected Rectangle p2");
                        return false;
                    case 11:
                        // Parsing Rectangle: looking for EndRectangle
                        m = pRectangleEnd.matcher(l);
                        if (m.matches()) {
                            state = 1;
                            break;
                        }
                        error("Expected End Rectangle");
                        return false;
                    case 12:
                        // Start Parsing Squiggle: looking for color
                        m = pColor.matcher(l);
                        if (m.matches()) {
                            state = 13;
                            break;
                        }
                        error("Expected Squiggle color");
                    case 13:
                        // Parsing Squiggle: looking for filled
                        m = pFilled.matcher(l);
                        if (m.matches()) {
                            state = 14;
                            break;
                        }
                        error("Expected Squiggle filled");
                        return false;
                    case 14:
                        // Parsing Squiggle: looking for points
                        m = pPointsBegin.matcher(l);
                        if (m.matches()) {
                            state = 15;
                            break;
                        }
                        error("Expected Squiggle points");
                        return false;
                    case 15:
                        // Parsing Squiggle: looking for point or end points
                        m = pPoint.matcher(l);
                        if (m.matches()) {
                            break;
                        }
                        m = pPointsEnd.matcher(l);
                        if (m.matches()) {
                            state = 16;
                            break;
                        }
                        error("Expected Squiggle point or end points");
                        return false;
                    case 16:
                        // Parsing Squiggle: looking for EndSquiggle
                        m = pSquiggleEnd.matcher(l);
                        if (m.matches()) {
                            state = 1;
                            break;
                        }
                        error("Expected End Squiggle");
                        return false;
                    case 17:
                        // Start Parsing Polyline: looking for color
                        m = pColor.matcher(l);
                        if (m.matches()) {
                            state = 18;
                            break;
                        }
                        error("Expected Polyline color");
                        return false;
                    case 18:
                        // Parsing Polyline: looking for filled
                        m = pFilled.matcher(l);
                        if (m.matches()) {
                            state = 19;
                            break;
                        }
                        error("Expected Polyline filled");
                        return false;
                    case 19:
                        // Parsing Polyline: looking for points
                        m = pPointsBegin.matcher(l);
                        if (m.matches()) {
                            state = 20;
                            break;
                        }
                        error("Expected Polyline points");
                        return false;
                    case 20:
                        // Parsing Polyline: looking for point or end points
                        m = pPoint.matcher(l);
                        if (m.matches()) {
                            break;
                        }
                        m = pPointsEnd.matcher(l);
                        if (m.matches()) {
                            state = 21;
                            break;
                        }
                        error("Expected Polyline point or end points");
                        return false;
                    case 21:
                        // Parsing Polyline: looking for EndPolyline
                        m = pPolylineEnd.matcher(l);
                        if (m.matches()) {
                            state = 1;
                            break;
                        }
                        error("Expected End Polyline");
                        return false;
                    case 22:
                        // Accepting/End State: Has read EndPaintSaveFile (Anything further and do not accept)
                        error("Extra content after End of File");
                        return false;

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
            if (state != 22) {
                error("Unexpected end of file");
                return false;
            }
        } catch (Exception e) {

        }
        return true;
    }
}
