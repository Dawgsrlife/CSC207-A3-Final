package ca.utoronto.utm.paint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaPaint extends Ollama {
    private final String system;

    public OllamaPaint(String host) {
        super(host);

        // Preparing a system prompt with Format, Example, and Constraints:
        String format = FileIO.readResourceFile("paintSaveFileFormat.txt");
        String example = FileIO.readResourceFile("paintSaveFileExample.txt");
        String constraints = "Do not include English explanations or unnecessary punctuation. "
                + "Do not forget to end your circle, rectangle, squiggle, or polyline shapes after starting them. "
                + "Do not write in-line comments. "
                + "Avoid arithmetic expressions in coordinates (e.g., 275-25 should be 250). "
                + "Shapes should follow specified formats: circles need integer center/radius; rectangles use p1:(x,y) and p2:(x,y). "
                + "The canvas colour is white; use darker colours for visibility, and avoid completely overlapping filled shapes. "
                + "The canvas size is 500x500; center drawings unless otherwise specified. "
                + "Please scale the drawings to be relatively big. "
                + "Please center the drawings unless otherwise told. "
                + "Please follow the examples accurately. They are great references! "
                + "Please also be mindful of the number of shapes you are drawing (e.g. one polyline followed by a circle and two polylines results in three polylines). "
                + "Do not create incredibly tiny shapes (e.g. avoid skinny rectangles). "
                + "Ensure proper placement, alignment, and symmetry for all shapes. "
                + "Avoid randomly generating shapes or modifying existing shapes unless explicitly requested. "
                + "Any concentric circles should be created in the middle of the canvas. "
                + "Do not end shapes before starting them. "
                + "Ensure outputs strictly follow the format, starting with 'Paint Save File Version 1.0' "
                + "and ending with 'End Paint Save File'.";
        String warnings = "You have limited 'lives.' Each mistake (e.g., misaligned shapes, incorrect coordinates, "
                + "not putting stuff in the center of the canvas, not ending shapes) will cost lives. "
                + "Losing all lives ends the process. Follow instructions carefully.";

        this.system = warnings + "\n" + format + "\n" + constraints + "\n" + "Example:\n" + example;
    }

    /**
     * Ask llama3 to generate a new Paint File based on the given prompt
     *
     * @param prompt
     * @param outFileName name of new file to be created in users home directory
     */
    public void newFile(String prompt, String outFileName) {
        String processedResponse = postProcess(this.call(system, prompt));
        FileIO.writeHomeFile(processedResponse, outFileName);
    }

    /**
     * Ask llama3 to generate a new Paint File based on a modification of inFileName and the prompt
     *
     * @param prompt      the user supplied prompt
     * @param inFileName  the Paint File Format file to be read and modified to outFileName
     * @param outFileName name of new file to be created in users home directory
     */
    public void modifyFile(String prompt, String inFileName, String outFileName) {
        String f = FileIO.readHomeFile(inFileName);
        String fullPrompt = "Produce a new PaintSaveFileFormat Document, resulting from the following OPERATION " +
                "being performed on the following PaintSaveFileFormat Document. OPERATION START"
                + prompt + " OPERATION END " + f + "\n\nEnsure that modifications follow:\n\n" + system;
        String processedResponse = postProcess(this.call(system, fullPrompt));
        FileIO.writeHomeFile(processedResponse, outFileName);
    }

    /**
     * newFile1: A bunch of circles tangent to one another, filling up the canvas; truly trypophobia inducing!
     *
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void newFile1(String outFileName) {
        String example = FileIO.readResourceFile("tangent_circles.txt");
        String prompt = """
                    Create a grid of circles on a 500x500 canvas with the following conditions:
                    - Each circle has a diameter of 50 pixels (radius = 25).
                    - Circles must have a random dark color.
                    - They must be tangent to adjacent circles, with no overlap or spacing.
                    - Arrange in a 10x10 grid, starting at (25,25).
                    - Draw at least 20 circles (no shorthand).
                    - No extraneous shapes.
                    - The first circle's center is at (25,25), the second at (75,25), etc.
                    - Ensure circles fill the grid, staying within the canvas boundaries.
                    - Avoid inline comments.
                    - Provide the full output without shortening.
                    - Do not add any notes or explanations.
                    - Follow the exact format (e.g., use "End", not "END").
                    Example output:
                    """ + example;

        newFile(prompt, outFileName);
    }


    /**
     * newFile2: A two-dimension house with a roof and chimney, perfect for the winter cold! It's also snowing!
     *
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void newFile2(String outFileName) {
        String example = FileIO.readResourceFile("funny_house.txt");
        String prompt = """
                        Draw a house on a 500x500 canvas, represented in 3D:
                        - The house base is a rectangle (200x150) centered at (250,300).
                        - The roof is an isosceles triangle created using a polyline connecting the rectangle's top corners and a peak at (250,150).
                        - Add a chimney as a rectangle (30x80) on the roof, starting at (270,120).
                        - Create a door: a rectangle (50x80) centered at the bottom of the house.
                        - Add two windows: each a rectangle (50x50), one on either side of the door.
                        All dimensions and placements must maintain alignment and symmetry.
                        Do not forget to end your shapes that you've begun!
                        - Please see the example by the following (don't be lazy and give the verbatim format, but unique colours and make shapes aligned):
                        
                        """ + example;
                                newFile(prompt, outFileName);
    }


    /**
     * newFile3: TODO
     *
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void newFile3(String outFileName) {
        String example1 = FileIO.readResourceFile("stickfigure_in_landscape_scenery.txt");
        String example2 = FileIO.readResourceFile("stickfigure_in_landscape_scenery2.txt");
        String prompt = """
                        Create a landscape drawing with the following elements:
                        - Trees:
                          - Each tree consists of a brown rectangle trunk (10x40) and a green circle canopy (radius = 30).
                          - Position one tree at (100,400) and another at (400,400).
                        - Birds:
                          - Represent birds using polylines: two arcs forming an open "M" shape.
                          - Place three birds: one at (150,100), another at (250,80), and the last at (350,120).
                        - Landscape:
                          - A green rectangle (500x100) at the bottom for grass.
                          - A blue circle (radius = 50) at the top-right for the sun, centered at (450,50).
                        - Stick Figure:
                          - Head: A circle (radius = 10) centered at (250,350).
                          - Body: A vertical polyline from (250,360) to (250,400).
                          - Arms: Two diagonal polylines from (250,370) to (230,390) and (250,370) to (270,390).
                          - Legs: Two diagonal polylines from (250,400) to (230,420) and (250,400) to (270,420).
                        Ensure all elements are proportional and centered, avoiding overlaps.
                        You can only use circles, rectangles, polylines, and squiggles to draw everything.
                        See the examples:
                        
                        """ + example1 + "\n\nAnother example:" + example2;
        newFile(prompt, outFileName);
    }


    /**
     * modifyFile1: TODO
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile1(String inFileName, String outFileName) {
        String prompt = """
                        Add a circle (radius = 20) tangent to the top-right corner of the canvas. 
                        The circle's center must be at (480,20). Ensure no other shapes are affected.
                        """;
        modifyFile(prompt, inFileName, outFileName);
    }


    /**
     * modifyFile2: TODO
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile2(String inFileName, String outFileName) {
        String prompt = """
                        Change all shapes with red fill color to blue. 
                        Maintain all other properties (size, position, etc.).
                        """;
        modifyFile(prompt, inFileName, outFileName);
    }


    /**
     * modifyFile3: TODO
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile3(String inFileName, String outFileName) {
        String prompt = """
                        Replace all polylines with circles (radius = 10) centered at the midpoint of each polyline's starting and ending points. 
                        Remove the polylines after the conversion.
                        """;
        modifyFile(prompt, inFileName, outFileName);
    }


    private String postProcess(String result) {
        // Remove possible quotation marks (") and periods (.):
        String processedResult = result.replaceAll("[\".*]", "");

        // Ollama may try to add redundant information that surrounds the relevant Paint Save File Format.
        // This excess information can be removed by regex matching, and then restoring the start and end of the
        // file format.
        String startOrEndRegex = "(?s)^.*?Paint Save File Version (1\\.0|10)|End Paint Save File.*$";
        processedResult = processedResult.replaceAll(startOrEndRegex, "");
        processedResult = "Paint Save File Version 1.0\n" + processedResult + "\nEnd Paint Save File\n";

        // Ollama may still provide the arithmetic version of points, so it's necessary to capture and replace
        // instances of that with the computed result.
        Pattern arithmeticPattern = Pattern.compile("\\b(-?\\d+)([+-])(\\d+)\\b");
        processedResult = getStringBuffer(arithmeticPattern, processedResult).toString();

        // Ollama may try to provide in-line comments as if this were Java code, so remove those.
        processedResult = processedResult.replaceAll("//.*$", "");

        // Return the result:
        return processedResult.trim();
    }

    private static StringBuffer getStringBuffer(Pattern arithmeticPattern, String processedResult) {
        Matcher matcher = arithmeticPattern.matcher(processedResult);

        // Doing the replacement of arithmetic expressions with computed values:
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            int computedValue = getComputedValue(matcher);

            // Do replacement:
            matcher.appendReplacement(sb, computedValue + "");
        }
        matcher.appendTail(sb);
        return sb;
    }

    private static int getComputedValue(Matcher matcher) {
        boolean firstNumNegative = matcher.group(0).startsWith("-");
        int operand1 = Integer.parseInt(matcher.group(1));  // grab first num
        if (firstNumNegative) operand1 = -operand1;
        String operator = matcher.group(2);                 // grab operator (+ or -)
        int operand2 = Integer.parseInt(matcher.group(3));  // grab second num

        // Do correct computation (either + or -):
        return "+".equals(operator) ? operand1 + operand2 : operand1 - operand2;
    }

    /**
     * File Outputs:
     * - OllamaPaintFile1.txt is a rectangle with circles on its vertices;
     * - OllamaPaintFile2.txt is OllamaPaintFile1.txt but only with the circles;
     * - OllamaPaintFile3.txt is five different-colored concentric circles;
     * - OllamaPaintFile4.txt is OllamaPaintFile3.txt but with the circles as rectangles;
     * - OllamaPaintFile5.txt is a canvas with a four polylines, two circles, and one rectangle;
     * - OllamaPaintFile6.txt is OllamaPaintFile5.txt but with each circle surrounded by a non-filled rectangle.
     *
     * @param args
     */
    public static void main(String[] args) {
        String prompt;

        OllamaPaint op = new OllamaPaint("dh2010pc42.utm.utoronto.ca"); // Replace this with your assigned Ollama server.

        // One
        prompt = "Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        op.newFile(prompt, "OllamaPaintFile1.txt");

        // Two
        op.modifyFile("Remove all shapes except for the circles.", "OllamaPaintFile1.txt", "OllamaPaintFile2.txt");

        // Three
        prompt = "Draw 5 concentric circles with different colors.";
        op.newFile(prompt, "OllamaPaintFile3.txt");

        // Four
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile3.txt", "OllamaPaintFile4.txt");

        // Five
        prompt = "Draw a polyline then two circles then a rectangle then 3 polylines all with different colors.";
        op.newFile(prompt, "OllamaPaintFile5.txt");

        // Six
        prompt = "Modify the following Paint Save File so that each circle is surrounded by a non-filled rectangle. ";
        op.modifyFile(prompt, "OllamaPaintFile5.txt", "OllamaPaintFile6.txt");

        for (int i = 1; i <= 3; i++) {
            op.newFile1("PaintFile1_" + i + ".txt");
            op.newFile2("PaintFile2_" + i + ".txt");
            op.newFile3("PaintFile3_" + i + ".txt");
        }
//        for (int i = 1; i <= 3; i++) {
//            for (int j = 1; j <= 3; j++) {
//                op.modifyFile1("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_1.txt");
//                op.modifyFile2("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_2.txt");
//                op.modifyFile3("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_3.txt");
//            }
//        }
    }
}
