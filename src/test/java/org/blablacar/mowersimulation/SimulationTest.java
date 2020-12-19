package org.blablacar.mowersimulation;

import static org.blablacar.mowersimulation.Direction.F;
import static org.blablacar.mowersimulation.Direction.L;
import static org.blablacar.mowersimulation.Direction.R;
import static org.blablacar.mowersimulation.Orientation.E;
import static org.blablacar.mowersimulation.Orientation.N;
import static org.blablacar.mowersimulation.Simulation.buildLawn;
import static org.blablacar.mowersimulation.Simulation.buildMowers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class SimulationTest {

    @Test
    public void testSimulationOneMower() throws Exception {

        Lawn lawn = new Lawn(3, 3);
        Mower mower = new Mower(0, 0, N, Arrays.asList(R, F, F, F, L, F, F, F));
        MowerTask mowerTask = new MowerTask("test", lawn, mower);

        Future<String> result = Executors.newSingleThreadExecutor().submit(mowerTask);
        assertEquals("test : 3 3 N", result.get());
    }

    @Test
    public void testSimulationMultipleMowers() throws Exception {
        File file = new File(getClass().getClassLoader().getResource("test.txt").getPath());
        List<String> lines = Files.lines(file.toPath()).collect(Collectors.toList());
        Lawn lawn = buildLawn(lines.get(0));
        List<Mower> mowers = buildMowers(lawn, lines.subList(1, lines.size()));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<MowerTask> taskList = new ArrayList<>();
        for (int i = 1; i <= mowers.size(); i++) {
            MowerTask mowerTask = new MowerTask("test" + i, lawn, mowers.get(i - 1));
            taskList.add(mowerTask);
        }

        List<Future<String>> results = executor.invokeAll(taskList);
        assertEquals("test1 : 1 3 N", results.get(0).get());
        assertEquals("test2 : 5 1 E", results.get(1).get());
    }

    @Test
    public void testInvalidFile() throws Exception {
        testInvalidFile("invalidTest.txt", "Please specify a valid description for lawn surface");
    }

    @Test
    public void testInvalidFile2() throws Exception {
        testInvalidFile("invalidTest2.txt", "Please specify positive values for lawn surface");
    }

    @Test
    public void testInvalidFile3() throws Exception {
        testInvalidFile("invalidTest3.txt", "Please specify a valid description for mowers position and steps");
    }

    @Test
    public void testInvalidFile4() throws Exception {
        testInvalidFile("invalidTest4.txt", "Please specify a valid description for mower initial position: 1 2");
    }

    @Test
    public void testInvalidFile5() throws Exception {
        testInvalidFile("invalidTest5.txt", "Please specify valid values for mower position (out of bounds): -1 -2 N");
    }

    private void testInvalidFile(String filePath, String message) throws IOException {
        File file = new File(getClass().getClassLoader().getResource(filePath).getPath());
        List<String> lines = Files.lines(file.toPath()).collect(Collectors.toList());
        try {
            Lawn lawn = buildLawn(lines.get(0));
            buildMowers(lawn, lines.subList(1, lines.size()));
            fail("Invalid file should not pe parsed");
        } catch (IllegalArgumentException e) {
            assertEquals(message, e.getMessage());
        }
    }
}
