import java.awt.Color;
import java.lang.Math;
import java.awt.event.KeyEvent;

public class ProofOfConceptCFD {
    public static int xdim = 600;
    public static int ydim = 240;
    public static double[][] H  = new double[xdim][ydim];
    public static double[][] B  = new double[xdim][ydim];
    public static Mode mode = Mode.ALL;

    public static void main(String[] args) {
        // ProofOfConceptCFD poc = new ProofOfConceptCFD();
        reset();
        int ppc = 2; // pixels per coordinate
        int width = xdim * ppc, height = ydim * ppc;
        StdDraw.setCanvasSize(width, height);  //default is 1200 x 480

        //Set the drawing scale to dimentions
        StdDraw.setXscale(0, xdim);
        StdDraw.setYscale(0, ydim);

        double r = 1.0/width;
        StdDraw.setPenRadius(r);

        run();
    }

    /***************************************************************************
	* METHODS                                                                 *
	**************************************************************************/

    public static void reset(){
        for (int i = 0; i < xdim; i++){
            for (int j = 0; j < ydim; j++){
                assert xdim == H.length;
                assert ydim == H[i].length;
                int x = xdim/2 - i;
                int y = ydim/2 - j;
                // double r =  Math.sqrt(x*x + y*y) * 0.001;
                double r =  Math.sqrt(x*x + y*y) * 0.01;

                H[i][j] = r % 1.0;//(x + y) % 10.0;
                B[i][j] = (x + y) % 10.0;
            }
        }
    }

    public static void advance(){
        for (int i = 0; i < xdim; i++){
            for (int j = 0; j < ydim; j++){
                // H[i][j] = (H[i][j] * 1.01) % 1.0;
                H[i][j] = (H[i][j] + 0.1) % 1.0;
                B[i][j] = (B[i][j] + 1.0) % 10.0;
            }
        }
    }

    public static void draw(){
        for (int i = 0; i < xdim; i++){
            for (int j = 0; j < ydim; j++){
                float h = (float) H[i][j];
                float b = (float) (1.0f - B[i][j]/25);
                Color color = Color.getHSBColor(h,1.0f,b);
                double r = 0.5;
                double x = 0.5 + i;
                double y = 0.5 + j;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }

    public static void drawH(){
        for (int i = 0; i < xdim; i++){
            for (int j = 0; j < ydim; j++){
                float h = (float) H[i][j];
                Color color = Color.getHSBColor(h,1.0f,1.0f);
                double r = 0.5;
                double x = 0.5 + i;
                double y = 0.5 + j;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }

    public static void drawB(){
        for (int i = 0; i < xdim; i++){
            for (int j = 0; j < ydim; j++){
                float b = (float) B[i][j]/10;
                Color color = Color.getHSBColor(1.0f,0.0f,b);
                double r = 0.5;
                double x = 0.5 + i;
                double y = 0.5 + j;
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
            }
        }
    }

    public static void run(){
        // control when to show to save running time
        StdDraw.enableDoubleBuffering();

        int shortDelay = 20;
        int normalDelay = 100;

        while (true){
            for (int i = 0; i < 1; i++){
                advance();
            }
            if(StdDraw.isKeyPressed(KeyEvent.VK_A)){
                mode = Mode.ALL;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_B)){
                mode = Mode.BRIGHTNESS;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_H)){
                mode = Mode.HUE;
            }
            // StdDraw.clear();
            switch(mode){
                case ALL: draw(); break;
                case BRIGHTNESS: drawB(); break;
                case HUE: drawH(); break;
            }
            StdDraw.show();
            StdDraw.pause(shortDelay);
        }
    }

    /***************************************************************************
    *                           - MODE ENUMERATION -                           *
    ***************************************************************************/

    public enum Mode {
        ALL, HUE, BRIGHTNESS
    }
}
