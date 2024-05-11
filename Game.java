import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Game {

    public static final int ROOM_WIDTH = 5;
    public static final int ROOM_LENGTH = 5;
    public static final int PLAYER_COUNT = 10;
    public static void main(String[] args) {
        Room room = new Room(ROOM_WIDTH, ROOM_LENGTH);
        Random rand = new Random();
        Ball ball = new Ball(room, rand.nextInt(ROOM_WIDTH), rand.nextInt(ROOM_LENGTH));
        ExecutorService executorService = Executors.newFixedThreadPool(PLAYER_COUNT + 1);
        String names = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        List<Thread> playerThreads = new ArrayList<>();
        for(int i = 0; i < PLAYER_COUNT; i++){
            Player player = new Player(room, String.valueOf(names.charAt(i)));
            playerThreads.add(new Thread(player, "PlayerThread" + i));
        }
        Thread ballThread = new Thread(ball);
        executorService.execute(ballThread);
        playerThreads.forEach(executorService::execute);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.print("");
        }
        while(room.getPlayerCount() > 1){
            room.draw();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.print("");
            }
        }
        executorService.shutdownNow();
        boolean found = false;
        for(int i = 0; i < room.getWidth(); i++){
            for(int j = 0; j < room.getLength(); j++){
                if(room.getObjectOnPosition(i, j) instanceof Player){
                    System.out.println(room.getObjectOnPosition(i, j).toString());
                    found = true;
                    break;
                }
            }
            if(found) break;
        }
        System.exit(0);
    }
}
