package ds;

public class PatientHistoryList {

    // Private inner Node class - holds one report's details
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

    private Node head;   // oldest report
    private Node tail;   // newest report
    private int size;

    public PatientHistoryList() {
        head = null;
        tail = null;
        size = 0;
    }

    // addFirst - inserts at the very beginning (oldest end)
    public void addFirst(int reportId, String testName, double resultValue,
                         String resultStatus, String analysisDate) {
        Node newNode = new Node(reportId, testName, resultValue, resultStatus, analysisDate);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    // addLast - inserts at the end (newest end)
    // used when loading reports from the file/DB in oldest-to-newest order
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

    // deleteFirst - removes the oldest report from the list
    public void deleteFirst() {
        if (isEmpty()) {
            System.out.println("History list is empty.");
            return;
        }
        head = head.next;
        if (head == null) {
            tail = null;   // list is now empty, reset tail too
        } else {
            head.prev = null;
        }
        size--;
    }

    // display - shows history oldest to newest (head -> tail)
    public void display() {
        if (isEmpty()) {
            System.out.println("No report history available.");
            return;
        }
        Node current = head;
        System.out.println("---- Report History (Oldest to Newest) ----");
        while (current != null) {
            System.out.println("Report ID: " + current.reportId +
                    " | Test: " + current.testName +
                    " | Result: " + current.resultValue +
                    " | Status: " + current.resultStatus +
                    " | Date: " + current.analysisDate);
            current = current.next;
        }
        System.out.println("--------------------------------------------");
    }

    // displayFromLast - shows history newest to oldest (tail -> head)
    // this matches the order reports are written into the patient's .txt file
    public void displayFromLast() {
        if (isEmpty()) {
            System.out.println("No report history available.");
            return;
        }
        Node current = tail;
        System.out.println("---- Report History (Newest to Oldest) ----");
        while (current != null) {
            System.out.println("Report ID: " + current.reportId +
                    " | Test: " + current.testName +
                    " | Result: " + current.resultValue +
                    " | Status: " + current.resultStatus +
                    " | Date: " + current.analysisDate);
            current = current.prev;
        }
        System.out.println("--------------------------------------------");
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int getSize() {
        return size;
    }
}