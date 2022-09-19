import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

public class Panel extends JPanel implements KeyListener {
    private final static int rows = 21;
    private final static int columns = 9;
    private final static int blockSize = 3;  //Width and height of set of blocks. The set is currently (3 x 3).
    private final static int bombCountsAs = 1;  //How much a bomb counts as toward exploding a bomb.
    private final static int regularBlockCountsAs = 1;  //How much a regular block counts as toward exploding a bomb.
    private final static int explodeBlockAt = 3;  //How many blocks you need around a bomb before it explodes.
    private final static int previewBlockAmount = 4;
    private final static int levelUpAt = 10;
    private final static double levelUpMultiplierMultiplier = 1.2;
    private final static int maxTickChange = 50;
    private final static int explosionTickMax = 250;

    private int blockHeight; //Block's image's height in pixels.
    private int blockWidth;  //Block's image's width in pixels.
    private int leftMargin; //Margin at the left in pixels.
    private int topMargin; //Margin at the top in pixels.
    private int leftTextMargin;
    private int topTextMargin;
    private int textWidth;
    private int textHeight;
    private int leftPreviewMargin;
    private int topPreviewMargin;
    private int explosionTick;
    private boolean shouldFinishExplosion;
    private boolean exploding;
    private int maxTick; //How high a tick can go up to before the block is dropped by 1.
    private int tick;
    private double scoreMultiplier;
    private Set<Integer> set;
    private Insets insets;
    private JLabel[][] board;
    private int[][] collision;
    private Pictures tiles;
    private int level;
    private Block currentBlock;
    private int blockX;
    private int blockY;
    private boolean moved;
    private JLabel scoreLabel;
    private JLabel scoreNumberLabel;
    private JLabel levelLabel;
    private JLabel levelNumberLabel;
    private Queue<Integer> bombQueue;
    private Block[] previewBlocks;
    private JLabel[][] previewBlockDisplay;
    private int bombsExplodedThisLevel;
    private int blocksExploded;
    private int bombsExploded;
    private int chain;
    private int score;
    private JLabel gameOverLabel;
    private JLabel gameOverScoreLabel;
    private JButton gameOverButton;
    static boolean pause = false;


    public Panel() {
        initialize();
    }

    public void initializeScreenSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        blockHeight = 20;
        blockWidth = 20;

        leftMargin = ((width - blockWidth * columns) / 2);
        topMargin = ((height - blockHeight * rows) / 2);

