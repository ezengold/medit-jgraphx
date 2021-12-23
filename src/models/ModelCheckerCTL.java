package models;

import verifier.ast.*;

import java.beans.Expression;
import java.util.ArrayList;
import java.util.Hashtable;

public class ModelCheckerCTL {
    private  Automata automata;


    public ModelCheckerCTL(Automata automata) {
        this.automata = automata;
    }

    public Automata getAutomata() {
        return automata;
    }

    public void setAutomata(Automata automata) {
        this.automata = automata;
    }

    public  boolean satisfies(Automata automata, Exp exp){
       setAutomata(automata);
        for (State state:automata.getStatesList()) {
            state.resetProperties();
            automata.addLabelProperty(state.getStateId(),state.getName());
        }
        return satisfies(exp);
    }




    private boolean satisfies(Exp expression) {
        if(expression instanceof  BooleanLiteral) {
            for (State eachSate:automata.getStatesList()) {
                eachSate.addProperty(expression.toString(),((BooleanLiteral) expression).getValue());
            }
            return  ((BooleanLiteral) expression).getValue();
        } else if(expression instanceof  IdentifierExp){
            for (State eachSate:automata.getStatesList()) {
                if (isAtomSatisfy(eachSate,(IdentifierExp) expression)) {
                    eachSate.addProperty(expression.toString(),true);

                } else {
                    eachSate.addProperty(expression.toString(),false);

                }

            }
            return automata.getInitialState().isPropertySatisfy(expression.toString());



        } else if(expression instanceof Or){
            Exp lhs = ((Or) expression).getLHS();
            Exp rhs = ((Or) expression).getRHS();
            satisfies(lhs);
            satisfies(rhs);

            for (State eachSate:automata.getStatesList()) {
                boolean result = eachSate.isPropertySatisfy(lhs.toString())
                        || eachSate.isPropertySatisfy(rhs.toString());


                eachSate.addProperty(expression.toString(),result);
            }

          return automata.getInitialState().isPropertySatisfy(expression.toString());

        }else if(expression instanceof Imply) {
            Exp lhs = ((Imply) expression).getLHS();
            Exp rhs = ((Imply) expression).getRHS();
            satisfies(lhs);
            satisfies(rhs);

            for (State eachSate:automata.getStatesList()) {
                boolean result = !(eachSate.isPropertySatisfy(lhs.toString())
                        && !eachSate.isPropertySatisfy(rhs.toString()) );
                eachSate.addProperty(expression.toString(),result);
            }

            return automata.getInitialState().isPropertySatisfy(expression.toString());

        } else if(expression instanceof And) {
            Exp lhs = ((And) expression).getLHS();
            Exp rhs = ((And) expression).getRHS();
            satisfies(lhs);
            satisfies(rhs);

            for (State eachSate:automata.getStatesList()) {
                boolean result = eachSate.isPropertySatisfy(lhs.toString())
                        && eachSate.isPropertySatisfy(rhs.toString());
                eachSate.addProperty(expression.toString(),result);
            }



            return automata.getInitialState().isPropertySatisfy(expression.toString());

        } else if(expression instanceof  Not) {
            Exp expressionNot = ((Not) expression).getExp();
            satisfies(expressionNot);
            for (State eachSate:automata.getStatesList()) {
                boolean result = !eachSate.isPropertySatisfy(expressionNot.toString());
                eachSate.addProperty(expression.toString(),result);
            }


            return automata.getInitialState().isPropertySatisfy(expression.toString());

        } else if(expression instanceof  AlwaysNext) {

            Exp expressionNext = ((AlwaysNext) expression).getExp();
            satisfies(expressionNext);
            for (State eachState:automata.getStatesList()) {
                eachState.addProperty(expression.toString(),true);
            }

            for (State eachState:automata.getStatesList()) {
                if(automata.findOutgoingStates(eachState.getStateId()).size()>0) {
                    for (State nextState:automata.findOutgoingStates(eachState.getStateId()) ) {
                        if (!nextState.isPropertySatisfy(expressionNext.toString())) {
                            eachState.addProperty(expression.toString(),false);
                            break;
                        }
                    }
                } else {
                    eachState.addProperty(expression.toString(),false);
                }

            }
            for (State eachState:automata.getStatesList()) {
                if(eachState.isPropertySatisfy(expression.toString())) {
                    return true;
                }
            }
            return false;


        } else if(expression instanceof  ExistsNext) {
            Exp expressionNext = ((ExistsNext) expression).getExp();
            satisfies(expressionNext);
            for (State eachState:automata.getStatesList()) {
               eachState.addProperty(expression.toString(),false);
            }

            for (State eachState:automata.getStatesList()) {

                for (State nextState:automata.findOutgoingStates(eachState.getStateId()) ) {
                    if (nextState.isPropertySatisfy(expressionNext.toString())) {
                        eachState.addProperty(expression.toString(),true);
                        return true;
                    }
                }


            }

            return false;
        } else if(expression instanceof  ExistsUntil) {
            Exp lhs = ((ExistsUntil) expression).getLhs();
            Exp rhs = ((ExistsUntil) expression).getRhs();
            ArrayList<State> list = new ArrayList<>();
            satisfies(lhs);
            satisfies(rhs);
            for (State eachState:automata.getStatesList()) {
                eachState.addProperty(expression.toString(),false);
                eachState.setSeenBefore(false);
            }
            for (State eachState:automata.getStatesList()) {
                if (eachState.isPropertySatisfy(rhs.toString())) {
                    list.add(eachState);
                    eachState.setSeenBefore(true);
                }
            }

            while (!list.isEmpty()) {
                State s = list.get(0);
                list.remove(0);
                s.addProperty(expression.toString(),true);
                for (Transition transition: automata.getTransitionsList()) {

                    if(transition.getTargetStateId().equals(s.getStateId())) {
                        State previous = automata.findState(transition.getSourceStateId());
                        if(!previous.isSeenBefore()) {
                            previous.setSeenBefore(true);
                            if(previous.isPropertySatisfy(lhs.toString())) {
                                list.add(previous);
                            }
                        }
                    }
                }
            }
            return automata.getInitialState().isPropertySatisfy(expression.toString());

        } else if(expression instanceof  AlwaysUntil) {

            Exp lhs = ((AlwaysUntil) expression).getLhs();
            Exp rhs = ((AlwaysUntil) expression).getRhs();
            ArrayList<State> list = new ArrayList<>();
            satisfies(lhs);
            satisfies(rhs);
            for (State eachState:automata.getStatesList()) {
                eachState.setDegrees(automata.findOutgoingTransitions(eachState.getStateId()).size());
                eachState.addProperty(expression.toString(),false);
                if (eachState.isPropertySatisfy(rhs.toString())) {
                    list.add(eachState);
                }
            }

            while (!list.isEmpty()) {
                State s = list.get(0);
                list.remove(0);
                s.addProperty(expression.toString(),true);
                for (Transition transition: automata.getTransitionsList()) {

                    if(transition.getTargetStateId().equals(s.getStateId())) {
                        State previous = automata.findState(transition.getSourceStateId());
                        previous.setDegrees(previous.getDegrees()-1);
                        if(previous.getDegrees() == 0 && previous.isPropertySatisfy(lhs.toString())
                                && !previous.isPropertySatisfy(expression.toString())) {
                            list.add(previous);
                        }
                    }
                }
            }
            return automata.getInitialState().isPropertySatisfy(expression.toString());


        } else if(expression instanceof  ExistsEventually) {
            ExistsUntil expTemp = new ExistsUntil(new BooleanLiteral(true),((ExistsEventually) expression).getExp());
            return satisfies(expTemp);
        } else if(expression instanceof  AlwaysEventually){
            AlwaysUntil expTemp = new AlwaysUntil(new BooleanLiteral(true),((AlwaysEventually) expression).getExp());
            return satisfies(expTemp);
        } else if(expression instanceof  AlwaysGlobally) {
            //AGφ = ¬EF¬φ
            Not exp = new Not(new ExistsUntil(new BooleanLiteral(true),new Not(((AlwaysGlobally) expression).getExp())));
            return satisfies(exp);
        } else if(expression instanceof  ExistsGlobally) {
            //EGφ ≣ ¬AF¬φ
            Not notExpression = new Not(new AlwaysUntil(new BooleanLiteral(true),new Not(((ExistsGlobally) expression).getExp())));
            return satisfies(notExpression);
        } else {
            return false;
        }

    }





    private void removePropertyFromState(String property) {
        //Remove specific property for all states of automata
        for (State state:automata.getStatesList()) {
            state.removeProperty(property);
        }
    }

    private Boolean isAtomSatisfy(State state,IdentifierExp expression) {
        Hashtable<String,ArrayList<String>> labels =  automata.getLabelProperties();
//        System.out.println(labels);
        if (labels.get(state.getStateId()).contains(expression.toString())) {
            return true;
        }
        return false;
    }

    private void resetAllStates() {

        for (State state:automata.getStatesList()) {
            state.resetProperties();
            automata.addLabelProperty(state.getStateId(),state.getName());
        }

    }




}
