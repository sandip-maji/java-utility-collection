import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Scanner;

public class PreventLockAndRunForever {
    @SuppressWarnings("BusyWait")
    public static void main(String[] args) {
        try {
            Robot robot = new Robot();
            Random random = new Random();
            Scanner scanner = new Scanner(System.in);

            System.out.println("Preventing screen lock. Press 'ESC' to exit.");

            // Create a separate thread to listen for exit command
            Thread stopThread = new Thread(() -> {
                while (true) {
                    if (scanner.nextLine().equalsIgnoreCase("exit")) {
                        System.out.println("Exiting...");
                        System.exit(0);
                    }
                }
            });
            stopThread.setDaemon(true);
            stopThread.start();

            while (true) {
                // Get current mouse location
                Point location = MouseInfo.getPointerInfo().getLocation();

                // Move the mouse randomly
                int moveX = random.nextInt(50) - 25; // Move between -25 to +25 pixels
                int moveY = random.nextInt(50) - 25;
                robot.mouseMove(location.x + moveX, location.y + moveY);
                Thread.sleep(1000);

                // Press and release keys
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyRelease(KeyEvent.VK_SHIFT);

                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_CONTROL);

                robot.keyPress(KeyEvent.VK_NUM_LOCK);
                robot.keyRelease(KeyEvent.VK_NUM_LOCK);

                Thread.sleep(5000); // Pause before next action
            }
        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
