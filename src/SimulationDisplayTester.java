public class SimulationDisplayTester {

	public static void main(String[] args) {
        SimulationDisplay sd = new SimulationDisplay();
        int pix = 2;
        int width = sd.xdim * pix, height = sd.ydim * pix;
        StdDraw.setCanvasSize(width, height);  //default is 512 x 512

        //Set the drawing scale to dimentions
        StdDraw.setXscale(0, sd.xdim);
        StdDraw.setYscale(0, sd.ydim);

        // control when to show to save running time
        StdDraw.enableDoubleBuffering();

        double r = 1.0/width;
        StdDraw.setPenRadius(r);

        int shortDelay = 20;
        int normalDelay = 100;

        // for (int i = 0; i < 10000; i++){
        //     sd.advance();
        // }

        while (true){
            for (int i = 0; i < 1; i++){
                sd.advance();
            }
            // StdDraw.clear();
            sd.draw();
            StdDraw.show();
            StdDraw.pause(shortDelay);
        }
    }

}
