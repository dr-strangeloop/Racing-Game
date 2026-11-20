
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Screen extends JPanel implements KeyListener {

    // Instance variables
    private Car car;
    private Traffic traffic;

    private Color green;
    private Color gray;

    // Keyboard controls
    private boolean up, down, left, right;

    // Road movement
    private double roadOffset;
    private double roadSpeed;
    private double maxSpeed;

    // Game over
    private boolean gameOver;

    public Screen() {
        setFocusable(true);
        setLayout(null);

        // Create player and traffic
        car = new Car(376, 400);
        traffic = new Traffic();

        green = new Color(63, 155, 11);
        gray = new Color(112, 110, 106);

        roadOffset = 0;
        roadSpeed = 7;
        maxSpeed = 15;

        gameOver = false;

        addKeyListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        drawBackground(g);

        // Draw opponent cars
        traffic.draw(g, car);

        // Draw player
        car.draw(g);

        // Draw statistics
        drawStatistics(g);

        // Draw game over screen
        if (gameOver) {
            drawGameOver(g);
        }
    }

    // Draw scrolling background
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

        // Moving markings for five lanes
        for (int i = -60; i < 600; i += 60) {
            for (int lane = 1; lane < 5; lane++) {
                g.fillRect(200 + lane * 80, i + (int)roadOffset, 4, 30);
            }
        }
    }

    // Display game statistics
    private void drawStatistics(Graphics g) {

        g.setColor(Color.WHITE);

        g.drawString("APEX RACER", 20, 20);

        g.drawString("Speed: " + (int)(roadSpeed * 20)
                + " km/h", 20, 45);

        g.drawString("Score: " + traffic.getScore(), 20, 65);

        g.drawString("Collisions: " + traffic.getCollisions()
                + "/5", 20, 85);
        g.drawString("Rage cars crashed: " + traffic.getRageCrashes(), 20, 105);
        g.drawString("Block black cars: +500", 20, 125);

        int warningLane = traffic.getRageWarningLane(car);
        if (warningLane > 0) {
            g.setColor(Color.RED);
            g.drawString("RAGE BEHIND: LANE " + warningLane, 20, 150);
        }
    }

    // Draw game over screen
    private void drawGameOver(Graphics g) {

        // Dark background
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, 800, 600);

        // Game over title
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("GAME OVER", 195, 210);

        // Final statistics
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        g.drawString("Final Score: " + traffic.getScore(),
                290, 290);

        g.drawString("Collisions: " + traffic.getCollisions()
                + "/5", 290, 330);
        g.drawString("Rage cars crashed: " + traffic.getRageCrashes(),
                290, 365);

        // Restart instructions
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Press ENTER to Restart", 270, 410);
    }

    // Restart the game
    public void restart() {

        // Reset player and opponents
        car = new Car(376, 400);
        traffic = new Traffic();

        // Reset speed
        roadSpeed = 7;
        roadOffset = 0;

        // Reset controls
        up = false;
        down = false;
        left = false;
        right = false;

        // Resume game
        gameOver = false;

        repaint();
    }

    // Animation loop
    public void animate() {

        Timer timer = new Timer(16, e -> {

            // Stop updating when game is over
            if (gameOver) {
                repaint();
                return;
            }

            // Move left if space is clear
            if (left) {

                int newX = car.getX() - car.getSpeed();

                if (traffic.canMoveTo(car, newX)) {
                    car.moveLeft();
                }
            }

            // Move right if space is clear
            if (right) {

                int newX = car.getX() + car.getSpeed();

                if (traffic.canMoveTo(car, newX)) {
                    car.moveRight();
                }
            }

            // Accelerate
            if (up) {
                roadSpeed += 0.15;
            }

            // Brake
            else if (down) {
                roadSpeed -= 0.3;
            }

            // Gradually decelerate
            else {
                roadSpeed -= 0.03;
            }

            // Limit speed
            if (roadSpeed > maxSpeed) {
                roadSpeed = maxSpeed;
            }

            if (roadSpeed < 0) {
                roadSpeed = 0;
            }

            // Scroll road
            roadOffset += roadSpeed;

            if (roadOffset >= 60) {
                roadOffset -= 60;
            }

            // Update traffic
            roadSpeed = traffic.update(roadSpeed, car);

            // End game after 5 collisions
            if (traffic.getCollisions() >= 5) {
                gameOver = true;
            }

            repaint();
        });

        timer.start();
    }

    // Detect key presses
    public void keyPressed(KeyEvent e) {

        // Restart game when ENTER is pressed
        if (gameOver) {

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                restart();
            }

            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = true;
        }

        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = true;
        }

        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = true;
        }

        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = true;
        }
    }

    // Detect key releases
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = false;
        }

        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = false;
        }

        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = false;
        }

        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = false;
        }
    }

    // Required KeyListener method
    public void keyTyped(KeyEvent e) {}
}
