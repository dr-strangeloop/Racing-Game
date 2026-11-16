import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

public class Screen extends JPanel implements KeyListener {
    private static int width = 600;
    private static int height = 600;
    private static int size = 6;   // each sprite pixel becomes a size x size square
    private static int fps = 16;   // milliseconds per frame (16 ms = about 60 frames per second)

    private Car car;
    private Road road;

    public Screen() {
        car = new Car();
        road = new Road(width, height, 300);   // 300 = scroll speed in pixels per second
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        road.draw(g2);             // draw the road first so the car is on top
        car.draw(g2, width / 2, height / 2, size);
    }

    public void animate() {
        while (true) {
            car.update(fps / 1000.0);
            road.update(fps / 1000.0);
            repaint();
            try {
                Thread.sleep(fps);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            car.setTurningLeft(true);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            car.setTurningRight(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            car.setTurningLeft(false);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            car.setTurningRight(false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}