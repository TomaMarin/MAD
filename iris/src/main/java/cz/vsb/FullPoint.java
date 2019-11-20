package cz.vsb;

import java.util.Objects;

public class FullPoint implements Comparable<FullPoint> {
    private Double xSepalPoint;
    private Double ySepalPoint;
    private Double xPetalPoint;
    private Double yPetalPoint;
    private String classname;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public FullPoint(Double xSepalPoint, Double ySepalPoint, Double xPetalPoint, Double yPetalPoint, String classname) {
        this.xSepalPoint = xSepalPoint;
        this.ySepalPoint = ySepalPoint;
        this.xPetalPoint = xPetalPoint;
        this.yPetalPoint = yPetalPoint;
        this.classname = classname;
    }

    public FullPoint(Double xSepalPoint, Double ySepalPoint, Double xPetalPoint, Double yPetalPoint) {
        this.xSepalPoint = xSepalPoint;
        this.ySepalPoint = ySepalPoint;
        this.xPetalPoint = xPetalPoint;
        this.yPetalPoint = yPetalPoint;
    }

    public FullPoint() {
    }

    public Double getxSepalPoint() {
        return xSepalPoint;
    }

    public void setxSepalPoint(Double xSepalPoint) {
        this.xSepalPoint = xSepalPoint;
    }

    public Double getySepalPoint() {
        return ySepalPoint;
    }

    public void setySepalPoint(Double ySepalPoint) {
        this.ySepalPoint = ySepalPoint;
    }

    public Double getxPetalPoint() {
        return xPetalPoint;
    }

    public void setxPetalPoint(Double xPetalPoint) {
        this.xPetalPoint = xPetalPoint;
    }

    public Double getyPetalPoint() {
        return yPetalPoint;
    }

    public void setyPetalPoint(Double yPetalPoint) {
        this.yPetalPoint = yPetalPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullPoint fullPoint = (FullPoint) o;
        return Objects.equals(xSepalPoint, fullPoint.xSepalPoint) &&
                Objects.equals(ySepalPoint, fullPoint.ySepalPoint) &&
                Objects.equals(xPetalPoint, fullPoint.xPetalPoint) &&
                Objects.equals(yPetalPoint, fullPoint.yPetalPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xSepalPoint, ySepalPoint, xPetalPoint, yPetalPoint);
    }

    @Override
    public int compareTo(FullPoint o) {
          if(Objects.equals(xSepalPoint, o.xSepalPoint) &&
                Objects.equals(ySepalPoint, o.ySepalPoint) &&
                Objects.equals(xPetalPoint, o.xPetalPoint) &&
                Objects.equals(yPetalPoint, o.yPetalPoint)) {
              return 0;
          }
        return  1;
    }
}
