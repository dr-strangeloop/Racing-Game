
import java.awt.Color;
import java.awt.Graphics;

public class Traffic {

    // Array of opponent cars
    private Car[] cars;

    // Forward speed of each opponent
    private int[] speeds;
    private double[] currentSpeed;
    private int[] targetLane;
    private int[] signalTime;
    private int[] rageWarning;
    private int frame;

    // Track collisions and respawning
    private boolean[] touching;
    private boolean[] waiting;
    private boolean[] spawnAhead;

    private int score;
    private int collisions;
    private int rageCrashes;

    public Traffic() {

        cars = new Car[8];
        speeds = new int[8];
        currentSpeed = new double[8];
        targetLane = new int[8];
        signalTime = new int[8];
        rageWarning = new int[8];
        touching = new boolean[8];
        waiting = new boolean[8];
        spawnAhead = new boolean[8];

        // Create opponent cars
        cars[0] = new Car(216, -100, Color.BLUE);
        cars[1] = new Car(376, -250, Color.YELLOW, true);
        cars[2] = new Car(536, -400, Color.GREEN);
        cars[3] = new Car(296, -550, Color.BLACK);
        cars[4] = new Car(456, -700, Color.WHITE, true);
        cars[5] = new Car(216, -850, Color.CYAN);
        cars[6] = new Car(376, -1000, Color.BLACK);
        cars[7] = new Car(536, -1150, Color.ORANGE);

        // Set each opponent's forward speed
        speeds[0] = 6;
        speeds[1] = 7;
        speeds[2] = 10;
        speeds[3] = 17;
        speeds[4] = 8;
        speeds[5] = 12;
        speeds[6] = 18;
        speeds[7] = 14;

        for (int i = 0; i < cars.length; i++) {
            targetLane[i] = -1;
            currentSpeed[i] = speeds[i];
        }

        score = 0;
        collisions = 0;
        rageCrashes = 0;
    }

    // Draw all active cars
    public void draw(Graphics g, Car player) {

        for (int i = 0; i < cars.length; i++) {

            if (!waiting[i]) {
                cars[i].draw(g);

                if (currentSpeed[i] < speeds[i] - 0.5) {
                    cars[i].drawBrakeLights(g);
                }

                // Show the chosen direction while waiting and turning
                if (targetLane[i] != -1) {
                    boolean left = 216 + targetLane[i] * 80 < cars[i].getX();
                    cars[i].drawSignal(g, left, frame % 16 < 8);
                }

                // Show where a rage car is approaching from below the screen
                if (cars[i].isRage() && cars[i].getY() > player.getY() &&
                    cars[i].getY() - player.getY() < 500 && frame % 20 < 14) {
                    int center = cars[i].getX() + cars[i].getWidth() / 2;
                    g.setColor(Color.RED);
                    g.fillPolygon(new int[] {center, center - 12, center + 12},
                            new int[] {570, 592, 592}, 3);
                }
            }
        }
    }

    // Check the destination lane before moving into it
    private boolean laneClear(int index, int newX, Car player) {
        int y = cars[index].getY();
        int gap = 40;

        for (int i = 0; i < cars.length; i++) {
            if (i != index && !waiting[i] &&
                newX < cars[i].getX() + cars[i].getWidth() &&
                newX + cars[index].getWidth() > cars[i].getX() &&
                y < cars[i].getY() + cars[i].getHeight() + gap &&
                y + cars[index].getHeight() + gap > cars[i].getY()) {
                return false;
            }
        }

        if (newX < player.getX() + player.getWidth() &&
            newX + cars[index].getWidth() > player.getX() &&
            y < player.getY() + player.getHeight() + gap &&
            y + cars[index].getHeight() + gap > player.getY()) {
            return false;
        }

        return true;
    }

    // Check whether two cars overlap horizontally
    public boolean sameLane(Car a, Car b) {

        if (a.getX() < b.getX() + b.getWidth() &&
            a.getX() + a.getWidth() > b.getX()) {

            return true;
        }

        return false;
    }

    // Check if player can move left or right
    public boolean canMoveTo(Car player, int newX) {
        int rageToCrash = -1;

        for (int i = 0; i < cars.length; i++) {

            if (!waiting[i]) {

                // Check if the new position overlaps another car
                if (newX < cars[i].getX() + cars[i].getWidth() &&
                    newX + player.getWidth() > cars[i].getX() &&
                    player.getY() < cars[i].getY() + cars[i].getHeight() &&
                    player.getY() + player.getHeight() > cars[i].getY()) {

                    if (cars[i].isRage()) {
                        rageToCrash = i;
                        continue;
                    }

                    // Count side collision only once per contact
                    if (!touching[i]) {
                        collisions++;
                        touching[i] = true;

                        System.out.println("SIDE COLLISION!");
                    }

                    // Prevent player from moving through car
                    return false;
                }
            }
        }

        if (rageToCrash != -1) {
            crashRageCar(rageToCrash);
        }

        return true;
    }

