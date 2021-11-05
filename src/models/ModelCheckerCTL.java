package models;

import verifier.ast.*;

import java.util.ArrayList;
import java.util.Hashtable;

public class ModelCheckerCTL {
    private Automata automata;

    public ModelCheckerCTL(Automata automata) {
        this.automata = automata;
    }

    public Automata getAutomata() {
        return automata;
    }




    private void markingStates(Exp expression) {
        if(expression instanceof IdentifierExp) {  //Identifier

            for (State state:automata.getStatesList()) {



                if (isAtomSatisfy(state,(IdentifierExp) expression)) {
                    state.addProperty(expression.toString(),true);
                } else {
                    state.addProperty(expression.toString(),false);
                }

            }

        } else if(expression instanceof  BooleanLiteral) {

            for (State state:automata.getStatesList()) {
                    state.addProperty(expression.toString(),((BooleanLiteral) expression).getValue());
            }

        } else if(expression instanceof Not) { //Not Expression
            Exp expressionNot = ((Not) expression).getExp();
            markingStates(expressionNot);
            for (State state:automata.getStatesList()) {
                state.addProperty(expression.toString(),!state.getPropertiesVerified().get(expressionNot.toString()));
            }


        } else if(expression instanceof And) { //Exp AND Exp
            Exp lhs = ((And) expression).getLHS();
            Exp rhs = ((And) expression).getRHS();
            markingStates(lhs);
            markingStates(rhs);
            for (State state:automata.getStatesList()) {
                state.addProperty(expression.toString(),(
                        state.getPropertiesVerified().get(lhs.toString())
                && state.getPropertiesVerified().get(rhs.toString())
                        ));

            }


        } else if(expression instanceof Or) { // Exp OR Exp
            Exp lhs = ((Or) expression).getLHS();
            Exp rhs = ((Or) expression).getRHS();
            markingStates(lhs);
            markingStates(rhs);
            for (State state:automata.getStatesList()) {
                state.addProperty(expression.toString(),(
                        state.getPropertiesVerified().get(lhs.toString())
                                || state.getPropertiesVerified().get(rhs.toString())
                ));
            }
        } else if(expression instanceof ExistsNext) { //Exists Next E-> Expression
            Exp expressionNext = ((ExistsNext) expression).getExp();
            markingStates(expressionNext);
            //set property not verified in all states
            for (State state:automata.getStatesList()) {
                state.removeProperty(expression.toString());
            }


            for (Transition transition: automata.getTransitionsList()) {
                State nextState = automata.findState(transition.getTargetStateId());
                State previousState = automata.findState(transition.getSourceStateId());
                if (nextState.isPropertySatisfy(expressionNext.toString())) {
                    previousState.addProperty(expression.toString(),true);
                }
            }


        } else if(expression instanceof AlwaysNext) { //Always Next A-> Expression )

            Exp expressionNext = ((AlwaysNext) expression).getExp();
            markingStates(expressionNext);
            //set property not verified in all states
            for (State state:automata.getStatesList()) {
                state.addProperty(expression.toString(),true);
            }

            for (Transition transition: automata.getTransitionsList()) {
                State nextState = automata.findState(transition.getTargetStateId());
//                State previousState = automata.findState(transition.getSourceStateId());
                if (!nextState.isPropertySatisfy(expressionNext.toString())) {
                    removePropertyFromState(expression.toString());
                    break;
                }
            }
        } else if(expression instanceof ExistsUntil) { //E Expression UNTIL Expression
            Exp lhs = ((ExistsUntil) expression).getLhs();
            Exp rhs = ((ExistsUntil) expression).getRhs();
            ArrayList<State> list = new ArrayList<>();
            markingStates(lhs);
            markingStates(rhs);
            for (State state:automata.getStatesList()) {
                state.addProperty(expression.toString(),false);
                state.setSeenBefore(false);
            }
            for (State state:automata.getStatesList()) {
                if (state.isPropertySatisfy(rhs.toString())) {
                    list.add(state);
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
                            if(previous.getPropertiesVerified().get(lhs.toString())) {
                                list.add(previous);
                            }
                        }
                    }
                }
            }




        }  else if(expression instanceof AlwaysUntil) { //E Expression UNTIL Expression
            Exp lhs = ((AlwaysUntil) expression).getLhs();
            Exp rhs = ((AlwaysUntil) expression).getRhs();
            ArrayList<State> list = new ArrayList<>();
            markingStates(lhs);
            markingStates(rhs);
            for (State state:automata.getStatesList()) {
                state.setDegrees(automata.findOutgoingTransitions(state.getStateId()).size());
                state.addProperty(expression.toString(),false);
               if (state.getPropertiesVerified().get(rhs.toString())) {
                   list.add(state);
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
                        if(previous.getDegrees() == 0 && previous.getPropertiesVerified().get(lhs.toString())) {
                                list.add(previous);
                        }
                    }
                }
            }

        } else if(expression instanceof ExistsEventually) {
            ExistsUntil expTemp = new ExistsUntil(new BooleanLiteral(true),((ExistsEventually) expression).getExp());
            markingStates(expTemp);
            for (State state:automata.getStatesList()) {
                if(state.getPropertiesVerified().get(expTemp.toString()) !=null) {
                    state.addProperty(expression.toString(),state.getPropertiesVerified().get(expTemp.toString()));

                }
            }

        }else if(expression instanceof AlwaysEventually) {
            AlwaysUntil expTemp = new AlwaysUntil(new BooleanLiteral(true),((AlwaysEventually) expression).getExp());
            markingStates(expTemp);
            for (State state:automata.getStatesList()) {
                if(state.getPropertiesVerified().get(expTemp.toString()) !=null) {
                    state.addProperty(expression.toString(),state.getPropertiesVerified().get(expTemp.toString()));

                }
            }
        } else if(expression instanceof  AlwaysGlobally) {
            //AGφ = ¬EF¬φ
            Not exp = new Not(new ExistsEventually(new Not(((AlwaysGlobally) expression).getExp())));
            markingStates(exp);
            for (State state:automata.getStatesList()) {
                if(state.getPropertiesVerified().get(exp.toString()) !=null) {
                    state.addProperty(expression.toString(),state.getPropertiesVerified().get(exp.toString()));

                }
            }

        } else if(expression instanceof  ExistsGlobally) {
            //EGφ ≣ ¬AF¬φ

            Not notExpression = new Not(new AlwaysEventually(new Not(((ExistsGlobally) expression).getExp())));
            markingStates(notExpression);
            for (State state:automata.getStatesList()) {
                if(state.getPropertiesVerified().get(notExpression.toString()) !=null) {
                    state.addProperty(expression.toString(),state.getPropertiesVerified().get(notExpression.toString()));

                }
            }
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


    public ArrayList<State> checkingModel(Exp exp) {
        ArrayList<State> statesPropertyVerified = new ArrayList<>();
        resetAllStates();
        markingStates(exp);
        for (State state:automata.getStatesList()) {
            System.out.println("STATE "+state.getName()+":: "+state.getPropertiesVerified().get(exp));
            if (state.isPropertySatisfy(exp.toString())) {
                statesPropertyVerified.add(state);
            }
        }
        return statesPropertyVerified;
    }






    public void setAutomata(Automata automata) {
        this.automata = automata;
    }
}
