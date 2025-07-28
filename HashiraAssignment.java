import java.math.BigInteger;
import java.util.*;
import java.util.regex.*;

public class HashiraAssignment {
    
    static class Point {
        BigInteger x;
        BigInteger y;
        
        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
    
    public static void main(String[] args) {
        // Test Case 1
        String testCase1 = """
        {
            "keys": {
                "n": 4,
                "k": 3
            },
            "1": {
                "base": "10",
                "value": "4"
            },
            "2": {
                "base": "2",
                "value": "111"
            },
            "3": {
                "base": "10",
                "value": "12"
            },
            "6": {
                "base": "4",
                "value": "213"
            }
        }
        """;
        
        // Test Case 2
        String testCase2 = """
        {
            "keys": {
                "n": 10,
                "k": 7
            },
            "1": {
                "base": "6",
                "value": "13444211440455345511"
            },
            "2": {
                "base": "15",
                "value": "aed7015a346d63"
            },
            "3": {
                "base": "15",
                "value": "6aeeb69631c227c"
            },
            "4": {
                "base": "16",
                "value": "e1b5e05623d881f"
            },
            "5": {
                "base": "8",
                "value": "316034514573652620673"
            },
            "6": {
                "base": "3",
                "value": "2122212201122002221120200210011020220200"
            },
            "7": {
                "base": "3",
                "value": "20120221122211000100210021102001201112121"
            },
            "8": {
                "base": "6",
                "value": "20220554335330240002224253"
            },
            "9": {
                "base": "12",
                "value": "45153788322a1255483"
            },
            "10": {
                "base": "7",
                "value": "1101613130313526312514143"
            }
        }
        """;
        
        System.out.println("Test Case 1 Secret: " + solve(testCase1));
        System.out.println("Test Case 2 Secret: " + solve(testCase2));
    }
    
    public static BigInteger solve(String jsonInput) {
        List<Point> points = parseAndDecodePoints(jsonInput);
        
        int k = extractKValue(jsonInput);
        
        List<Point> selectedPoints = points.subList(0, k);
        
        return findSecret(selectedPoints);
    }
    
    private static int extractKValue(String jsonInput) {
        // Extract k value using regex
        Pattern pattern = Pattern.compile("\"k\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(jsonInput);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new RuntimeException("Could not find k value in JSON");
    }
    
    private static List<Point> parseAndDecodePoints(String jsonInput) {
        List<Point> points = new ArrayList<>();
        
        String cleanJson = jsonInput.replaceAll("\\s+", "");
        
        Pattern pointPattern = Pattern.compile("\"(\\d+)\":\\{\"base\":\"(\\d+)\",\"value\":\"([^\"]+)\"\\}");
        Matcher matcher = pointPattern.matcher(cleanJson);
        
        while (matcher.find()) {
            // Extract x, base, and value
            BigInteger x = new BigInteger(matcher.group(1));
            int base = Integer.parseInt(matcher.group(2));
            String value = matcher.group(3);
            
            // Decode y coordinate from the given base
            BigInteger y = decodeFromBase(value, base);
            
            points.add(new Point(x, y));
        }
        
        // Sort points by x coordinate for consistency
        points.sort((p1, p2) -> p1.x.compareTo(p2.x));
        
        return points;
    }
    
    private static BigInteger decodeFromBase(String value, int base) {
        // Handle different bases including those with letters (hex, etc.)
        BigInteger result = BigInteger.ZERO;
        BigInteger baseBI = BigInteger.valueOf(base);
        
        for (char c : value.toCharArray()) {
            result = result.multiply(baseBI);
            
            int digit;
            if (c >= '0' && c <= '9') {
                digit = c - '0';
            } else if (c >= 'a' && c <= 'z') {
                digit = c - 'a' + 10;
            } else if (c >= 'A' && c <= 'Z') {
                digit = c - 'A' + 10;
            } else {
                throw new IllegalArgumentException("Invalid character in value: " + c);
            }
            
            if (digit >= base) {
                throw new IllegalArgumentException("Digit " + digit + " is invalid for base " + base);
            }
            
            result = result.add(BigInteger.valueOf(digit));
        }
        
        return result;
    }
    
    // Lagrange interpolation to find f(0) - the secret
    private static BigInteger findSecret(List<Point> points) {
        BigInteger secret = BigInteger.ZERO;
        
        for (int i = 0; i < points.size(); i++) {
            BigInteger xi = points.get(i).x;
            BigInteger yi = points.get(i).y;
            
            // Calculate Lagrange basis polynomial L_i(0)
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger xj = points.get(j).x;
                    // For f(0): numerator *= (0 - xj) = -xj
                    numerator = numerator.multiply(xj.negate());
                    // denominator *= (xi - xj)
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }
            
            // Add yi * L_i(0) to result
            // L_i(0) = numerator / denominator
            BigInteger term = yi.multiply(numerator).divide(denominator);
            secret = secret.add(term);
        }
        
        return secret;
    }
    
    // For reading from file (optional utility method)
    public static String readFile(String filename) {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filename)));
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + filename, e);
        }
    }
}