    // The player earns points for forcing a black rage car to crash
    private void crashRageCar(int index) {
        rageCrashes++;
        score += 500;
        waiting[index] = true;
        spawnAhead[index] = false;
        targetLane[index] = -1;
        touching[index] = false;
        System.out.println("RAGE CAR CRASHED!");
    }

    // Update traffic movement and collisions
    public double update(double playerSpeed, Car player) {

        double newSpeed = playerSpeed;
        frame++;

        for (int i = 0; i < cars.length; i++) {

            // Check if car is waiting to respawn
            if (waiting[i]) {
                respawn(i, spawnAhead[i]);
                continue;
            }


            // Cars signal when following traffic; black cars also chase the player
            if (targetLane[i] == -1) {
                int lane = Math.round((cars[i].getX() - 216) / 80.0f);

                if (cars[i].isRage() && cars[i].getY() > player.getY() &&
                    cars[i].getY() - player.getY() < 700 &&
                    !sameLane(cars[i], player)) {
                    int playerLane = Math.round((player.getX() - 216) / 80.0f);
                    targetLane[i] = lane + (playerLane > lane ? 1 : -1);
                }

                for (int j = 0; j < cars.length; j++) {
                    if (targetLane[i] == -1 && i != j && !waiting[j] &&
                        sameLane(cars[i], cars[j]) &&
                        cars[i].getY() > cars[j].getY() &&
                        cars[i].getY() - cars[j].getY() < 300 &&
                        currentSpeed[i] >= currentSpeed[j] - 0.5) {

                        int left = lane - 1;
                        int right = lane + 1;

                        if (left >= 0 && laneClear(i, 216 + left * 80, player)) {
                            targetLane[i] = left;
                        }
                        else if (right < 5 && laneClear(i, 216 + right * 80, player)) {
                            targetLane[i] = right;
                        }
                        else if (left >= 0) {
                            targetLane[i] = left;
                        }
                        else if (right < 5) {
                            targetLane[i] = right;
                        }

                        break;
                    }
                }

                if (targetLane[i] != -1) {
                    signalTime[i] = 20;
                }
            }

            if (targetLane[i] != -1) {
                if (signalTime[i] > 0) {
                    signalTime[i]--;
                }
                else {
                    int destination = 216 + targetLane[i] * 80;
                    int distance = Math.abs(destination - cars[i].getX());
                    int step = Math.min(4, Math.max(2, distance / 12));
                    if (destination < cars[i].getX()) {
                        step = -step;
                    }
                    int nextX = cars[i].getX() + step;

                    if (Math.abs(destination - cars[i].getX()) < Math.abs(step)) {
                        nextX = destination;
                    }

                    if (laneClear(i, destination, player)) {
                        cars[i].setPosition(nextX, cars[i].getY());

                        if (nextX == destination) {
                            targetLane[i] = -1;
                        }
                    }
                }
            }

            // Slow down behind traffic, then accelerate once the road opens
            double desiredSpeed = speeds[i];
            for (int j = 0; j < cars.length; j++) {
                if (i != j && !waiting[j] && sameLane(cars[i], cars[j]) &&
                    cars[i].getY() > cars[j].getY()) {
                    int gapAhead = cars[i].getY() -
                            (cars[j].getY() + cars[j].getHeight());
                    if (gapAhead < 220) {
                        desiredSpeed = Math.min(desiredSpeed,
                                Math.max(0, currentSpeed[j] + (gapAhead - 85) / 20.0));
                    }
                }
            }

            if (!cars[i].isRage() && sameLane(cars[i], player) &&
                cars[i].getY() > player.getY()) {
                int gapAhead = cars[i].getY() -
                        (player.getY() + player.getHeight());
                if (gapAhead < 220) {
                    desiredSpeed = Math.min(desiredSpeed,
                            Math.max(0, playerSpeed + (gapAhead - 85) / 20.0));
                }
            }

            if (currentSpeed[i] < desiredSpeed) {
                currentSpeed[i] = Math.min(desiredSpeed, currentSpeed[i] + 0.12);
            }
            else if (currentSpeed[i] > desiredSpeed) {
                currentSpeed[i] = Math.max(desiredSpeed, currentSpeed[i] - 0.35);
            }

            // Convert world speed to movement relative to the player
            int movement = (int)Math.round(playerSpeed - currentSpeed[i]);

            int currentY = cars[i].getY();
            int newY = currentY + movement;

            // Prevent traffic cars from passing through each other
            for (int j = 0; j < cars.length; j++) {

                if (i != j && !waiting[j] &&
                    sameLane(cars[i], cars[j])) {

                    int otherY = cars[j].getY();
                    int otherHeight = cars[j].getHeight();

                    // Moving downward toward another car
                    if (movement > 0 &&
                        currentY + cars[i].getHeight() <= otherY &&
                        newY + cars[i].getHeight() > otherY - 12) {

                        newY = otherY - cars[i].getHeight() - 12;
                    }

                    // Moving upward toward another car
                    if (movement < 0 &&
                        currentY >= otherY + otherHeight &&
                        newY < otherY + otherHeight + 12) {

                        newY = otherY + otherHeight + 12;
                    }
                }
            }

            // Give the player time to react to a rage car from behind
            int holdY = player.getY() + player.getHeight() + 35;
            if (cars[i].isRage() && rageWarning[i] > 0 &&
                currentY > player.getY() && newY <= holdY) {
                newY = holdY;
                currentSpeed[i] = playerSpeed;
                rageWarning[i]--;
            }

            // Prevent opponents from passing through player
            if (sameLane(cars[i], player)) {

                // Opponent approaches player from above
                if (movement > 0 &&
                    currentY + cars[i].getHeight() <= player.getY() &&
                    newY + cars[i].getHeight() > player.getY()) {

                    newY = player.getY() - cars[i].getHeight();

                    // Reduce player speed
                    if (newSpeed > currentSpeed[i]) {
                        newSpeed = currentSpeed[i];
                    }

                    if (!touching[i]) {
                        collisions++;
                        touching[i] = true;

                        System.out.println("FRONT COLLISION!");
                    }
                }

                // Opponent approaches player from below
                else if (movement < 0 &&
                         currentY >= player.getY() + player.getHeight() &&
                         newY < player.getY() + player.getHeight()) {

                    if (cars[i].isRage()) {
                        crashRageCar(i);
                        continue;
                    }

                    newY = player.getY() + player.getHeight();

                    if (!touching[i]) {
                        collisions++;
                        touching[i] = true;

                        System.out.println("REAR COLLISION!");
                    }
                }
            }

            // Update opponent position
            cars[i].setPosition(cars[i].getX(), newY);

            // Check if player is still near the opponent
            // Small gap prevents repeated collision counting
            int gap = 8;

            boolean nearby =
                player.getX() <
                    cars[i].getX() + cars[i].getWidth() + gap &&
                player.getX() + player.getWidth() + gap >
                    cars[i].getX() &&
                player.getY() <
                    cars[i].getY() + cars[i].getHeight() + gap &&
                player.getY() + player.getHeight() + gap >
                    cars[i].getY();

            // Reset collision only when cars separate
            if (!nearby) {
                touching[i] = false;
            }

            // Car leaves bottom of screen
            if (movement > 0 && cars[i].getY() > 600) {

                score += 100;

                waiting[i] = true;
                spawnAhead[i] = true;
                targetLane[i] = -1;
            }

            // Car moves too far above screen
            else if (movement < 0 && cars[i].getY() < -1200) {

                waiting[i] = true;
                spawnAhead[i] = false;
                targetLane[i] = -1;
            }
        }

        return newSpeed;
    }

