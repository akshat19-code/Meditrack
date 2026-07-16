package ds;

public class TestRequestQueue {

    // Private inner Node class
    private class Node {
        int testRequestId;
        String patientName;
        String testName;
        String priority;   // "NORMAL" or "EMERGENCY"
        Node next;

        Node(int testRequestId, String patientName, String testName, String priority) {
            this.testRequestId = testRequestId;
            this.patientName = patientName;
            this.testName = testName;
            this.priority = priority;
            this.next = null;
        }
    }

    private Node front;   // Lab Technician always processes from here
    private Node rear;    // new NORMAL requests join here
    private int size;

    public TestRequestQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    // insertAtFront - used for EMERGENCY requests, so they jump ahead of everyone else
    private void insertAtFront(int testRequestId, String patientName, String testName, String priority) {
        Node newNode = new Node(testRequestId, patientName, testName, priority);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            newNode.next = front;
            front = newNode;
        }
        size++;
    }

    // insertAtRear - used for NORMAL requests, they join the back of the line
    private void insertAtRear(int testRequestId, String patientName, String testName, String priority) {
        Node newNode = new Node(testRequestId, patientName, testName, priority);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    // enqueue - the single entry point every Doctor request goes through.
    // Checks priority and decides where the request goes.
    public void enqueue(int testRequestId, String patientName, String testName, String priority) {
        if (priority.equalsIgnoreCase("EMERGENCY")) {
            insertAtFront(testRequestId, patientName, testName, priority);
            System.out.println("EMERGENCY request added - jumped to front of queue.");
        } else {
            insertAtRear(testRequestId, patientName, testName, priority);
            System.out.println("NORMAL request added - joined the back of queue.");
        }
    }

    // deleteFromFront - called by Lab Technician when picking up the next request to process
    public int deleteFromFront() {
        if (isEmpty()) {
            System.out.println("No pending test requests.");
            return -1;
        }
        int processedId = front.testRequestId;
        System.out.println("Processing -> Patient: " + front.patientName +
                " | Test: " + front.testName +
                " | Priority: " + front.priority);

        front = front.next;
        if (front == null) {
            rear = null;   // queue is now empty, reset rear too
        }
        size--;
        return processedId;
    }

    // peek - view the next request to be processed, without removing it
    public void peek() {
        if (isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        System.out.println("Next in line -> Patient: " + front.patientName +
                " | Test: " + front.testName +
                " | Priority: " + front.priority +
                " | Request ID: " + front.testRequestId);
    }

    // display - show the entire queue, front to rear
    public void display() {
        if (isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        Node current = front;
        System.out.println("---- Current Test Request Queue ----");
        while (current != null) {
            System.out.println("Request ID: " + current.testRequestId +
                    " | Patient: " + current.patientName +
                    " | Test: " + current.testName +
                    " | Priority: " + current.priority);
            current = current.next;
        }
        System.out.println("-------------------------------------");
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int getSize() {
        return size;
    }
}