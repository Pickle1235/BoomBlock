import java.util.Random;

public class Block {
    private final static int numberOfColors = 4;
    private final static int numberOfBlockTypes = 21;
    private final static int probabilityOne = 400;
    private final static int probabilityTwo = 500;
    private final static int probabilityThree = 550;
    private final static int probabilityFour = 700;
    private final static int probabilityFive = 850;
    private boolean rotation;
    private int blockSize;
    private boolean square;
    int[][] shape;
    public Block() {
        Random random = new Random();
        this.rotation = true;
        this.square = false;
        switch(random.nextInt(numberOfBlockTypes)) {
            // {upper-left , left  , lower-left }
            // {up         , middle, down       }
            // {upper-right, right , lower-right}

            //monomino
            case 0:
                shape = new int[][] {
                        {0, 0, 0},
                        {1, 0, 0},
                        {0, 0, 0}
                    };
                rotation = false;
                blockSize = 1;
                break;
            //domino
            case 1:
                shape = new int[][] {
                        {0, 0, 0},
                        {1, 1, 0},
                        {0, 0, 0}
                };
                blockSize = 2;
                break;
            //trominos
            case 2:
                shape = new int[][] {
                        {0, 1, 0},
                        {1, 1, 0},
                        {0, 0, 0}
                };
                blockSize = 3;
                break;
            case 3:
                shape = new int[][] {
                        {0, 0, 0},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                blockSize = 3;
                break;
            //tetrominos
            case 4:
                shape = new int[][] {
                        {0, 1, 0},
                        {1, 1, 0},
                        {0, 1, 0}
                };
                blockSize = 4;
                break;
            case 5:
                shape = new int[][] {
                        {1, 1, 0},
                        {1, 1, 0},
                        {0, 0, 0}

                };
                square = true;
                blockSize = 4;
                break;
            case 6:
                shape = new int[][] {
                        {0, 0, 1},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                blockSize = 4;
                break;
            case 7:
                shape = new int[][] {
                        {0, 0, 0},
                        {1, 1, 1},
                        {0, 0, 1}

                };
                blockSize = 4;
                break;
            case 8:
                shape = new int[][] {
                        {0, 1, 1},
                        {1, 1, 0},
                        {0, 0, 0}
                };
                blockSize = 4;
                break;
            case 9:
                shape = new int[][] {
                        {1, 1, 0},
                        {0, 1, 1},
                        {0, 0, 0}
                };
                blockSize = 4;
                break;
            //pentominos
            case 10:
                shape = new int[][] {
                        {0, 1, 0},
                        {1, 1, 1},
                        {0, 1, 0}
                };
                blockSize = 5;
                break;
            case 11:
                shape = new int[][] {
                        {1, 1, 1},
                        {0, 1, 1},
                        {0, 0, 0}
                };
                blockSize = 5;
                break;
            case 12:
                shape = new int[][] {
                        {0, 1, 1},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                blockSize = 5;
                break;
            case 13:
                shape = new int[][] {
                        {0, 0, 1},
                        {1, 1, 1},
                        {0, 1, 0}
                };
                blockSize = 5;
                break;
            case 14:
                shape = new int[][] {
                        {0, 1, 0},
                        {1, 1, 1},
                        {0, 0, 1}
                };
                blockSize = 5;
                break;
            case 15:
                shape = new int[][] {
                        {0, 0, 1},
                        {1, 1, 1},
                        {0, 0, 1}
                };
                blockSize = 5;
                break;
            case 16:
                shape = new int[][] {
                        {1, 0, 0},
                        {1, 1, 0},
                        {0, 1, 1}
                };
                blockSize = 5;
                break;
            case 17:
                shape = new int[][] {
                        {1, 0, 0},
                        {1, 1, 1},
                        {0, 0, 1}
                };
                blockSize = 5;
                break;
            case 18:
                shape = new int[][] {
                        {0, 0, 1},
                        {1, 1, 1},
                        {1, 0, 0}
                };
                blockSize = 5;
                break;
            case 19:
                shape = new int[][] {
                        {1, 1, 1},
                        {1, 0, 0},
                        {1, 0, 0}
                };
                blockSize = 5;
                break;
            case 20:
                shape = new int[][] {
                        {1, 1, 1},
                        {1, 0, 1},
                        {0, 0, 0}

                };
                blockSize = 5;
        }

        int bombLocation = -1;
        int probability = 0;
        switch (blockSize) {
            case 1:
                probability = probabilityOne;
                break;
            case 2:
                probability = probabilityTwo;
                break;
            case 3:
                probability = probabilityThree;
                break;
            case 4:
                probability = probabilityFour;
                break;
            case 5:
                probability = probabilityFive;
        }
        if (random.nextInt(1000) < probability) {
            bombLocation = random.nextInt(blockSize);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (shape[i][j] == 1) {
                    if (bombLocation == 0) {
                        shape[i][j] = (random.nextInt(numberOfColors) + 1) * 2;
                    } else {
                        shape[i][j] = random.nextInt(numberOfColors) * 2 + 1;
                    }
                    bombLocation--;
                }
            }
        }
    }

    public int[][] getShape() {
        return shape;
    }

    public boolean canRotate() {
        return this.rotation;
    }

    public void rotateRight() {
        if (this.square) {
            int temp = shape[0][0];
            shape[0][0] = shape[0][1];
            shape[0][1] = shape[1][1];
            shape[1][1] = shape[1][0];
            shape[1][0] = temp;
        } else {
            for (int i = 0; i < 2; i++) {
                int temp = shape[i][0];
                shape[i][0] = shape[0][2 - i];
                shape[0][2 - i] = shape[2 - i][2];
                shape[2 - i][2] = shape[2][i];
                shape[2][i] = temp;
            }
        }
    }

    public void rotateLeft() {
        if (this.square) {
            int temp = shape[0][0];
            shape[0][0] = shape[1][0];
            shape[1][0] = shape[1][1];
            shape[1][1] = shape[0][1];
            shape[0][1] = temp;
        } else {
            for (int i = 0; i < 2; i++) {
                int temp = shape[0][i];
                shape[0][i] = shape[2 - i][0];
                shape[2 - i][0] = shape[2][2 - i];
                shape[2][2 - i] = shape[i][2];
                shape[i][2] = temp;
            }
        }
    }
    public int getBlockSize() {
        return blockSize;
    }
}
