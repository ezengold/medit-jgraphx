package models;

public class EveryTimer extends Automata {

    public EveryTimer(String eventName,int time) {

        super();
        System.out.println("EVERY TIMER IS CALLED: "+eventName+" time = "+time);
        this.name = eventName.substring(0,1).toUpperCase() + eventName.substring(1).toLowerCase();;
        this.declarationsList.add("clock t;");
        State state = new State("init"+name);
        state.setInitial(true);
        state.setInvariant("t <= "+time);
        addState(state);
        Transition transition = new Transition(state.getStateId(),state.getStateId());
        transition.setEvent(eventName+ "!");
        transition.setGuard("t == "+time);
        transition.setUpdate("t := 0");
        addTransition(transition);
    }
}