    // Respawn only when the area is clear
    public void respawn(int index, boolean ahead) {

        // Choose random lane
        int lane = (int)(Math.random() * 5);

        int newX = 216 + lane * 80;
        int newY;

        if (ahead) {
            newY = -200;
        }
        else {
            newY = cars[index].isRage() ? 1000 : 650;
        }

        // Check if another car occupies spawn area
        for (int i = 0; i < cars.length; i++) {

            if (i != index && !waiting[i]) {

                if (newX < cars[i].getX() + cars[i].getWidth() &&
                    newX + cars[index].getWidth() > cars[i].getX() &&
                    Math.abs(newY - cars[i].getY()) < 150) {

                    // Wait and try again next frame
                    return;
                }
            }
        }

        // Spawn when area is clear
        cars[index].setPosition(newX, newY);
        currentSpeed[index] = speeds[index];
        rageWarning[index] = cars[index].isRage() && !ahead ? 90 : 0;

        waiting[index] = false;
        touching[index] = false;
    }

    // Getter methods
    public int getScore() {
        return score;
    }

    public int getCollisions() {
        return collisions;
    }

    public int getRageCrashes() {
        return rageCrashes;
    }

    public int getRageWarningLane(Car player) {
        for (int i = 0; i < cars.length; i++) {
            if (!waiting[i] && cars[i].isRage() &&
                cars[i].getY() > player.getY() &&
                cars[i].getY() - player.getY() < 500) {
                return Math.round((cars[i].getX() - 216) / 80.0f) + 1;
            }
        }
        return 0;
    }
}
