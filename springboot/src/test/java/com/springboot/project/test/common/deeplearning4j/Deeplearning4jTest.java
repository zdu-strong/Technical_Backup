package com.springboot.project.test.common.deeplearning4j;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import com.springboot.project.test.common.BaseTest.BaseTest;
import lombok.SneakyThrows;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.DataSet;
import org.deeplearning4j.eval.Evaluation;

public class Deeplearning4jTest extends BaseTest {

    @Test
    @SuppressWarnings("deprecation")
    @SneakyThrows
    public void test() {
        // 定义超参数
        int batchSize = 64; // 每次处理的样本数
        int epochs = 5; // 训练轮数
        int inputSize = 28 * 28; // MNIST 图像的输入大小 (28x28 像素)
        int outputSize = 10; // 输出类别数 (数字 0-9)

        // 加载 MNIST 数据集
        DataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, 12345);
        DataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, 12345);

        // 构建神经网络
        MultiLayerConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(123) // 随机种子，确保结果可复现
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam(0.001)) // 使用 Adam 优化器，学习率为 0.001
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(inputSize) // 输入大小
                        .nOut(128) // 第一层的神经元数量
                        .activation(Activation.RELU) // 激活函数
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(128) // 第二层输入大小
                        .nOut(64) // 第二层的神经元数量
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder()
                        .nIn(64) // 输出层输入大小
                        .nOut(outputSize) // 输出类别数
                        .activation(Activation.SOFTMAX) // Softmax 激活，用于多分类任务
                        .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) // 负对数似然损失函数
                        .build())
                .build();

        // 初始化网络
        MultiLayerNetwork model = new MultiLayerNetwork(config);
        model.init();

        // 添加监听器以显示每 100 次迭代的损失值
        model.setListeners(new ScoreIterationListener(100));

        // 开始训练
        System.out.println("Start training...");
        for (int epoch = 1; epoch <= epochs; epoch++) {
            model.fit(mnistTrain); // 训练网络
            System.out.println("Epoch " + epoch + " completed.");
        }

        // 评估模型性能
        System.out.println("Start evaluating...");
        var eval = new Evaluation(outputSize); // 创建评估对象
        while (mnistTest.hasNext()) {
            DataSet testData = mnistTest.next();
            INDArray output = model.output(testData.getFeatures()); // 模型预测
            eval.eval(testData.getLabels(), output); // 比较预测值与真实标签
        }

        // 输出评估结果
        System.out.println(eval.stats());

    }

}
