package models;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

import utils.Engine;

/**
 * Hold the model of an automata
 */
public class Automata {
	protected String name;
	protected ArrayList<State> statesList;
	protected ArrayList<Transition> transitionsList;
	protected String initialStateId;
	protected ArrayList<String> declarationsList;

	// variables declarations will stay here
	protected Hashtable<String, IntVariable> intVariablesList;
	protected Hashtable<String, BooleanVariable> boolVariablesList;
	protected Hashtable<String, ClockVariable> clockVariablesList;

	// hold the engine for computations
	protected Engine engine;

	public Automata() {
		this.name = "automata_" + UUID.randomUUID().toString();

		this.statesList = new ArrayList<State>();
		this.transitionsList = new ArrayList<Transition>();
		this.declarationsList = new ArrayList<String>();

		this.intVariablesList = new Hashtable<String, IntVariable>();
		this.boolVariablesList = new Hashtable<String, BooleanVariable>();
		this.clockVariablesList = new Hashtable<String, ClockVariable>();

		this.engine = new Engine();
	}

	public Automata(String name) {
		this.name = name;

		this.statesList = new ArrayList<State>();
		this.transitionsList = new ArrayList<Transition>();
		this.declarationsList = new ArrayList<String>();

		this.intVariablesList = new Hashtable<String, IntVariable>();
		this.boolVariablesList = new Hashtable<String, BooleanVariable>();
		this.clockVariablesList = new Hashtable<String, ClockVariable>();

		this.engine = new Engine();
	}

	public void addState(State state) {
		this.statesList.add(state);
	}

	public void addTransition(Transition transition) {
		this.transitionsList.add(transition);
	}

	public void addDeclaration(String declaration) {
		this.declarationsList.add(declaration);
	}

	public Transition findTransition(String transitionId) {
		for (int i = 0; i < this.transitionsList.size(); i++) {
			if (((Transition) this.transitionsList.get(i)).getTransitionId().equals(transitionId)) {
				return this.transitionsList.get(i);
			}
		}
		return null;
	}

	public State findState(String stateId) {
		for (int i = 0; i < this.statesList.size(); i++) {
			if (((State) this.statesList.get(i)).getStateId().equals(stateId)) {
				return this.statesList.get(i);
			}
		}
		return null;
	}

	public ArrayList<Transition> findIncomingTransitions(String stateId) {
		ArrayList<Transition> incomingTransitionList = new ArrayList<>();
		Transition transition = null;

		for (int i = 0; i < this.transitionsList.size(); i++) {
			transition = this.transitionsList.get(i);

			if (transition.getTargetStateId().equals(stateId)) {
				incomingTransitionList.add(transition);
			}
		}
		return incomingTransitionList;
	}

	public ArrayList<State> findIncomingStates(String stateId) {
		ArrayList<State> incomingStatesList = new ArrayList<>();
		Transition transition = null;

		for (int i = 0; i < this.transitionsList.size(); i++) {
			transition = this.transitionsList.get(i);

			if (transition.getTargetStateId().equals(stateId)) {
				incomingStatesList.add(findState(transition.getSourceStateId()));
			}
		}
		return incomingStatesList;
	}

	public ArrayList<Transition> findOutgoingTransitions(String stateId) {
		ArrayList<Transition> outgoingTransitionList = new ArrayList<>();
		Transition transition = null;

		for (int i = 0; i < this.transitionsList.size(); i++) {

			transition = this.transitionsList.get(i);

			if (transition.getSourceStateId().equals(stateId)) {
				outgoingTransitionList.add(transition);
			}
		}
		return outgoingTransitionList;
	}

	public ArrayList<State> findOutgoingStates(String stateId) {
		ArrayList<State> outgoingStatesList = new ArrayList<>();
		Transition transition = null;

		for (int i = 0; i < this.transitionsList.size(); i++) {

			transition = this.transitionsList.get(i);

			if (transition.getSourceStateId().equals(stateId)) {
				outgoingStatesList.add(findState(transition.getTargetStateId()));
			}
		}
		return outgoingStatesList;
	}

	public boolean evaluateCondition(String condition) {
		return this.engine.evaluateCondition(condition.trim());
	}

	public void executeUpdates(String statement) {
		//
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<State> getStatesList() {
		return statesList;
	}

	public void setStatesList(ArrayList<State> statesList) {
		this.statesList = statesList;
	}

	public ArrayList<Transition> getTransitionsList() {
		return transitionsList;
	}

	public void setTransitionsList(ArrayList<Transition> transitionsList) {
		this.transitionsList = transitionsList;
	}

	public void setInitialStateId(String initialStateId) {
		this.initialStateId = initialStateId;
	}

	public String getInitialStateId() {
		return initialStateId;
	}

	public void setDeclarationsList(ArrayList<String> declarationsList) {
		this.declarationsList = declarationsList;
	}

	public ArrayList<String> getDeclarationsList() {
		return declarationsList;
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	// variables handling

	public void addIntVariable(String name) {
		this.intVariablesList.put(name, new IntVariable(name));
		this.engine.setVariable(name, 0);
	}

	public void addIntVariable(String name, int value) {
		this.intVariablesList.put(name, new IntVariable(name, value));
		this.engine.setVariable(name, value);
	}

	public IntVariable findIntVariable(String name) {
		return this.intVariablesList.get(name);
	}

	public void addBooleanVariable(String name) {
		this.boolVariablesList.put(name, new BooleanVariable(name));
		this.engine.setVariable(name, true);
	}
	
	public void addBooleanVariable(String name, boolean value) {
		this.boolVariablesList.put(name, new BooleanVariable(name, value));
		this.engine.setVariable(name, value);
	}

	public BooleanVariable findBooleanVariable(String name) {
		return this.boolVariablesList.get(name);
	}

	public void addClockVariable(String name) {
		this.clockVariablesList.put(name, new ClockVariable(name));
		this.engine.setVariable(name, 0);
	}

	public void addClockVariable(String name, long value) {
		this.clockVariablesList.put(name, new ClockVariable(name, value));
		this.engine.setVariable(name, value);
	}

	public ClockVariable findClockVariable(String name) {
		return this.clockVariablesList.get(name);
	}

	public void debug() {
		System.out.println("==================================================\n");
		System.out.println("\nStructure of " + getName() + "\n");
		System.out.println("                      STATES                      \n");
		for (State state : statesList) {
			System.out.println(
					"State -> " + state.getName() + " [ " + (state.isInitial() ? "intial" : "not-initial") + " ]");
			System.out.println("invariant : " + state.getInvariant());
			System.out.println("");
		}

		System.out.println("                    TRANSITIONS                   \n");
		for (Transition transition : transitionsList) {
			System.out.println("Transition from [" + findState(transition.getSourceStateId()).getName() + "] to ["
					+ findState(transition.getTargetStateId()).getName() + "]");
			System.out.println("guard : " + transition.getGuard());
			System.out.println("update : " + transition.getUpdate());
			System.out.println("");
		}

		System.out.println("                   DECLARATIONS                   \n");
		for (String declaration : declarationsList) {
			System.out.println(declaration);
		}

		System.out.println("==================================================\n");
	}
}