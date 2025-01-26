package com.springboot.project.test.common.deeplearning4j;

import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import com.springboot.project.test.common.BaseTest.BaseTest;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.NumberUtil;
import lombok.SneakyThrows;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import java.util.List;

public class Deeplearning4jTest extends BaseTest {

    @Test
    @SneakyThrows
    public void test() {
        // 1. Prepare data
        double[][] areaList = new double[][] {
                { 50 }, { 60 }, { 70 }, { 80 }, { 90 }, { 100 }, { 110 }, { 120 }, { 130 }, { 140 }
        }; // Area (square meters)
        double[][] priceList = new double[][] {
                { 150000 }, { 180000 }, { 210000 }, { 240000 }, { 270000 }, { 300000 }, { 330000 }, { 360000 },
                { 390000 }, { 420000 }
        }; // Price (in currency units)

        INDArray featureMatrix = Nd4j.create(areaList);
        INDArray labelMatrix = Nd4j.create(priceList);

        DataSet dataSet = new DataSet(featureMatrix, labelMatrix);

        // Split data into mini-batches for training
        int batchSize = 5;
        List<DataSet> listDataSet = dataSet.asList();
        ListDataSetIterator<DataSet> iterator = new ListDataSetIterator<>(listDataSet, batchSize);

        // 2. Build the model
        MultiLayerNetwork model = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(12345)
                .updater(new Sgd(0.0000001)) // Learning rate
                .list()
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .nIn(1) // Number of input neurons (area)
                        .nOut(1) // Number of output neurons (price)
                        .activation(Activation.IDENTITY) // Linear activation function
                        .build())
                .build());

        model.init();
        model.setListeners(new ScoreIterationListener(1)); // Print loss value every iteration

        // 3. Train the model
        int epochs = 1000;
        for (int i = 0; i < epochs; i++) {
            iterator.reset();
            model.fit(iterator);
        }

        System.out.println("Area (square meters) -> Predicted Price (USD $)");
        for (var area : List.of(55, 85, 125)) {
            // 4. Predict using the model
            var predictedPrice = model.output(Nd4j.create(new double[][] {
                    { area }
            })).getDouble(0, 0);
            // 5. Output predictions
            System.out
                    .println(StrFormatter.format("Area:{}(square meters) -> Predicted Price:${}",
                            NumberUtil.decimalFormat("#,###", area),
                            NumberUtil.decimalFormat("#,###", predictedPrice)));
        }
    }

}
