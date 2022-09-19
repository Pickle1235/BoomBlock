import javax.swing.*;
import java.awt.*;

public class Screen extends JFrame {
    private final static double widthMultiplier = 1;
    private final static double heightMultiplier = 1;
    private Panel panel;

    public Screen() {
        Pictures pictures = new Pictures();
        panel = new Panel();
        addKeyListener(panel);
        add(panel);

        //TODO: Add different resolutions
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        setSize((int) Math.ceil((screenSize.width * widthMultiplier)), (int) Math.ceil((screenSize.height * heightMultiplier)));
        setSize(1920, 1080);
        setTitle("BoomBlock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setIconImage(pictures.getIcon());

    }
    public void run() {
        setVisible(true);
        Runner runner = new Runner();
        runner.run();
    }



    private class Runner extends Thread
    {
        private boolean pause = false;

        //TODO: Add pause
        public void pauseGame() { pause = true; }
        public void resumeGame() { pause = false; }

        public void run()
        {
            while (!pause) {
                try {
                    Thread.sleep(1, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!pause) {
                    panel.runGame();
                    panel.revalidate();
                    panel.repaint();
                }
            }
        }
    }
}
