package com.norikoff.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Task {
    final static String path = System.getProperty("user.dir");
    private static Logger logger = LoggerFactory.getLogger(Task.class);

    public static void main(String[] args) throws Exception {

        System.out.println("Try to get points");
        final String sourceFile = Optional.ofNullable(args[0]).orElseThrow(() -> new AppException("Empty arg for source file"));
        final Map<Long, String> pointsMap = readPointsFromFile(sourceFile);
        System.out.println("Try to get operations count");
        final Long operationCount = Optional.ofNullable(args[1]).map(Long::parseLong).orElseThrow(() -> new AppException("Empty arg for operation count"));
        System.out.println("Try to get files list");
        final List<Path> filesList = createfilesList(args);
        System.out.println("Try to get operations");
        final List<Operation> operations = generateOperations(operationCount, pointsMap);
        System.out.println("Operation size: " + operations.size());
        computeOperationsListSize();
        System.out.println("Try to write to files");
        for (int i = 0; i < filesList.size(); i++) {
            writeToFile(filesList.get(i), operations, (operations.size() * i) / filesList.size(), (operations.size() * (i + 1)) / filesList.size());
        }

    }

    private static void computeOperationsListSize() {

    }

    static Map<Long, String> readPointsFromFile(String filename) throws AppException {
        final Map<Long, String> pointsMap = Optional.ofNullable(filename).map(fileName -> {
            System.out.println("Try to get points from file: " + fileName);
            try {
                return Files.lines(Paths.get(path + "/" + fileName)).collect(Collectors.toMap(x -> Long.parseLong(x.split(" ")[0]), x -> x.split(" ")[1]));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return null;
        }).orElseThrow(() -> new AppException("No argument for points"));
        if (pointsMap.isEmpty())
            throw new AppException("Problem with read file: " + filename);
        return pointsMap;
    }

    static List<Path> createfilesList(String[] list) throws AppException {
        Path dir = Paths.get(path);
        final List<Path> paths = Stream.of(list).skip(2).map(file -> {
            try {
                final Path resolve = dir.resolve(file);
                if (!Files.exists(resolve, LinkOption.NOFOLLOW_LINKS))
                    return Files.createFile(resolve);
                else {
                    BufferedWriter writer = Files.newBufferedWriter(resolve);
                    writer.write("");
                    writer.flush();
                    return resolve;
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            return null;
        }).collect(Collectors.toList());
        if (paths.isEmpty() || paths.contains(null))
            throw new AppException("Files list is empty or contains null elements");
        return paths;
    }

    static List<Operation> generateOperations(Long operationCount, Map<Long, String> pointsMap) {
        Random rand = new Random();
        final ArrayList<Long> pointsKeys = new ArrayList<>(pointsMap.keySet());
        return LongStream.rangeClosed(1, operationCount).parallel()
                .mapToObj(longCount -> new Operation(getRandomLocalDate(), getRandomLocalTime(), pointsKeys.get(rand.nextInt(pointsKeys.size())), longCount, getRandomAmoun()))
                .collect(Collectors.toList());

    }

    static LocalDate getRandomLocalDate() {
        final LocalDate now = LocalDate.now();
        long minDay = LocalDate.of(now.getYear() - 1, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(now.getYear(), 1, 1).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    static LocalTime getRandomLocalTime() {
        LocalTime time1 = LocalTime.of(0, 0, 0);
        LocalTime time2 = LocalTime.of(23, 59, 59);
        long totalNanos = Math.abs(ChronoUnit.NANOS.between(time1, time2));
        long randomNanos = ThreadLocalRandom.current().nextLong(totalNanos);
        return LocalTime.ofNanoOfDay(randomNanos);
    }

    static BigDecimal getRandomAmoun() {
        final BigDecimal min = BigDecimal.valueOf(10_000.12).setScale(2, BigDecimal.ROUND_HALF_UP);
        final BigDecimal max = BigDecimal.valueOf(100_000.50).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);

    }

    static void writeToFile(Path file, List<Operation> operations, int start, int end) {
        try (RandomAccessFile stream = new RandomAccessFile(file.toString(), "rw");
             FileChannel channel = stream.getChannel()) {
            for (int i = start; i < end; i++) {
                byte[] strBytes = operations.get(i).toString().concat("\n").getBytes();
                ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
                buffer.put(strBytes);
                buffer.flip();
                channel.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        for (int i = start; i < end; i++) {
//            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toString()))) {
//                out.writeObject(operations.get(i));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
