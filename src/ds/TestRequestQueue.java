package ds;

public class TestRequestQueue {

    private class Node {
        int testRequestId;
        String patientName;
        String testName;
        String priority;
        Node next;

        Node(int testRequestId, String patientName, String testName, String priority) {
            this.testRequestId = testRequestId;
            this.patientName = patientName;
            this.testName = testName;
            this.priority = priority;
            this.next = null;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    public TestRequestQueue() {
        front = null;
        rear = null;
        size = 0;
    }


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

    public void enqueue(int testRequestId, String patientName, String testName, String priority) {
        if (priority.equalsIgnoreCase("EMERGENCY")) {
            insertAtFront(testRequestId, patientName, testName, priority);
        } else {
            insertAtRear(testRequestId, patientName, testName, priority);
        }
    }

    public String[] deleteFromFrontWithDetails() {
        if (isEmpty()) {
            System.out.println("No pending test requests.");
            return null;
        }

        String[] details = new String[] {
                String.valueOf(front.testRequestId),
                front.patientName,
                front.testName,
                front.priority
        };

        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;

        return details;
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        Node current = front;
        System.out.println("---- Current Test Request Queue ----");
        System.out.println("-".repeat(100));
        System.out.printf("%-6s %-25s %-35s %-15s%n",
                "No.", "Patient Name", "Test Name", "Priority");
        System.out.println("-".repeat(100));
        int srNo = 1;
        while(current !=null){
            System.out.printf("%-6d %-25s %-35s %-15s%n",srNo++,
                    current.patientName , current.testName,current.priority);
            current = current.next;
        }
        System.out.println("-".repeat(100));

    }

    public boolean isEmpty() {
        return front == null;
    }

}