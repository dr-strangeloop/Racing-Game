import java.awt.Graphics;
// The following imports allow use of imported images
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Obstacle {

    //Instance Variables
    private int x, y;
    private BufferedImage rockImage;

    // Constructor
    public Obstacle(int x, int y) {
        // initialize the instance variables
        this.x = x;
        this.y = y;

        //Instantiate the image object 
        try {
            rockImage = ImageIO.read(new File("rock.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    // draw the obstacle
    public void draw(Graphics g) {
        g.drawImage(rockImage,x, y,null);
    }
    public int getX() {
        return y;
    } 
    public int getY() {
        return y;
    } 
    public int getWidth() {
        return rockImage.getWidth();
    }
    public int getHeight() {
        return rockImage.getHeight();
    }
    
}

