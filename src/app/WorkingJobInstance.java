package app;

import app.model.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkingJobInstance implements Runnable, Cancellable {

    private final String jobName;
    private final String fractalId;
    private final double proportion;
    private final int width;
    private final int height;
    private final List<Point> startingPoints;
    private List<Point> drawnPoints;

    private boolean working;

    public WorkingJobInstance(String jobName, String fractalId, double proportion, int width, int height, List<Point> startingPoints) {
        this.jobName = jobName;
        this.fractalId = fractalId;
        this.proportion = proportion;
        this.width = width;
        this.height = height;
        this.startingPoints = startingPoints;
        this.drawnPoints = new ArrayList<>();
        this.working = true;
    }

    @Override
    public void run() {
        AppConfig.timestampedStandardPrint("Racuna tacke za \'" + jobName + "\'.");
        while (working) {
            drawnPoints.add(calculatePoint());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        this.working = false;
        AppConfig.timestampedStandardPrint("Zaustavljeno racunanje tacaka za posao: \'" + jobName + "\'");
    }

    public String getJobName() {
        return jobName;
    }

    public String getFractalId() {
        return fractalId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Point> getDrawnPoints() { return drawnPoints; }

    public double getProportion() {
        return proportion;
    }

    private Point generateStartPoint() {
        Random r = new Random();
        int index = r.nextInt(startingPoints.size());
        return startingPoints.get(index);
    }

    private Point generatePoint() {
        Random r = new Random();
        int x = r.nextInt(width + 1);
        int y = r.nextInt(height + 1);
        return new Point(x, y);
    }

    private Point calculatePoint() {
        if (drawnPoints.isEmpty()) {
            return generatePoint();
        }

        Point lastPoint = drawnPoints.get(drawnPoints.size() - 1);
        Point randomPoint = generateStartPoint();
        int newX = (int) (randomPoint.getCoordX() + proportion * (lastPoint.getCoordX() - randomPoint.getCoordX()));
        int newY = (int) (randomPoint.getCoordY() + proportion * (lastPoint.getCoordY() - randomPoint.getCoordY()));
        return new Point(newX, newY);
    }

    public int getComputedPointsCount() {
        return drawnPoints.size();
    }
}
