package cz.vsb;

public class Point {
    private Double xPoint;
    private Double yPoint;
    private Double euclidanNorm;


    public Point(Double xPoint, Double yPoint, Double euclidanNorm) {
        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.euclidanNorm = euclidanNorm;
    }

    public Point() {
    }

    public Point(Double xPoint, Double yPoint) {
        this.xPoint = xPoint;
        this.yPoint = yPoint;
    }

    public Double getEuclidanNorm() {
        return euclidanNorm;
    }

    public void setEuclidanNorm(Double euclidanNorm) {
        this.euclidanNorm = euclidanNorm;
    }

    public Double getxPoint() {
        return xPoint;
    }

    public void setxPoint(Double xPoint) {
        this.xPoint = xPoint;
    }

    public Double getyPoint() {
        return yPoint;
    }

    public void setyPoint(Double yPoint) {
        this.yPoint = yPoint;
    }

    @Override
    public String toString() {
        return "Point{" +
                "xPoint=" + xPoint +
                ", yPoint=" + yPoint +
                ", euclidanNorm=" + euclidanNorm +
                '}';
    }
}
