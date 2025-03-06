import java.awt.*;
import java.awt.event.KeyEvent;

public class PreventLockAndRunForever {
    @SuppressWarnings("BusyWait")
    public static void main(String[] args) {
        try {
            Robot robot = new Robot();
            System.out.println("Program is running. Press Ctrl+☭ to stop.");

            // Handle Ctrl+C to exit cleanly
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nStopping program... Exiting gracefully.");
            }));

            while (true) {
                Point location = MouseInfo.getPointerInfo().getLocation();

                // Move the mouse in a large movement
                robot.mouseMove(location.x + 100, location.y + 100);
                Thread.sleep(1000); // Pause to make movement visible

                robot.mouseMove(location.x, location.y);
                Thread.sleep(1000);

                // Simulate pressing Shift key
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyRelease(KeyEvent.VK_SHIFT);

                //System.out.println("Mouse moved and key pressed.");

                Thread.sleep(5000); // Wait 5 seconds before repeating
            }
        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
