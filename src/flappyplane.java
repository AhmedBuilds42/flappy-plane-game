import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;

public class flappyplane extends JPanel implements ActionListener, KeyListener {
    int frameHeight = 650;
    int frameWidth = 380;

    Image background;
    Image plane;
    Image topT;
    Image bottomT;

    int planeX = frameWidth / 8;
    int planeY = frameWidth / 2;
    int planeWidth = 100;
    int planeHeight = 80;

    int TX = frameWidth - 20;
    int TY = -20;
    int T_width = 110;
    int T_height = 340;

    class tower {
        int x = TX;
        int y = TY;
        int width = T_width;
        int height = T_height;
        Image img;
        boolean passed = false;

        tower(Image img) {
            this.img = img;
        }
    }

    Timer gameloop;
    boolean gameover = false;

    class plane {
        int x = planeX;
        int y = planeY;
        int width = planeWidth;
        int height = planeHeight;
        Image img;

        plane(Image img) {
            this.img = img;
        }
    }

    plane airplane;

    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<tower> towers;

    Random random = new Random();

    Timer placetowertimer;
    double score = 0;

    flappyplane() {
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        addKeyListener(this);

        background = loadImage("background.png");
        plane = loadImage("airplane.png");
        topT = loadImage("top building.png");
        bottomT = loadImage("bottom building.png");

        towers = new ArrayList<>();
        placetowertimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placetower();
            }
        });
        placetowertimer.start();

        gameloop = new Timer(1000 / 60, this);
        gameloop.start();

        airplane = new plane(plane);
    }

    private Image loadImage(String path) {
        Image image = new ImageIcon(path).getImage();
        if (image.getWidth(null) == -1 || image.getHeight(null) == -1) {
            System.out.println("Failed to load image: " + path);
            return null;
        }
        return image;
    }

    public void placetower() {
        int randomTowerY = (int) (TY - T_height / 4 - Math.random() * (T_height) / 2 + 50);
        int opening = frameHeight / 4;
        tower top = new tower(topT);
        towers.add(top);
        top.y = randomTowerY;

        tower bottom = new tower(bottomT);
        bottom.y = top.y + T_height + opening;
        towers.add(bottom);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void move() {
        velocityY += gravity;
        airplane.y += velocityY;
        airplane.y = Math.max(airplane.y, 0);

        for (int i = 0; i < towers.size(); i++) {
            tower t = towers.get(i);
            t.x += velocityX;
            if (collision(airplane, t)) {
                gameover = true;
                PlayAudio("planeCrash.wav");
            }
            if (!t.passed && airplane.x-10 > t.x + t.width){
                t.passed=true;
                score+=0.5;
            }
        }
        if (airplane.y > frameHeight) {
            gameover = true;
        }

    }

    public boolean collision(plane a, tower b) {
        return a.x < b.x + b.width-35 &&
                a.x + a.width-20 > b.x &&
                a.y < b.y + b.height-35 &&
                a.y + a.height-20 > b.y;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameover){
                airplane.y= planeY;
                velocityY=0;
                towers.clear();
                score=0;
                gameover=false;
                gameloop.start();
                placetowertimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public void draw(Graphics g) {
        g.drawImage(background, 0, 0, frameWidth, frameHeight, null);
        g.drawImage(plane, airplane.x, airplane.y, airplane.width, airplane.height, null);
        for (int i = 0; i < towers.size(); i++) {
            tower t = towers.get(i);
            g.drawImage(t.img, t.x, t.y, t.width, t.height, null);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if (gameover){

            g.drawString("game over:"+ String.valueOf((int) score),10,35);

        }
        else {
            g.drawString(String.valueOf((int)score),10,35);
        }
    }

    private void PlayAudio(String audioFilePath) {
        new Thread(() -> {
            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(audioFilePath))) {
                Clip audioClip = AudioSystem.getClip();
                audioClip.open(audioStream);
                audioClip.start();
                Thread.sleep(audioClip.getMicrosecondLength() / 1000);
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameover) {
            placetowertimer.stop();
            gameloop.stop();
        }
    }


}