        leftTextMargin = width / 3;
        topTextMargin = ((height - blockHeight * rows) / 2);
        textWidth = width / 20;
        textHeight = height / 50;
        leftPreviewMargin = width * 2 / 3 - width / 20;
        topPreviewMargin = ((height - blockHeight * rows) / 2);
    }

    public void initialize() {
        removeAll();
        repaint();

        initializeScreenSize();

        this.set = new HashSet<>();
        this.insets = this.getInsets();
        this.tiles = new Pictures();
        this.bombQueue = new LinkedList<>();
        this.tick = 0;
        this.maxTick = 1000;
        this.score = 0;
        this.explosionTick = 0;
        this.level = 1;
        this.scoreMultiplier = 10.0;
        this.currentBlock = null;
        this.shouldFinishExplosion = false;
        this.moved = true;
        this.exploding = false;
        this.bombsExplodedThisLevel = 0;
        this.blocksExploded = 0;
        this.chain = 1;

        this.setLayout(null);

        //Initialize the board
        this.board = new JLabel[columns][rows];
        this.collision = new int[columns][rows];

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                ImageIcon image = tiles.getBlock0();
                JLabel picLabel = new JLabel(image);
                Dimension size = new Dimension(blockWidth, blockHeight);
                picLabel.setSize(size);
                picLabel.setBounds(i * blockWidth + leftMargin + insets.left, j * blockHeight + topMargin + insets.top,
                        size.width, size.height);
                this.board[i][j] = picLabel;
                this.add(picLabel);
                this.collision[i][j] = 0;
            }
        }

        //Initialize left side texts
        levelLabel = new JLabel("Level");
        levelNumberLabel = new JLabel(Integer.toString(this.level));
        scoreLabel = new JLabel("Score");
        scoreNumberLabel = new JLabel(Integer.toString(this.score));

        levelLabel.setLocation(leftTextMargin, topTextMargin);
        levelNumberLabel.setLocation(leftTextMargin, topTextMargin + textHeight);
        scoreLabel.setLocation(leftTextMargin, topTextMargin + 3 * textHeight);
        scoreNumberLabel.setLocation(leftTextMargin, topTextMargin + 4 * textHeight);

        levelLabel.setSize(textWidth, textHeight);
        levelNumberLabel.setSize(textWidth, textHeight);
        scoreLabel.setSize(textWidth, textHeight);
        scoreNumberLabel.setSize(textWidth, textHeight);

        this.add(levelLabel);
        this.add(levelNumberLabel);
        this.add(scoreLabel);
        this.add(scoreNumberLabel);

        //Initialize right side blocks
        previewBlocks = new Block[previewBlockAmount];
        previewBlockDisplay = new JLabel[blockSize][blockSize * previewBlockAmount];
        int gap = 0;
        for (int i = 0; i < previewBlockAmount; i++) {
            previewBlocks[i] = new Block();

            for (int j = 0; j < blockSize; j++) {
                for (int k = 0; k < blockSize; k++) {
                    ImageIcon image = tiles.getBlock0();
                    JLabel picLabel = new JLabel(image);
                    Dimension size = new Dimension(blockWidth, blockHeight);
                    picLabel.setSize(size);
                    picLabel.setBounds(j * blockWidth + leftPreviewMargin + insets.left,
                            k * blockHeight + topPreviewMargin + insets.top + (int) (blockHeight * i * blockSize * 1.5),
                            size.width, size.height);
                    this.previewBlockDisplay[j][k + (i * 3)] = picLabel;
                    this.add(picLabel);
                }
            }
        }
        pause = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("key pressed: " + e.getKeyCode());
        switch (e.getKeyCode()) {
            case 37:
                if (!collisionCheck(-1, 0)) {
                    unDisplayBlock();
                    this.blockX--;
                }
                break;
            case 39:
                if (!collisionCheck(1, 0)) {
                    unDisplayBlock();
                    this.blockX++;
                }
                break;
            case 40:
                if (!collisionCheck(0, 1)) {
                    this.tick = 0;
                    unDisplayBlock();
                    this.blockY++;
                } else {
                    lockBlock();
                }
                break;
            default:
                if (!set.contains(e.getKeyCode())) {
                    set.add(e.getKeyCode());
                    switch(e.getKeyCode()) {
                        case 27: //Escape
                            break;
                        case 38: //Up Arrow
                            int movement = 0;
                            for (int i = 0; i < rows; i++) {
                                if (!collisionCheck(0, i)) {
                                    movement = i;
                                } else {
                                    break;
                                }
                            }
                            unDisplayBlock();
                            this.blockY += movement;
                            lockBlock();
                            break;
                        case 67: //C (Hold)
                            break;
                        case 88: //X (Rotate right)
                            if (this.currentBlock.canRotate()) {
                                unDisplayBlock();
                                this.currentBlock.rotateRight();
                                if (collisionCheck()) {
                                    this.currentBlock.rotateLeft();
                                }
                            }
                            break;
                        case 90: //Z (Rotate left)
                            if (this.currentBlock.canRotate()) {
                                unDisplayBlock();
                                this.currentBlock.rotateLeft();
                                if (collisionCheck()) {
                                    this.currentBlock.rotateRight();
                                }
                            }
                            break;
                        default:
                    }
            }
        }
    }
    public void runGame() {
        if (pause) {
            return;
        }
        if (!bombQueue.isEmpty() && explosionTick >= explosionTickMax) {
            for (int k = -1; k <= 1; k++) {
                for (int l = -1; l <= 1; l++) {
                    if (k == 0 && l == 0) {
                        explodeCenter(bombQueue.peek() / 100, bombQueue.peek() % 100);
                    } else {
                        explode(bombQueue.peek() / 100 + k, bombQueue.peek() % 100 + l);
                    }
                }
            }
            explosionTick = 0;
            shouldFinishExplosion = true;
            bombQueue.remove();
        } else if (!bombQueue.isEmpty()) {
            explosionTick++;
        } else if (shouldFinishExplosion) {
            if (explosionTick >= explosionTickMax) {
                shouldFinishExplosion = false;
                explosionTick = 0;
                finishExplosion();
                unDisplayEverything();
                exploding = true;
            } else {
                explosionTick++;
            }
        } else if (exploding) {
            if (explosionTick >= explosionTickMax) {
                exploding = false;
                explosionTick = 0;
                dropBlocks();
                unDisplayEverything();
                checkBombs();
                if (!bombQueue.isEmpty()) {
                    chain *= 4;
                } else {//Nothing else to explode. Add score
                    score += (bombsExploded * 0.25 + 0.75) * scoreMultiplier * chain * blocksExploded;
                    bombsExplodedThisLevel++;
                    chain = 1;
                    bombsExploded = 0;
                    blocksExploded = 0;
                }
                checkLevel();
            } else {
                explosionTick++;
            }
        } else if (this.currentBlock == null) {
            this.newBlock();
        } else if (maxTick <= tick) {
            if (collisionCheck(0, 1)) {
                lockBlock();
            } else {
                this.unDisplayBlock();
                blockY++;
            }
            tick = 0;
        } else {
            tick++;
        }
        this.updateDisplay();
    }
    @Override
    public void keyReleased(KeyEvent e) {
        set.remove(e.getKeyCode());
    }

    //Checks if selected movement will result in collision.
    //Returns true if it will result in collision.
    //Returns false if it will not result in collision.
    //int movementX = left (-1) or right (1)
    //int movementY = down (1)
    private boolean collisionCheck(int movementX, int movementY) {
        int newX = this.blockX + movementX;
        int newY = this.blockY + movementY;
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if (this.currentBlock.getShape()[i][j] != 0) {
                    if (newX + i < 0 || newY + j < 0
                            || newX + i >= columns || newY + j >= rows
                            || collision[newX + i][newY + j] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Checks if current block will result in collision.
    //Returns true if it will result in collision.
    //Returns false if it will not result in collision.
    private boolean collisionCheck() {
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if (this.currentBlock.getShape()[i][j] != 0) {
                    if (this.blockX + i < 0 || blockY + j < 0
                            || blockX + i >= columns || blockY + j >= rows
                            || collision[blockX + i][blockY + j] != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //Updates the display accordingly using collision[][];
    //Also updates the current block if it has moved.
    //Also updates the score and level.
    private void updateDisplay() {

        //Update blocks on the board.
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                ImageIcon image = null;
                if (this.collision[i][j] != 0) {
                    switch (this.collision[i][j]) {
                        case -1:
                            image = this.tiles.getBlock00();
                            break;
                        case 1:
                            image = this.tiles.getBlock1();
                            break;
                        case 2:
                            image = this.tiles.getBlock1b();
                            break;
                        case 3:
                            image = this.tiles.getBlock2();
                            break;
                        case 4:
                            image = this.tiles.getBlock2b();
                            break;
                        case 5:
                            image = this.tiles.getBlock3();
                            break;
                        case 6:
                            image = this.tiles.getBlock3b();
                            break;
                        case 7:
                            image = this.tiles.getBlock4();
                            break;
                        case 8:
                            image = this.tiles.getBlock4b();
                            break;
                        case 9:
                            image = this.tiles.getBlock5();
                            break;
                        case 10:
                            image = this.tiles.getBlock5b();
                            break;
                    }
                    board[i][j].setIcon(image);
                }
            }
        }

        //Update current block's display if it has moved.
        if (moved && currentBlock != null) {
            for (int i = 0; i < blockSize; i++) {
                for (int j = 0; j < blockSize; j++) {
                    ImageIcon image = null;
                    if (this.currentBlock.getShape()[i][j] != 0) {
                        switch (this.currentBlock.getShape()[i][j]) {
                            case -1:
                                image = this.tiles.getBlock00();
                                break;
                            case 1:
                                image = this.tiles.getBlock1();
                                break;
                            case 2:
                                image = this.tiles.getBlock1b();
                                break;
                            case 3:
                                image = this.tiles.getBlock2();
                                break;
                            case 4:
                                image = this.tiles.getBlock2b();
                                break;
                            case 5:
                                image = this.tiles.getBlock3();
                                break;
                            case 6:
                                image = this.tiles.getBlock3b();
                                break;
                            case 7:
                                image = this.tiles.getBlock4();
                                break;
                            case 8:
                                image = this.tiles.getBlock4b();
                                break;
                            case 9:
                                image = this.tiles.getBlock5();
                                break;
                            case 10:
                                image = this.tiles.getBlock5b();
                                break;
                        }
                        board[this.blockX + i][this.blockY + j].setIcon(image);
                    }
                }
            }
            this.moved = false;
        }

        //Update score.
        this.levelNumberLabel.setText(Integer.toString(this.level));
        this.scoreNumberLabel.setText(Integer.toString(this.score));
    }


    //Change preview blocks.
    private void updatePreviewDisplay() {
        ImageIcon image = this.tiles.getBlock0();
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize * previewBlockAmount; j++) {
                previewBlockDisplay[i][j].setIcon(image);
            }
        }
        for (int i = 0; i < previewBlockAmount; i++) {
            for (int j = 0; j < blockSize; j++) {
                for (int k = 0; k < blockSize; k++) {
                    image = null;
                    if (previewBlocks[i].getShape()[j][k] != 0) {
                        switch (previewBlocks[i].getShape()[j][k]) {
                            case -1:
                                image = this.tiles.getBlock00();
                                break;
                            case 1:
                                image = this.tiles.getBlock1();
                                break;
                            case 2:
                                image = this.tiles.getBlock1b();
                                break;
                            case 3:
                                image = this.tiles.getBlock2();
                                break;
                            case 4:
                                image = this.tiles.getBlock2b();
                                break;
                            case 5:
                                image = this.tiles.getBlock3();
                                break;
                            case 6:
                                image = this.tiles.getBlock3b();
                                break;
                            case 7:
                                image = this.tiles.getBlock4();
                                break;
                            case 8:
                                image = this.tiles.getBlock4b();
                                break;
                            case 9:
                                image = this.tiles.getBlock5();
                                break;
                            case 10:
                                image = this.tiles.getBlock5b();
                                break;
                        }
                        previewBlockDisplay[j][k + (i * 3)].setIcon(image);
                    }
                }
            }
        }
    }


    //Removes the current block from the display.
    private void unDisplayBlock() {
        ImageIcon image = this.tiles.getBlock0();
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if (this.currentBlock.getShape()[i][j] != 0) {
                    if (this.blockX + i >= 0 && this.blockY + j >= 0
                            && this.blockX + i < columns && this.blockY + j < rows)
                        board[this.blockX + i][this.blockY + j].setIcon(image);
                }
            }
        }
        this.moved = true;
    }

    //Remove blocks that have collision[][] value of 0 from the display.
    private void unDisplayEverything() {
        ImageIcon image = tiles.getBlock0();
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (this.collision[i][j] == 0) {
                    board[i][j].setIcon(image);
                }
            }
        }
    }

    //Locks the current block
    private void lockBlock() {
        unDisplayBlock();
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if (this.currentBlock.getShape()[i][j] != 0) {
                    collision[this.blockX + i][this.blockY + j] = this.currentBlock.getShape()[i][j];
                }
            }
        }
        this.score += currentBlock.getBlockSize();
        this.checkBombs();
        this.currentBlock = null;
    }


    //Creates a new block and sets currentBlock to the new block.
    private void newBlock() {
        this.currentBlock = previewBlocks[0];
        for (int i = 0; i < previewBlockAmount - 1; i++) {
            previewBlocks[i] = previewBlocks[i + 1];
        }
        previewBlocks[previewBlockAmount - 1] = new Block();
        this.blockX = columns / 2 - 1;
        this.blockY = 0;
        this.tick = 0;
        this.moved = true;
        if (collisionCheck()) {
            gameOver();
        }
        updatePreviewDisplay();
    }

    private void gameOver() {
        pause = true;
        removeAll();
        repaint();


        gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setLocation(leftTextMargin, topTextMargin);
        gameOverLabel.setSize(textWidth, textHeight);
        gameOverScoreLabel = new JLabel("Score: " + score);
        gameOverScoreLabel.setLocation(leftTextMargin, topTextMargin + textHeight);
        gameOverScoreLabel.setSize(textWidth, textHeight);
        add(gameOverLabel);
        add(gameOverScoreLabel);
        gameOverButton = new JButton("Play Again");
        gameOverButton.setFocusable(false);
        gameOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initialize();
            }
        });
        gameOverButton.setLocation(leftTextMargin, topTextMargin + textHeight * 2);
        gameOverButton.setSize(textWidth * 2, textHeight);
        add(gameOverButton);
    }

    //This function checks if there are bombs ready to explode.
    //All bombs that can be exploded are added to the queue.
    private void checkBombs() {
        //Look if there is a bomb.
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (collision[i][j] > 0 && collision[i][j] % 2 == 0) {//Check if a block is a bomb.
                    int count = -bombCountsAs; //count starts at -bombCountsAs to not count itself.
                    //Check all surrounding blocks.
                    for (int k = -1; k <= 1 ; k++) {
                        for (int l = -1; l <= 1; l++) {
                            count += isSameColor(i + k, j + l, collision[i][j]);  //Add to the count accordingly.
                        }
                    }
                    if (count >= explodeBlockAt) {//If count adds up, add the bomb to the queue.
                        bombQueue.add(i * 100 + j);
                        bombsExploded++;
                    }
                }
            }
        }
    }


    //Checks if the block on board (x,y) have the same color as int color.
    //If board (x,y) does not have the same color, it returns 0.
    //If board (x,y) has the same  color, it returns regularBlockCountsAs.
    //If board (x,y) has the same color and it is a bomb, it returns bombCountsAs.
    private int isSameColor(int x, int y, int color) {
        if (x < 0 || y < 0 || x >= columns || y >= rows) {//Not same color or out of bounds.
            return 0;
        }
        if (collision[x][y] == color - 1) {//Same color.
            return regularBlockCountsAs;
        }
        if (collision[x][y] == color) {//Same color and it is a bomb.
            return bombCountsAs;
        }
        return 0;
    }


    //This function removes the block at (x,y) if it is not a bomb.
    //If the block at (x,y) is a bomb, the block is not removed, and it is added to the queue to explode next.
    private void explode(int x, int y) {
        if (x >= 0 && y >= 0 && x < columns && y < rows) {//Check if the block is out of bounds.
            if (collision[x][y] > 0 && collision[x][y] % 2 == 0) {//It is a bomb.
                if (!bombQueue.contains(x * 100 + y)) {//If the bomb is already in the queue, it is not added to the queue.
                    bombQueue.add(x * 100 + y);
                    bombsExploded++;
                }
            } else {//It is not a bomb
                if (collision[x][y] > 0) {
                    blocksExploded++;
                }
                collision[x][y] = -1; //block is removed.
            }
        }
    }

    //This function removes the bomb at (x,y).
    private void explodeCenter(int x, int y) {
        blocksExploded++;
        collision[x][y] = -1;
    }

    private void finishExplosion() {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if (collision[i][j] < 0) {
                    collision[i][j] = 0;
                }
            }
        }
    }


    //Drops all blocks that are not connected to the floor.
    //Uses DFS to search through the blocks.
    //Uses hashset to store all blocks that are connected to the floor.
    private void dropBlocks() {
        HashSet<Integer> connectedToFloor = new HashSet<>();
        HashSet<Integer> checked = new HashSet<>();
        for (int i = 0; i < columns; i++) {
            if (collision[i][rows - 1] != 0 && !connectedToFloor.contains(i * 100)) {
                Queue<Integer> queue = new LinkedList<>();
                queue.add(i * 100 + rows - 1);
                while (!queue.isEmpty()) {
                    int currentBlock = queue.remove();
                    connectedToFloor.add(currentBlock);
                    if ((currentBlock / 100) - 1 >= 0 &&
                            collision[(currentBlock / 100) - 1][currentBlock % 100] != 0 &&
                            !checked.contains((((currentBlock / 100) - 1) * 100) + (currentBlock % 100))) {
                        checked.add((((currentBlock / 100) - 1) * 100) + (currentBlock % 100));
                        queue.add((((currentBlock / 100) - 1) * 100) + (currentBlock % 100));
                    }
                    if ((currentBlock / 100) + 1 < columns &&
                            collision[(currentBlock / 100) + 1][currentBlock % 100] != 0 &&
                            !checked.contains((((currentBlock / 100) + 1) * 100) + (currentBlock % 100))) {
                        checked.add((((currentBlock / 100) + 1) * 100) + (currentBlock % 100));
                        queue.add((((currentBlock / 100) + 1) * 100) + (currentBlock % 100));
                    }
                    if ((currentBlock % 100) - 1 >= 0 &&
                            collision[currentBlock / 100][(currentBlock % 100) - 1] != 0 &&
                            !checked.contains((currentBlock / 100) * 100 + ((currentBlock % 100) - 1))) {
                        checked.add((currentBlock / 100 * 100) + ((currentBlock % 100) - 1));
                        queue.add((currentBlock / 100 * 100) + ((currentBlock % 100) - 1));
                    }
                    if ((currentBlock % 100) + 1 < rows &&
                            collision[currentBlock / 100][(currentBlock % 100) + 1] != 0 &&
                            !checked.contains((currentBlock / 100) * 100 + ((currentBlock % 100) + 1))) {
                        checked.add((currentBlock / 100 * 100) + ((currentBlock % 100) + 1));
                        queue.add((currentBlock / 100 * 100) + ((currentBlock % 100) + 1));
                    }
                }
            }
        }
        ArrayList<Integer> toDrop = new ArrayList<>();
        for (int j = rows - 1; j >= 0; j--) {
            for (int i = 0; i < columns; i++) {
                if (collision[i][j] != 0 && !connectedToFloor.contains(i * 100 + j)) {
                    toDrop.add(i * 100 + j);
                }
            }
        }
        while (!toDrop.isEmpty()) {
            for (int i = 0; i < toDrop.size(); i++) {
                int x = toDrop.get(i) / 100;
                int y = toDrop.get(i) % 100;
                if (y + 1 < rows && collision[x][y + 1] == 0) {
                    collision[x][y + 1] = collision[x][y];
                    collision[x][y] = 0;
                    toDrop.set(i, x * 100 + y + 1);
                } else {
                    toDrop.remove(i);
                    i--;
                }
            }
        }
    }
    private void checkLevel() {
        if (bombsExplodedThisLevel >= levelUpAt) {
            level++;
            scoreMultiplier *= levelUpMultiplierMultiplier;
            if (maxTick - maxTickChange < 0) {
                maxTick--;
            } else {
                maxTick -= maxTickChange;
            }
            bombsExplodedThisLevel = 0;
        }
    }

}
