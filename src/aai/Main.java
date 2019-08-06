package aai;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.print("Settings file missing.");
            return;
        }

        String inputFolder = null;
        String trainFileNameBase = null;
        String testFileNameBase = null;
        String outputFolder = null;
        int experimentsToCreate = 0;
        String folderSeperator = null;

        InputStream input = null;
        try {
            Properties prop = new Properties();
            input = new FileInputStream(args[0]);

            // load a properties file
            prop.load(input);

            inputFolder = prop.getProperty("inputFolder");
            trainFileNameBase = prop.getProperty("trainFileNameBase");
            testFileNameBase = prop.getProperty("testFileNameBase");
            outputFolder = prop.getProperty("outputFolder");
            experimentsToCreate = Integer.parseInt(prop.getProperty("experimentsToCreate"));
            folderSeperator = prop.getProperty("folderSeperator");
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

        for (int i = 1; i < experimentsToCreate + 1; ++i) {

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
                    experimentContent.replaceAll(trainFileNameBase + "1", trainFileNameBase + i);
            experimentContent =
                    experimentContent.replaceAll(testFileNameBase + "1", testFileNameBase + i);

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
                    scenarioContent.replaceAll(trainFileNameBase + "1", trainFileNameBase + i);
            scenarioContent =
                    scenarioContent.replaceAll(testFileNameBase + "1", testFileNameBase + i);


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
