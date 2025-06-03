package edu.pucmm;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author me@fredpena.dev
 * @created 02/06/2025  - 20:46
 */
public class ParallelMatrixSearch {

    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_COUNT = 4;
    private static final int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
    private static final int TARGET = 256; // Número a buscar
    private static boolean SECUENCIAL_CONTEXT_TARGET_FOUND = false;
    private static AtomicBoolean MULTITHREAD_CONTEXT_TARGET_FOUND = new AtomicBoolean(false);

    public static void main(String[] args) {
        // Inicializar la matriz con valores aleatorios
        fillMatrixRandom();

        // Medir el tiempo de ejecución de la búsqueda secuencial
        long startTime = System.nanoTime();
        sequentialSearch();
        long endTime = System.nanoTime();
        System.out.println("Tiempo búsqueda secuencial: " + ((endTime - startTime) / 1_000_000) + "ms");

        int [][][] dividedMatrix = divideMatrixBaseOnAmountOfThreads();
        // Medir el tiempo de ejecución de la búsqueda paralela
        startTime = System.nanoTime();
        parallelSearch(dividedMatrix);
        endTime = System.nanoTime();
        System.out.println("Tiempo búsqueda paralela: " + ((endTime - startTime) / 1_000_000) + "ms");
    }

    private static void sequentialSearch() {
        int row = 0, col = 0;
        for (int[] rows : matrix) {
            row++;
            col = 0;
            if (SECUENCIAL_CONTEXT_TARGET_FOUND)
                break;

            for (int values : rows) {
                col++;
                if (values == TARGET) {
                    SECUENCIAL_CONTEXT_TARGET_FOUND = true;
                    System.out.printf("""
									TARGET(%d) FOUND IN ROW: %d and COL: %d
									""", TARGET, row, col);
                    break;
                }
            }
        }
    }

    private static void parallelSearch(int[][][] dividedMatrix) {
        // Implementar búsqueda paralela
        // Sugerencia: usar AtomicBoolean para indicar si ya se encontró el número y detener hilos
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int section = 0; section < THREAD_COUNT; section++) {
            int[][] dividedMatrixSection = dividedMatrix[section];
            threads[section] = new Thread(() -> matrixSearch(dividedMatrixSection, Thread.currentThread()));
            threads[section].start();
        }
    }
    private static void matrixSearch(int[][] dividedMatrix, Thread thread) {
        int row = 0, col = 0;
        for (int[] rows : dividedMatrix) {
            row++;
            col = 0;
            if (MULTITHREAD_CONTEXT_TARGET_FOUND.get()){
                thread.interrupt();
                break;
            }
            for (int values : rows) {
                col++;
                if (values == TARGET) {
                    MULTITHREAD_CONTEXT_TARGET_FOUND.set(true);
                    System.out.printf("""
									TARGET(%d) FOUND IN ROW: %d and COL: %d
									""", TARGET, row, col);
                    break;
                }
            }
        }

    }

    private static int[][][] divideMatrixBaseOnAmountOfThreads() {
        int chunkSize = (int) Math.ceil((double) MATRIX_SIZE / THREAD_COUNT);
        int[][][] dividedMatrix = new int[THREAD_COUNT][][];
        for (int section = 0; section < THREAD_COUNT; section++) {
            dividedMatrix[section] = new int[chunkSize][chunkSize];
            for (int i = 0; i < chunkSize; i++) {
                for (int j = 0; j < chunkSize; j++) {
                    dividedMatrix[section][i][j] = matrix[section * chunkSize + i][section * chunkSize + j];
                }
            }
        }
        return dividedMatrix;
    }

    private static void fillMatrixRandom() {
        Random rand = new Random();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                matrix[i][j] = rand.nextInt(1000); // Rango arbitrario
            }
        }
    }

    public static AtomicBoolean getMultithreadContextTargetFound() {
        return MULTITHREAD_CONTEXT_TARGET_FOUND;
    }

    public static void setMultithreadContextTargetFound(AtomicBoolean multithreadContextTargetFound) {
        ParallelMatrixSearch.MULTITHREAD_CONTEXT_TARGET_FOUND = multithreadContextTargetFound;
    }
}