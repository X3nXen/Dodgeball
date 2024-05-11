import java.util.Random;

public class Player implements Runnable{
    private final long waitBetweenMove = 100;
    private Room room;
    private boolean active;
    private String name;
    public Player(Room room, String name){
        this.room = room;
        this.name = name;
    }

    public synchronized void run(){
        Random rand = new Random();
        int[] coords = placePlayer(this.room.getWidth(), this.room.getLength(), rand);
        int playerCoordX = coords[0];
        int playerCoordY = coords[1];
        this.active = true;
        while(active){
            try {
                Thread.sleep(waitBetweenMove);
            } catch (InterruptedException e) {
                stop();
            }
            int directionX, directionY;
            directionX = makeRandomDirection(rand);
            directionY = makeRandomDirection(rand);
            while(directionX == 0 && directionY == 0){
                directionX = makeRandomDirection(rand);
                directionY = makeRandomDirection(rand);
            }
            if(!isValidMove(playerCoordX+directionX, playerCoordY+directionY)){
                continue;
            }
            this.room.roomLock.lock();
            try{
                if(this.room.getObjectOnPosition(playerCoordX+directionX, playerCoordY+directionY) instanceof Empty){
                    this.room.moveObjectFromPosition(playerCoordX, playerCoordY, playerCoordX+directionX, playerCoordY+directionY);
                    playerCoordX += directionX;
                    playerCoordY += directionY;
                }
            } finally{
                this.room.roomLock.unlock();
            }
            throwBall(playerCoordX, playerCoordY, rand);
        }
        this.room.removeObjectFromPosition(playerCoordX, playerCoordY);
    }

    private int[] placePlayer(int width, int length, Random rand){
        int playerCoordX = rand.nextInt(this.room.getWidth()), playerCoordY = rand.nextInt(this.room.getLength());
        while(!(this.room.getObjectOnPosition(playerCoordX, playerCoordY) instanceof Empty)){
            playerCoordX = rand.nextInt(this.room.getWidth());
            playerCoordY = rand.nextInt(this.room.getLength());
        }
        synchronized (Room.class){
            room.placeObjectOnPosition(this, playerCoordX, playerCoordY);
        }
        return new int[]{playerCoordX, playerCoordY};
    }

    private int makeRandomDirection(Random rand){
        return rand.nextInt(3)-1;
    }

    private boolean isValidMove(int moveX, int moveY){
        return moveX >= 0 && moveY >= 0 && moveX < this.room.getWidth() && moveY < this.room.getLength() && this.room.getObjectOnPosition(moveX, moveY) instanceof Empty;
    }

    private void throwBall(int positionX, int positionY, Random rand){
        for (int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if((i != 0 && j != 0) || (i == 0 && j == 0)) continue;
                if(this.room.getObjectOnPosition(positionX+i, positionY+j) instanceof Ball){
                    int ballDirectionX = makeRandomDirection(rand);
                    int ballDirectionY = makeRandomDirection(rand);
                    while((ballDirectionX == 0 && ballDirectionY == 0) || (positionX+i+ballDirectionX == positionX && positionY+j+ballDirectionY == positionY)){
                        ballDirectionX = makeRandomDirection(rand);
                        ballDirectionY = makeRandomDirection(rand);
                    }
                    ((Ball)this.room.getObjectOnPosition(positionX+i, positionY+j)).throwBall(ballDirectionX, ballDirectionY);
                    return;
                }
            }
        }
    }

    public void throwOut() {
        this.active = false;
    }

    @Override
    public String toString(){
        return this.name;
    }

    public void stop(){
        Thread.currentThread().interrupt();
    }
}
