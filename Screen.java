
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Screen extends JPanel implements KeyListener {

    // Instance variables
    private Car car;
    private Obstacle rock;
    private Color green;
    private Color gray;

    // Keyboard controls
    private boolean up, down, left, right;

    // Road movement
    private double roadOffset;
    private double roadSpeed;
    private double maxSpeed;

    public Screen() {
        setFocusable(true);
        setLayout(null);

        car = new Car(376, 450);
        rock = new Obstacle(200, 100);

        green = new Color(63, 155, 11);
        gray = new Color(112, 110, 106);

        roadOffset = 0;
        roadSpeed = 4;
        maxSpeed = 15;

        addKeyListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBackground(g);

        rock.draw(g);
        car.draw(g);
    }

    // Draw the background
    private void drawBackground(Graphics g) {

        // Grass
        g.setColor(green);
        g.fillRect(0, 0, 800, 600);

        // Road
        g.setColor(gray);
        g.fillRect(200, 0, 400, 600);

        // Road boundaries
        g.setColor(Color.WHITE);
        g.fillRect(200, 0, 5, 600);
        g.fillRect(595, 0, 5, 600);

        // Moving dashed lane lines
        for (int i = -60; i < 600; i += 60) {
            g.fillRect(398, i + (int) roadOffset, 4, 30);
        }

        // Speed display
        g.setColor(Color.WHITE);
        g.drawString("Speed: " + (int)(roadSpeed * 20)
                     + " km/h", 20, 30);
    }

    // Animation loop
    public void animate() {

        Timer timer = new Timer(16, e -> {

            // Steering
            if (left) {
                car.moveLeft();
            }

            if (right) {
                car.moveRight();
            }

            // Accelerate
            if (up) {
                roadSpeed += 0.15;
            }

            // Brake
            else if (down) {
                roadSpeed -= 0.3;
            }

            // Gradually slow down
            else {
                roadSpeed -= 0.03;
            }

            // Maximum speed
            if (roadSpeed > maxSpeed) {
                roadSpeed = maxSpeed;
            }

            // Minimum speed
            if (roadSpeed < 0) {
                roadSpeed = 0;
            }

            // Move lane markings downward
            roadOffset += roadSpeed;

            // Reset the lane markings every 60 pixels
            if (roadOffset >= 60) {
                roadOffset -= 60;
            }

            repaint();
        });

        timer.start();
    }

    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == 38) {
            up = true;
        }
        else if (e.getKeyCode() == 40) {
            down = true;
        }
        else if (e.getKeyCode() == 37) {
            left = true;
        }
        else if (e.getKeyCode() == 39) {
            right = true;
        }
    }

    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == 38) {
            up = false;
        }
        else if (e.getKeyCode() == 40) {
            down = false;
        }
        else if (e.getKeyCode() == 37) {
            left = false;
        }
        else if (e.getKeyCode() == 39) {
            right = false;
        }
    }

    public void keyTyped(KeyEvent e) {}
}
