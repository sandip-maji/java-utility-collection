import java.awt.*;

public class HeartMouseMover {
    public static void main(String[] args) {
        try {
            Robot robot = new Robot();
            System.out.println("Moving mouse in a small heart shape. Press Ctrl+C to stop.");

            int centerX = 800; // Adjust as needed
            int centerY = 400;
            int scale = 10;    // Smaller heart size
            int waitTime = 3000; // Wait 3 seconds before repeating

            while (true) {
                for (double t = 0; t < Math.PI * 2; t += 0.1) { // Reduced steps for smoother motion
                    int x = (int) (centerX + scale * (16 * Math.pow(Math.sin(t), 3)));
                    int y = (int) (centerY - scale * (13 * Math.cos(t) - 5 * Math.cos(2 * t)
                            - 2 * Math.cos(3 * t) - Math.cos(4 * t)));

                    robot.mouseMove(x, y);
                    Thread.sleep(50); // Small delay for smooth movement
                }

                System.out.println("Waiting before repeating...");
                Thread.sleep(waitTime); // Wait before drawing the heart again
            }
        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
