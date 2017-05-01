/*******************************************************************************
*                             - ProofOfConcept -                               *
*                                                                              *
* PROGRAMMER:  Jean Flaherty  04/29/17                                         *
* CLASS:  CS102                                                                *
* SEMESTER:  Spring, 2017                                                      *
* INSTRUCTOR:  Dean Zeller                                                     *
*                                                                              *
* DESCRIPTION:                                                                 *
* This program shows simple examples of how a physics simulation works.        *
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
import javax.swing.JOptionPane;

public class ProofOfConcept extends Simulation {
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

    public double[][] H  = new double[xdim][ydim]; // hue
    public double[][] B  = new double[xdim][ydim]; // brightness
    public State[][] S = new State[xdim][ydim]; // state
    public DisplayMode displayMode = DisplayMode.EXAMPLE1;


    /***************************************************************************
    *                              - MAIN METHOD -                             *
    ***************************************************************************/

    public static void main(String[] args) {
        ProofOfConcept simulation = new ProofOfConcept();
        simulation.setDimentions(width, height, xdim, ydim);

        // messege to user
        String message = "Pressable keys:\n" +
        "   (1) Example 1 - shows example 1\n" +
        "   (2) Example 2 - shows example 2\n" +
        "   (3) Example 3 - shows example 3\n" +
        "   (4) Example 4 - shows example 4\n" +
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
        // Set H and B
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                int relx = xdim/2 - x;
                int rely = ydim/2 - y;
                double r =  Math.sqrt(relx*relx + rely*rely) * 0.01;

                H[x][y] = r % 1.0;
                B[x][y] = (x + y) % 10.0;
                S[x][y] = State.EMPTY_SPACE;
            }
        }
        // Set states
        for (int y = 0; y < ydim; y++){
            int left_x = 0;
            // random x coordinate between barriers for particles
            int rand_x = (int) (Math.random()*(xdim-3) + 1);
            int right_x = xdim-1;
            S[left_x][y] = State.BARRIER;
            S[rand_x][y] = State.LEFT_PARTICLE;
            S[right_x][y] = State.BARRIER;
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
        State[][] newS = new State[xdim][ydim];
        // copy
        for (int i = 0; i < xdim; i++){
            for (int j = 0; j < ydim; j++){
                newS[i][j] = S[i][j];
            }
        }
        for (int i = 0; i < xdim; i++){
            for (int j = 0; j < ydim; j++){
                H[i][j] = (H[i][j] + 0.1) % 1.0;
                B[i][j] = (B[i][j] + 1.0) % 10.0;
                State s = S[i][j];
                if (s == State.RIGHT_PARTICLE){
                    if(isOnScreen(i+1,j)){
                        State rs = S[i+1][j];
                        if (rs == State.BARRIER){
                            newS[i][j] = State.LEFT_PARTICLE;
                        }else if(rs == State.EMPTY_SPACE){
                            newS[i][j] = State.EMPTY_SPACE;
                            newS[i+1][j] = State.RIGHT_PARTICLE;
                        }
                    }
                } else if (s == State.LEFT_PARTICLE){
                    if(isOnScreen(i-1,j)){
                        State ls = S[i-1][j];
                        if (ls == State.BARRIER){
                            newS[i][j] = State.RIGHT_PARTICLE;
                        }else if(ls == State.EMPTY_SPACE){
                            newS[i][j] = State.EMPTY_SPACE;
                            newS[i-1][j] = State.LEFT_PARTICLE;
                        }
                    }
                }
            }
        }
        S = newS;
    }

    public boolean isOnScreen(int x, int y){
        return x >= 0 && x < xdim && y >= 0 && y < ydim;
    }


    /***************************************************************************
    *                            - DRAW SIMULATION -                           *
    ***************************************************************************/

    @Override
    public void draw(){
        switch(displayMode){
            case EXAMPLE1: drawExample1(); break;
            case EXAMPLE2: drawExample2(); break;
            case EXAMPLE3: drawExample3(); break;
            case EXAMPLE4: drawExample4(); break;
        }
    }

    public void drawExample1(){
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                float b = (float) B[x][y]/10;
                Color color = Color.getHSBColor(1.0f,0.0f,b);
                double r = 0.5;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }

    public void drawExample2(){
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

    public void drawExample3(){
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                float h = (float) H[x][y];
                float b = (float) (1.0f - B[x][y]/25);
                Color color = Color.getHSBColor(h,1.0f,b);
                double r = 0.5;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }

    public void drawExample4(){
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                State state = S[x][y];
                Color color = Color.WHITE;
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
                double r = 0.5;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }


    /***************************************************************************
    *                      - DISPLAY MODE ENUMERATION -                        *
    ***************************************************************************/

    public enum DisplayMode {
        EXAMPLE1, EXAMPLE2, EXAMPLE3, EXAMPLE4
    }


    @Override
    public void run(){
        reset();
        // control when to show to save running time
        StdDraw.enableDoubleBuffering();

        boolean wasMousePressed = false;
        while (true){
            DisplayMode oldDispMode = displayMode;
            if(StdDraw.isKeyPressed(KeyEvent.VK_1)){
                displayMode = DisplayMode.EXAMPLE1;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_2)){
                displayMode = DisplayMode.EXAMPLE2;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_3)){
                displayMode = DisplayMode.EXAMPLE3;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_4)){
                displayMode = DisplayMode.EXAMPLE4;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_A)){
                playMode = PlayMode.ANIMATE;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_C)){
                playMode = PlayMode.CLICK_THROUGH;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_R)){
                // if "r" key was pressed
                reset();
                draw();
                StdDraw.show();
            }

            if (oldDispMode != displayMode){
                // update canvas
                reset();
                draw();
                StdDraw.show();
            }
            // StdDraw.clear();
            switch(playMode){
                case ANIMATE:
                    advance();
                    draw();
                    StdDraw.show();
                    StdDraw.pause(frameDelay);
                    break;
                case CLICK_THROUGH:
                    if(StdDraw.mousePressed() && !wasMousePressed){
                        // if new click
                        advance();
                        draw();
                        StdDraw.show();
                    }
                    break;
            }
            wasMousePressed = StdDraw.mousePressed();
        }
    }

}
