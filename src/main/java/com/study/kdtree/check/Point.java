package com.study.kdtree.check;

import java.util.Objects;

/**
 * @author Hash
 * @since 2021/2/1
 */
public class Point {

    public static void main(String[] args) {
        float f1 = Float.NaN;
        float f2 = 9999;
        System.out.println(f1 > f2);
        System.out.println(f1 < f2);
        System.out.println(f1 == f2);
    }

    private double lon;
    private double lat;
    private double distance;

    public Point(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public Point(double lon, double lat, double distance) {
        this.lon = lon;
        this.lat = lat;
        this.distance = distance;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Point{" +
                "lon=" + lon +
                ", lat=" + lat +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.lon, lon) == 0 &&
                Double.compare(point.lat, lat) == 0 &&
                Double.compare(point.distance, distance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lon, lat, distance);
    }
}
