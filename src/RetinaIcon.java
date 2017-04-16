// import java.awt.BasicStroke;
// import java.awt.Color;
// import java.awt.FileDialog;
// import java.awt.Font;
// import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
// import java.awt.MediaTracker;
// import java.awt.RenderingHints;
// import java.awt.Toolkit;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics;
import java.awt.Component;

// import java.awt.geom.Arc2D;
// import java.awt.geom.Ellipse2D;
// import java.awt.geom.GeneralPath;
// import java.awt.geom.Line2D;
// import java.awt.geom.Rectangle2D;
// import java.awt.geom.AffineTransform;

// import java.awt.image.BufferedImage;
// import java.awt.image.DirectColorModel;
// import java.awt.image.WritableRaster;

// import java.io.File;
// import java.io.IOException;
//
// import java.net.MalformedURLException;
// import java.net.URL;

// import java.util.LinkedList;
// import java.util.TreeSet;
// import java.util.NoSuchElementException;
// import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JMenu;
// import javax.swing.JMenuBar;
// import javax.swing.JMenuItem;
// import javax.swing.KeyStroke;

import java.lang.reflect.Field;

public class RetinaIcon extends ImageIcon {

    RetinaIcon(Image image){
        super(image);
    }

    public static boolean isRetina() {
        boolean isRetina = false;
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        try {
            Field field = graphicsDevice.getClass().getDeclaredField("scale");
            if (field != null) {
                field.setAccessible(true);
                Object scale = field.get(graphicsDevice);
                if(scale instanceof Integer && ((Integer) scale).intValue() == 2) {
                    isRetina = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRetina;
    }

    @Override
    public int getIconWidth()
    {
        if(isRetina())
        {
            return super.getIconWidth()/2;
        }
        return super.getIconWidth();
    }

    @Override
    public int getIconHeight()
    {
        if(isRetina())
        {
            return super.getIconHeight()/2;
        }
        return super.getIconHeight();
    }

    @Override
    public synchronized void paintIcon(Component c, Graphics g, int x, int y)
    {
        ImageObserver observer = getImageObserver();

        if (observer == null)
        {
            observer = c;
        }

        Image image = getImage();
        int width = image.getWidth(observer);
        int height = image.getHeight(observer);
        final Graphics2D g2d = (Graphics2D)g.create(x, y, width, height);

        if(isRetina())
        {
            g2d.scale(0.5, 0.5);
        }
        else
        {

        }
        g2d.drawImage(image, 0, 0, observer);
        g2d.scale(1, 1);
        g2d.dispose();
    }

}
