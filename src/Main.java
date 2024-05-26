import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        int frameHeight = 650;
        int frameWidth = 380;

        JFrame frame = new JFrame("flappy plane");
        frame.setSize(frameWidth, frameHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        flappyplane plane = new flappyplane();
        frame.add(plane);
        plane.requestFocus();
        frame.pack();
        frame.setVisible(true);
    }
}
