package models;

import java.util.UUID;

public class EventAutomata extends Automata {

    public EventAutomata(String eventName) {
        super();
        this.name = eventName.substring(0,1).toUpperCase() + eventName.substring(1).toLowerCase();;
        this.declarationsList.add("chan "+eventName+";");
        State state = new State();
        state.setInitial(true);
        state.setName("init"+name);
        addState(state);
        Transition transition = new Transition(state.getStateId(),state.getStateId());
        transition.setEvent(eventName+ "!");
        addTransition(transition);
    }
}
