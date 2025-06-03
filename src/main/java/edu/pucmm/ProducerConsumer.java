package edu.pucmm;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author me@fredpena.dev
 * @created 02/06/2025 - 20:46
 */
public class ProducerConsumer {
	private static final int QUEUE_CAPACITY = 10;
	private static final int PRODUCER_COUNT = 2;
	private static final int CONSUMER_COUNT = 2;
	private static final int PRODUCE_COUNT = 100;
	private static final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
	private static final ConcurrentHashMap<Thread, Integer> consumedNumbers = new ConcurrentHashMap<>();

	/**
	 * Requerimientos Crear una cola bloqueante con capacidad limitada (ArrayBlockingQueue).
	 * Implementar n productores que generen números aleatorios y los coloquen en la cola. Implementar
	 * n consumidores que extraigan y procesen los números de la cola (ej. sumarlos). Ejecutar el
	 * sistema con múltiples productores y consumidores simultáneos. Medir el tiempo total de
	 * procesamiento. Mostrar cuántos elementos consumió cada hilo consumidor.
	 */
	public static void main(String[] args) {

		// Sugerencia: Usar ExecutorService o crear threads manualmente para iniciar Productores y
		// Consumidores
		Thread[] producers = new Thread[PRODUCER_COUNT];
		Thread[] consumers = new Thread[CONSUMER_COUNT];
		for (int i = 0; i < PRODUCER_COUNT; i++) {
			producers[i] = new Thread(new Producer());
			producers[i].start();
		}
		for (int i = 0; i < CONSUMER_COUNT; i++) {
			consumers[i] = new Thread(new Consumer());
			consumers[i].start();
		}

		// Se opto por hacer un join para esperar a que todos los hilos terminen antes de mostrar el resultado
		for (Thread producer : producers) {
			try {
				producer.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		showConsumedByConsumer();
	}

	private static void showConsumedByConsumer() {
		System.out.println("Total consumed numbers by each consumer:");
		consumedNumbers.forEach((consumer, number) -> System.out.println(consumer.getName() + ": " + number));
	}

	static class Producer implements Runnable {
		@Override
		public void run() {
			// Generar PRODUCE_COUNT números aleatorios y colocarlos en la cola
			// Sugerencia: usar Thread.sleep(10) para simular tiempo de producción
			for (int i = 0; i < PRODUCE_COUNT; i++) {
				try {
					int number = (int) (Math.random() * 100);
					queue.put(number);
					System.out.println("Produced: " + number);
					Thread.sleep(10);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}

	static class Consumer implements Runnable {
		@Override
		public void run() {
			// Extraer elementos de la cola y procesarlos (ej: sumarlos)
			// Sugerencia: llevar la suma total por hilo y reportar al final
			int totalSum = 0;
			while (true) {
				try {
					Integer number = queue.take();
					totalSum += number;
					consumedNumbers.put(Thread.currentThread(), totalSum);
					Thread.sleep(10);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
	}
}

