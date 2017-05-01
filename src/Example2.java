/*******************************************************************************
*                                 - Example2 -                                 *
*                                                                              *
* PROGRAMMER:  Jean Flaherty  04/29/17                                         *
* CLASS:  CS102                                                                *
* SEMESTER:  Spring, 2017                                                      *
* INSTRUCTOR:  Dean Zeller                                                     *
*                                                                              *
* DESCRIPTION:                                                                 *
* This program show a simple example of how a physics simulation works.        *
*                                                                              *
* EXTERNAL FILES:                                                              *
* - StdDraw.java                                                               *
* - RetinaIcon.java                                                            *
* - Simulation.java                                                            *
*                                                                              *
* These files must be in the workspace of the program for it to work correctly.*
*                                                                              *
* The drawing commands used in this program are part of the StdDraw            *
* graphics libary. Slight modifications were made in order to make the graphic *
* display nicely on a retina display macbook. Include RatinaIcon.java when     *
* using this verion of StdDraw. However the original StdDraw.java should work  *
* as well. It can be found at http://introcs.cs.princeton.edu/java/stdlib/     *
*                                                                              *
* CREDITS:                                                                     *
* This program is copyright (c) 2017 Jean Flaherty.                            *
*******************************************************************************/

import java.awt.Color;
import java.lang.Math;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JOptionPane;

public class Example2 extends Simulation {
    /***************************************************************************
    *                                - DIMENTIONS -                            *
    ***************************************************************************/
    // simulation canvas size
    static int width = 1200, height = 480;
    // number of data points / pixels per dimention
    static int xdim = 100, ydim = 40;
    // static int xdim = 4800, ydim = 1920; // HD
    // static int xdim = 2400, ydim = 960;
    // static int xdim = 1200, ydim = 480;
    // static int xdim = 600, ydim = 240;
    // static int xdim = 400, ydim = 160;
    // static int xdim = 200, ydim = 80;

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

        // messege to user
        String message = "Pressable keys:\n" +
        "   (A) Animate - plays the simulation\n" +
        "   (C) Click through - click to advance to next frame\n" +
        "   (R) Reset - resets the simulation\n";
        JOptionPane.showMessageDialog(null, message);

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
                // translate into coordinate relative to center
                int relx = xdim/2 - x;
                int rely = ydim/2 - y;
                // distance from center
                double dist =  Math.sqrt(relx*relx + rely*rely);
                // diagonal of canvas for a good scale
                double diagonal = Math.sqrt(xdim*xdim + ydim*ydim);
                H[x][y] = (dist / diagonal) % 1.0;
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
