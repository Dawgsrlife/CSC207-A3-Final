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
                + "Be very careful to avoid mistakes with the format. Take your time in generation. "
                + "Do not accidentally put brackets where they don't belong (e.g. \"(point:(349,25)\" should just be \"point:(349,25)\"). "
                + "Do not do use incorrect formats for shapes (e.g. \"Non-filledRectangle(forthecircle)\" should just be \"Rectangle\"); no need to be descriptive. "
                + "Do not use the grave (`) symbol. Just provide the code. "
                + "Do not do stuff like \"(removedsomelinesforbrevity)\". Just complete the file with all the shapes in the correct format. "
                + "Ensure outputs strictly follow the format, starting with 'Paint Save File Version 1.0' "
                + "and ending with 'End Paint Save File'. It is also vital that you end circle, polyline, rectangle, or squiggle, if you started them and they're not yet ended. "
                + "Do not add any notes before or after the file. You are tasked with writing me output that CAN BE PARSED IMMEDIATELY, "
                + "as if it is funneled directly into a paint program. There is no character limit. "
                + "If you are asked to replace circles with rectangles, then the rectangle should have the same size as the radius of the circle. "
                + "Keep the positions of the shapes the same. "
                + "Do not add random comments describing what you are doing in the file. The goal is to create a file that remains parsable by following the format.";
        String warnings = "You have limited 'lives.' Each mistake (e.g., misaligned shapes, incorrect coordinates, "
                + "not putting stuff in the center of the canvas, not ending shapes) will cost lives. "
                + "Losing all lives ends the process. Follow instructions carefully.";
        String specific_example = FileIO.readResourceFile("concentric_circles.txt");

        this.system = warnings + "\n" + format + "\n" + constraints + "\nExample:\n" + example + "\nConcentric Circles Example:\n" + specific_example;
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
     * newFile1: A bunch of circles tangent to one another; truly trypophobia inducing!
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
                    ALMOST COPY THE Example output BUT MAKE THE CIRCLES COLORFUL:
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
        String example1 = FileIO.readResourceFile("funny_house.txt");
        String example2 = FileIO.readResourceFile("funny_house2.txt");
        String prompt = """
                        Draw a house on a 500x500 canvas, represented in 3D:
                        - The house base is a rectangle (200x150) centered at (250,300).
                        - The roof is an isosceles triangle created using a polyline connecting the rectangle's top corners and a peak at (250,150).
                        - Add a chimney as a rectangle (30x80) on the roof, starting at (270,120).
                        - Create a door: a rectangle (50x80) centered at the bottom of the house.
                        - Add two windows: each a rectangle (50x50), one on either side of the door.
                        All dimensions and placements must maintain alignment and symmetry.
                        Do not forget to end your shapes that you've begun!
                        - Rectangles have 2 points max. If you need to draw a triangle then use polyline.
                        - Please see the example by the following (don't be lazy and give the verbatim format, but unique colours and make shapes aligned):
                        See the examples:
                        
                        """ + example1 + "\n\nAnother Example:" + example2;
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
     * modifyFile1: Funny shapes! Every shape turns into some other shape.
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile1(String inFileName, String outFileName) {
        String prompt = """
                        Requirements:
                            For every shape, change it into another shape.
                            There are circles, rectangles, polylines, and squiggles available.
                            Do not try to tell me what you are doing; just make the change.
                            Shapes must not change into their own types (e.g. a circle shouldn't become another circle).
                        """;
        modifyFile(prompt, inFileName, outFileName);
    }


    /**
     * modifyFile2: Invert Colors of All Shapes
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile2(String inFileName, String outFileName) {
        String prompt = """
                        Requirements:
                            For every shape, change its color to its inverse.
                            To calculate this change, take the RGB values and subtract them from 255 to obtain the new value.
                            For example, if you have "color:255,255,0", then you should do 255-255, 255-255, and 255-0 to obtain "color:0,0,255" as the new color.
                            Color values must not be negative.
                            Write that in your output then.
                        """;
        modifyFile(prompt, inFileName, outFileName);
    }


    /**
     * modifyFile3: Change everything to be super tiny.
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile3(String inFileName, String outFileName) {
        String prompt = """
                        Scale all the shapes to be very small. Just follow the format.
                        """;
        modifyFile(prompt, inFileName, outFileName);
    }


    private String postProcess(String result) {
        // Remove possible quotation marks (") and periods (.):
        String processedResult = result.replaceAll("[\".*]", "");

        // Ollama may try to use triple grave symbols to begin and end code. Run through the result and snip that out if any!
        processedResult = extractContentBetweenTripleGraves(processedResult);

        // If there were no triple graves, Ollama may try to add redundant information that surrounds the relevant Paint Save File Format.
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

    public static String extractContentBetweenTripleGraves(String input) {
        int firstIndex = input.indexOf("```");
        if (firstIndex == -1) {
            // Means no triple graves found.
            return input;
        }

        int secondIndex = input.indexOf("```", firstIndex + 3);
        if (secondIndex == -1) {
            // Only one set of triple graves found.
            return input.substring(firstIndex + 3);
        }

        // Extract content between the first and second triple graves
        return input.substring(firstIndex + 3, secondIndex).trim();
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
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                op.modifyFile1("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_1.txt");
                op.modifyFile2("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_2.txt");
                op.modifyFile3("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_3.txt");
            }
        }
    }
}
