import java.awt.Color;
import java.lang.Math;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Example3 extends Simulation {
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
    double[][] B  = new double[xdim][ydim]; // brightness

    /***************************************************************************
    *                              - MAIN METHOD -                             *
    ***************************************************************************/

    public static void main(String[] args) {
        Example3 simulation = new Example3();
        simulation.setDimentions(width, height, xdim, ydim);
        simulation.frameDelay = 30;
        simulation.timeStepsPerFrame = 1;
        simulation.screenshotRate = 1;
        simulation.screenshotName = "Example3";
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
                B[x][y] = (x + y) % 10.0;
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
                B[x][y] = (B[x][y] + 1.0) % 10.0;
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
                float b = (float) (0.6f + B[x][y]/25);
                Color color = Color.getHSBColor(h,1.0f,b);
                double r = 0.5;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }
}
