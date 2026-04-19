import java.util.Random;

public class BankersAlgorithm {

    static final int NUMBER_OF_CUSTOMERS = 5;
    static final int NUMBER_OF_RESOURCES = 3;

    static final int[] available   = new int[NUMBER_OF_RESOURCES];
    static final int[][] maximum   = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static final int[][] allocation = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];
    static final int[][] need       = new int[NUMBER_OF_CUSTOMERS][NUMBER_OF_RESOURCES];

    static final Random random = new Random();

    
    // Algoritmo de segurança — verifica se o sistema está em estado seguro
  
    static boolean isSafeState() {
        int[] work = available.clone();
        boolean[] finish = new boolean[NUMBER_OF_CUSTOMERS];

        boolean found;
        do {
            found = false;
            for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
                if (!finish[i] && needFitsWork(i, work)) {
                    for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                        work[j] += allocation[i][j];
                    }
                    finish[i] = true;
                    found = true;
                }
            }
        } while (found);

        for (boolean f : finish) {
            if (!f) return false;
        }
        return true;
    }

    static boolean needFitsWork(int customer, int[] work) {
        for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
            if (need[customer][j] > work[j]) return false;
        }
        return true;
    }

    static void printState() {
        System.out.println("  Disponível : " + arrayToString(available));
        System.out.print("  Alocação   : ");
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
            System.out.print("C" + i + ":" + arrayToString(allocation[i]) + " ");
        System.out.println();
        System.out.print("  Need       : ");
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
            System.out.print("C" + i + ":" + arrayToString(need[i]) + " ");
        System.out.println();
    }

    static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        if (args.length != NUMBER_OF_RESOURCES) {
            System.err.printf("Uso: java BankersAlgorithm <rec1> <rec2> <rec3>%n");
            System.err.printf("Exemplo: java BankersAlgorithm 10 5 7%n");
            System.exit(1);
        }

        for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
            available[j] = Integer.parseInt(args[j]);
        }

        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {
            for (int j = 0; j < NUMBER_OF_RESOURCES; j++) {
                maximum[i][j] = 1 + random.nextInt(available[j]);
                need[i][j]    = maximum[i][j];
            }
        }

        System.out.println("=== Algoritmo do Banqueiro ===");
        System.out.println("Disponível: " + arrayToString(available));
        System.out.print("Maximum   : ");
        for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++)
            System.out.print("C" + i + ":" + arrayToString(maximum[i]) + " ");
        System.out.println();

        System.out.println("\nSistema em estado seguro? " + isSafeState());
        printState();
    }
}
