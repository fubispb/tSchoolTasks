
import java.util.*;

public class Calculator {

    private final char[] OPERATIONS = {'/', '*', '+', '-', '.'};
    private final char[] BRACKETS = {'(', ')'};
    private final char[] EXPECTED_CHARS = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '(', ')', '+', '-', '*', '/', '.'};

    public String evaluate(String str) {
        if (str == null || str.equals("") || !checkBrackets(str)) return null;
        if (hasUnexpectedSigns(str) && !hasDoubleOperations(str)) {
            if (checkDecimalOrInteger(str)) return evaluateDecimal(str);
            else if (!checkDecimalOrInteger(str)) return evaluateInteger(str);
        }
        return null;
    }

    private boolean hasDoubleOperations(String str) {
        for (int i = 0; i < str.length(); i++) {
            for (int j = 0; j < OPERATIONS.length; j++) {
                if (str.charAt(i) == OPERATIONS[j]) {
                    for (int k = 0; k < OPERATIONS.length; k++) {
                        if (str.charAt(i + 1) == OPERATIONS[k]) return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasUnexpectedSigns(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            for (int j = 0; j < EXPECTED_CHARS.length; j++) {
                if (str.charAt(i) == EXPECTED_CHARS[j]) {
                    count++;
                    break;
                }
            }
        }
        return count == str.length();
    }

    private boolean checkBrackets(String str) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) return false;
                if (stack.pop() != '(') return false;
            }
        }
        return stack.isEmpty();
    }

    // true = Decimal | false = Integer
    private boolean checkDecimalOrInteger(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '.') return true;
        }
        return false;
    }

    // true = digit | false = operation
    private boolean nextIsDigit(char sign) {
        for (int i = 0; i < OPERATIONS.length - 1; i++) {
            if (sign == OPERATIONS[i] || sign == '(' || sign == ')') return false;
        }
        return true;
    }

    private String evaluateInteger(String str) {
        StringBuilder buildDigits = new StringBuilder();
        Deque<Integer> stackDigits = new LinkedList<>();
        Deque<Character> stackOperations = new LinkedList<>();
        Integer result = 0;
        for (int i = 0; i < str.length(); i++) {
            if (nextIsDigit(str.charAt(i))) buildDigits.append(str.charAt(i));
            else if (str.charAt(i) == '(') {
                for (int j = i; j < str.length(); j++) {
                    if (str.charAt(j + 1) == ')') {
                        i = j;
                        break;
                    } else {
                        buildDigits.append(str.charAt(j + 1));
                    }
                }
                stackDigits.addLast(evaluateIntegerBrackets(buildDigits.toString()));
                buildDigits.delete(0, buildDigits.length());
                if (!stackOperations.isEmpty()) {
                    if (stackOperations.peekLast() == '/') {
                        if (stackDigits.peekLast() == 0) return null;
                        stackDigits.addLast(divisionInteger(stackDigits.pollLast(), stackDigits.pollLast()));
                        stackOperations.removeLast();
                        i++;
                    }
                }
                if (!stackOperations.isEmpty()) {
                    if (stackOperations.peekLast() == '*') {
                        stackDigits.addLast(multiplyInteger(stackDigits.pollLast(), stackDigits.pollLast()));
                        stackOperations.removeLast();
                        i++;
                    }
                }
            } else {
                if (buildDigits.length() != 0) {
                    stackDigits.addLast(Integer.parseInt(buildDigits.toString()));
                    buildDigits.delete(0, buildDigits.length());
                }
                if (stackDigits.size() > 1) {
                    if (!stackOperations.isEmpty()) {
                        if (stackOperations.peekLast() == '/') {
                            if (stackDigits.peekLast() == 0) return null;
                            stackDigits.addLast(divisionInteger(stackDigits.pollLast(), stackDigits.pollLast()));
                            stackOperations.removeLast();
                        }
                        if (!stackOperations.isEmpty()) {
                            if (stackOperations.peekLast() == '*') {
                                stackDigits.addLast(multiplyInteger(stackDigits.pollLast(), stackDigits.pollLast()));
                                stackOperations.removeLast();
                            }
                        }
                    }
                }
                stackOperations.addLast(str.charAt(i));
            }
        }
        if (buildDigits.length() != 0) stackDigits.addLast(Integer.parseInt(buildDigits.toString()));
        if (!stackOperations.isEmpty()) {
            if (stackOperations.peekLast() == '/') {
                if (stackDigits.peekLast() == 0) return null;
                stackDigits.addLast(divisionInteger(stackDigits.pollLast(), stackDigits.pollLast()));
                result = stackDigits.peekLast();
                stackOperations.removeLast();
            }
            if (!stackOperations.isEmpty()) {
                if (stackOperations.peekLast() == '*') {
                    stackDigits.addLast(multiplyInteger(stackDigits.pollLast(), stackDigits.pollLast()));
                    result = stackDigits.peekLast();
                    stackOperations.removeLast();
                }
            }
        }
        while (!stackOperations.isEmpty()) {
            Integer firstArgument = stackDigits.pollFirst();
            Integer secondArgument = stackDigits.pollFirst();
            switch (stackOperations.pollFirst()) {
                case '+':
                    result = firstArgument + secondArgument;
                    break;
                case '-':
                    result = firstArgument - secondArgument;
                    break;
                case '*':
                    result = firstArgument * secondArgument;
                    break;
                case '/':
                    if (secondArgument == 0) return null;
                    result = firstArgument / secondArgument;
                    break;
            }
            stackDigits.addFirst(result);
        }
        return result.toString();
    }

    private int evaluateIntegerBrackets(String str) {
        StringBuilder buildDigits = new StringBuilder();
        Deque<Integer> stackDigits = new LinkedList<>();
        Deque<Character> stackOperations = new LinkedList<>();
        int result = 0;
        for (int i = 0; i < str.length(); i++) {
            if (nextIsDigit(str.charAt(i))) buildDigits.append(str.charAt(i));
            else if (!nextIsDigit(str.charAt(i))) {
                stackOperations.addLast(str.charAt(i));
                stackDigits.addLast(Integer.parseInt(buildDigits.toString()));
                buildDigits.delete(0, buildDigits.length());
            }
        }
        stackDigits.addLast(Integer.parseInt(buildDigits.toString()));
        while (!stackOperations.isEmpty()) {
            Integer firstArgument = stackDigits.pollFirst();
            Integer secondArgument = stackDigits.pollFirst();
            switch (stackOperations.pollFirst()) {
                case '+':
                    result = firstArgument + secondArgument;
                    break;
                case '-':
                    result = firstArgument - secondArgument;
                    break;
                case '*':
                    result = firstArgument * secondArgument;
                    break;
                case '/':

                    result = firstArgument / secondArgument;
                    break;
            }
            stackDigits.addFirst(result);
        }
        return result;
    }

    private int multiplyInteger(int a, int b) {
        return a * b;
    }

    private int divisionInteger(int a, int b) {
        return b / a;
    }

    private double evaluateDecimalBrackets(String str) {
        StringBuilder buildDigits = new StringBuilder();
        Deque<Double> stackDigits = new LinkedList<>();
        Deque<Character> stackOperations = new LinkedList<>();
        double result = 0;
        for (int i = 0; i < str.length(); i++) {
            if (nextIsDigit(str.charAt(i))) buildDigits.append(str.charAt(i));
            else if (!nextIsDigit(str.charAt(i))) {
                stackOperations.addLast(str.charAt(i));
                stackDigits.addLast(Double.parseDouble(buildDigits.toString()));
                buildDigits.delete(0, buildDigits.length());
            }
        }
        stackDigits.addLast(Double.parseDouble(buildDigits.toString()));
        while (!stackOperations.isEmpty()) {
            Double firstArgument = stackDigits.pollFirst();
            Double secondArgument = stackDigits.pollFirst();
            switch (stackOperations.pollFirst()) {
                case '+':
                    result = firstArgument + secondArgument;
                    break;
                case '-':
                    result = firstArgument - secondArgument;
                    break;
                case '*':
                    result = firstArgument * secondArgument;
                    break;
                case '/':
                    if (secondArgument == 0) return 0;
                    result = firstArgument / secondArgument;
                    break;
            }
            stackDigits.addFirst(result);
        }
        return result;
    }

    private String evaluateDecimal(String str) {
        StringBuilder buildDigits = new StringBuilder();
        Deque<Double> stackDigits = new LinkedList<>();
        Deque<Character> stackOperations = new LinkedList<>();
        Double result = 0.0;
        for (int i = 0; i < str.length(); i++) {
            if (nextIsDigit(str.charAt(i))) buildDigits.append(str.charAt(i));
            else if (str.charAt(i) == '(') {
                for (int j = i; j < str.length(); j++) {
                    if (str.charAt(j + 1) == ')') {
                        i = j;
                        break;
                    } else {
                        buildDigits.append(str.charAt(j + 1));
                    }
                }
                stackDigits.addLast(evaluateDecimalBrackets(buildDigits.toString()));
                buildDigits.delete(0, buildDigits.length());
                for (int j = i; j < str.length(); j++) {
                    if (str.charAt(j) == ')') {
                        i = j;
                        break;
                    }
                }
            } else {
                stackOperations.addLast(str.charAt(i));
                if (!buildDigits.toString().equals("")) stackDigits.addLast(Double.parseDouble(buildDigits.toString()));
                buildDigits.delete(0, buildDigits.length());
            }
        }
        if (!buildDigits.toString().equals(")") && !buildDigits.toString().equals(""))
            stackDigits.addLast(Double.parseDouble(buildDigits.toString()));
        while (!stackOperations.isEmpty()) {
            Double firstArgument = stackDigits.pollFirst();
            Double secondArgument = stackDigits.pollFirst();
            switch (stackOperations.pollFirst()) {
                case '+':
                    result = firstArgument + secondArgument;
                    break;
                case '-':
                    result = firstArgument - secondArgument;
                    break;
                case '*':
                    result = firstArgument * secondArgument;
                    break;
                case '/':
                    if (secondArgument == 0) return null;
                    result = firstArgument / secondArgument;
                    break;
            }
            stackDigits.addFirst(result);
        }
        Double scale = Math.pow(10, 4);
        Double round = Math.ceil(result * scale) / scale;
        return round.toString();
    }
}