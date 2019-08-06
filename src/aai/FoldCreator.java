// The class creates n training and testing datasets (80/20 split) from the input dataset.
package aai;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class FoldCreator {

    /**
     *
     * @param nFolds
     * @param dataFolder The files will be saved here. Train files will have name "train[n].arff" and test files will
     *                   be named "test[n].arff"
     * @param originalDataName without .arff
     * @param seed
     * @return true on success, false on fail.
     */
    public boolean createFolds(int nFolds, String dataFolder, String originalDataName, String folderSeperator,
                               int seed) {
        try{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(dataFolder + folderSeperator +
                    originalDataName + ".arff");
            Instances dataSet = source.getDataSet();

            Random rand = new Random(seed);   // create seeded number generator
            dataSet.randomize(rand);         // randomize data with number generator

            for (int n = 0; n < nFolds; n++) {
                Instances train = dataSet.trainCV(nFolds, n);
                Instances test = dataSet.testCV(nFolds, n);

                ConverterUtils.DataSink.write(dataFolder + folderSeperator + originalDataName + "Train" + n +
                                ".arff", train);
                ConverterUtils.DataSink.write(dataFolder + folderSeperator + originalDataName + "Test" + n +
                                ".arff", test);
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.print("Settings file missing.");
            return;
        }

        int nuberOfFolds = 0;
        String folderSeperator = null;
        String dataFolder = null;
        String originalDataName = null;

        InputStream input = null;
        try {
            Properties prop = new Properties();
            input = new FileInputStream(args[0]);

            // load a properties file
            prop.load(input);

            nuberOfFolds = Integer.parseInt(prop.getProperty("nuberOfFolds"));
            folderSeperator = prop.getProperty("folderSeperator");
            dataFolder = prop.getProperty("dataFolder");
            originalDataName = prop.getProperty("originalDataName");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Wee create datasets.
        FoldCreator foldcreator = new FoldCreator();
        if (!foldcreator.createFolds(nuberOfFolds, dataFolder, originalDataName, folderSeperator, 0)) {
            System.out.println("Could not create data sets.");
            return;
        }

    }
}
