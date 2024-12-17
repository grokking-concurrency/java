package chapter05;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MissionE {

    private static final String NOT_FOUND = "NOT_FOUND";
    private static final int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private static final int PASSWORD_LENGTH = 7;
    private static final String CRYPTO_HASH =  "fe68a21fc76bba7b3a3d8e454eca8cd258de68fd08dddf035f23ddbdce6fc049";

    public static void main(String[] args) {
        System.out.println("Processing number combination concurrently");

        boolean cracked = false;
        String password = null;

        List<Chunk> chunks = getChunks();

        long startTimeNanos = System.nanoTime();

        try (ExecutorService threadPool = Executors.newFixedThreadPool(chunks.size())) {
            List<Future<String>> futures = new ArrayList<>();

            for (Chunk chunk : chunks) {
                Future<String> future = threadPool.submit(() -> crackChunk(chunk));
                futures.add(future);
            }

            System.out.println("Waiting for chunks to finish");

            for (Future<String> future : futures) {
                try {
                    String result = future.get();
                    if (checkResult(result)) {
                        cracked = true;
                        password = result;
                        break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        if (cracked) {
            System.out.printf("PASSWORD CRACKED: %s%n", password);
        } else {
            System.out.println("PASSWORD NOT CRACKED");
        }

        long endTimeNanos = System.nanoTime();
        long processTimeSeconds = TimeUnit.NANOSECONDS.toSeconds(endTimeNanos - startTimeNanos);
        System.out.println(processTimeSeconds + " seconds");
    }

    private static boolean checkResult(String result) {
        return !NOT_FOUND.equals(result);
    }

    private static List<Chunk> getChunks() {
        List<Chunk> chunks = new ArrayList<>();
        int maxNumber = (int) Math.pow(10, PASSWORD_LENGTH) - 1;
        int chunkSize = maxNumber / NUM_PROCESSORS;

        for (int i = 0; i < NUM_PROCESSORS; i++) {
            int chunkStart = i * chunkSize;
            int chunkEnd = chunkStart + chunkSize - 1;
            if (i == NUM_PROCESSORS - 1) {
                chunks.add(new Chunk(chunkStart, maxNumber));
            } else {
                chunks.add(new Chunk(chunkStart, chunkEnd));
            }
        }

        return chunks;
    }

    private static List<String> getCombinations(int min, int max) {
        List<String> combinations = new ArrayList<>();

        for (int i = min; i <= max; i++) {
            String num = Integer.toString(i);
            String zeros = "0".repeat(PASSWORD_LENGTH - num.length());

            combinations.add(zeros + num);
        }

        return combinations;
    }

    private static String crackChunk(Chunk chunk) {
        System.out.printf("Processing %d to %d%n", chunk.from(), chunk.to());

        List<String> combinations = getCombinations(chunk.from(), chunk.to());

        for(String combination : combinations) {
            if (checkPassword(getCryptoHash(combination))) {
                return combination;
            }
        }

        return NOT_FOUND;
    }

    private static boolean checkPassword(String possibleHash) {
        return CRYPTO_HASH.equals(possibleHash);
    }

    private static String getCryptoHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }
                
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private record Chunk(int from, int to) {
    }
}
