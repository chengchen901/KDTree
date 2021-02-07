package com.study.kdtree;

import com.study.kdtree.check.KDTreeCheck;
import com.study.kdtree.check.Point;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hash
 * @since 2021/2/1
 */
public class KDTreeCheckTest {

    public static final Logger logger = LoggerFactory.getLogger(KDTreeCheckTest.class);

    KDTreeCheck kdTreeCheck = new KDTreeCheck();
    KDTree kdTree = new KDTree();

    public static final int STATION_SIZE = 100_000;
    public static final String FILE_PATH = "D:\\CodingStudy\\KDTree\\src\\main\\resources\\random_point.csv";
    public static final double X = 30;
    public static final double Y = 40;
    public static final int K = 64;

    @Test
    public void writePointsToFileTest() {
        final List<Point> points = kdTreeCheck.generateRandomPoint(STATION_SIZE);
        kdTreeCheck.writePointsToFile(FILE_PATH, points);
        logger.info("随机生成 [{}] 点输出到 [{}]", STATION_SIZE, FILE_PATH);
    }

    @Test
    public void readPointsFromFileTest() {
        final List<Point> points = kdTreeCheck.readPointsFromFile(FILE_PATH);
        logger.info("读取到数据 [{}]", points.size());
    }

    @Test
    public void checkSearchKnnTest() {
        final List<Point> kdTreeKnnPoints = searchKnnByKDTreeTest();
        final List<Point> simpleKnnPoints = searchKnnBySimpleTest();

        logger.info("检验kdTree是否能遍历到所以的邻近点");
        // 检验kdTree是否能遍历到所以的邻近点
        for (final Point simplePoint : simpleKnnPoints) {
            if (!kdTreeKnnPoints.contains(simplePoint)) {
                logger.error("邻近点 [{}] 不存在", simplePoint.toString());
            } else {
                logger.info("邻近点 [{}] 存在", simplePoint.toString());
            }
        }

        logger.info("最终校验，一一匹配邻近点");
        // 最终校验，一一匹配邻近点
        for (int i = 0; i < simpleKnnPoints.size(); i++) {
            final Point kdTreePoint = kdTreeKnnPoints.get(i);
            final Point simplePoint = simpleKnnPoints.get(i);
            if (!kdTreePoint.equals(simplePoint)) {
                logger.error("邻近第 [{}] 个站点不一致,kdTreePoint [{}] simplePoint [{}]", (i + 1), kdTreePoint, simpleKnnPoints);
            } else {
                logger.info("邻近第 [{}] 个站点一致,point [{}]", (i + 1), kdTreePoint);
            }
        }
    }

    public List<Point> searchKnnByKDTreeTest() {
        final List<Point> points = kdTreeCheck.readPointsFromFile(FILE_PATH);
        List<KDTree.Node> nodeList = new ArrayList<>();
        for (Point point : points) {
            nodeList.add(new KDTree.Node(new double[]{point.getLon(), point.getLat()}));
        }

        long start = System.currentTimeMillis();

        final KDTree.Node root = kdTree.buildKDTree(nodeList);

        logger.info("KDTree 构建耗时 [{}] ms", (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();

        final List<KDTree.Node> knnNodes = kdTree.searchKNN(root, new KDTree.Node(new double[]{X, Y}), K);

        logger.info("KDTree 算法查询最近 [{}] 点耗时 [{}] ms", K, (System.currentTimeMillis() - start));

        List<Point> knnPoints = new ArrayList<>();
        for (KDTree.Node knnNode : knnNodes) {
            knnPoints.add(new Point(knnNode.data[0], knnNode.data[1], knnNode.distance));
        }
        return knnPoints;
    }

    public List<Point> searchKnnBySimpleTest() {
        final List<Point> points = kdTreeCheck.readPointsFromFile(FILE_PATH);

        final long start = System.currentTimeMillis();

        final List<Point> knnPoints = kdTreeCheck.searchKnn(points, X, Y, K);

        final long end = System.currentTimeMillis();
        logger.info("simple for 算法查询最近 [{}] 点耗时 [{}] ms", K, (end - start));

        return knnPoints;
    }
}
