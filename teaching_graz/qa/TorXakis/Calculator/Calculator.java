import java.net.*;
import java.io.*;
import java.util.Random;

public class Calculator implements Runnable {
    private int portNr;

    Calculator(String portNrStr) {
        this.portNr = Integer.parseInt(portNrStr);
    }

    public void run() {
        startCalculator();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Port number required");
            return;
        }
        // Start calculator servers in parallel for each port number
        String msg = String.format("Starting %d calculator servers.", args.length);
        System.out.println(msg);
        for (String arg : args) {
            (new Thread(new Calculator(arg))).start();
        }
    }

    private void startCalculator() {
        System.out.println(String.format("Starting a calculator listening on port %d", portNr));
        try {
            try (ServerSocket serverSock = new ServerSocket(portNr)) {
                Socket sock = serverSock.accept();

                InputStream inStream = sock.getInputStream();
                BufferedReader sockIn = new BufferedReader(new InputStreamReader(inStream));

                OutputStream outStream = sock.getOutputStream();
                PrintWriter sockOut = new PrintWriter(new OutputStreamWriter(outStream));

                final int maxSleepTime = 180; // Simulate some delay in processing

                while (true) {
                    try {
                        String s = sockIn.readLine();
                        if (s == null) {
                            System.out.println("Connection closed by client.");
                            break; // Exit loop if the client disconnects
                        }
                
                        processInput(s, sockOut, maxSleepTime);
                    } catch (IOException ex) {
                        System.out.println("IO error: " + ex.getMessage());
                        break; // Exit loop if there's an IO error
                    } catch (Exception ex) {
                        System.out.println("Error processing input: " + ex.getMessage());
                        sockOut.print("ERROR\n");
                        sockOut.flush();
                    }
                }
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processInput(String s, PrintWriter sockOut, int maxSleepTime) throws InterruptedException {
        s = s.trim();
        System.out.println(String.format("Calculator on port %d received input: %s", portNr, s));
    
        // Updated regex to handle negative numbers
        String regex = "(\\w+)\\((-?\\d+),(-?\\d+)\\)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(s);
    
        if (!matcher.matches()) {
            System.out.println("Invalid input format: " + s);
            sockOut.print("ERROR\n");
            sockOut.flush();
            return;
        }
    
        String operation = matcher.group(1); // Operation name, e.g., Sub
        int x = Integer.parseInt(matcher.group(2)); // First operand
        int y = Integer.parseInt(matcher.group(3)); // Second operand
    
        Random random = new Random();
        Thread.sleep(random.nextInt(maxSleepTime)); // Simulate delay
    
        int result = 0;
        try {
            switch (operation) {
                case "Add":
                    result = x + y;
                    System.out.println("Addition: " + x + " + " + y + " = " + result);
                    break;
                case "Sub":
                    result = x - y;
                    System.out.println("Subtaction: " + x + " - " + y + " = " + result);
                    break;
                case "Mul":
                    result = x * y;
                    System.out.println("Multiplication: " + x + " * " + y + " = " + result);
                    break;
                case "Mod":
                    if(y == 0) {
                        result = 0;
                        break;
                    }
                    result = x % y;
                    if (result < 0 && y < 0) {
                        result += (-1 * y);
                    }
                    else if (result < 0 && y > 0) {
                        result += y;
                    }
                    System.out.println("Modulo: " + x + " % " + y + " = " + result);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + operation);
            }
            printResult(sockOut, result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sockOut.print("ERROR\n");
            sockOut.flush();
        }
    }
    
    
    private void printResult(PrintWriter sockOut, int result) {
        sockOut.print(" " + result + "\n");
        sockOut.flush();
        System.out.println("Result: " + result);
    }
}
