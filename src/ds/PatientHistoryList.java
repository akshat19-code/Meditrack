package ds;

public class PatientHistoryList {

    private class Node {
        int reportId;
        String testName;
        double resultValue;
        String resultStatus;
        String analysisDate;
        Node next;
        Node prev;

        Node(int reportId, String testName, double resultValue,
             String resultStatus, String analysisDate) {
            this.reportId = reportId;
            this.testName = testName;
            this.resultValue = resultValue;
            this.resultStatus = resultStatus;
            this.analysisDate = analysisDate;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public PatientHistoryList() {
        head = null;
        tail = null;
        size = 0;
    }


    public void addLast(int reportId, String testName, double resultValue,
                        String resultStatus, String analysisDate) {
        Node newNode = new Node(reportId, testName, resultValue, resultStatus, analysisDate);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public void displayFromLast() {
        if (isEmpty()) {
            System.out.println("No report history available.");
            return;
        }
        Node current = tail;
        System.out.println("---- Report History (Newest to Oldest) ----");
        System.out.println("-".repeat(100));
        System.out.printf("%-6s %-35s %-15s %-15s %-15s%n",
                "No.", "Test", "Result", "Status" , "Date");
        System.out.println("-".repeat(100));
        int srNo = 1;
        while(current !=null){
            System.out.printf("%-6d %-35s %-15s %-15s %-15s%n",srNo++,
                current.testName,String.format("%.2f", current.resultValue),
            current.resultStatus,current.analysisDate);
            current = current.prev;
        }
        System.out.println("-".repeat(100));
    }

    public boolean isEmpty() {
        return head == null;
    }

}