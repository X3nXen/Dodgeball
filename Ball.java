public class Ball implements Runnable{
    private final long waitBetweenMove = 50;
    private Room room;
    private int positionX, positionY;
    private int movementDirectionX, movementDirectionY;

    public Ball(Room room, int startingPositionX, int startingPositionY){
        this.room = room;
        this.positionX = startingPositionX;
        this.positionY = startingPositionY;
        this.room.placeObjectOnPosition(this, this.positionX, this.positionY);
    }

    public void run(){
        this.movementDirectionX = 0;
        this.movementDirectionY = 0;
        while(true){
            try {
                Thread.sleep(waitBetweenMove);
            } catch (InterruptedException e) {
                stop();
            }
            if(this.room.getPlayerCount() > 1){
                if(this.isMoving() &&
                  ((this.positionX+movementDirectionX >= this.room.getWidth() || this.positionX+movementDirectionX < 0) ||
                  (this.positionY+movementDirectionY >= this.room.getLength() || this.positionY+movementDirectionY < 0))){
                    this.movementDirectionX = 0;
                    this.movementDirectionY = 0;
                }
                if(this.isMoving() &&
                   (this.room.getObjectOnPosition(this.positionX+movementDirectionX, this.positionY+movementDirectionY) instanceof Player)){
                    ((Player)this.room.getObjectOnPosition(this.positionX+movementDirectionX, this.positionY+movementDirectionY)).throwOut();
                    this.movementDirectionX = 0;
                    this.movementDirectionY = 0;
                }
            }
            if(this.isMoving()){
                this.room.roomLock.lock();
                try{
                    if(isValidThrow(this.positionX+movementDirectionX, this.positionY+movementDirectionY)) {
                        this.room.moveObjectFromPosition(this.positionX, this.positionY, this.positionX+movementDirectionX, this.positionY+movementDirectionY);
                        this.positionX += movementDirectionX;
                        this.positionY += movementDirectionY;
                    }
                }finally{
                    this.room.roomLock.unlock();
                }
            }
        }
    }

    private boolean isValidThrow(int posX, int posY){
        return posX >= 0 && posY >= 0 && posX < this.room.getWidth() && posY < this.room.getLength();
    }

    public void throwBall(int x, int y){
        this.movementDirectionX = x;
        this.movementDirectionY = y;
    }

    public boolean isMoving() {
        return this.movementDirectionX != 0 || this.movementDirectionY != 0;
    }

    @Override
    public String toString(){
        return "o";
    }

    public void stop(){
        Thread.currentThread().interrupt();
    }
}
