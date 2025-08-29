package com.compiler.lexer.regex;

import java.util.Stack;

import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;
import com.compiler.lexer.nfa.Transition;

/**
 * RegexParser
 * -----------
 * This class provides functionality to convert infix regular expressions into nondeterministic finite automata (NFA)
 * using Thompson's construction algorithm. It supports standard regex operators: concatenation (·), union (|),
 * Kleene star (*), optional (?), and plus (+). The conversion process uses the Shunting Yard algorithm to transform
 * infix regex into postfix notation, then builds the corresponding NFA.
 *
 * Features:
 * - Parses infix regular expressions and converts them to NFA.
 * - Supports regex operators: concatenation, union, Kleene star, optional, plus.
 * - Implements Thompson's construction rules for NFA generation.
 *
 * Example usage:
 * <pre>
 *     RegexParser parser = new RegexParser();
 *     NFA nfa = parser.parse("a(b|c)*");
 * </pre>
 */
/**
 * Parses regular expressions and constructs NFAs using Thompson's construction.
 */
public class RegexParser {
    /**
     * Default constructor for RegexParser.
     */
        public RegexParser() {
        }

    /**
     * Converts an infix regular expression to an NFA.
     *
     * @param infixRegex The regular expression in infix notation.
     * @return The constructed NFA.
     */
    public NFA parse(String infixRegex) {
        // Pseudocode: Convert infix to postfix, then build NFA from postfix
        return this.buildNfaFromPostfix(ShuntingYard.toPostfix(infixRegex));
    }

    /**
     * Builds an NFA from a postfix regular expression.
     *
     * @param postfixRegex The regular expression in postfix notation.
     * @return The constructed NFA.
     */
    private NFA buildNfaFromPostfix(String postfixRegex) {
        // Pseudocode: For each char in postfix, handle operators and operands using a stack
        Stack<NFA> stack = new Stack<>();
        for(char c : postfixRegex.toCharArray()){
            //char ch = c.charValue();
            if(this.isOperand(c)){
                stack.push(this.createNfaForCharacter(c));
            } else if (c == '|') {
                this.handleUnion(stack);
            } else if (c == '·') {
                this.handleConcatenation(stack);
            } else if (c == '*') {
                this.handleKleeneStar(stack);
            } else if (c == '+') {
                this.handlePlus(stack);
            } else if (c == '?'){
                this.handleOptional(stack);
            }
        }
        return stack.pop();
    }

    /**
     * Handles the '?' operator (zero or one occurrence).
     * Pops an NFA from the stack and creates a new NFA that accepts zero or one occurrence.
     * @param stack The NFA stack.
     */
    private void handleOptional(Stack<NFA> stack) {
        // Pseudocode: Pop NFA, create new start/end, add epsilon transitions for zero/one occurrence
        NFA nfa = stack.pop();

        State q0 = new State();
        State qf = new State();

        nfa.getEndState().setFinal(false);

        nfa.getEndState().addTransition(new Transition(null, qf));
        q0.addTransition(new Transition(null, nfa.getStartState()));
        q0.addTransition(new Transition(null, qf));

        stack.push(new NFA(q0, qf));
    }

    /**
     * Handles the '+' operator (one or more occurrences).
     * Pops an NFA from the stack and creates a new NFA that accepts one or more occurrences.
     * @param stack The NFA stack.
     */
    private void handlePlus(Stack<NFA> stack) {
        // Pseudocode: Pop NFA, create new start/end, add transitions for one or more occurrence
        NFA nfa = stack.pop();

        State q0 = new State();
        State qf = new State();
        
        nfa.getEndState().setFinal(false);

        nfa.getEndState().addTransition(new Transition(null, nfa.getStartState()));
        nfa.getEndState().addTransition(new Transition(null, qf));
        q0.addTransition(new Transition(null, nfa.getStartState()));
        
        stack.push(new NFA(q0, qf));
    }
    
    /**
     * Creates an NFA for a single character.
     * @param c The character to create an NFA for.
     * @return The constructed NFA.
     */
    private NFA createNfaForCharacter(char c) {
        // Pseudocode: Create start/end state, add transition for character
        State A = new State();
        State B = new State();
        
        A.addTransition(new Transition(c, B));
        
        return new NFA(A, B);
    }

    /**
     * Handles the concatenation operator (·).
     * Pops two NFAs from the stack and connects them in sequence.
     * @param stack The NFA stack.
     */
    private void handleConcatenation(Stack<NFA> stack) {
        // Pseudocode: Pop two NFAs, connect end of first to start of second
        NFA nfaB = stack.pop();
        NFA nfaA = stack.pop();

        nfaA.getEndState().setFinal(false);
        nfaA.getEndState().addTransition(new Transition(null, nfaB.getStartState()));
        stack.push(new NFA(nfaA.getStartState(), nfaB.getEndState()));
    }

    /**
     * Handles the union operator (|).
     * Pops two NFAs from the stack and creates a new NFA that accepts either.
     * @param stack The NFA stack.
     */
    private void handleUnion(Stack<NFA> stack) {
        // Pseudocode: Pop two NFAs, create new start/end, add epsilon transitions for union
        NFA nfaB = stack.pop();
        NFA nfaA = stack.pop();

        State q0 = new State();
        State qf = new State();
        
        nfaA.getEndState().setFinal(false);
        nfaB.getEndState().setFinal(false);

        q0.addTransition(new Transition(null, nfaA.getStartState()));
        q0.addTransition(new Transition(null, nfaB.getStartState()));
        nfaA.getEndState().addTransition(new Transition(null, qf));
        nfaB.getEndState().addTransition(new Transition(null, qf));
        
        stack.push(new NFA(q0, qf));
    }

    /**
     * Handles the Kleene star operator (*).
     * Pops an NFA from the stack and creates a new NFA that accepts zero or more repetitions.
     * @param stack The NFA stack.
     */
    private void handleKleeneStar(Stack<NFA> stack) {
        // Pseudocode: Pop NFA, create new start/end, add transitions for zero or more repetitions
        NFA nfa = stack.pop();

        State q0 = new State();
        State qf = new State();
        
        nfa.getEndState().setFinal(false);

        nfa.getEndState().addTransition(new Transition(null, nfa.getStartState()));
        nfa.getEndState().addTransition(new Transition(null, qf));
        q0.addTransition(new Transition(null, nfa.getStartState()));
        q0.addTransition(new Transition(null, qf));
        
        stack.push(new NFA(q0, qf));
    }

    /**
     * Checks if a character is an operand (not an operator).
     * @param c The character to check.
     * @return True if the character is an operand, false if it is an operator.
     */
    private boolean isOperand(char c) {
        // Pseudocode: Return true if c is not an operator
        return !Character.valueOf(c).toString().matches("[|*?+()·]");
    }
}
