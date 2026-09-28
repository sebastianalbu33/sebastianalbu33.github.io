# Calculator Testing Framework

This README explains how to set up, execute, and test a calculator system implemented with a Java server and a TorXakis model. The system supports operations like addition, subtraction, multiplication, and modulo.

## Prerequisites
- Java Development Kit (JDK)
- TorXakis installed and accessible from the command line

For installation help for TorXakis:

- Read into the [Installation Guide](https://torxakis.org/userdocs/stable/Installation.html#installationchapter
)
- Download [this](https://github.com/TorXakis/TorXakis/releases/download/v0.9.0/torxakis_0.9.0-ubuntu_20.04-amd64.deb) if you are on Debian Ubuntu
- In the folder where TorXakis has been downloaded, execute `sudo apt-get install ./torxakis_0.9.0-ubuntu_20.04-amd64.deb -y`
- Try typing `torxakis` in the terminal, if the installation worked you can then interact with TorX.
- More details in the [official docs](https://torxakis.org/userdocs/stable/getting-started.html).
- GLHF

---

## Steps to Execute

### 1. Compile and Run the Java Calculator Server
1. Compile the server:
   ```bash
   javac Calculator.java
   ```
2. Run the server on a specific port (e.g., 7890):
   ```bash
   java Calculator 7890
   ```
   The server listens for incoming requests and processes supported operations.

### 2. Run the TorXakis Model
1. Start TorXakis with the model file:
   ```bash
   torxakis Calculator.txs
   ```
2. Initialize the tester:
   ```
   TXS >> tester CalculatorModel SutConnection
   ```
   This command sets up the connection between the TorXakis model and the Java server.
3. Begin testing:
   ```
   TXS >> test 2
   ```
   The `test` command sends input to the server and verifies the output. Replace `2` with the number of tests you wish to perform (e.g., `test 1000`). The number represents the steps including request to the server and response. So, you always should test an even number, because a full request + response needs 2 steps. See also the sample output at the bottom of the README for a better visualization.

---

## Testing the Server Directly
You can also test the Java server independently using manual inputs:

1. Open a terminal and connect to the server using `telnet` or a similar tool:
   ```bash
   telnet localhost 7890
   ```
2. Send operations in the format:
   ```
   Add(5,3)
   ```
   Example inputs:
   - `Sub(10,4)`
   - `Mul(7,2)`
   - `Mod(9,4)`
3. Observe the server's response for each operation.

---

## Supported Operations
The calculator supports the following operations:
- **Addition** (`Add(x, y)`): Returns the sum of `x` and `y`.
- **Subtraction** (`Sub(x, y)`): Returns the difference of `x` and `y`.
- **Multiplication** (`Mul(x, y)`): Returns the product of `x` and `y`.
- **Modulo** (`Mod(x, y)`): Returns `x % y` with adjusted behavior for negative results:
  - If the result is negative and `y` is negative, the result is adjusted by adding `(-y)`.
  - If the result is negative and `y` is positive, the result is adjusted by adding `y`.

---

## How the Components Work
1. **Java Server**:
   - Listens on a specified port.
   - Processes arithmetic operations received in a specific format.
   - Responds with the computed result or an error for invalid input.

2. **TorXakis Model**:
   - Sends operations to the server.
   - Verifies that the server's responses match the expected results.

### Example Interaction
- Input from TorXakis: `Act { { ( Action, [ Mod(9,4) ] ) } }`
- Server response: `Res !(1)`
- TorXakis validation: Pass/Fail depending on whether the response matches the expected result.

---
 
 ## Example Output
 This is the example output in the terminal where TorXakis has been started after testing 5 requests:
 ```
 TXS >> test 10
TXS >>  .....1: IN: Act { { ( Action, [ Mod(-5,57) ] ) } }
TXS >>  .....2: OUT: Act { { ( Result, [ 52 ] ) } }
TXS >>  .....3: IN: Act { { ( Action, [ Mul(79,49) ] ) } }
TXS >>  .....4: OUT: Act { { ( Result, [ 3871 ] ) } }
TXS >>  .....5: IN: Act { { ( Action, [ Mod(-51,2) ] ) } }
TXS >>  .....6: OUT: Act { { ( Result, [ 1 ] ) } }
TXS >>  .....7: IN: Act { { ( Action, [ Add(94,35) ] ) } }
TXS >>  .....8: OUT: Act { { ( Result, [ 129 ] ) } }
TXS >>  .....9: IN: Act { { ( Action, [ Add(93,-81) ] ) } }
TXS >>  ....10: OUT: Act { { ( Result, [ 12 ] ) } }
TXS >>  PASS
```
This is the output in the terminal where the Java server has been started: 
```
Calculator on port 7890 received input: Mod(-5,57)
Modulo: -5 % 57 = 52
Result: 52
Calculator on port 7890 received input: Mul(79,49)
Multiplication: 79 * 49 = 3871
Result: 3871
Calculator on port 7890 received input: Mod(-51,2)
Modulo: -51 % 2 = 1
Result: 1
Calculator on port 7890 received input: Add(94,35)
Addition: 94 + 35 = 129
Result: 129
Calculator on port 7890 received input: Add(93,-81)
Addition: 93 + -81 = 12
Result: 12
```
