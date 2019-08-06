package aai;

import org.apache.commons.io.FileUtils;
import weka.core.Instances;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * The file creates number of eperiments from initial experiment file and initial dataset. It takes a setting file as
 * the argument.
 */
public class Main {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.print("Settings file missing.");
            return;
        }

        String inputFolder = null;
        String outputFolder = null;
        int nuberOfFolds = 0;
        String folderSeperator = null;
        String originalDataName = null;

        InputStream input = null;
        try {
            Properties prop = new Properties();
            input = new FileInputStream(args[0]);

            // load a properties file
            prop.load(input);

            inputFolder = prop.getProperty("experimentInputFolder");
            outputFolder = prop.getProperty("experimentOutputFolder");
            nuberOfFolds = Integer.parseInt(prop.getProperty("nuberOfFolds"));
            folderSeperator = prop.getProperty("folderSeperator");
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

        // Creating experiments.
        for (int i = 1; i < nuberOfFolds; ++i) {

            String destinationFolder = outputFolder + i;

            // Copy all contents to the new folder, create the folder if it does not exist.
            try {
                FileUtils.copyDirectory(new File(inputFolder), new File(destinationFolder));
            } catch (IOException e) {
                System.out.println("Could not copy files to: " + destinationFolder);
                e.printStackTrace();
                return;
            }

            // Rename the experiment file
            String originalExperimentName =  inputFolder.substring(inputFolder.lastIndexOf(folderSeperator) + 1);
            String destinationExperimentName =
                    destinationFolder.substring(destinationFolder.lastIndexOf(folderSeperator) + 1);
            File originalExperiment = new File(destinationFolder +
                    folderSeperator + originalExperimentName + ".experiment");
            File newExperimentFile = new File(destinationFolder +
                    folderSeperator + destinationExperimentName + ".experiment");

            if (!originalExperiment.renameTo(newExperimentFile)) {
                System.out.println("Could not rename experiment file for i = " + i);
                return;
            }

            // Modifying experiment file.
            String experimentFileName = destinationFolder +
                    destinationFolder.substring(destinationFolder.lastIndexOf(folderSeperator)) + ".experiment";

            String experimentContent = null;
            try {
                experimentContent = new Scanner(new File(experimentFileName)).useDelimiter("\\Z").next();
            } catch (FileNotFoundException e) {
                System.out.println("Could not open experiment file for i = " + i);
                e.printStackTrace();
                return;
            }

            experimentContent =
                    experimentContent.replaceAll(originalDataName + "Train0", originalDataName +
                            "Train" + i);
            experimentContent =
                    experimentContent.replaceAll(originalDataName + "Test0", originalDataName +
                            "Test" + i);

            experimentContent =
                    experimentContent.replaceAll(originalExperimentName, destinationExperimentName);

            try {
                FileUtils.writeStringToFile(new File(experimentFileName), experimentContent);
            } catch (IOException e) {
                System.out.println("Could not write scenario file for i = " + i);
                e.printStackTrace();
                return;
            }

            // Modifying scenario file.
            String scenarioFileName = destinationFolder + folderSeperator + "autoweka.scenario";
            String scenarioContent = null;
            try {
                scenarioContent = new Scanner(new File(scenarioFileName)).useDelimiter("\\Z").next();
            } catch (FileNotFoundException e) {
                System.out.println("Could not open scenario file for i = " + i);
                e.printStackTrace();
                return;
            }

            scenarioContent =
                    scenarioContent.replaceAll(originalDataName + "Train0", originalDataName + "Train"
                            + i);
            scenarioContent =
                    scenarioContent.replaceAll(originalDataName + "Test0", originalDataName + "Test"
                            + i);


            try {
                FileUtils.writeStringToFile(new File(scenarioFileName), scenarioContent);
            } catch (IOException e) {
                System.out.println("Could not write scenario file for i = " + i);
                e.printStackTrace();
                return;
            }
        }

    }
}
