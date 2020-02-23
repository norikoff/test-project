package project;

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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Task2 {
    final static String path = System.getProperty("user.dir");
    private static Logger logger = LoggerFactory.getLogger(Task2.class);

    public static void main(String[] args) throws Exception {

        System.out.println("Try to get operations from files");
        final List<Operation> operations = readOperationsFromFiles(args);
        System.out.println("Operation size: " + operations.size());
        System.out.println("Try to compute stats by dates");
        final List<Operation> statsByDate = computeStatsByDate(operations);
        System.out.println("Try to compute stats by points");
        final List<Operation> statsByPoints = computeStatsByPoints(operations);
        System.out.println("Try to get files list");
        final List<Path> filesList = createfilesList(args);
        System.out.println("Try to write to files");
        writeToFile(filesList.get(0), statsByDate, "date");
        writeToFile(filesList.get(1), statsByPoints, "point");
    }

    static List<Operation> readOperationsFromFiles(String[] files) throws AppException {
        return Stream.of(files).skip(2).map(fileName -> {
            logger.debug("Try to get points from file: {}", fileName);
            try {
                return Files.lines(Paths.get(path + "/" + fileName)).collect(Collectors.toList());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            return null;
        }).filter(Objects::nonNull).flatMap(Collection::stream).map(op -> {
            Operation operation = new Operation();
            Stream.of(op.split(",")).forEach(s -> {
                final String[] split = s.trim().split("=");
                if ("date".equals(split[0]))
                    operation.setDate(LocalDate.parse(split[1]));
                if ("time".equals(split[0]))
                    operation.setTime(LocalTime.parse(split[1]));
                if ("pointId".equals(split[0]))
                    operation.setPointId(Long.parseLong(split[1]));
                if ("operationId".equals(split[0]))
                    operation.setOperationId(Long.parseLong(split[1]));
                if ("amount".equals(split[0]))
                    operation.setAmount(new BigDecimal(split[1]));
            });
            return operation;
        }).collect(Collectors.toList());
    }

    static List<Operation> computeStatsByDate(List<Operation> operations) {
        return operations.stream()
                .collect(Collectors.groupingBy(Operation::getDate))
                .values().stream()
                .map(operationList -> operationList.stream()
                        .reduce((f1, f2) -> new Operation(f1.getDate(), f1.getAmount().add(f2.getAmount()))))
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    static List<Operation> computeStatsByPoints(List<Operation> operations) {
        return operations.stream()
                .collect(Collectors.groupingBy(Operation::getDate))
                .values().stream()
                .map(operationList -> operationList.stream()
                        .reduce((f1, f2) -> new Operation(f1.getPointId(), f1.getAmount().add(f2.getAmount()))))
                .map(Optional::get)
                .collect(Collectors.toList());
    }


    static List<Path> createfilesList(String[] list) throws AppException {
        Path dir = Paths.get(path);
        final List<Path> paths = Stream.of(list).limit(2).map(file -> {
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


    static void writeToFile(Path file, List<Operation> statsList, String type) {
        try (RandomAccessFile stream = new RandomAccessFile(file.toString(), "rw");
             FileChannel channel = stream.getChannel()) {
            for (Operation stats : statsList) {
                byte[] strBytes;
                if ("date".equals(type))
                    strBytes = stats.getDateStatus().concat("\n").getBytes();
                else
                    strBytes = stats.getPointsStatus().concat("\n").getBytes();
                ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
                buffer.put(strBytes);
                buffer.flip();
                channel.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
