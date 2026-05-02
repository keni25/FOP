package fop_pasti_assignment;

import java.util.Scanner;

public class Filter_Sorting_History {
  public static void History(Scanner sc, String[] transactionDates, String[] description, double[] debits, double[] credits, int transactionCount) {
        if (transactionCount == 0){
            System.out.println("You have no transactions yet. ");
            return;
        }
        String[] filteredTransaction = new String [100];
        
        while (true){
            System.out.print("\nDo you wish to filter your history? (Y/N) ");
            String filter = sc.next();
            if (filter.equalsIgnoreCase("y")){
                filterTransactions(sc, transactionDates, description, debits, credits, transactionCount, filteredTransaction);
                break;
            }else if (filter.equalsIgnoreCase("n"))
                break;
            else
                System.out.println("Invalid input. Please enter 'Y' or 'N' only.");
        }        
        
        while (true){
            System.out.print("\nDo you wish to sort your history? (Y/N) ");
            String sort = sc.next();
            if (sort.equalsIgnoreCase("y")){
                sortTransactions(sc, transactionDates, description, debits, credits, transactionCount, filteredTransaction);
                break;
            }else if (sort.equalsIgnoreCase("n"))
                break;
            else
                System.out.println("Invalid input. Please enter 'Y' or 'N' only.");
        }
        printHistory(transactionDates, description, debits, credits, transactionCount, filteredTransaction);
    }

    public static void filterTransactions(Scanner sc, String[] transactionDates, String[] description, double[] debits, double[] credits, int transactionCount, String[] filteredTransaction) {
        for (int i = 0; i < transactionCount; i++) {
            filteredTransaction[i]="";
        }
        int filterChoice;
        do{
            System.out.print("\n-- Filtering Options --\n1. Date Range\n2. Transaction Type\n3. Amount Range\n\n>");
            filterChoice = sc.nextInt();
            switch (filterChoice) {
                case 1 -> {
                    System.out.print("\n-- Date Range --\nEnter start date (DD-MM-YYYY): ");
                    String startDate = sc.next();
                    startDate = startDate.substring(6) + startDate.substring(3, 5) + startDate.substring(0, 2);
                    System.out.print("Enter end date (DD-MM-YYYY): ");
                    String endDate = sc.next();
                    endDate = endDate.substring(6) + endDate.substring(3, 5) + endDate.substring(0, 2);
                    for (int i = 0; i < transactionCount; i++) {
                        String date = transactionDates[i].substring(6) + transactionDates[i].substring(3, 5) + transactionDates[i].substring(0, 2);
                        if (date.compareTo(startDate) > 0 && date.compareTo(endDate) < 0) {
                            filteredTransaction[i] = "filtered";
                        }
                    }
                    break;
                }case 2 -> {
                    System.out.print("\n-- Transaction type --\n1. Debit \n2. Credit\n\n>");
                    int type = sc.nextInt();
                    if (type == 1){
                        for (int i = 0; i < transactionCount; i++) {
                            if (credits[i] == 0)
                                filteredTransaction[i] = "filtered";
                        }
                    }else{
                        for (int i = 0; i < transactionCount; i++) {
                            if (debits[i] == 0)
                                filteredTransaction[i] = "filtered";
                        }
                    }
                    break;
                }case 3 -> {
                    System.out.print("\n-- Amount Range --\nEnter minimum amount: ");
                    double minAmount = sc.nextDouble();
                    System.out.print("Enter maximum amount: ");
                    double maxAmount = sc.nextDouble();
                    for (int i = 0; i < transactionCount; i++) {
                        double totalAmount = debits[i] + credits[i];
                        if (totalAmount >= minAmount && totalAmount <= maxAmount) {
                            filteredTransaction[i] = "filtered";
                        }
                    }
                    break;
                }default ->{
                    System.out.println("Invalid Input. Choose only 1, 2 or 3.");
                }
            }
        }while(filterChoice < 1 || filterChoice > 3);
    }

    public static void sortTransactions(Scanner sc, String[] transactionDates, String[] description, double[] debits, double[] credits, int transactionCount, String[] filteredTransaction) {
        int sortChoice;
        do{
            System.out.print("\n-- Sorting Options --\n1. By Date\n2. By Amount\n\n>");
            sortChoice = sc.nextInt();
            switch (sortChoice){
                case 1 ->{//by date
                    System.out.print("\n-- By Date --\n1. Sort from newest to oldest\n2. Sort from oldest to newest\n\n>");
                    int arrangement = sc.nextInt();
                    for (int pass = 0; pass < transactionCount; pass++) {
                        for (int i = 0; i < transactionCount-1; i++) {
                            String date1 = transactionDates[i].substring(6) + transactionDates[i].substring(3, 5) + transactionDates[i].substring(0, 2);
                            String date2 = transactionDates[i + 1].substring(6) + transactionDates[i + 1].substring(3, 5) + transactionDates[i + 1].substring(0, 2);

                            if (arrangement == 1 && date1.compareTo(date2) < 0 || arrangement == 2 && date1.compareTo(date2)>0){
                                swapAll(transactionDates, description, debits, credits, filteredTransaction, i, (i+1));
                            }
                        }
                    }
                }case 2 -> {//by amount
                    double amount1, amount2;
                    System.out.print("\n-- By Amount --\n1. Sort from highest to lowest amount\n2. Sort from lowest to highest amount\n\n>");
                    int arrangement = sc.nextInt();
                    for (int pass = 0; pass < transactionCount; pass++) {
                        for (int i = 0; i < transactionCount-1; i++) {
                            amount1 = credits[i] + debits[i];
                            amount2 = credits[i+1] + debits[i+1];
                            if (arrangement == 1 && amount1 < amount2 || arrangement == 2 && amount1 > amount2){
                                swapAll(transactionDates, description, debits, credits, filteredTransaction, i, (i+1));
                            }
                        }
                    }
                }default ->{
                    System.out.println("Invalid Input. Please select 1, 2 or 3.");
                }
            }
        }while(sortChoice < 1 || sortChoice > 3);
    }

    public static void printHistory(String[] transactionDates, String[] description, double[] debits, double[] credits, int transactionCount, String[] filteredTransaction) {
        System.out.printf("\n%-15s %-20s %-10s %-10s %-10s\n", "Date", "Description", "Debit", "Credit", "Balance");
        double balance = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (filteredTransaction[i] == null){
                balance += debits[i] - credits[i];
                System.out.printf("%-15s %-20s %-10.2f %-10.2f %-10.2f\n", transactionDates[i], description[i], debits[i], credits[i], balance);
            }else if (filteredTransaction[i].equals("filtered")) {
                balance += debits[i] - credits[i];
                System.out.printf("%-15s %-20s %-10.2f %-10.2f %-10.2f\n", transactionDates[i], description[i], debits[i], credits[i], balance);
            }
        }
    }
    
    public static void swapAll(String[] transactionDates, String[] description, double[] debits, double[] credits, String[] filteredTransaction, int i, int j){
        swap(transactionDates, i, j);
        swap(description, i, j);
        swap(debits, i, j);
        swap(credits, i, j);
        swap(filteredTransaction, i, j);
    }
    
    public static void swap(String[] arr, int i, int j) {
        String temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void swap(double[] arr, int i, int j) {
        double temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
