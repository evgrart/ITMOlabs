package controller;
import org.tribuo.Example; 
import org.tribuo.MutableDataset;
import org.tribuo.clustering.ClusterID;
import org.tribuo.clustering.ClusteringFactory;
import org.tribuo.clustering.kmeans.KMeansModel;
import org.tribuo.clustering.kmeans.KMeansTrainer;
import org.tribuo.datasource.ListDataSource;
import org.tribuo.impl.ArrayExample;
import org.tribuo.provenance.SimpleDataSourceProvenance;
import org.tribuo.util.Util;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeans {

    public static void main(String[] args) throws Exception {

        KMeansTrainer trainer = new KMeansTrainer(3, 10, KMeansTrainer.Distance.EUCLIDEAN, 1, 42);

        String[] featureNames = {"x", "y"};
        Random rng = new Random(55);
        
        List<Example<ClusterID>> data = new ArrayList<>();

        for (int i = 0; i < 300000; i++) {
            double x = (rng.nextDouble() * 4) - 2;
            double y = (rng.nextDouble() * 8) - 5;
            data.add(new ArrayExample<>(new ClusterID(ClusterID.UNASSIGNED), featureNames, new double[]{x, y}));
        }

        ClusteringFactory factory = new ClusteringFactory();
        SimpleDataSourceProvenance provenance = new SimpleDataSourceProvenance("Generated Data", factory);

        ListDataSource<ClusterID> source = new ListDataSource<>(data, factory, provenance);
        
        MutableDataset<ClusterID> trainingDataset = new MutableDataset<>(source);

        long startTime = System.currentTimeMillis();
        KMeansModel model = trainer.train(trainingDataset);
        long endTime = System.currentTimeMillis();
        System.out.println("Обучение завершено за: " + Util.formatDuration(startTime, endTime));

        System.out.println(model.getCentroids().toString());

        String modelPath = "src/main/resources/kmeans-tribuo.model";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath))) {
            oos.writeObject(model);
        }
    }
}