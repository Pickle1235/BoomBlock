import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


//This class loads image files to be used for the game.
public class Pictures {

    private ImageIcon block0;
    private ImageIcon block00;
    private ImageIcon block1;
    private ImageIcon block1b;
    private ImageIcon block2;
    private ImageIcon block2b;
    private ImageIcon block3;
    private ImageIcon block3b;
    private ImageIcon block4;
    private ImageIcon block4b;
    private ImageIcon block5;
    private ImageIcon block5b;
    private Image icon;
    public Pictures() {
        try {
            this.block0 = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\0.png")));
            this.block00 = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\00.png")));
            this.block1 = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\1.png")));
            this.block1b = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\1b.png")));
            this.block2 = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\2.png")));
            this.block2b = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\2b.png")));
            this.block3 = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\3.png")));
            this.block3b = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\3b.png")));
            this.block4 = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\4.png")));
            this.block4b = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\4b.png")));
            this.block5 = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\5.png")));
            this.block5b = new ImageIcon(ImageIO.read(new File(".\\src\\Image\\5b.png")));
            this.icon = ImageIO.read(new File(".\\src\\Image\\2b.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageIcon getBlock0() {
        return block0;
    }

    public ImageIcon getBlock00() {
        return block00;
    }

    public ImageIcon getBlock1() {
        return block1;
    }

    public ImageIcon getBlock1b() {
        return block1b;
    }

    public ImageIcon getBlock2() {
        return block2;
    }

    public ImageIcon getBlock2b() {
        return block2b;
    }

    public ImageIcon getBlock3() {
        return block3;
    }

    public ImageIcon getBlock3b() {
        return block3b;
    }

    public ImageIcon getBlock4() {
        return block4;
    }

    public ImageIcon getBlock4b() {
        return block4b;
    }

    public ImageIcon getBlock5() {
        return block5;
    }

    public ImageIcon getBlock5b() {
        return block5b;
    }

    public Image getIcon() {
        return icon;
    }
}
