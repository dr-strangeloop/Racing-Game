import java.awt.Color;
import java.awt.Graphics2D;
public class Road {
    private static Color grass = new Color(84, 156, 62);
    private static Color grass_dark = new Color(70, 138, 52);
    private static Color asphalt = new Color(80, 78, 88);
    private static Color line = new Color(240, 236, 226);
    private static Color kerb_red = new Color(222, 40, 40);
    private static Color kerb_white = new Color(246, 242, 234);
    private static Color outline = new Color(20, 16, 24);

    private static int road_width = 300;   
    private static int kerb_width = 14;
    private static int block = 40;         
    private static int dash_length = 40;
    private static int dash_gap = 40;
    private static int lanes = 3;

    private int screenWidth;
    private int screenHeight;
    private double scroll;    
    private double speed;     

    public Road(int screenWidth, int screenHeight, double speed) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.speed = speed;
        scroll = 0;
    }

    public void update(double dt) {
        scroll += speed * dt;
        int pattern = block * 2;              
        if (scroll >= pattern) {
            scroll -= pattern;                
        }
    }

    public void setSpeed(double newSpeed) {
        speed = newSpeed;
    }

   public void draw(Graphics2D g) {
        int left = (screenWidth - road_width) / 2;
        int right = left + road_width;
        int offset = (int) scroll;

        // grass with stripes on both sides
        g.setColor(grass);
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setColor(grass_dark);
        for (int y = -block * 2 + offset; y < screenHeight; y += block * 2) {
            g.fillRect(0, y, screenWidth, block);
        }

        // black outline around the road and kerbs
        g.setColor(outline);
        g.fillRect(left - kerb_width - 4, 0, road_width + kerb_width * 2 + 8, screenHeight);

        // red and white kerbs
        int count = 0;
        for (int y = -block * 2 + offset; y < screenHeight; y += block) {
            if (count % 2 == 0) {
                g.setColor(kerb_red);
            } else {
                g.setColor(kerb_white);
            }
            g.fillRect(left - kerb_width, y, kerb_width, block);
            g.fillRect(right, y, kerb_width, block);
            count++;
        }

        // asphalt
        g.setColor(asphalt);
        g.fillRect(left, 0, road_width, screenHeight);

        // solid white edge lines
        g.setColor(line);
        g.fillRect(left + 6, 0, 4, screenHeight);
        g.fillRect(right - 10, 0, 4, screenHeight);

        // dashed lane lines
        int laneWidth = road_width / lanes;
        for (int lane = 1; lane < lanes; lane++) {
            int x = left + lane * laneWidth - 2;
            for (int y = -(dash_length + dash_gap) + offset % (dash_length + dash_gap);
                    y < screenHeight; y += dash_length + dash_gap) {
                g.fillRect(x, y, 4, dash_length);
            }
        }
    }
}