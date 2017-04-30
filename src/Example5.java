/*******************************************************************************
*                                 - Example5 -                                 *
*                                                                              *
* PROGRAMMER:  Jean Flaherty  04/29/17                                         *
* CLASS:  CS102                                                                *
* SEMESTER:  Spring, 2017                                                      *
* INSTRUCTOR:  Dean Zeller                                                     *
*                                                                              *
* DESCRIPTION:                                                                 *
* This program show a simple example of how a physics simulation works.        *
* The math/physics was adapted from http://physics.weber.edu/schroeder/fluids/ *
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
* Adapted from: http://physics.weber.edu/schroeder/fluids/                     *
*******************************************************************************/

import java.awt.Color;
import java.lang.Math;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JOptionPane;

public class Example5 extends Simulation {
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

    double spontaneity = 0.00003;
    double viscocity = 0.100;


    double[][] n0  = new double[xdim][ydim];
    double[][] nN  = new double[xdim][ydim];
    double[][] nS  = new double[xdim][ydim];
    double[][] nE  = new double[xdim][ydim];
    double[][] nW  = new double[xdim][ydim];
    double[][] nNW  = new double[xdim][ydim];
    double[][] nNE  = new double[xdim][ydim];
    double[][] nSW  = new double[xdim][ydim];
    double[][] nSE  = new double[xdim][ydim];

    // Calculated variables
    double[][] density  = new double[xdim][ydim];
    double[][] speed2  = new double[xdim][ydim];

    // Boolean array, true at sites that contain barriers:
    boolean[][] barrier = new boolean[xdim][ydim];


    // Calculation short-cuts:
    double four9ths = 4.0 / 9;
    double one9th = 1.0 / 9;
    double one36th = 1.0 / 36;

    /***************************************************************************
    *                              - MAIN METHOD -                             *
    ***************************************************************************/

