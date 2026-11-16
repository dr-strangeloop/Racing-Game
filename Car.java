import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class Car {
    private static int SPRITE_W = 16;
    private static int SPRITE_H = 28;

    private static Color body = new Color(214, 16, 16);
    private static Color detail_dark = new Color(139, 10, 10);
    private static Color detail_light = new Color(230, 108, 108);
    private static Color accent = new Color(255, 205, 0);
    private static Color aero = new Color(24, 22, 28);
    private static Color helmet = new Color(255, 220, 0);
    private static Color tire = new Color(28, 26, 30);
    private static Color shine = new Color(82, 80, 88);
    private static Color arm = new Color(60, 60, 66);
    private static Color cockpit = new Color(14, 14, 18);
    private static Color halo = new Color(96, 96, 104);
    private static Color light = new Color(255, 60, 60);
    private static int outline = 0xFF141018;
    private static double turn_speed = 3.0;

    private BufferedImage sprite;
    private double angle;
    private boolean turningLeft;
    private boolean turningRight;

    public Car() {
        sprite = new BufferedImage(SPRITE_W, SPRITE_H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sprite.createGraphics();
        paintCar(g);
        g.dispose();
        addoutline();
    }

    /** Paints the car pixel by pixel (x 1..14, y 1..26 so the outline fits around it). */
    private void paintCar(Graphics2D g) {
        // tyres with a shiny inner edge
        px(g, tire, 1, 4, 3, 5);
        px(g, tire, 12, 4, 3, 5);
        px(g, tire, 1, 19, 3, 6);
        px(g, tire, 12, 19, 3, 6);
        px(g, shine, 3, 5, 1, 3);
        px(g, shine, 12, 5, 1, 3);
        px(g, shine, 3, 20, 1, 4);
        px(g, shine, 12, 20, 1, 4);

        // suspension arms
        px(g, arm, 4, 6, 2, 1);
        px(g, arm, 10, 6, 2, 1);
        px(g, arm, 4, 21, 2, 1);
        px(g, arm, 10, 21, 2, 1);

        // front wing with yellow endplates
        px(g, aero, 1, 1, 14, 2);
        px(g, accent, 1, 1, 1, 3);
        px(g, accent, 14, 1, 1, 3);

        // nose cone
        px(g, body, 7, 3, 2, 5);
        px(g, body, 6, 7, 4, 3);
        px(g, detail_light, 7, 4, 1, 3);
        px(g, accent, 7, 3, 2, 1);

        // sidepods
        px(g, body, 3, 11, 2, 8);
        px(g, body, 11, 11, 2, 8);
        px(g, detail_dark, 3, 11, 1, 8);
        px(g, detail_dark, 12, 11, 1, 8);

        // chassis and engine cover
        px(g, body, 5, 10, 6, 8);
        px(g, detail_dark, 5, 10, 1, 8);
        px(g, body, 6, 18, 4, 5);
        px(g, body, 7, 23, 2, 1);
        px(g, detail_light, 6, 18, 1, 4);
        px(g, accent, 7, 17, 2, 6);

        // cockpit, helmet and halo
        px(g, cockpit, 6, 11, 4, 5);
        px(g, helmet, 7, 12, 2, 2);
        px(g, halo, 6, 10, 4, 1);

        // rear wing, endplates and rain light
        px(g, aero, 2, 24, 12, 2);
        px(g, accent, 2, 23, 1, 3);
        px(g, accent, 13, 23, 1, 3);
        px(g, light, 7, 26, 2, 1);
    }

    private void px(Graphics2D g, Color color, int x, int y, int w, int h) {
        g.setColor(color);
        g.fillRect(x, y, w, h);
    }

    /** Every empty pixel that touches a painted pixel becomes the outline colour. */
    private void addoutline() {
        int[] copy = new int[SPRITE_W * SPRITE_H];
        for (int y = 0; y < SPRITE_H; y++) {
            for (int x = 0; x < SPRITE_W; x++) {
                copy[y * SPRITE_W + x] = sprite.getRGB(x, y);
            }
        }
        for (int y = 0; y < SPRITE_H; y++) {
            for (int x = 0; x < SPRITE_W; x++) {
                if (isSolid(copy, x, y)) {
                    continue;
                }
                if (isSolid(copy, x - 1, y) || isSolid(copy, x + 1, y)
                        || isSolid(copy, x, y - 1) || isSolid(copy, x, y + 1)) {
                    sprite.setRGB(x, y, outline);
                }
            }
        }
    }

    private boolean isSolid(int[] pixels, int x, int y) {
        if (x < 0 || y < 0 || x >= SPRITE_W || y >= SPRITE_H) {
            return false;
        }
        return (pixels[y * SPRITE_W + x] >>> 24) >= 128;
    }

    public void setTurningLeft(boolean turning) {
        turningLeft = turning;
    }

    public void setTurningRight(boolean turning) {
        turningRight = turning;
    }

    /** Turns the car while an arrow key is held. dt = seconds since the last update. */
    public void update(double dt) {
        if (turningLeft) {
            angle -= turn_speed * dt;
        }
        if (turningRight) {
            angle += turn_speed * dt;
        }
    }

    /** Draws the car centred on (centerX, centerY), rotated by its angle, each sprite pixel scaled up. */
    public void draw(Graphics2D g, int centerX, int centerY, int scale) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int w = SPRITE_W * scale;
        int h = SPRITE_H * scale;
        AffineTransform saved = g.getTransform();
        g.translate(centerX, centerY);   // move the origin to the car's centre
        g.rotate(angle);                 // spin around that centre
        g.drawImage(sprite, -w / 2, -h / 2, w, h, null);
        g.setTransform(saved);           // undo the move and spin for anything drawn after
    }
}