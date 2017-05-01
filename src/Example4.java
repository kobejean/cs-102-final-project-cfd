/*******************************************************************************
*                                 - Example4 -                                 *
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

public class Example4 extends Simulation {
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
    *                           - STATE ENUMERATION -                          *
    ***************************************************************************/

    public enum State {
        EMPTY_SPACE, BARRIER, LEFT_PARTICLE, RIGHT_PARTICLE
    }

    /***************************************************************************
    *                           - SIMULATION VARIABLES -                       *
    ***************************************************************************/

    public State[][] S = new State[xdim][ydim]; // states

    /***************************************************************************
    *                              - MAIN METHOD -                             *
    ***************************************************************************/

    public static void main(String[] args) {
        Example4 simulation = new Example4();
        simulation.setDimentions(width, height, xdim, ydim);
        simulation.frameDelay = 30;
        simulation.timeStepsPerFrame = 1;
        simulation.screenshotRate = 1;
        simulation.screenshotName = "Example4";
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
        // Add side walls and particles
        for (int y = 0; y < ydim; y++){
            int left_x = 0;
            int right_x = xdim-1;
            // random x coordinate between barriers for particles
            int rand_x = (int) (Math.random() * (xdim-3) + 1);

            S[left_x][y] = State.BARRIER;
            S[right_x][y] = State.BARRIER;
            S[rand_x][y] = State.LEFT_PARTICLE;

            for (int x = 0; x < xdim; x++){
                if (S[x][y] == null){
                    S[x][y] = State.EMPTY_SPACE;
                }
            }
        }

        // Uncomment this to include a circular barrier in the middle

        // // Add center circle barrier
        // for (int x = 0; x < xdim; x++){
        //     for (int y = 0; y < ydim; y++){
        //         // relative coordinates to the center
        //         int relx = x - xdim/2;
        //         int rely = y - ydim/2;
        //
        //         // curent distance from center
        //         double dist =  Math.sqrt(relx*relx + rely*rely);
        //         // radius of barrier
        //         double r = Math.min(xdim,ydim) * 0.2;
        //
        //         if (dist < r){
        //             S[x][y] = State.BARRIER;
        //         }
        //     }
        // }
    }


    /***************************************************************************
    *                          - ADVANCE SIMULATION -                          *
    ***************************************************************************/

    @Override
    public void advance(){
        State[][] tempS = new State[xdim][ydim];
        // copy S into tempS
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                tempS[x][y] = S[x][y];
            }
        }
        // create the next states
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                // current state
                State s = S[x][y];
                if (s == State.RIGHT_PARTICLE){
                    // current state is right particle
                    if(isOnScreen(x+1,y)){
                        // right state - state of the space one to the right
                        State rs = S[x+1][y];
                        if (rs == State.BARRIER){
                            // bounce by switching color
                            tempS[x][y] = State.LEFT_PARTICLE;
                        }else if(rs == State.EMPTY_SPACE){
                            // move to next space
                            tempS[x][y] = State.EMPTY_SPACE;
                            tempS[x+1][y] = State.RIGHT_PARTICLE;
                        }
                    }
                } else if (s == State.LEFT_PARTICLE){
                    // current state is left particle
                    if(isOnScreen(x-1,y)){
                        // left state - state of the space one to the left
                        State ls = S[x-1][y];
                        if (ls == State.BARRIER){
                            // bounce by switching color
                            tempS[x][y] = State.RIGHT_PARTICLE;
                        }else if(ls == State.EMPTY_SPACE){
                            // move to next space
                            tempS[x][y] = State.EMPTY_SPACE;
                            tempS[x-1][y] = State.LEFT_PARTICLE;
                        }
                    }
                }
            }
        }
        S = tempS;
    }


    /***************************************************************************
    *                            - DRAW SIMULATION -                           *
    ***************************************************************************/

    @Override
    public void draw(){
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                State state = S[x][y];
                Color color = Color.BLACK;
                switch(state){
                    case EMPTY_SPACE:
                        color = Color.CYAN;
                        break;
                    case BARRIER:
                        color = Color.YELLOW;
                        break;
                    case LEFT_PARTICLE:
                        color = Color.BLUE;
                        break;
                    case RIGHT_PARTICLE:
                        color = Color.RED;
                        break;
                }
                double r = 0.5; // radius of square is half a pixel
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }

    public boolean isOnScreen(int x, int y){
        return x >= 0 && x < xdim && y >= 0 && y < ydim;
    }
}
