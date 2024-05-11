import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Room {
    private int width, length;
    private Object[][] positionMatrix;
    //public Lock[][] boardLock;
    public final Lock roomLock;
    private AtomicInteger playerCount;

    public Room(int width, int length){
        this.width = width;
        this.length = length;
        positionMatrix = new Object[this.width][this.length];
        //boardLock = new ReentrantLock[this.width][this.length];
        roomLock = new ReentrantLock();
        for(int i = 0; i < this.width; i++){
            for(int j = 0; j < this.length; j++){
                positionMatrix[i][j] = new Empty();
                //boardLock[i][j] = new ReentrantLock();
            }
        }
        this.playerCount = new AtomicInteger(0);
    }

    public void draw(){
        System.out.println("\033[H\033[2J");
        System.out.println("\u001B[0;0H");
        for(int i = 0; i < this.width+2; i++){
            for(int j = 0; j < this.length+2; j++){
                if(((i == 0 || i == this.width+1) && (j == 0 || j == this.length+1))){
                    System.out.print("+");
                }else if(i == 0 || i == this.width+1){
                    System.out.print("-");
                }else if(j == 0 || j == this.length+1){
                    System.out.print("|");
                }else{
                     System.out.print(this.positionMatrix[i-1][j-1].toString());
                }
            }
            System.out.print("\n");
        }
    }

    public Object getObjectOnPosition(int x, int y){
        if(x < 0 || y < 0 || x >= this.width || y >= this.length){
            return null;
        }
        return this.positionMatrix[x][y];
    }

    public void placeObjectOnPosition(Object obj, int x, int y){
        if(obj instanceof Player){
            this.playerCount.addAndGet(1);
        }
        this.positionMatrix[x][y] = obj;
    }

    public void moveObjectFromPosition(int fromX, int fromY, int toX, int toY){
        Object o = this.positionMatrix[fromX][fromY];
        this.positionMatrix[toX][toY] = o;
        this.positionMatrix[fromX][fromY] = new Empty();
    }

    public void removeObjectFromPosition(int x, int y){
        if(this.positionMatrix[x][y] instanceof Player){
            this.playerCount.addAndGet(1);
        }
        this.positionMatrix[x][y] = new Empty();
    }
    public int getPlayerCount() {
        return this.playerCount.get();
    }

    public int getWidth(){
        return this.width;
    }

    public int getLength(){
        return this.length;
    }
}
