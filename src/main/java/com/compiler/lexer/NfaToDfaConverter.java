package com.compiler.lexer;

import java.util.*;
import com.compiler.lexer.dfa.DFA;
import com.compiler.lexer.dfa.DfaState;
import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;

/**
 * NfaToDfaConverter
 * -----------------
 * This class provides a static method to convert a Non-deterministic Finite Automaton (NFA)
 * into a Deterministic Finite Automaton (DFA) using the standard subset construction algorithm.
 */
/**
 * Utility class for converting NFAs to DFAs using the subset construction algorithm.
 */
public class NfaToDfaConverter {
	/**
	 * Default constructor for NfaToDfaConverter.
	 */
		public NfaToDfaConverter() {
			// TODO: Implement constructor if needed
		}

	/**
	 * Converts an NFA to a DFA using the subset construction algorithm.
	 * Each DFA state represents a set of NFA states. Final states are marked if any NFA state in the set is final.
	 *
	 * @param nfa The input NFA
	 * @param alphabet The input alphabet (set of characters)
	 * @return The resulting DFA
	 */
	public static DFA convertNfaToDfa(NFA nfa, Set<Character> alphabet) {
		// TODO: Implement convertNfaToDfa
		/*
		 Pseudocode:
		 1. Create initial DFA state from epsilon-closure of NFA start state
		 2. While there are unmarked DFA states:
			  - For each symbol in alphabet:
				  - Compute move and epsilon-closure for current DFA state
				  - If target set is new, create new DFA state and add to list/queue
				  - Add transition from current to target DFA state
		 3. Mark DFA states as final if any NFA state in their set is final
		 4. Return DFA with start state and all DFA states
		*/
		Queue<DfaState> unmarked = new LinkedList<>();
		List<DfaState> allStates = new ArrayList<>();
		
		Set<State> aux = new HashSet<>();
		aux.add(nfa.getStartState());
		Set<State> initialDfaState = epsilonClosure(aux);

		DfaState start = new DfaState(initialDfaState);
		allStates.add(start);
		unmarked.add(start);

		while(!unmarked.isEmpty()){
			DfaState current = unmarked.poll();
			for(char symbol : alphabet){
				Set<State> moveRest = move(current.getNfaStates(), symbol);
				Set<State> target = epsilonClosure(moveRest);
				if(target != null) {
					DfaState newState = findDfaState(allStates, target);
					if(newState == null) {		// Real new state
						newState = new DfaState(target);
						allStates.add(newState);
						unmarked.add(newState);
					}
					current.addTransition(symbol, newState);
				}
			}
		}

		for(DfaState state : allStates) {
			for(State nfaState : state.getNfaStates()) {
				if(nfaState.isFinal()) {
					state.setFinal(true);
					break; 
				}
			}
		}

		return new DFA(start, allStates);
	}

	/**
	 * Computes the epsilon-closure of a set of NFA states.
	 * The epsilon-closure is the set of states reachable by epsilon (null) transitions.
	 *
	 * @param states The set of NFA states.
	 * @return The epsilon-closure of the input states.
	 */
	private static Set<State> epsilonClosure(Set<State> states) {
	// TODO: Implement epsilonClosure
	/*
	 Pseudocode:
	 1. Initialize closure with input states
	 2. Use stack to process states
	 3. For each state, add all reachable states via epsilon transitions
	 4. Return closure set
	*/
		if(states == null)
			return null;
		Set<State> closure = new HashSet<>();
		Stack<State> stack = new Stack<>();
		
		closure.addAll(states);
		stack.addAll(states);

		while(!stack.isEmpty()){
			State current = stack.pop();
      for (State eState : current.getEpsilonTransitions()) {
      	if (!closure.contains(eState)) {
        	closure.add(eState);
          stack.push(eState);
        }
      }
		}

		return closure;
	}

	/**
	 * Returns the set of states reachable from a set of NFA states by a given symbol.
	 *
	 * @param states The set of NFA states.
	 * @param symbol The input symbol.
	 * @return The set of reachable states.
	 */
	private static Set<State> move(Set<State> states, char symbol) {
		// TODO: Implement move
		/*
		 Pseudocode:
		 1. For each state in input set:
			  - For each transition with given symbol:
				  - Add destination state to result set
		 2. Return result set
		*/
		Set<State> result = new HashSet<>();

		for(State state : states){
			for(State transitionState : state.getTransitions(symbol)) {
				result.add(transitionState);
			}
		}

		return result;
	}

	/**
	 * Finds an existing DFA state representing a given set of NFA states.
	 *
	 * @param dfaStates The list of DFA states.
	 * @param targetNfaStates The set of NFA states to search for.
	 * @return The matching DFA state, or null if not found.
	 */
	private static DfaState findDfaState(List<DfaState> dfaStates, Set<State> targetNfaStates) {
	   // TODO: Implement findDfaState
	   /*
	    Pseudocode:
	    1. For each DFA state in list:
		    - If its NFA state set equals target set, return DFA state
	    2. If not found, return null
	   */
		for(DfaState state : dfaStates) {
			if(state.getNfaStates().equals(targetNfaStates))
				return state;
		}
		return null;
	}
}
