
import java.awt.Color;
import java.awt.Graphics;

public class Car {

    // Instance variables
    private int x, y;
    private int width, height;
    private int speed;

    private Color body;
    private Color darkRed;
    private Color lightRed;
    private Color yellow;
    private Color black;
    private Color gray;
    private Color darkGray;

    public Car(int x, int y) {
        this.x = x;
        this.y = y;

        width = 48;
        height = 84;
        speed = 3;

        body = new Color(214, 16, 16);
        darkRed = new Color(139, 10, 10);
        lightRed = new Color(230, 108, 108);
        yellow = new Color(255, 205, 0);
        black = new Color(24, 22, 28);
        gray = new Color(96, 96, 104);
        darkGray = new Color(60, 60, 66);
    }

    // Draw the Formula 1 car
    public void draw(Graphics g) {

        // Front tires
        g.setColor(black);
        g.fillRect(x + 3, y + 12, 9, 15);
        g.fillRect(x + 36, y + 12, 9, 15);

        // Rear tires
        g.fillRect(x + 3, y + 57, 9, 18);
        g.fillRect(x + 36, y + 57, 9, 18);

        // Tire highlights
        g.setColor(gray);
        g.fillRect(x + 9, y + 15, 3, 9);
        g.fillRect(x + 36, y + 15, 3, 9);
        g.fillRect(x + 9, y + 60, 3, 12);
        g.fillRect(x + 36, y + 60, 3, 12);

        // Suspension arms
        g.setColor(darkGray);
        g.fillRect(x + 12, y + 18, 6, 3);
        g.fillRect(x + 30, y + 18, 6, 3);
        g.fillRect(x + 12, y + 63, 6, 3);
        g.fillRect(x + 30, y + 63, 6, 3);

        // Front wing
        g.setColor(black);
        g.fillRect(x + 3, y + 3, 42, 6);

        // Front wing endplates
        g.setColor(yellow);
        g.fillRect(x + 3, y + 3, 3, 9);
        g.fillRect(x + 42, y + 3, 3, 9);

        // Nose cone
        g.setColor(body);
        g.fillRect(x + 21, y + 9, 6, 15);
        g.fillRect(x + 18, y + 21, 12, 9);

        // Nose highlights
        g.setColor(lightRed);
        g.fillRect(x + 21, y + 12, 3, 9);

        g.setColor(yellow);
        g.fillRect(x + 21, y + 9, 6, 3);

        // Sidepods
        g.setColor(body);
        g.fillRect(x + 9, y + 33, 6, 24);
        g.fillRect(x + 33, y + 33, 6, 24);

        // Sidepod shadows
        g.setColor(darkRed);
        g.fillRect(x + 9, y + 33, 3, 24);
        g.fillRect(x + 36, y + 33, 3, 24);

        // Main chassis
        g.setColor(body);
        g.fillRect(x + 15, y + 30, 18, 24);

        // Chassis shadow
        g.setColor(darkRed);
        g.fillRect(x + 15, y + 30, 3, 24);

        // Engine cover
        g.setColor(body);
        g.fillRect(x + 18, y + 54, 12, 15);
        g.fillRect(x + 21, y + 69, 6, 3);

        // Engine cover highlight
        g.setColor(lightRed);
        g.fillRect(x + 18, y + 54, 3, 12);

        // Yellow center stripe
        g.setColor(yellow);
        g.fillRect(x + 21, y + 51, 6, 18);

        // Cockpit
        g.setColor(black);
        g.fillRect(x + 18, y + 33, 12, 15);

        // Driver helmet
        g.setColor(yellow);
        g.fillRect(x + 21, y + 36, 6, 6);

        // Halo
        g.setColor(gray);
        g.fillRect(x + 18, y + 30, 12, 3);

        // Rear wing
        g.setColor(black);
        g.fillRect(x + 6, y + 72, 36, 6);

        // Rear wing endplates
        g.setColor(yellow);
        g.fillRect(x + 6, y + 69, 3, 9);
        g.fillRect(x + 39, y + 69, 3, 9);

        // Rear light
        g.setColor(Color.RED);
        g.fillRect(x + 21, y + 78, 6, 3);
    }

    // Move the car up
    public void moveUp() {
        if (y - speed >= 0) {
            y -= speed;
        }
    }

    // Move the car down
    public void moveDown() {
        if (y + height + speed <= 600) {
            y += speed;
        }
    }

    // Move the car left
    public void moveLeft() {
        if (x - speed >= 0) {
            x -= speed;
        }
    }

    // Move the car right
    public void moveRight() {
        if (x + width + speed <= 800) {
            x += speed;
        }
    }

    // Check for collision with an obstacle
    public boolean checkCollision(Obstacle obs) {

        int oX = obs.getX();
        int oY = obs.getY();
        int oWidth = obs.getWidth();
        int oHeight = obs.getHeight();

        if ((x + width >= oX) &&
            (x <= oX + oWidth) &&
            (y + height >= oY) &&
            (y <= oY + oHeight)) {

            return true;
        }

        return false;
    }

    // Set the car's position
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Set the car's speed
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // Getter methods
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSpeed() {
        return speed;
    }
}
