import java.awt.Color;
import java.lang.Math;
import java.awt.event.KeyEvent;

public class CFD implements Runnable {
    int xdim = 2400;
    int ydim = 960;
    // int xdim = 1200;
    // int ydim = 480;
    // int xdim = 600;
    // int ydim = 240;
    // int xdim = 400;
    // int ydim = 160;
    // int xdim = 200;
    // int ydim = 80;

    double velocity = 0.1;
    double viscocity = 0.020;

    int stepTime = 0;			// performance measure: time in ms for a single iteration of the algorithm
    int collideTime = 0;
	int streamTime = 0;
	int paintTime = 0;
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

    int time = 0;	// time in units of the fundamental step size
    int timeStepsPerFrame = 10;

    Mode mode = Mode.SPEED;

    // calculation short-cuts:
    double four9ths = 4.0 / 9;
    double one9th = 1.0 / 9;
    double one36th = 1.0 / 36;

    CFD() {
        reset();

        // int width = xdim * ppc, height = ydim * ppc;
        int width = 1200, height = 480;
        // int width = 300, height = 120;
        StdDraw.setCanvasSize(width, height);  //default is 1200 x 480

        //Set the drawing scale to dimentions
        StdDraw.setXscale(0-.5, xdim-.5);
        StdDraw.setYscale(0-.5, ydim-.5);

        double r = 1.0/width;
        StdDraw.setPenRadius(r);

        // Now start the simulation thread:
        Thread simThread = new Thread(this);
        simThread.start();

        // run();
    }
    public static void main(String[] args) {
        new CFD();
    }

    /***************************************************************************
	* METHODS                                                                 *
	**************************************************************************/

    public void reset(){
        time = 0;
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

    // Collide particles within each cell.  Adapted from Wagner's D2Q9 code.
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

    // Stream particles into neighboring cells:
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

    // Bounce particles off of barriers:
    // (The ifs are needed to prevent array index out of bounds errors. Could handle edges
    //  separately to avoid this.)
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
        // Last but not least, stream particles in from non-existent space to the right,
        // assuming the left-moving densities are the same as they are immediately to the left:
/*		for (int y=0; y<ydim; y++) {
            if (!barrier[xdim-1][y]) {
                nW[xdim-1][y] = nW[xdim-2][y];
                nNW[xdim-1][y] = nNW[xdim-2][y];
                nSW[xdim-1][y] = nSW[xdim-2][y];
                // normalize density to 1 (this seems to prevent density build-up over time):
                double dens = n0[xdim-1][y] + nE[xdim-1][y] + nW[xdim-1][y] + nN[xdim-1][y] + nS[xdim-1][y] +
                        nNE[xdim-1][y] + nNW[xdim-1][y] + nSE[xdim-1][y] + nSW[xdim-1][y];
                n0[xdim-1][y] /= dens;
                nE[xdim-1][y] /= dens;
                nW[xdim-1][y] /= dens;
                nN[xdim-1][y] /= dens;
                nS[xdim-1][y] /= dens;
                nNE[xdim-1][y] /= dens;
                nNW[xdim-1][y] /= dens;
                nSE[xdim-1][y] /= dens;
                nSW[xdim-1][y] /= dens;
            }
        } */
    }

    synchronized void advance(){
        long startTime = System.currentTimeMillis();
		//force();
		long forceTime = System.currentTimeMillis();
		collide();
		long afterCollideTime = System.currentTimeMillis();
		collideTime = (int) (afterCollideTime - forceTime);		// 23-24 ms for 600x600 grid
		stream();
		streamTime = (int) (System.currentTimeMillis() - afterCollideTime);	// 9-10 ms for 600x600 grid
		bounce();
		stepTime = (int) (System.currentTimeMillis() - startTime);	// 33-35 ms for 600x600 grid
		time++;
    }

    void draw(){
        for (int x = 0; x < xdim; x++){
            for (int y = 0; y < ydim; y++){
                Color color = Color.WHITE;;
                switch(mode){
                    case BARRIER:
                        if (barrier[x][y]) {
        					color = Color.BLACK;
        				}
                        break;
                    case SPEED:
                        float S = Math.min((float) Math.sqrt(speed2[x][y])*3.0f, 1.0f);
                        color = Color.getHSBColor(0.5f,1.0f,S);
                        break;
                    case DENSITY:
                        float D = Math.min((float) (density[x][y]-1)*0.3f, 1.0f);
                        color = Color.getHSBColor(0.75f,1.0f,D);
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

        int shortDelay = 20;
        int normalDelay = 100;

        draw();

        int screenshot_num = 0;
        while (true){

            for (int s = 0; s < timeStepsPerFrame; s++) {
                advance();
                if (time % 250 == timeStepsPerFrame + 1){
                    screenshot_num++;
                    String filepath = "screenshots/CFD-250-" + screenshot_num + ".png";
                    StdDraw.save(filepath);
                }
            }
            try {Thread.sleep(1);} catch (InterruptedException e) {}

            if(StdDraw.isKeyPressed(KeyEvent.VK_B)){
                mode = Mode.BARRIER;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_S)){
                mode = Mode.SPEED;
            }else if (StdDraw.isKeyPressed(KeyEvent.VK_D)){
                mode = Mode.DENSITY;
            }
            // StdDraw.clear();
            draw();
            StdDraw.show();
            StdDraw.pause(shortDelay);
        }
    }

    /***************************************************************************
    *                           - MODE ENUMERATION -                           *
    ***************************************************************************/

    public enum Mode {
        BARRIER, SPEED, DENSITY
    }
}
