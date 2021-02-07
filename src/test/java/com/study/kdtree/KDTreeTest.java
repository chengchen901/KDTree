package com.study.kdtree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Hash
 * @since 2021/2/6
 */
public class KDTreeTest {

    @Test
    public void searchKnnTest() {
        List<KDTree.Node> nodeList = new ArrayList<>();
        nodeList.add(new KDTree.Node(new double[]{1, 3}));
        nodeList.add(new KDTree.Node(new double[]{2, 12}));
        nodeList.add(new KDTree.Node(new double[]{3, 7}));
        nodeList.add(new KDTree.Node(new double[]{4, 4}));
        nodeList.add(new KDTree.Node(new double[]{5, 9}));
        nodeList.add(new KDTree.Node(new double[]{6, 1}));
        nodeList.add(new KDTree.Node(new double[]{7, 5}));
        nodeList.add(new KDTree.Node(new double[]{8, 11}));
        nodeList.add(new KDTree.Node(new double[]{9, 8}));
        nodeList.add(new KDTree.Node(new double[]{10, 2}));
        nodeList.add(new KDTree.Node(new double[]{11, 6}));
        nodeList.add(new KDTree.Node(new double[]{12, 10}));

        KDTree kdTree = new KDTree();
        final KDTree.Node root = kdTree.buildKDTree(nodeList);
        final List<KDTree.Node> knnNodes = kdTree.searchKNN(root, new KDTree.Node(new double[]{3, 5}), 3);
        for (KDTree.Node knnNode : knnNodes) {
            System.out.println(Arrays.toString(knnNode.data));
        }
    }
}
