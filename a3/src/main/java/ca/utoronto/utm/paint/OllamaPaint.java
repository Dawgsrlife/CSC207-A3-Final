package ca.utoronto.utm.paint;

public class OllamaPaint extends Ollama {
    public OllamaPaint(String host) {
        super(host);
    }

    /**
     * Ask llama3 to generate a new Paint File based on the given prompt
     *
     * @param prompt
     * @param outFileName name of new file to be created in users home directory
     */
    public void newFile(String prompt, String outFileName) {
        String format = FileIO.readResourceFile("paintSaveFileFormat.txt");
        String example = FileIO.readResourceFile("paintSaveFileExample.txt");
        String system = "The answer to this question should be a PaintSaveFileFormat Document. " +
                "Respond only with a PaintSaveFileFormat Document and nothing else. Don't even say what you're giving me. " +
                format + "Here's an example (your file should look like the structure of the example;" +
                "if what you're being asked isn't a feature, then try to create something similar " +
                "[features include: color, filled, points for squiggle and polyline, center and radius for circle, p1 and p2 for rectangle]):" + example;
        String response = this.call(system, prompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * Ask llama3 to generate a new Paint File based on a modification of inFileName and the prompt
     *
     * @param prompt      the user supplied prompt
     * @param inFileName  the Paint File Format file to be read and modified to outFileName
     * @param outFileName name of new file to be created in users home directory
     */
    public void modifyFile(String prompt, String inFileName, String outFileName) {
        // Your job is to create the right system and prompt.
        // then call Ollama and write the new file in the home directory
        // HINT: You should have a collection of resources, examples, prompt wrapper etc. available
        // in the resources directory. See OllamaNumberedFile as an example.
        String format = FileIO.readResourceFile("paintSaveFileFormat.txt");
        String example = FileIO.readResourceFile("paintSaveFileExample.txt");
        String system = "The answer to this question should be a PaintSaveFileFormat Document. " +
                "Respond only with a PaintSaveFileFormat Document and nothing else. Don't even say what you're giving me. " +
                format + "Here's an example (your file should look like the structure of the example;" +
                "if what you're being asked isn't a feature, then try to create something similar " +
                "[features include: color, filled, points for squiggle and polyline, center and radius for circle, p1 and p2 for rectangle]):" + example;
        String f = FileIO.readHomeFile(inFileName);
        String fullPrompt = "Produce a new PaintSaveFileFormat Document, resulting from the following OPERATION " +
                            "being performed on the following PaintSaveFileFormat Document. OPERATION START"
                            + prompt + " OPERATION END " + f;
        String response = this.call(system, fullPrompt);
        FileIO.writeHomeFile(response, outFileName);
    }

    /**
     * newFile1: Creates a Paint File with basic shapes in a grid layout.
     *
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile1(String outFileName) {
        String prompt = "Create a Paint file with a grid layout of 3x3 rectangles, each 50x50 in size, with alternating colors.";
        newFile(prompt, outFileName);
    }

    /**
     * newFile2: Creates a Paint File with a gradient of circles.
     *
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile2(String outFileName) {
        String prompt = "Draw 10 circles in a vertical line with increasing radius, each circle a different shade of blue.";
        newFile(prompt, outFileName);
    }

    /**
     * newFile3: Creates a Paint File with a geometric art pattern.
     *
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void newFile3(String outFileName) {
        String prompt = "Create an abstract pattern of overlapping triangles and circles with random colors.";
        newFile(prompt, outFileName);
    }

    /**
     * modifyFile1: MODIFY inFileName TO PRODUCE outFileName adding a shadow effect to all shapes.
     *
     * @param inFileName  the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile1(String inFileName, String outFileName) {
        String prompt = "Add a shadow effect to every shape in the Paint file.";
        modifyFile(prompt, inFileName, outFileName);
    }

    /**
     * modifyFile2: MODIFY inFileName TO PRODUCE outFileName BY converting all shapes in the file
     *              to have rounded corners.
     *
     * @param inFileName  the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile2(String inFileName, String outFileName) {
        String prompt = "Convert all shapes in the Paint file to have rounded corners.";
        modifyFile(prompt, inFileName, outFileName);
    }

    /**
     * modifyFile3: MODIFY inFileName TO PRODUCE outFileName BY changing all shapes to have some shade of red.
     *
     * @param inFileName  the name of the source file in the users home directory
     * @param outFileName the name of the new file in the users home directory
     */
    @Override
    public void modifyFile3(String inFileName, String outFileName) {
        String prompt = "Change all shapes in the Paint file to be some shade of red.";
        modifyFile(prompt, inFileName, outFileName);
    }

    public static void main(String[] args) {
        String prompt;

        OllamaPaint op = new OllamaPaint("dh2010pc42.utm.utoronto.ca"); // Replace this with your assigned Ollama server.

        prompt = "Draw a 100 by 120 rectangle with 4 radius 5 circles at each rectangle corner.";
        op.newFile(prompt, "OllamaPaintFile1.txt");
        op.modifyFile("Remove all shapes except for the circles.", "OllamaPaintFile1.txt", "OllamaPaintFile2.txt");

        prompt = "Draw 5 concentric circles with different colors.";
        op.newFile(prompt, "OllamaPaintFile3.txt");
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile3.txt", "OllamaPaintFile4.txt");

        prompt = "Draw a polyline then two circles then a rectangle then 3 polylines all with different colors.";
        op.newFile(prompt, "OllamaPaintFile4.txt");

        prompt = "Modify the following Paint Save File so that each circle is surrounded by a non-filled rectangle. ";
        op.modifyFile("Change all circles into rectangles.", "OllamaPaintFile4.txt", "OllamaPaintFile5.txt");

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
