import java.awt.Color;
import java.lang.Math;
import java.awt.event.KeyEvent;
import java.util.Random;

public class ProofOfConcept {
    // public static int xdim = 2400;
    // public static int ydim = 960;
    // public static int xdim = 1200;
    // public static int ydim = 480;
    // public static int xdim = 600;
    // public static int ydim = 240;
    // public static int xdim = 400;
    // public static int ydim = 160;
    public int xdim = 100;
    public int ydim = 40;
    public double[][] H  = new double[xdim][ydim]; // hue
    public double[][] B  = new double[xdim][ydim]; // brightness
    public State[][] S = new State[xdim][ydim]; // state
    public DisplayMode displayMode = DisplayMode.EXAMPLE1;
    public PlayState playState = PlayState.FRAME_BY_FRAME;

    ProofOfConcept() {
        reset();

        int width = 1200, height = 480;
        StdDraw.setCanvasSize(width, height);

        // Set the drawing scale to dimentions
        // the -.5 is so that the coordinates align with the center of the pixel
        StdDraw.setXscale(0-.5, xdim-.5);
        StdDraw.setYscale(0-.5, ydim-.5);

        // Set 1px pen radius
        double r = 1.0/width;
        StdDraw.setPenRadius(r);

        run();
    }

    public static void main(String[] args) {
        new ProofOfConcept();
    }

    /***************************************************************************
	* METHODS                                                                 *
	**************************************************************************/

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
    }

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

    public void run(){
        // control when to show to save running time
        StdDraw.enableDoubleBuffering();

        int shortDelay = 30;

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
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_P)){
                playState = PlayState.PLAY;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_F)){
                playState = PlayState.FRAME_BY_FRAME;
            }

            if (oldDispMode != displayMode){
                // update canvas
                reset();
                draw();
                StdDraw.show();
            }
            // StdDraw.clear();
            switch(playState){
                case PLAY:
                    advance();
                    draw();
                    StdDraw.show();
                    StdDraw.pause(shortDelay);
                    break;
                case FRAME_BY_FRAME:
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

    public boolean isOnScreen(int x, int y){
        return x >= 0 && x < xdim && y >= 0 && y < ydim;
    }

    /***************************************************************************
    *                      - DISPLAY MODE ENUMERATION -                        *
    ***************************************************************************/

    public enum DisplayMode {
        EXAMPLE1, EXAMPLE2, EXAMPLE3, EXAMPLE4
    }

    /***************************************************************************
    *                        - PLAY STATE ENUMERATION -                        *
    ***************************************************************************/

    public enum PlayState {
        PLAY, FRAME_BY_FRAME
    }

    /***************************************************************************
    *                           - STATE ENUMERATION -                          *
    ***************************************************************************/

    public enum State {
        EMPTY_SPACE, BARRIER, LEFT_PARTICLE, RIGHT_PARTICLE
    }
}
