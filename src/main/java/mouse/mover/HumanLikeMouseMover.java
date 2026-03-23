package mouse.mover;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class HumanLikeMouseMover {

    private static final int CHECK_INTERVAL_MS = 1000;
    private static final int USER_IDLE_THRESHOLD_MS = 60_000;

    private Point lastMousePosition;
    private long lastUserMoveTime;

    private final Robot robot;

    public HumanLikeMouseMover() throws AWTException {
        this.robot = new Robot();
        this.lastMousePosition = getMousePosition();
        this.lastUserMoveTime = System.currentTimeMillis();
    }

    public void start() throws InterruptedException {
        while (true) {
            Point current = getMousePosition();

            if (isUserActive(current)) {
                handleUserMovement(current);
            } else if (isUserIdle()) {
                performAutoMovement();
            }

            Thread.sleep(CHECK_INTERVAL_MS);
        }
    }

    private boolean isUserActive(Point current) {
        return current != null && !current.equals(lastMousePosition);
    }

    private void handleUserMovement(Point current) {
        lastUserMoveTime = System.currentTimeMillis();
        lastMousePosition = current;
        log("INFO", "User moved mouse: " + current);
    }

    private boolean isUserIdle() {
        long idleTime = System.currentTimeMillis() - lastUserMoveTime;
        return idleTime >= USER_IDLE_THRESHOLD_MS;
    }

    private void performAutoMovement() throws InterruptedException {
        Point newPosition = moveMouseSmoothly();
        if (newPosition != null) {
            lastMousePosition = newPosition;
        }
    }

    private Point moveMouseSmoothly() throws InterruptedException {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Point start = getMousePosition();

        if (start == null) return null;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Point target = new Point(random.nextInt(screen.width), random.nextInt(screen.height));
        int steps = random.nextInt(20, 50);

        double dx = (target.x - start.x) / (double) steps;
        double dy = (target.y - start.y) / (double) steps;

        log("INFO", "Moving mouse from " + start + " to " + target + " in " + steps + " steps");

        for (int i = 1; i <= steps; i++) {
            int x = (int) (start.x + dx * i + random.nextGaussian() * 1.5);
            int y = (int) (start.y + dy * i + random.nextGaussian() * 1.5);

            robot.mouseMove(x, y);
            Thread.sleep(random.nextInt(10, 30));
        }

        Thread.sleep(random.nextInt(1000, 4000));
        Point finalPos = getMousePosition();
        log("INFO", "Mouse movement finished at " + finalPos);
        return finalPos;
    }

    private Point getMousePosition() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        return pointerInfo != null ? pointerInfo.getLocation() : null;
    }

    private void log(String level, String message) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + time + "] [" + level + "] " + message);
    }

    public static void main(String[] args) throws Exception {
        HumanLikeMouseMover mover = new HumanLikeMouseMover();
        mover.start();
    }
}