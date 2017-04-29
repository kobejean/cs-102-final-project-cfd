/*******************************************************************************
*                                   - CFD -                                    *
*                                                                              *
* PROGRAMMER:  Jean Flaherty  04/29/17                                         *
* CLASS:  CS102                                                                *
* SEMESTER:  Spring, 2017                                                      *
* INSTRUCTOR:  Dean Zeller                                                     *
*                                                                              *
* DESCRIPTION:                                                                 *
* This program simulates fluid dynamics using Lattice Boltzman Methods.        *
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
import javax.swing.JOptionPane;

public class CFD extends Simulation {

    /***************************************************************************
    *                                - DIMENTIONS -                            *
    ***************************************************************************/
    // simulation canvas size
    static int width = 1200, height = 480;
    // number of data points / pixels per dimention
    static int xdim = 200, ydim = 80;
    // static int xdim = 4800, ydim = 1920; // HD
    // static int xdim = 2400, ydim = 960;
    // static int xdim = 1200, ydim = 480;
    // static int xdim = 600, ydim = 240;
    // static int xdim = 400, ydim = 160;
    // static int xdim = 100, ydim = 40;

    /***************************************************************************
    *                           - SIMULATION VARIABLES -                       *
    ***************************************************************************/

    // Constants
    double velocity = 0.070;
    double viscocity = 0.020;

    // Here are the arrays of densities by velocity, named by velocity directions with north up:
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
    double[][] xvel  = new double[xdim][ydim];
    double[][] yvel  = new double[xdim][ydim];
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
        CFD simulation = new CFD();
        simulation.setDimentions(width, height, xdim, ydim);
        simulation.frameDelay = 30;
        simulation.timeStepsPerFrame = 10;
        simulation.screenshotRate = 250;
        simulation.screenshotName = "CFD";
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
        // initial conditions
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                int relx = xdim/2 - x;
                int rely = ydim/2 - y;
                double r =  Math.sqrt(relx*relx + rely*rely);

                barrier[x][y] = (r < Math.min(xdim, ydim) * 0.2);

                if (barrier[x][y]) {
                    n0[x][y] = 0;
                    nE[x][y] = 0;
                    nW[x][y] = 0;
                    nN[x][y] = 0;
                    nS[x][y] = 0;
                    nNE[x][y] = 0;
                    nNW[x][y] = 0;
                    nSE[x][y] = 0;
                    nSW[x][y] = 0;
                    xvel[x][y] = 0;
                    yvel[x][y] = 0;
                    speed2[x][y] = 0;
				} else {
                    double v = velocity;
					n0[x][y]  = four9ths * (1 - 1.5*v*v);
					nE[x][y]  =   one9th * (1 + 3*v + 3*v*v);
					nW[x][y]  =   one9th * (1 - 3*v + 3*v*v);
					nN[x][y]  =   one9th * (1 - 1.5*v*v);
					nS[x][y]  =   one9th * (1 - 1.5*v*v);
					nNE[x][y] =  one36th * (1 + 3*v + 3*v*v);
					nSE[x][y] =  one36th * (1 + 3*v + 3*v*v);
					nNW[x][y] =  one36th * (1 - 3*v + 3*v*v);
					nSW[x][y] =  one36th * (1 - 3*v + 3*v*v);
					density[x][y] = 1;
					xvel[x][y] = v;
					yvel[x][y] = 0;
					speed2[x][y] = v*v;
				}
            }
        }
    }


    /***************************************************************************
	*                          - ADVANCE SIMULATION -                          *
	***************************************************************************/

    @Override
    synchronized public void advance(){
		collide();
		stream();
		bounce();
    }


    /***************************************************************************
	*                               - COLLIDE -                                *
    * Collide particles within each cell.  Adapted from Wagner's D2Q9 code.    *
    * From: http://physics.weber.edu/schroeder/fluids/                         *
	***************************************************************************/

    void collide() {
        double n, one9thn, one36thn, vx, vy, vx2, vy2, vx3, vy3, vxvy2, v2, v215;
        double omega = 1 / (3*viscocity + 0.5);	// reciprocal of tau, the relaxation time
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                if (!barrier[x][y]) {
                    n = n0[x][y] + nN[x][y] + nS[x][y] + nE[x][y] + nW[x][y] + nNW[x][y] + nNE[x][y] + nSW[x][y] + nSE[x][y];
                    density[x][y] = n;		// macroscopic density may be needed for plotting
                    one9thn = one9th * n;
                    one36thn = one36th * n;
                    if (n > 0) {
                        vx = (nE[x][y] + nNE[x][y] + nSE[x][y] - nW[x][y] - nNW[x][y] - nSW[x][y]) / n;
                    } else vx = 0;
                    xvel[x][y] = vx;		// may be needed for plotting
                    if (n > 0) {
                        vy = (nN[x][y] + nNE[x][y] + nNW[x][y] - nS[x][y] - nSE[x][y] - nSW[x][y]) / n;
                    } else vy = 0;
                    yvel[x][y] = vy;		// may be needed for plotting
                    vx3 = 3 * vx;
                    vy3 = 3 * vy;
                    vx2 = vx * vx;
                    vy2 = vy * vy;
                    vxvy2 = 2 * vx * vy;
                    v2 = vx2 + vy2;
                    speed2[x][y] = v2;		// may be needed for plotting
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
    }


    /***************************************************************************
	*                               - STREAM -                                 *
    * Stream particles into neighboring cells                                  *
    * From: http://physics.weber.edu/schroeder/fluids/                         *
	***************************************************************************/
    void stream() {
        for (int x=0; x<xdim-1; x++) {		// first start in NW corner...
            for (int y=ydim-1; y>0; y--) {
                nN[x][y] = nN[x][y-1];		// move the north-moving particles
                nNW[x][y] = nNW[x+1][y-1];	// and the northwest-moving particles
            }
        }
        for (int x=xdim-1; x>0; x--) {		// now start in NE corner...
            for (int y=ydim-1; y>0; y--) {
                nE[x][y] = nE[x-1][y];		// move the east-moving particles
                nNE[x][y] = nNE[x-1][y-1];	// and the northeast-moving particles
            }
        }
        for (int x=xdim-1; x>0; x--) {		// now start in SE corner...
            for (int y=0; y<ydim-1; y++) {
                nS[x][y] = nS[x][y+1];		// move the south-moving particles
                nSE[x][y] = nSE[x-1][y+1];	// and the southeast-moving particles
            }
        }
        for (int x=0; x<xdim-1; x++) {		// now start in the SW corner...
            for (int y=0; y<ydim-1; y++) {
                nW[x][y] = nW[x+1][y];		// move the west-moving particles
                nSW[x][y] = nSW[x+1][y+1];	// and the southwest-moving particles
            }
        }
        // We missed a few at the left and right edges:
        for (int y=0; y<ydim-1; y++) {
            nS[0][y] = nS[0][y+1];
        }
        for (int y=ydim-1; y>0; y--) {
            nN[xdim-1][y] = nN[xdim-1][y-1];
        }
        // Now handle left boundary as in Pullan's example code:
        // Stream particles in from the non-existent space to the left, with the
        // user-determined speed:
        double v = velocity;
        for (int y=0; y<ydim; y++) {
            if (!barrier[0][y]) {
                nE[0][y] = one9th * (1 + 3*v + 3*v*v);
                nNE[0][y] = one36th * (1 + 3*v + 3*v*v);
                nSE[0][y] = one36th * (1 + 3*v + 3*v*v);
            }
        }
        // Try the same thing at the right edge and see if it works:
        for (int y=0; y<ydim; y++) {
            if (!barrier[0][y]) {
                nW[xdim-1][y] = one9th * (1 - 3*v + 3*v*v);
                nNW[xdim-1][y] = one36th * (1 - 3*v + 3*v*v);
                nSW[xdim-1][y] = one36th * (1 - 3*v + 3*v*v);
            }
        }
        // Now handle top and bottom edges:
        for (int x=0; x<xdim; x++) {
            n0[x][0]  = four9ths * (1 - 1.5*v*v);
            nE[x][0]  =   one9th * (1 + 3*v + 3*v*v);
            nW[x][0]  =   one9th * (1 - 3*v + 3*v*v);
            nN[x][0]  =   one9th * (1 - 1.5*v*v);
            nS[x][0]  =   one9th * (1 - 1.5*v*v);
            nNE[x][0] =  one36th * (1 + 3*v + 3*v*v);
            nSE[x][0] =  one36th * (1 + 3*v + 3*v*v);
            nNW[x][0] =  one36th * (1 - 3*v + 3*v*v);
            nSW[x][0] =  one36th * (1 - 3*v + 3*v*v);
            n0[x][ydim-1]  = four9ths * (1 - 1.5*v*v);
            nE[x][ydim-1]  =   one9th * (1 + 3*v + 3*v*v);
            nW[x][ydim-1]  =   one9th * (1 - 3*v + 3*v*v);
            nN[x][ydim-1]  =   one9th * (1 - 1.5*v*v);
            nS[x][ydim-1]  =   one9th * (1 - 1.5*v*v);
            nNE[x][ydim-1] =  one36th * (1 + 3*v + 3*v*v);
            nSE[x][ydim-1] =  one36th * (1 + 3*v + 3*v*v);
            nNW[x][ydim-1] =  one36th * (1 - 3*v + 3*v*v);
            nSW[x][ydim-1] =  one36th * (1 - 3*v + 3*v*v);
        }
    }


    /***************************************************************************
    *                               - BOUNCE -                                 *
    * Bounce particles off of barriers:                                        *
    * (The ifs are needed to prevent array index out of bounds errors.         *
    *  Could handle edges separately to avoid this.)                           *
    * From: http://physics.weber.edu/schroeder/fluids/                         *
    ***************************************************************************/

    void bounce() {
        for (int x=0; x<xdim; x++) {
            for (int y=0; y<ydim; y++) {
                if (barrier[x][y]) {
                    if (nN[x][y] > 0) { nS[x][y-1] += nN[x][y]; nN[x][y] = 0; }
                    if (nS[x][y] > 0) { nN[x][y+1] += nS[x][y]; nS[x][y] = 0; }
                    if (nE[x][y] > 0) { nW[x-1][y] += nE[x][y]; nE[x][y] = 0; }
                    if (nW[x][y] > 0) { nE[x+1][y] += nW[x][y]; nW[x][y] = 0; }
                    if (nNW[x][y] > 0) { nSE[x+1][y-1] += nNW[x][y]; nNW[x][y] = 0; }
                    if (nNE[x][y] > 0) { nSW[x-1][y-1] += nNE[x][y]; nNE[x][y] = 0; }
                    if (nSW[x][y] > 0) { nNE[x+1][y+1] += nSW[x][y]; nSW[x][y] = 0; }
                    if (nSE[x][y] > 0) { nNW[x-1][y+1] += nSE[x][y]; nSE[x][y] = 0; }
                }
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
                float S = Math.min((float) Math.sqrt(speed2[x][y])*3.0f, 1.0f);
                Color color = Color.getHSBColor(0.5f,1.0f,S);

                double r = 0.5;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }
}
