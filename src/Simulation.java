import java.awt.Color;
import java.lang.Math;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Simulation implements Runnable {

    PlayMode playMode = PlayMode.ANIMATE;
    int frameDelay = 30;
    int timeStepsPerFrame = 1;
    int screenshotRate = 1;
    boolean shouldTakeScreenshots = false;
    String screenshotName = "Screenshot";
    private int time = 0;

    public void setDimentions(int width, int height, int xdim, int ydim) {
        StdDraw.setCanvasSize(width, height);

        // Set the drawing scale to dimentions
        // the -.5 is so that the coordinates align with the center of the pixel
        StdDraw.setXscale(0-.5, xdim-.5);
        StdDraw.setYscale(0-.5, ydim-.5);

        // Set 1px pen radius
        double r = 1.0/width;
        StdDraw.setPenRadius(r);
    }

    /***************************************************************************
	* METHODS                                                                 *
	**************************************************************************/

    public void reset(){
        // to be implemented in a subclass
    }

    public void advance(){
        // to be implemented in a subclass
    }

    public void draw(){
        // to be implemented in a subclass
    }

    public void run(){
        reset();

        // control when to show to save running time
        StdDraw.enableDoubleBuffering();

        boolean previouslyMousePressed = false;

        // animation loop
        while (true){
            if (StdDraw.isKeyPressed(KeyEvent.VK_A)){
                // if "a" key was pressed
                playMode = PlayMode.ANIMATE;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_C)){
                // if "c" key was pressed
                playMode = PlayMode.CLICK_THROUGH;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_R)){
                // if "r" key was pressed
                time = 0;
                reset();
                draw();
                StdDraw.show();
            }

            // draw frame depending on what play mode we are in
            switch(playMode){
                case ANIMATE:
                    nextFrame();
                    StdDraw.pause(frameDelay);
                    break;
                case CLICK_THROUGH:
                    if(StdDraw.mousePressed() && !previouslyMousePressed){
                        // if new click
                        nextFrame();
                    }
                    break;
            }
            previouslyMousePressed = StdDraw.mousePressed();
        }
    }

    private void nextFrame(){
        for (int s = 0; s < timeStepsPerFrame; s++) {
            if (time % screenshotRate == 0 && shouldTakeScreenshots){
                draw();
                StdDraw.show();
                // String st = String.format("%08d", time);
                String filepath = screenshotName + "-T" + time + ".png";
                StdDraw.save(filepath);
            }
            advance();
            time++;
        }
        draw();
        StdDraw.show();
    }

    /***************************************************************************
    *                        - ANIMATE STATE ENUMERATION -                        *
    ***************************************************************************/

    public enum PlayMode {
        ANIMATE, CLICK_THROUGH
    }

}
