# 🔐 Shamir's Secret Sharing Algorithm

A Java implementation of Shamir's Secret Sharing algorithm for the Catalog Placements Assignment. This project reconstructs secrets from polynomial shares encoded in various number bases.

## 📋 Problem Overview

This is a **simplified version** of Shamir's Secret Sharing where:
- Secrets are hidden as the constant term (y-intercept) of polynomials
- Shares are points on these polynomials, encoded in different number bases
- The goal is to reconstruct the original secret using polynomial interpolation

## 🧮 How It Works

### Mathematical Foundation
```
f(x) = a_m·x^m + a_{m-1}·x^{m-1} + ... + a_1·x + SECRET

Where:
- SECRET = f(0) (the constant term we want to find)
- m = k-1 (polynomial degree)
- k = minimum shares needed for reconstruction
```

### Algorithm Steps
1. **Parse JSON Input** - Extract share data and parameters
2. **Base Conversion** - Decode values from various bases to decimal
3. **Point Selection** - Use exactly k points for interpolation
4. **Lagrange Interpolation** - Reconstruct polynomial and find f(0)
5. **Output Secret** - Return the constant term

## 🚀 Quick Start

### Prerequisites
- Java 8 or higher
- No external dependencies required

### Compilation & Execution
```bash
javac CatalogAssignment.java
java CatalogAssignment
```

## 📊 Input Format

```json
{
  "keys": {
    "n": 4,          // Total shares provided
    "k": 3           // Minimum shares needed
  },
  "1": {
    "base": "10",
    "value": "4"
  },
  "2": {
    "base": "2", 
    "value": "111"   // Binary: 111₂ = 7₁₀
  },
  "3": {
    "base": "10",
    "value": "12"
  },
  "6": {
    "base": "4",
    "value": "213"   // Base-4: 213₄ = 39₁₀
  }
}
```

**Point Format:**
- **Key**: x-coordinate (share ID)
- **Base**: Number base for encoding (2-16 supported)
- **Value**: Encoded y-coordinate

## 🎯 Test Cases

### Test Case 1
- **Input**: n=4, k=3
- **Expected Output**: `3`
- **Points**: (1,4), (2,7), (3,12), (6,39)

### Test Case 2  
- **Input**: n=10, k=7
- **Expected Output**: `79836264049851`
- **Features**: Large numbers, multiple bases (3,6,7,8,12,15,16)

## 🔧 Implementation Details

### Base Conversion
```java
// Supports bases 2-16 with letters for digits > 9
private static BigInteger decodeFromBase(String value, int base) {
    BigInteger result = BigInteger.ZERO;
    BigInteger baseBI = BigInteger.valueOf(base);
    
    for (char c : value.toCharArray()) {
        result = result.multiply(baseBI);
        int digit = (c >= '0' && c <= '9') ? c - '0' : 
                   (c >= 'a' && c <= 'z') ? c - 'a' + 10 :
                   c - 'A' + 10;
        result = result.add(BigInteger.valueOf(digit));
    }
    return result;
}
```

### Lagrange Interpolation
```java
// Calculate f(0) using Lagrange interpolation formula
private static BigInteger findSecret(List<Point> points) {
    BigInteger secret = BigInteger.ZERO;
    
    for (int i = 0; i < points.size(); i++) {
        BigInteger xi = points.get(i).x;
        BigInteger yi = points.get(i).y;
        
        // Calculate Lagrange basis polynomial Li(0)
        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = BigInteger.ONE;
        
        for (int j = 0; j < points.size(); j++) {
            if (i != j) {
                BigInteger xj = points.get(j).x;
                numerator = numerator.multiply(xj.negate()); // (0 - xj)
                denominator = denominator.multiply(xi.subtract(xj)); // (xi - xj)
            }
        }
        
        BigInteger term = yi.multiply(numerator).divide(denominator);
        secret = secret.add(term);
    }
    
    return secret;
}
```

## 🔒 Security Analysis

| Scenario | Available Shares | Security Status | Result |
|----------|------------------|-----------------|---------|
| **< k shares** | Insufficient | 🔐 **SECURE** | Cannot crack - infinite possibilities |
| **= k shares** | Exact | ⚠️ **VULNERABLE** | Can reconstruct perfectly |
| **> k shares** | Redundant | ✅ **ROBUST** | Can reconstruct + detect corruption |

### Key Security Properties
- **Information-Theoretic Security**: Safe even against quantum computers
- **Perfect Threshold**: k-1 shares reveal zero information
- **Error Detection**: Extra shares help identify corrupted data

## 📁 Project Structure

```
shamir-secret-sharing/
├── CatalogAssignment.java    # Main implementation
├── README.md                 # This file
├── testcase1.json           # Test case 1 data
├── testcase2.json           # Test case 2 data
└── output.txt               # Expected results
```

## 🧪 Example Usage

### From Code
```java
public static void main(String[] args) {
    String jsonInput = "{\"keys\":{\"n\":4,\"k\":3},...}";
    BigInteger secret = solve(jsonInput);
    System.out.println("Secret: " + secret);
}
```

### Expected Output
```
Test Case 1 Secret: 3
Test Case 2 Secret: 79836264049851
```

## 🔍 Algorithm Complexity

- **Time Complexity**: O(k²) for Lagrange interpolation
- **Space Complexity**: O(n) for storing points
- **Number Precision**: Unlimited (using BigInteger)

## ✨ Features

- ✅ **Multi-base Support**: Handles bases 2-16
- ✅ **Large Numbers**: BigInteger for 40+ digit numbers  
- ✅ **No Dependencies**: Pure Java implementation
- ✅ **Error Handling**: Robust input validation
- ✅ **Clean Code**: Well-documented and modular

## 🤔 How Lagrange Interpolation Works

Given k points, the Lagrange interpolation formula is:

```
f(x) = Σ(yi × Li(x))  where Li(x) = ∏(x - xj)/(xi - xj) for j ≠ i
```

For finding the secret f(0):
```
f(0) = Σ(yi × Li(0))  where Li(0) = ∏(-xj)/(xi - xj) for j ≠ i
```

**Intuition**: Each Li(x) is a basis polynomial that equals 1 at xi and 0 at all other points. The weighted sum reconstructs the original polynomial.

## 🎓 Educational Value

This project demonstrates:
- **Cryptographic Concepts**: Secret sharing and threshold schemes
- **Number Theory**: Polynomial interpolation and modular arithmetic
- **Algorithm Design**: Efficient mathematical computation
- **Software Engineering**: Clean, maintainable Java code

## 📝 Assignment Requirements Met

- ✅ Read JSON input from file
- ✅ Decode Y values from multiple bases
- ✅ Calculate secret using polynomial interpolation
- ✅ Handle large numbers (up to 256-bit)
- ✅ No Python dependency
- ✅ Clean, documented code

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Submit a pull request

## 📜 License

This project is created for educational purposes as part of the Catalog Placements Assignment.

---

**💡 Pro Tip**: Understanding this algorithm gives insight into how secrets are distributed in real-world cryptographic systems, blockchain key management, and secure multi-party computation!