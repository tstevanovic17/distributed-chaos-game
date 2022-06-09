package app.model;

public class Point {

    private static final long serialVersionUID = 812312312312354L;

    private final int coordX;
    private final int coordY;

    public Point(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

}
