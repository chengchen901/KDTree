package com.study.kdtree;

import java.util.*;

/**
 * @author Hash
 * @since 2021/2/1
 */
public class KDTree {

    /**
     * 构建kd树  返回根节点
     *
     * @param nodeList 坐标节点集合
     * @return 根节点
     */
    public Node buildKDTree(List<Node> nodeList) {
        return buildKDTree(nodeList, 0);
    }

    /**
     * 构建kd树  返回根节点
     *
     * @param nodeList 坐标节点集合
     * @param index    维度，建立树的时候判断的维度
     * @return 根节点
     */
    public Node buildKDTree(List<Node> nodeList, int index) {
        if (nodeList == null || nodeList.size() == 0) {
            return null;
        }

        // 中位数排序
        nodeList.sort(Comparator.comparingDouble(node -> node.getData(index)));

        // 中位数 当做根节点
        final int midIndex = nodeList.size() / 2;
        Node root = nodeList.get(midIndex);
        root.axis = index;

        // 放入左侧区域的节点  包括包含与中位数等值的节点
        List<Node> leftNodeList = nodeList.subList(0, midIndex);
        List<Node> rightNodeList = nodeList.subList(midIndex + 1, nodeList.size());

        // 计算从哪一维度切分
        int nextAxis = (index + 1) % root.data.length;

        root.left = buildKDTree(leftNodeList, nextAxis);
        root.right = buildKDTree(rightNodeList, nextAxis);

        if (root.left != null) {
            root.left.parent = root;
        }
        if (root.right != null) {
            root.right.parent = root;
        }
        return root;
    }



    /**
     * 查询最近邻
     *
     * @param root   kd树根节点
     * @param target 需要查询的坐标点
     * @param k      邻近站点数
     * @return 坐标点邻近k个点数据
     */
    public List<Node> searchKNN(Node root, Node target, int k) {
        List<Node> knnList = new ArrayList<>();
        searchBrother(knnList, root, target, k);
        Collections.sort(knnList);
        return knnList;
    }

    /**
     * 查询目标点邻近k个点站点
     *
     * @param knnList 邻近点数据存储集合
     * @param root    kd树根节点
     * @param k       邻近站点数
     * @param target  需要查询的坐标点
     */
    public void searchBrother(List<Node> knnList, Node root, Node target, int k) {
        // 查询到叶子节点最近近似点
        Node leafNNode = iterDown(root, target);
        leafNNode.distance = target.computeDistance(leafNNode);
        maintainMaxHeap(knnList, leafNNode, k);

        while (leafNNode != root) {
            Node brother = getBrother(leafNNode);
            if (brother != null) {
                /*
                 * 样本到分割线的距离要比之前候选集当中的最大距离要小，所以分割线的另一侧很有可能存在答案
                 */
                if (knnList.get(0).distance > Math.abs(target.getData(leafNNode.parent.axis) - leafNNode.parent.getData(leafNNode.parent.axis)) || knnList.size() < k) {
                    searchBrother(knnList, brother, target, k);
                }
            }

            // 返回上一级
            leafNNode = leafNNode.parent;
            leafNNode.distance = target.computeDistance(leafNNode);
            maintainMaxHeap(knnList, leafNNode, k);
        }
    }

    /**
     * 查询到叶子节点最近近似点
     *
     * @param node   kd树节点
     * @param target 需要查询的坐标点
     * @return 最近近似点
     */
    public Node iterDown(Node node, Node target) {
        // 如果是叶子节点，则返回
        if (node.left == null && node.right == null) {
            return node;
        }

        // 如果左节点为空，则递归右节点
        if (node.left == null) {
            return iterDown(node.right, target);
        }

        // 如果右节点为空，则递归左节点
        if (node.right == null) {
            return iterDown(node.left, target);
        }

        //都不为空,判断是左还是右
        int axis = node.axis;
        Node nextNode = target.getData(axis) <= node.getData(axis) ? node.left : node.right;
        return iterDown(nextNode, target);
    }

    /**
     * 获取兄弟节点
     *
     * @param node 当前节点
     * @return 兄弟节点
     */
    public Node getBrother(Node node) {
        if (node == node.parent.left) {
            return node.parent.right;
        } else {
            return node.parent.left;
        }
    }

