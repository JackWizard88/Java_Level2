package J3_L5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Car implements Runnable {

    private static int CARS_COUNT;
    static {
        CARS_COUNT = 0;
    }
    private final Race race;
    private final int speed;
    private final String name;
    private final CyclicBarrier cbStart;
    private final CountDownLatch cdlStart, cdlFinish;
    private final long startTime;
    private static volatile boolean winner = false;

    public String getFinishTime() {
        return finishTime;
    }

    private String finishTime;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed, CyclicBarrier cb1, CountDownLatch cdl1, CountDownLatch cdl2) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        cbStart = cb1;
        cdlStart = cdl1;
        cdlFinish = cdl2;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            cdlStart.countDown(); //декремент счетчика защелки Старта
            cbStart.await();      //барьер ожидания остальных участников
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        
        synchronized (this) {
            if (!winner) {
                winner = true;
                System.out.println(this.getName() + " >>> WINNER <<<");
            }
        }

        long raceTime = System.currentTimeMillis() - this.startTime;
        finishTime = String.format("Время заедза %s: %s мс\n", this.getName(), raceTime);
        cdlFinish.countDown();  //декремент счетчика защелки Финиша
    }
}