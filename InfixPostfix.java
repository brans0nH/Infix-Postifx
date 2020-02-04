// Branson Hanzo
// Worked with BF

import java.util.*;

public class InfixPostfix {



	/**

	 * The comparator used to calculate operator precedence.

	 * Obeys PEMDAS.

	 *

	 * @returns 0 if the operators are of equal precedence.

	 * @returns -1 if operator2 is of lesser precedence to operator1.

	 * @returns 1 if operator2 is of greater precedence to operator1.

	 * @returns -2 if either operator1 or operator2 is not an operator character.

	 */

	public static final Comparator<Character> operatorComparator = (operator1, operator2) -> {

		if (!isOperator(operator1) || !isOperator(operator2)) return -2;



		if (operator1 == '(' || operator1 == ')') {

			if (operator2 == '(' || operator2 == ')') {

				return 0;

			} else {

				return -1;

			}

		} else if (operator1 == '^') {

			if (operator2 == '(' || operator2 == ')') {

				return 1;

			} else if (operator2 == '^') {

				return 0;

			} else {

				return -1;

			}

		} else if (operator1 == '*' || operator1 == '/') {

			if (operator2 == '(' || operator2 == ')') {

				return 1;

			} else if (operator2 == '*' || operator2 == '/') {

				return 0;

			} else {

				return -1;

			}

		} else if (operator1 == '+' || operator1 == '-') {

			if (operator2 == '(' || operator2 == ')' || operator2 == '*' || operator2 == '/') {

				return 1;

			} else if (operator2 == '+' || operator2 == '-') {

				return 0;

			}

		}



		return -2; //???

	};

	private static final String numberRegex = "[0-9]+", singleLetterRegex = "[a-zA-Z]";



	public static void main(String[] args) {

		//The input scanner

		final Scanner s = new Scanner(System.in);

		System.out.print("Enter the expression: ");

		//Read in the expression and split it into characters

		//final char[] expression = s.nextLine().toCharArray();

		final String expression = s.nextLine();

		final String[] expressionTokens = tokenize(expression);

		//A flag for when the expression contains variables that need to be replaced

		boolean hasVariables = false;

		final Queue<String> variables = new LinkedList<>();



		//Ensure the expression is balanced

		if (checkParentheses(expression.toCharArray())) {

			System.out.println("Expression is balanced");



			//The stringbuilder for the output string

			final StringBuilder outputQueue = new StringBuilder(expression.length());

			//The operator stack

			final Stack<Character> opStack = new Stack<>();



			//Iterate through each character in the expression

			for (String token : expressionTokens) {

				if (token.length() == 1) {

					char c = token.charAt(0);



					if (isOperator(c)) {

						if (c == '(') {

							opStack.push(c);

						} else if (c == ')') {

							//If the character is a right parenthesis, pop the stack until you reach its matching left parenthesis

							//and append each operator to the output queue

							char d;

							while ((d = opStack.pop()) != '(') {

								outputQueue.append(d).append(" ");

							}

						} else {

							//Traverse the stack in LIFO fashion and compare it to all operators on the stack

							//if an operator on the stack has equal or higher precedence than the new operator,

							//remove it from the stack and put it into the output queue

							//finally, push the new operator onto the op stack

							for (int j = opStack.size() - 1; j >= 0; j--) {

								char d = opStack.get(j);

								if (d == '(') break; //Stop when a left parenthesis is reached, indicating that

								//you have reached the end of a scope of operators



								int res = operatorComparator.compare(c, d);



								if (res >= 0) { //Equal or greater precedence

									outputQueue.append(d).append(" ");

									opStack.remove(j);

								}

							}



							opStack.push(c);

						}

					}

				}



				boolean isVariable = token.matches(singleLetterRegex);



				if (token.matches(numberRegex) || isVariable) {

					outputQueue.append(token).append(" ");



					if (isVariable && !opStack.contains(token.charAt(0))) {

						variables.add(token);

						hasVariables = true;

					}

				}

			}



			//Pop the remaining operators out of the stack

			while (!opStack.isEmpty()) {

				outputQueue.append(opStack.pop()).append(" ");

			}



			//A copy of the output queue that allows for variable replacement and expression evaluation

			String numericalExpression = outputQueue.toString();



			//Replace the variables with the values in the map if there are any



			//Output the expressions

			System.out.println("Postfix expression: " + outputQueue.toString());



			//Handle variable->number replacement

			if (hasVariables) {

				while (!variables.isEmpty()) {

					String var = variables.remove();

					System.out.print(String.format("%s=? ", var));

					numericalExpression = numericalExpression.replace(var, String.valueOf(s.nextInt()));

				}



				System.out.println("With replaced vars: " + numericalExpression);

			}



			System.out.println("Evaluated expression: " + evalPostfix(numericalExpression));

		} else {

			//Display error and exit program

			System.out.println("Expression is not balanced");

			System.exit(0);

		}

	}