    /**
     * 维护一个k的最大堆
     *
     * @param listNode 邻近点数据存储集合
     * @param newNode  最近近似点
     * @param k        邻近站点数
     */
    public void maintainMaxHeap(List<Node> listNode, Node newNode, int k) {
        /*
         * 我们查找到的叶子节点之后，在两种情况下我们会把当前点加入候选集。
         * 第一种情况是候选集还有空余，也就是还没有满K个，这里的K是我们查询的数量。
         * 第二种情况是当前点到样本的距离小于候选集中最大的一个，那么我们需要更新候选集。
         */
        if (listNode.size() < k) {
            // 不足k个堆   直接向上修复
            maxHeapFixUp(listNode, newNode);
        } else if (newNode.distance < listNode.get(0).distance) {
            // 比堆顶的要小   还需要向下修复 覆盖堆顶
            maxHeapFixDown(listNode, newNode);
        }
    }

    /**
     * 从上往下修复  将会覆盖第一个节点
     * 如果一个节点比它的子节点小（最大堆）或者大（最小堆），那么需要将它向下移动。这个操作也称作“堆化（heapify）”。
     *
     * @param listNode 邻近点数据存储集合
     * @param newNode  最近近似点
     */
    private void maxHeapFixDown(List<Node> listNode, Node newNode) {
        listNode.set(0, newNode);
        int i = 0;
        // 获取当前节点的左节点
        int j = i * 2 + 1;
        while (j < listNode.size()) {
            if (j + 1 < listNode.size() && listNode.get(j).distance < listNode.get(j + 1).distance) {
                // 选出子结点中较大的点，第一个条件是要满足右子树不为空
                j++;
            }

            if (listNode.get(i).distance >= listNode.get(j).distance) {
                break;
            }

            Node t = listNode.get(i);
            listNode.set(i, listNode.get(j));
            listNode.set(j, t);

            i = j;
            j = i * 2 + 1;
        }
    }

    /**
     * 最大堆修复
     * 如果一个节点比它的父节点大（最大堆）或者小（最小堆），那么需要将它同父节点交换位置。这样是这个节点在数组的位置上升。
     *
     * @param listNode 邻近点数据存储集合
     * @param newNode  最近近似点
     * @author Hash
     */
    private void maxHeapFixUp(List<Node> listNode, Node newNode) {
        listNode.add(newNode);
        // 当前数据节点
        int j = listNode.size() - 1;

        /*
         * i是j的parent节点
         * 计算公式为：(数据下标 - 1) / 2 = 父节点下标
         * 关于堆数据结构的相关知识点见博客：https://www.jianshu.com/p/6b526aa481b1
         */
        int i = j / 2;
        while (i >= 0) {
            if (listNode.get(i).distance >= listNode.get(j).distance) {
                break;
            }

            Node t = listNode.get(i);
            listNode.set(i, listNode.get(j));
            listNode.set(j, t);

            j = i;
            i = (j - 1) / 2;
        }
    }

    static class Node implements Comparable<Node> {

        /**
         * 树上节点的数据，是一个多维的向量
         */
        double[] data;

        /**
         * 与当前查询点的距离，初始化的时候是没有的
         */
        double distance;

        /**
         * 左右子节点以及父节点
         */
        Node left, right, parent;

        /**
         * 维度，建立树的时候判断的维度
         */
        int axis = -1;

        public Node(double[] data) {
            this.data = data;
        }

        /**
         * 返回指定维度上的数值
         *
         * @param axis 维度
         * @return 指定维度上的数值
         */
        public double getData(int axis) {
            if (data == null || data.length <= axis) {
                return Integer.MIN_VALUE;
            }
            return data[axis];
        }

        @Override
        public int compareTo(Node o) {
            return Double.compare(this.distance, o.distance);
        }

        /**
         * 计算距离 这里返回欧式距离
         *
         * @param that 另一个节点
         * @return 欧式距离
         */
        public double computeDistance(Node that) {
            if (this.data == null || that.data == null || this.data.length != that.data.length) {
                // 出问题了  距离最远
                return Double.MAX_VALUE;
            }
            double d = 0;
            for (int i = 0; i < this.data.length; i++) {
                d += Math.pow(this.data[i] - that.data[i], 2);
            }
            return Math.sqrt(d);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data=" + Arrays.toString(data) +
                    ", distance=" + distance +
                    ", left=" + left +
                    ", right=" + right +
                    ", parent=" + parent +
                    ", axis=" + axis +
                    '}';
        }
    }
}
