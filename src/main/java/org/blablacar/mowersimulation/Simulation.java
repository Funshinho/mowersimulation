package org.blablacar.mowersimulation;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main application
 */
public class Simulation {

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please specify a simulation file path");
        }
        String filePath = args[0];
        Path simulationFilePath = Paths.get(filePath);
        if (!Files.exists(simulationFilePath)) {
            throw new IllegalArgumentException("Specified file path does not exist");
        }

        try {
            List<String> lines = Files.lines(Paths.get(filePath)).collect(Collectors.toList());
            Lawn lawn = buildLawn(lines.get(0));
            List<Mower> mowers = buildMowers(lawn, lines.subList(1, lines.size()));

            ExecutorService executor = Executors.newFixedThreadPool(4);
            List<MowerTask> taskList = new ArrayList<>();
            for (int i = 1; i <= mowers.size(); i++) {
                MowerTask mowerTask = new MowerTask("mower " + i, lawn, mowers.get(i - 1));
                taskList.add(mowerTask);
            }

            try {
                List<Future<String>> completedTasks = executor.invokeAll(taskList);
                System.out.println("Result");
                for (Future<String> completedTask : completedTasks) {
                    try {
                        String result = completedTask.get();
                        System.out.println(result);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            executor.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Builds lawn object from surface description
     *
     * @param surfaceDescription the surface description
     * @return the built lawn
     */
    public static Lawn buildLawn(String surfaceDescription) {
        String[] surface = surfaceDescription.split(" ");
        if (surface.length != 2) {
            throw new IllegalArgumentException("Please specify a valid description for lawn surface");
        }
        int witdh = Integer.parseInt(surface[0]);
        int height = Integer.parseInt(surface[1]);
        if (witdh <= 0 || height <= 0) {
            throw new IllegalArgumentException("Please specify positive values for lawn surface");
        }
        return new Lawn(witdh, height);
    }

    /**
     * Builds mowers from the description
     *
     * @param lawn              the associated lawn
     * @param mowersDescription the list of mowers description
     * @return the list of mowers
     */
    public static List<Mower> buildMowers(Lawn lawn, List<String> mowersDescription) {
        List<Mower> mowers = new ArrayList<>();
        if (mowersDescription.size() % 2 != 0) {
            throw new IllegalArgumentException("Please specify a valid description for mowers position and steps");
        }
        for (int i = 0; i < mowersDescription.size(); i = i + 2) {
            String mowerPosition = mowersDescription.get(i);
            String[] mower = mowerPosition.split(" ");
            if (mower.length != 3) {
                throw new IllegalArgumentException("Please specify a valid description for mower initial position: " + mowerPosition);
            }
            String mowerSteps = mowersDescription.get(i + 1);
            List<Direction> steps =
                    Stream.of(mowerSteps.split("")).map(Direction::valueOf).collect(Collectors.toList());
            int x = Integer.parseInt(mower[0]);
            int y = Integer.parseInt(mower[1]);
            if (x < 0 || y < 0 || x > lawn.getWidth() || y > lawn.getHeight()) {
                throw new IllegalArgumentException("Please specify valid values for mower position (out of bounds): " + mowerPosition);
            }
            mowers.add(new Mower(x, y, Orientation.valueOf(mower[2]), steps));
            lawn.getGrid()[x][y].set(true);
        }
        return mowers;
    }

}