	/**

	 * Checks the balance of an expression.

	 *

	 * @param expression the expression to test.

	 * @return true if the expression has an equal number of left and right parentheses.

	 * false if the expression has an unequal number of left and right parentheses.

	 */

	private static boolean checkParentheses(char[] expression) {

		final Stack<Character> stack = new Stack<>();



		for (char c : expression) {

			if (c == '(') { //Push all left parentheses onto the stack

				stack.push('(');

			} else if (c == ')') { //If you hit a right parenthesis, try and pop the stack

				if (!stack.isEmpty()) stack.pop();

				else return false; //if you can't pop the stack then there are too many right parentheses

			}

		}



		//If the stack isn't empty then there are too many left parentheses

		//if it is, then the expression is balanced

		return stack.isEmpty();

	}



	/**

	 * Evaluates postfix expressions.

	 *

	 * @param expression the expression to evaluate.

	 * @return the integer result of the single digit postfix expression.

	 */

	public static int evalPostfix(String expression) {

		Stack<Integer> stack = new Stack<>();

		String[] tokens = expression.split("\\s+");



		for (String token : tokens) {

			if (token.matches(numberRegex)) {

				stack.push(Integer.parseInt(token));

			} else if (isOperator(token)) {

				int i = stack.pop(), j = stack.pop();



				switch (token) {

					case "+":

						stack.push(j + i);

						break;



					case "-":

						stack.push(j - i);

						break;



					case "/":

						stack.push(j / i);

						break;



					case "*":

						stack.push(j * i);

						break;



					case "^":

						stack.push((int) Math.pow(j, i));

						break;

				}

			}

		}



		return stack.pop();

	}



	/**

	 * Tokenizes the expression in such a way that it splits the expression into operators

	 * and terms while preserving order and digits that span multiple characters.

	 * <p>

	 * For example, the expression: (A + B) * (C + D)

	 * when tokenized will be a string array as such: [(, A, +, B, ), *, (, C, +, D, )]

	 * And as another example, the expression: (300 + 400)

	 * will be tokenized as such: [(, 300, +, 400, )]

	 * <p>

	 * The important thing to realize is that this offers the advantage of splitting everything

	 * into a list of variables, operators (incl. parentheses), and variables, all while preserving

	 * digits that span multiple characters.

	 *

	 * @param expression the expression to tokenize.

	 * @return the tokenized expression. More examples (keep in mind the thing on the left is a string

	 * and the thing on the right is an array of strings):

	 * "A + B * C + D" -> [A, +, B, *, C, +, D]

	 * "(A + B) * (C + D)" -> [(, A, +, B, ), *, (, C, +, D, )]

	 * "A * B + C * D" -> [A, *, B, +, C, *, D]

	 * "400 * 300 + 200 * 100" -> [400, *, 300, +, 200, *, 100]

	 * "(400 * 300) + (200 * 100)" -> [(, 400, *, 300, ), +, (, 200, *, 100, )]

	 */

	public static String[] tokenize(String expression) {

		String delims = "()+-/*^"; //The delimiters to split the string on

		StringTokenizer tokenizer = new StringTokenizer(expression, delims, true);

		ArrayList<String> tokens = new ArrayList<>();



		String token;

		while (tokenizer.hasMoreTokens()) {

			token = tokenizer.nextToken().trim();

			if (!token.isEmpty()) {

				tokens.add(token);

			}

		}



		return tokens.toArray(new String[tokens.size()]);

	}



	/**

	 * Checks if a character is an arithmetic operator.

	 *

	 * @param c the character to check.

	 * @return true if the character is an arithmetic operator.

	 * false if the character is not an arithmetic operator.

	 */

	private static boolean isOperator(char c) {

		return c == '+' || c == '-' || c == '/' ||

				c == '*' || c == '(' || c == ')' ||

				c == '^';

	}



	/**

	 * Proxy method for {@see Main.isOperator()}

	 * allows strings to be checked for operator status

	 *

	 * @param s the string to check

	 * @return true if the character is an arithmetic operator.

	 * false if the character is not an arithmetic operator.

	 */

	private static boolean isOperator(String s) {

		return s.length() == 1 && isOperator(s.charAt(0));

	}

}