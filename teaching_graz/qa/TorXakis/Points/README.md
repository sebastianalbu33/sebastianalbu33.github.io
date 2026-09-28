# Points SUT – Model-Based Testing Example

This example demonstrates a model-based testing scenario using TorXakis and a Java server. The system under test (SUT) accepts a "Point" via a TCP socket, applies a transformation to that point, and then returns the transformed point. The TorXakis model specifies the expected behaviour of this transformation.

---

## Overview

### Java SUT: PointsSUT.java

- **Purpose:**  
  The Java server listens on TCP port 9999 for incoming connections. It receives a string representing a point, parses it, validates that the point is valid (i.e., both coordinates are non-zero), applies a transformation, and sends the transformed point back to the client.

- **Transformation Logic:**  
  The transformation function works as follows:
  - If the input point `p1` has `x ≤ y`, then:
    - `p2.x = p1.x + 2`
    - `p2.y = p1.y - 2`
  - Otherwise (if `p1.x > p1.y`):
    - `p2.x = p1.x - 2`
    - `p2.y = p1.y + 2`
  - If any coordinate of the transformed point becomes 0, it is adjusted (by adding 1 or subtracting further) to ensure that the point remains valid.

- **Input/Output Format:**  
  The points are represented as strings in the format:  
  ```
  Point(x,y)
  ```
  The server parses this string, applies the transformation, and echoes back the new point using the same format.

### TorXakis Model: Points.txs

- **Point Data Type:**  
  The model defines a custom `Point` type with two integer fields (`x` and `y`).

- **Validity Check:**  
  The function `validPoint` ensures that neither coordinate is zero.

- **Transformation Relation ("inRange"):**  
  The function `inRange` defines the relation between the input and output point:
  - The output point `p2` is in range of the input point `p1` if either:
    - `x(p1) < x(p2)` **and** `y(p1) > y(p2)`, or
    - `x(p1) > x(p2)` **and** `y(p1) < y(p2)`

- **Behaviour:**  
  The process `allowedBehaviour` expects:
  1. An input on channel `In` with a valid point `p1`.
  2. An output on channel `Out` with a valid point `p2` such that `p2` is in range of `p1`.
  
  The process then recurses.

- **Connections:**  
  Two connection definitions are provided:
  - **Sut:** A CLIENTSOCK connection that connects to the running Java SUT on `localhost:9999`.
  - **Sim:** A SERVERSOCK connection (if needed for simulation purposes).

  The connection uses built-in converters (`toString` and `fromString`) to convert between the Point type and its string representation.

---

## Prerequisites

- **Java JDK:** To compile and run the SUT.
- **TorXakis:** Installed and accessible via the command line.
- **Basic knowledge of TCP/IP:** For understanding client-server communication.

---

## TorXakis Installation
For installation help for TorXakis:

- Read into the [Installation Guide](https://torxakis.org/userdocs/stable/Installation.html#installationchapter
)
- Download [this](https://github.com/TorXakis/TorXakis/releases/download/v0.9.0/torxakis_0.9.0-ubuntu_20.04-amd64.deb) if you are on Debian Ubuntu
- In the folder where TorXakis has been downloaded, execute `sudo apt-get install ./torxakis_0.9.0-ubuntu_20.04-amd64.deb -y`
- Try typing `torxakis` in the terminal, if the installation worked you can then interact with TorX.
- More details in the [official docs](https://torxakis.org/userdocs/stable/getting-started.html).
- GLHF
---

## Files

- **PointsSUT.java**  
  Contains the Java implementation of the SUT.

- **Points.txs**  
  Contains the TorXakis model that defines the expected behaviour and the connection to the SUT.

- **README.md**  
  This file, which explains how the system works and how to test it.

---

## How to Build and Run the System

### 1. Compile and Run the Java SUT

1. Open a terminal in the project directory.
2. Compile the Java file:
   ```bash
   javac PointsSUT.java
   ```
3. Run the SUT on port 9999:
   ```bash
   java PointsSUT
   ```
   You should see output similar to:
   ```
   PointsSUT server starting on port 9999...
   ```
   The server will wait for a client connection.

### 2. Test with TorXakis

1. **Start the SUT:**  
   Ensure that the Java SUT is running (as described above).

2. **Run the TorXakis Model:**  
   Open a new terminal in the same directory as the `Points.txs` file and run:
   ```bash
   torxakis Points.txs
   ```
3. **Initialize the Tester:**  
   At the TorXakis prompt, initialize the tester by typing:
   ```
   TXS >> tester Model Sut
   ```
   This command tells TorXakis to use the model (`Model`) and connect to the SUT using the connection definition (`Sut`).

4. **Execute the Test:**  
   For example, to run 10 test steps (i.e., 5 complete input/output cycles), type:
   ```
   TXS >> test 10
   ```
   TorXakis will send points (on channel `In`) to the SUT and verify that the responses (on channel `Out`) satisfy the model, i.e., that they are valid points and that the output is in range relative to the input.

   Best is to start (and repeat) with:
   ```
   TXS >> test 2
   ```
   The `test` command sends input to the server and verifies the output. Replace `2` with the number of tests you wish to perform (e.g., `test 1000`). The number represents the steps including request to the server and response. So, you always should test an even number, because a full request + response needs 2 steps. See also the sample output at the bottom of the README for a better visualization.

5. **Observe the Output:**  
   - **In the TorXakis console:** You will see a trace of the actions performed, including the points sent and received.
   - **In the SUT console:** You will see the received point, the transformation applied, and the point sent back.

---

## How It Works – Detailed Explanation

1. **Input Handling:**  
   The SUT receives a string such as `Point(-1,3)`.  
   - The `fromString` method in Java handles both plain and prefixed formats (by removing the `Point(` prefix and `)` suffix if present).
   - The point is parsed into its `x` and `y` components.

2. **Validation:**  
   The SUT checks whether the received point is valid (i.e., both `x` and `y` are non-zero).

3. **Transformation:**  
   Based on the relation between the `x` and `y` coordinates:
   - If `x` is less than or equal to `y`, the point is transformed to `(x+2, y-2)`.
   - If `x` is greater than `y`, the point is transformed to `(x-2, y+2)`.
   - Additional adjustments are made to ensure that neither coordinate becomes zero.

4. **Output:**  
   The transformed point is converted back into a string (e.g., `Point(1,1)`) and sent back to the client.

5. **Model Verification:**  
   The TorXakis model defines what is considered acceptable behaviour:
   - The input point must be valid.
   - The output point must be valid and in range relative to the input point (as defined by the `inRange` function).
   - The test cycles through this behaviour repeatedly, verifying that the SUT adheres to the model.

6. **Mutate the SUT**
In case you want to see what happens if the SUT does not conform to the model, change the logic of the point handling and see if TorXakis catches any flaws in the SUT. (Simply modify the logic in the Java file for this)

---

## Summary

- **Java SUT:**  
  Implements a server that transforms incoming points according to a set rule and echoes the result. It ensures that all points are valid.

- **TorXakis Model:**  
  Defines the expected behaviour of the SUT. It checks that the input point is valid and that the output point is appropriately transformed (i.e., in range).

- **Testing Process:**  
  TorXakis connects to the running Java SUT, sends test inputs, and verifies the outputs against the model.


---

