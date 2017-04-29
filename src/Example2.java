import java.awt.Color;
import java.lang.Math;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Example2 extends Simulation {
    /***************************************************************************
    *                                - DIMENTIONS -                            *
    ***************************************************************************/
    // simulation canvas size
    static int width = 1200, height = 480;
    // number of data points / pixels per dimention
    static int xdim = 100, ydim = 40;
    // static int xdim = 4800, ydim = 1920; // HD

    /***************************************************************************
    *                           - SIMULATION VARIABLES -                       *
    ***************************************************************************/

    double[][] H  = new double[xdim][ydim]; // hue

    /***************************************************************************
    *                              - MAIN METHOD -                             *
    ***************************************************************************/

    public static void main(String[] args) {
        Example2 simulation = new Example2();
        simulation.setDimentions(width, height, xdim, ydim);
        simulation.frameDelay = 30;
        simulation.timeStepsPerFrame = 1;
        simulation.screenshotRate = 1;
        simulation.screenshotName = "Example2";
        simulation.shouldTakeScreenshots = false;

        // Now start the simulation thread:
        Thread simThread = new Thread(simulation);
        simThread.start();
    }

    /***************************************************************************
	*                            - RESET SIMULATION -                          *
	***************************************************************************/

    @Override
    public void reset(){
        // Initial conditions
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                // make a circlular pattern
                int relx = xdim/2 - x;
                int rely = ydim/2 - y;
                // distance from center
                double dist =  Math.sqrt(relx*relx + rely*rely);
                double max_dist = Math.sqrt(xdim*xdim + ydim*ydim);
                H[x][y] = (dist / max_dist) % 1.0;
            }
        }
    }


    /***************************************************************************
	*                          - ADVANCE SIMULATION -                          *
	***************************************************************************/

    @Override
    public void advance(){
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                H[x][y] = (H[x][y] + 0.1) % 1.0;
            }
        }
    }


    /***************************************************************************
    *                            - DRAW SIMULATION -                           *
    ***************************************************************************/

    @Override
    public void draw(){
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                float h = (float) H[x][y];
                Color color = Color.getHSBColor(h,1.0f,1.0f);
                double r = 0.5;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }
}
