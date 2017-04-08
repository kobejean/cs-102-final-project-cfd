import java.awt.Color;
import java.lang.Math;

public class SimulationDisplay {
    int xdim = 600;
    int ydim = 240;
    double[][] nC  = new double[xdim][ydim];

    /********************************************************************
	 * CONSTRUCTORS                                                     *
	 *******************************************************************/


    public SimulationDisplay(){
        assert xdim == nC.length;
        for (int i = 0; i < xdim; i++){
            assert ydim == nC[i].length;
            for (int j = 0; j < ydim; j++){
                int x = xdim/2 - i;
                int y = ydim/2 - j;
                // double r =  Math.sqrt(x*x + y*y) * 0.001;
                double r =  (x*x + y*y) * 0.00001;
                nC[i][j] = r % 1.0;
            }
        }
    }

    /********************************************************************
	 * METHODS                                                          *
	 *******************************************************************/

    public void advance(){
        for (int i = 0; i < nC.length; i++){
            for (int j = 0; j < nC[i].length; j++){
                nC[i][j] = (nC[i][j] * 1.01) % 1;
            }
        }
    }

    public void draw(){
        Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW,
            Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,
            Color.PINK, Color.WHITE, Color.BLACK};

        for (int i = 0; i < nC.length; i++){
            for (int j = 0; j < nC[i].length; j++){
                float c = (float) nC[i][j];
                Color color = Color.getHSBColor(c,1.0f,1.0f);

                double r = 0.5;
                double x = 0.5 + i;
                double y = 0.5 + j;


                StdDraw.setPenColor(color);
                StdDraw.filledSquare(x,y,r);
                // StdDraw.setPenColor(Color.BLACK);
                // StdDraw.square(x,y,r);

            }
        }
    }

}