    public static void main(String[] args) {
        Example5 simulation = new Example5();
        simulation.setDimentions(width, height, xdim, ydim);
        simulation.frameDelay = 0;
        simulation.timeStepsPerFrame = 1;
        simulation.screenshotRate = 1;
        simulation.screenshotName = "Example5";
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
                n0[x][y]  = four9ths;
                nE[x][y]  =   one9th;
                nW[x][y]  =   one9th;
                nN[x][y]  =   one9th;
                nS[x][y]  =   one9th;
                nNE[x][y] =  one36th;
                nSE[x][y] =  one36th;
                nNW[x][y] =  one36th;
                nSW[x][y] =  one36th;

                density[x][y] = 0;
                speed2[x][y] = 0;
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
                boolean diceroll = Math.random() < spontaneity;
                if (diceroll){
                    double amount = Math.random() * 10.0;
                    n0[x][y] += amount*four9ths;
                    nE[x][y] += amount*one9th;
                    nW[x][y] += amount*one9th;
                    nN[x][y] += amount*one9th;
                    nS[x][y] += amount*one9th;
                    nNE[x][y] += amount*one36th;
                    nNW[x][y] += amount*one36th;
                    nSE[x][y] += amount*one36th;
                    nSW[x][y] += amount*one36th;
                }
            }
        }
        collide();
        stream();
    }

    public void collide(){
        double n, one9thn, one36thn, vx, vy, vx2, vy2, vx3, vy3, vxvy2, v2, v215;
        double omega = 1 / (3*viscocity + 0.5);    // reciprocal of tau, the relaxation time

        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                n = n0[x][y] + nN[x][y] + nS[x][y] + nE[x][y] + nW[x][y] + nNW[x][y] + nNE[x][y] + nSW[x][y] + nSE[x][y];
                density[x][y] = n;
                one9thn = one9th * n;
                one36thn = one36th * n;
                if (n > 0) {
                    vx = (nE[x][y] + nNE[x][y] + nSE[x][y] - nW[x][y] - nNW[x][y] - nSW[x][y]) / n;
                } else vx = 0;
                if (n > 0) {
                    vy = (nN[x][y] + nNE[x][y] + nNW[x][y] - nS[x][y] - nSE[x][y] - nSW[x][y]) / n;
                } else vy = 0;
                vx3 = 3 * vx;
                vy3 = 3 * vy;
                vx2 = vx * vx;
                vy2 = vy * vy;
                vxvy2 = 2 * vx * vy;
                v2 = vx2 + vy2;
                speed2[x][y] = v2;        // may be needed for plotting
                v215 = 1.5 * v2;
                n0[x][y]  += omega * (four9ths*n * (1                              - v215) - n0[x][y]);
                nE[x][y]  += omega * (   one9thn * (1 + vx3       + 4.5*vx2        - v215) - nE[x][y]);
                nW[x][y]  += omega * (   one9thn * (1 - vx3       + 4.5*vx2        - v215) - nW[x][y]);
                nN[x][y]  += omega * (   one9thn * (1 + vy3       + 4.5*vy2        - v215) - nN[x][y]);
                nS[x][y]  += omega * (   one9thn * (1 - vy3       + 4.5*vy2        - v215) - nS[x][y]);
                nNE[x][y] += omega * (  one36thn * (1 + vx3 + vy3 + 4.5*(v2+vxvy2) - v215) - nNE[x][y]);
                nNW[x][y] += omega * (  one36thn * (1 - vx3 + vy3 + 4.5*(v2-vxvy2) - v215) - nNW[x][y]);
                nSE[x][y] += omega * (  one36thn * (1 + vx3 - vy3 + 4.5*(v2-vxvy2) - v215) - nSE[x][y]);
                nSW[x][y] += omega * (  one36thn * (1 - vx3 - vy3 + 4.5*(v2+vxvy2) - v215) - nSW[x][y]);
            }
        }
    }

    public void stream(){
        for (int x=0; x<xdim-1; x++) {        // first start in NW corner...
            for (int y=ydim-1; y>0; y--) {
                nN[x][y] = nN[x][y-1];        // move the north-moving particles
                nNW[x][y] = nNW[x+1][y-1];    // and the northwest-moving particles
            }
        }
        for (int x=xdim-1; x>0; x--) {        // now start in NE corner...
            for (int y=ydim-1; y>0; y--) {
                nE[x][y] = nE[x-1][y];        // move the east-moving particles
                nNE[x][y] = nNE[x-1][y-1];    // and the northeast-moving particles
            }
        }
        for (int x=xdim-1; x>0; x--) {        // now start in SE corner...
            for (int y=0; y<ydim-1; y++) {
                nS[x][y] = nS[x][y+1];        // move the south-moving particles
                nSE[x][y] = nSE[x-1][y+1];    // and the southeast-moving particles
            }
        }
        for (int x=0; x<xdim-1; x++) {        // now start in the SW corner...
            for (int y=0; y<ydim-1; y++) {
                nW[x][y] = nW[x+1][y];        // move the west-moving particles
                nSW[x][y] = nSW[x+1][y+1];    // and the southwest-moving particles
            }
        }

        // Now handle left boundary as in Pullan's example code:
        // Stream particles in from the non-existent space to the left, with the
        // user-determined speed:
        for (int y=0; y<ydim; y++) {
            if (!barrier[0][y]) {
                nE[0][y] = one9th;
                nNE[0][y] = one36th;
                nSE[0][y] = one36th;
            }
        }
        // Try the same thing at the right edge and see if it works:
        for (int y=0; y<ydim; y++) {
            if (!barrier[0][y]) {
                nW[xdim-1][y] = one9th;
                nNW[xdim-1][y] = one36th;
                nSW[xdim-1][y] = one36th;
            }
        }
        // Now handle top and bottom edges:
        for (int x=0; x<xdim; x++) {
            n0[x][0]  = four9ths;
            nE[x][0]  =   one9th;
            nW[x][0]  =   one9th;
            nN[x][0]  =   one9th;
            nS[x][0]  =   one9th;
            nNE[x][0] =  one36th;
            nSE[x][0] =  one36th;
            nNW[x][0] =  one36th;
            nSW[x][0] =  one36th;
            n0[x][ydim-1]  = four9ths;
            nE[x][ydim-1]  =   one9th;
            nW[x][ydim-1]  =   one9th;
            nN[x][ydim-1]  =   one9th;
            nS[x][ydim-1]  =   one9th;
            nNE[x][ydim-1] =  one36th;
            nSE[x][ydim-1] =  one36th;
            nNW[x][ydim-1] =  one36th;
            nSW[x][ydim-1] =  one36th;
        }
    }

    /***************************************************************************
    *                            - DRAW SIMULATION -                           *
    ***************************************************************************/

    @Override
    public void draw(){
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                float b = Math.min(100.0f * (float) speed2[x][y], 1.0f);
                Color color = Color.getHSBColor(0.75f,1.0f,b);
                double r = 0.5;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }

    public boolean isOnScreen(int x, int y){
        return x >= 0 && x < xdim && y >= 0 && y < ydim;
    }
}
