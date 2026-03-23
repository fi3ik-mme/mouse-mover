package mouse.mover;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class HumanLikeMouseMover {

    private static final Logger LOGGER = LogManager.getLogger(HumanLikeMouseMover.class);

    private final Robot robot;
    private final int checkIntervalMs;
    private final int idleThresholdMs;

    private Point lastMousePosition;
    private long lastUserMoveTime;

    public HumanLikeMouseMover(Robot robot, int checkIntervalMs, int idleThresholdMs) {
        this.robot = robot;
        this.checkIntervalMs = checkIntervalMs;
        this.idleThresholdMs = idleThresholdMs;

        this.lastMousePosition = getMousePosition();
        this.lastUserMoveTime = System.currentTimeMillis();
    }

    public void start() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            Point current = getMousePosition();

            if (isUserActive(current)) {
                handleUserMovement(current);
            } else if (isUserIdle()) {
                performAutoMovement();
            }

            Thread.sleep(checkIntervalMs);
        }
    }

    private boolean isUserActive(Point current) {
        return current != null && !current.equals(lastMousePosition);
    }

    private void handleUserMovement(Point current) {
        lastUserMoveTime = System.currentTimeMillis();
        lastMousePosition = current;
        LOGGER.info("User moved mouse: {}", current);
    }

    private boolean isUserIdle() {
        long idleTime = System.currentTimeMillis() - lastUserMoveTime;
        return idleTime >= idleThresholdMs;
    }

    private void performAutoMovement() throws InterruptedException {
        Point newPosition = moveMouseSmoothly();
        lastMousePosition = newPosition;
    }

    private Point moveMouseSmoothly() throws InterruptedException {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Point start = getMousePosition();

        if (start == null) return null;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        Point target = new Point(
                random.nextInt(screen.width),
                random.nextInt(screen.height)
        );

        int steps = random.nextInt(20, 50);

        double dx = (target.x - start.x) / (double) steps;
        double dy = (target.y - start.y) / (double) steps;

        LOGGER.info("Moving from {} to {} in {} steps", start, target, steps);

        for (int i = 1; i <= steps; i++) {
            int x = (int) (start.x + dx * i + random.nextGaussian() * 1.5);
            int y = (int) (start.y + dy * i + random.nextGaussian() * 1.5);

            robot.mouseMove(x, y);
            Thread.sleep(random.nextInt(10, 30));
        }

        Thread.sleep(random.nextInt(1000, 4000));

        Point finalPosition = getMousePosition();
        LOGGER.info("Final position: {}", finalPosition);

        return finalPosition;
    }

    private Point getMousePosition() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        return pointerInfo != null ? pointerInfo.getLocation() : null;
    }

    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();

        HumanLikeMouseMover mover = new HumanLikeMouseMover(
                robot,
                1000,      // check interval
                60_000     // idle threshold
        );

        mover.start();
    }
}