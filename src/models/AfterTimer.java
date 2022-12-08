package models;

public class AfterTimer extends Automata {

    public AfterTimer(String eventName,int time) {

        super();
        System.out.println("AFTER TIMER IS CALLED: "+eventName+" time = "+time);
        this.name = eventName.substring(0,1).toUpperCase() + eventName.substring(1).toLowerCase();;
        this.declarationsList.add("clock t;");
        State state = new State("init"+name);
        state.setInitial(true);
        state.setInvariant("t <= "+time);
        addState(state);

        State state1 = new State("state"+name);
        state.setInitial(false);
        addState(state1);

        Transition transition = new Transition(state.getStateId(),state1.getStateId());
        transition.setEvent(eventName+ "!");
        transition.setGuard("t == "+time);
        transition.setUpdate("t := 0");
        addTransition(transition);
    }
}