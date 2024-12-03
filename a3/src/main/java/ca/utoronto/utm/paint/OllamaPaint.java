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
                + "Do not forget to end your shapes after starting them. "
                + "Do not write in-line comments. "
                + "Avoid arithmetic expressions in coordinates (e.g., 275-25 should be 250). "
                + "Shapes should follow specified formats: circles need integer center/radius; rectangles use p1:(x,y) and p2:(x,y). "
                + "The canvas colour is white; use darker colours for visibility, and avoid completely overlapping filled shapes. "
                + "The canvas size is 500x500; center drawings unless otherwise specified. "
                + "Do not create incredibly tiny shapes (e.g. avoid skinny rectangles). "
                + "Ensure proper placement, alignment, and symmetry for all shapes. "
                + "Avoid randomly generating shapes or modifying existing shapes unless explicitly requested. "
                + "Ensure outputs strictly follow the format, starting with 'Paint Save File Version 1.0' "
                + "and ending with 'End Paint Save File'.";
        String warnings = "You have limited 'lives.' Each mistake (e.g., misaligned shapes, incorrect coordinates) will cost lives. "
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
                + prompt + " OPERATION END " + f;
        String processedResponse = postProcess(this.call(system, fullPrompt));
        FileIO.writeHomeFile(processedResponse, outFileName);
    }

    /**
     * newFile1: Create a 3x3 grid of rectangles with alternating colors
     *
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void newFile1(String outFileName) {
        String prompt = "Create a Paint file consisting of a grid layout of 3x3 rectangles. " +
                "Each rectangle should be 50x50 in size, with alternating red and blue colors. " +
                "Place the first rectangle at position (50,50), the second at (150,50), and so on. " +
                "Ensure the rectangles are aligned in a 3x3 grid pattern. The canvas size should be centered, and all shapes should be visible on the canvas.";
        newFile(prompt, outFileName);
    }

    /**
     * newFile2: Create a Paint file with circles centered at the corners of a rectangle
     *
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void newFile2(String outFileName) {
        String prompt = "Create a Paint file with a rectangle at position (150,150) with dimensions 200x100. " +
                "Place four circles at the four corners of the rectangle. " +
                "Each circle should have a radius of 10, and the circles must be positioned precisely at the corners of the rectangle. " +
                "Ensure that the circles are clearly placed on the rectangle vertices, with their centers at (150,150), (350,150), (150,250), and (350,250).";
        newFile(prompt, outFileName);
    }

    /**
     * newFile3: Create a Paint file with a single polyline shape
     *
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void newFile3(String outFileName) {
        String prompt = "Create a Paint file containing a polyline shape connecting the points (100,100), (200,200), and (300,100). " +
                "Ensure the polyline is drawn as a continuous path between these three points. " +
                "The polyline should have no fill and no stroke, creating a clean, open shape. " +
                "Position the polyline in the center of the canvas, ensuring that it is fully visible.";
        newFile(prompt, outFileName);
    }

    /**
     * modifyFile1: Modify an existing Paint file by adding a rectangle
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile1(String inFileName, String outFileName) {
        String prompt = "Modify the existing Paint file by adding a rectangle with dimensions 100x100 at position (50,50). " +
                "Ensure that the rectangle is drawn with a filled color, and make sure the new rectangle does not overlap with any existing shapes. " +
                "Place the rectangle closer to the center of the canvas for balanced composition.";
        modifyFile(prompt, inFileName, outFileName);
    }

    /**
     * modifyFile2: Modify an existing Paint file by changing the color of all rectangles to green
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile2(String inFileName, String outFileName) {
        String prompt = "Modify the existing Paint file by changing the color of all rectangles to green. " +
                "Ensure that no other shapes are affected, and all rectangles should now appear in the green color. " +
                "Ensure that the rectangles' positions and sizes remain unchanged while applying the color transformation.";
        modifyFile(prompt, inFileName, outFileName);
    }

    /**
     * modifyFile3: Modify an existing Paint file by deleting all circles
     *
     * @param inFileName  The source Paint file to be modified
     * @param outFileName The name of the new file in the user's home directory
     */
    @Override
    public void modifyFile3(String inFileName, String outFileName) {
        String prompt = "Modify the existing Paint file by removing all circles. " +
                "Ensure that no circles remain in the modified file, and that all other shapes and content are preserved intact. " +
                "The final file should have no circles, but should retain any other existing shapes like rectangles or polylines.";
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
        Pattern arithmeticPattern = Pattern.compile("-?\\b(-?\\d+)([+-])(\\d+)\\b");
        processedResult = getStringBuffer(arithmeticPattern, processedResult).toString();
        
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

//        for (int i = 1; i <= 3; i++) {
//            op.newFile1("PaintFile1_" + i + ".txt");
//            op.newFile2("PaintFile2_" + i + ".txt");
//            op.newFile3("PaintFile3_" + i + ".txt");
//        }
//        for (int i = 1; i <= 3; i++) {
//            for (int j = 1; j <= 3; j++) {
//                op.modifyFile1("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_1.txt");
//                op.modifyFile2("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_2.txt");
//                op.modifyFile3("PaintFile" + i + "_" + j + ".txt", "PaintFile" + i + "_" + j + "_3.txt");
//            }
//        }
    }
}
