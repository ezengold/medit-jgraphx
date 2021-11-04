package models;

public class ModelChecker {
    private Automata automata;

    public ModelChecker(Automata automata) {
        this.automata = automata;
    }

    public Automata getAutomata() {
        return automata;
    }

    public void markState(State state,String property) {
        state.addProperty(property);
    }




//    public void checkEX(String property) {
//        for (Transition transition:automata.getTransitionsList()) {
//
//        }
//    }



    public void setAutomata(Automata automata) {
        this.automata = automata;
    }
}
