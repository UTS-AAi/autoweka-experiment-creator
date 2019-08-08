package aai;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import autoweka.tools.GetBestFromTrajectoryGroup;

// The class for batch analysis of results.
public class ResultAnalyzer {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.print("Settings file missing.");
            return;
        }

        int nuberOfFolds = 0;
        String folderSeperator = null;
        String dataFolder = null;
        String originalDataName = null;
        String inputFolder = null;
        String outputFolder = null;


        InputStream input = null;
        try {
            Properties prop = new Properties();
            input = new FileInputStream(args[0]);

            // load a properties file
            prop.load(input);


            dataFolder = prop.getProperty("dataFolder");
            inputFolder = prop.getProperty("experimentInputFolder");
            outputFolder = prop.getProperty("experimentOutputFolder");
            nuberOfFolds = Integer.parseInt(prop.getProperty("nuberOfFolds"));
            folderSeperator = prop.getProperty("folderSeperator");
            originalDataName = prop.getProperty("originalDataName");

        } catch (IOException ex) {
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

        String experimentsFolder = outputFolder.substring(0, outputFolder.lastIndexOf(folderSeperator));
        String [] experimentNames = {"SurfaceEnsambleTrainTestSplit1h", "XlevelClass41h", "XlevelClass51h",
                "DipClass41h", "DipClass51h"};

        for (int j = 0; j < experimentNames.length; ++ j) {
            outputFolder = experimentsFolder + folderSeperator + experimentNames[j];
            for (int i = 0; i < nuberOfFolds; ++i) {
                String experimentName = outputFolder.substring(outputFolder.lastIndexOf(folderSeperator) + 1);

                String trajectoryFileName = i == 0 ? outputFolder + folderSeperator + experimentName + ".trajectories.0"
                        : outputFolder + i + folderSeperator + experimentName + i + ".trajectories.0";
                GetBestFromTrajectoryGroup trajectoryGroup = new GetBestFromTrajectoryGroup(trajectoryFileName);

                System.out.println(experimentName + "\t" + trajectoryGroup.errorEstimate + "\t" +
                        trajectoryGroup.classifierClass + "\t" + trajectoryGroup.classifierArgs);
            }
        }
    }
}

