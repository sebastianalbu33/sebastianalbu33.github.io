import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class PointsSUT {

    // Simple Point class representing a point with integer x and y.
    public static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Converts the point to a string in the format "Point(x,y)"
        @Override
        public String toString() {
            return "Point(" + x + "," + y + ")";
        }

        // Parses a string (formats: "x,y" or "Point(x,y)") to create a Point object.
        public static Point fromString(String s) throws NumberFormatException {
            s = s.trim();
            // If the string starts with "Point(" and ends with ")", remove them.
            if (s.startsWith("Point(") && s.endsWith(")")) {
                s = s.substring("Point(".length(), s.length() - 1);
            }
            String[] parts = s.split(",");
            if (parts.length != 2) {
                throw new NumberFormatException("Expected format: x,y or Point(x,y)");
            }
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            return new Point(x, y);
        }

        // Returns true if both coordinates are non-zero.
        public boolean isValid() {
            return x != 0 && y != 0;
        }
    }

    // Transformation function:
    // If p.x < p.y, then p2 = (p.x+2, p.y-2)
    // If p.x > p.y, then p2 = (p.x-2, p.y+2)
    // If p.x == p.y, use the first rule.
    // If any coordinate of p2 becomes 0, adjust it by adding 1.
    public static Point transform(Point p) {
        int newX, newY;
        if (p.x <= p.y) {
            newX = p.x + 2;
            newY = p.y - 2;
        } else {
            newX = p.x - 2;
            newY = p.y + 2;
        }
        // Adjust if any coordinate becomes zero:
        if (newX == 0) {
            newX = (p.x <= p.y) ? p.x + 3 : p.x - 3;
        }
        if (newY == 0) {
            newY = (p.x <= p.y) ? p.y - 3 : p.y + 3;
        }
        return new Point(newX, newY);
    }

    public static void main(String[] args) {
        int port = 9999; // Default port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("Invalid port number; using default port 9999.");
            }
        }

        System.out.println("PointsSUT server starting on port " + port + "...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Received: " + line);
                try {
                    // Parse the received string to a Point
                    Point p1 = Point.fromString(line);
                    if (!p1.isValid()) {
                        System.out.println("Invalid point received: " + line);
                        out.println("ERROR");
                    } else {
                        // Compute transformed point p2.
                        Point p2 = transform(p1);
                        // Ensure the output point is valid (should be if transform() is correct)
                        if (!p2.isValid()) {
                            System.out.println("Transformed point is invalid: " + p2.toString());
                            out.println("ERROR");
                        } else {
                            System.out.println("Sending: " + p2.toString());
                            out.println(p2.toString());
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Parsing error: " + e.getMessage());
                    out.println("ERROR");
                }
            }
            System.out.println("Client disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
