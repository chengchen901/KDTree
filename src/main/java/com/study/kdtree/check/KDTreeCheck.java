package com.study.kdtree.check;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Hash
 * @since 2021/2/1
 */
public class KDTreeCheck {

    /**
     * 随机生成指定数量点
     *
     * @author Hash
     * @param size 点数量
     * @return 随机生成的点结果
     */
    public List<Point> generateRandomPoint(int size) {
        List<Point> points = new ArrayList<>();
        int minX = 0, maxX = 360, minY = -90, maxY = 90;
        for (int i = 0; i < size; i++) {
            final double x = Math.random() * (maxX - minX) + minX;
            final double y = Math.random() * (maxY - minY) + minY;
            points.add(new Point(x, y));
        }
        return points;
    }

    /**
     * 输出点数据到文件
     *
     * @author Hash
     * @param filepath 文件路径
     * @param points 点数据
     */
    public void writePointsToFile(String filepath, List<Point> points) {
        final File file = new File(filepath);
        try (final FileOutputStream fos = new FileOutputStream(file);) {
            for (Point point : points) {
                String text = point.getLon() + "," + point.getLat() + "\n";
                fos.write(text.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取点数据
     *
     * @author Hash
     * @param filepath 文件路径
     * @return 点数据
     */
    public List<Point> readPointsFromFile(String filepath) {
        List<Point> points = new ArrayList<>();
        try (final FileReader fr = new FileReader(filepath);
             BufferedReader br = new BufferedReader(fr);) {
            String text;
            while ((text = br.readLine()) != null) {
                final String[] strings = text.split(",");
                points.add(new Point(Double.parseDouble(strings[0]), Double.parseDouble(strings[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }

    /**
     * 查询目标点邻近k的站点信息
     *
     * @author Hash
     * @param points 点数据集
     * @param x 目标点经度
     * @param y 目标点纬度
     * @param k 邻近k个点数据查询
     * @return 邻近k个点数据集合
     */
    public List<Point> searchKnn(List<Point> points, double x, double y, int k) {
        List<Point> knnPoints = new ArrayList<>();
        for (Point point : points) {
            final double distance = getDistance(point.getLon(), point.getLat(), x, y);
            point.setDistance(distance);
        }
        points.sort(Comparator.comparingDouble(Point::getDistance));

        k = Math.min(points.size(), k);
        for (int i = 0; i < k; i++) {
            knnPoints.add(points.get(i));
        }
        return knnPoints;
    }

    /**
     * 获取二维欧氏距离
     *
     * @author Hash
     * @param x1 坐标经度1
     * @param y1 坐标纬度1
     * @param x2 坐标经度2
     * @param y2 坐标纬度2
     * @return 二维欧氏距离
     */
    public double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
