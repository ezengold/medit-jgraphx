package utils;

import app.App;
import com.mxgraph.layout.*;
import com.mxgraph.layout.orthogonal.mxOrthogonalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;

import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import models.Position;
import models.State;
import models.Transition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class QmHandler {
    private static HashMap<String, String> excepts = new HashMap<String, String>();
    private static HashMap<String, String> globalDeclarationsExcepts = new HashMap<String, String>();
    protected String globalDeclarations = "";
    protected ArrayList<State> stateList = new ArrayList<State>();
    protected ArrayList<Transition> transitionList = new ArrayList<Transition>();
    protected HashMap<String, String> transitionUpdates = new HashMap<String, String>();
    protected HashMap<String, String> transitionGuards = new HashMap<String, String>();
    protected HashMap<String, String> transitionEvents = new HashMap<String, String>();
    protected HashMap<String, String> statesId = new HashMap<String, String>();
    protected ArrayList<String> declarations = new ArrayList<>();
    protected ArrayList<String> guardDeclarations = new ArrayList<>();

    private App app;

    public QmHandler(App parent) {
        this.app = parent;
        excepts.put("<", "&lt;");
        excepts.put(">", "&gt;");
        excepts.put("&", "&amp;");
    }

    public static String escapeStr(final String input) {
        String output = input;
        for (String token : excepts.keySet()) {
            output = output.replaceAll(token, excepts.get(token));
        }
        return output;
    }

    public static String restoreStr(final String input) {
        String output = input;
        for (String token : excepts.keySet()) {
            output = output.replaceAll(excepts.get(token), token);
        }
        return output;
    }


    public mxGraph readQuantumLeapsFile(File file) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        dbf.setIgnoringElementContentWhitespace(true);
//        dbf.setNamespaceAware(true);
        mxGraph graph = new mxGraph();

        graph.setCellsResizable(false);
        graph.setCellsEditable(false);
        graph.isLabelMovable(true);

        Hashtable<String, Object> edgeStyle = (Hashtable<String, Object>) graph.getStylesheet().getDefaultEdgeStyle();
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
        edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
        edgeStyle.put(mxConstants.STYLE_ROUNDED, "1");
        graph.getStylesheet().setDefaultEdgeStyle(edgeStyle);

        Hashtable<String, Object> vertexStyle = (Hashtable<String, Object>) graph.getStylesheet()
                .getDefaultVertexStyle();
        vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        vertexStyle.put(mxConstants.STYLE_FILLCOLOR, "#78c4fc");
        graph.getStylesheet().setDefaultVertexStyle(vertexStyle);
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            doc.getDocumentElement().normalize();

            //get model and package
            Element model = (Element) doc.getElementsByTagName("model").item(0);

            NodeList element = model.getElementsByTagName("package");
            for (int iter = 0; iter< element.getLength();iter++) {
                Element firstPackage = (Element) model.getElementsByTagName("package").item(iter);
                Element firstStatechart = (Element) firstPackage.getElementsByTagName("statechart").item(0);
                if (firstStatechart != null) {

                    //initial state
                    Node initialNode = firstStatechart.getElementsByTagName("initial").item(0);
                    Element initialElement = (Element) initialNode;
                    State initialState = new State();
                    initialState.setInitial(true);
                    String initialGlyphPosition = getGliphInitial(initialElement);
                    Position position = getStatePosition(initialGlyphPosition);
                    initialState.setPosition(position.getX(),position.getY());
                    stateList.add(initialState);

                    Node action = initialElement.getElementsByTagName("action").item(0);
                    String target = ((Element) initialNode).getAttribute("target");

                    String[] elements = target.split("/");
                    Node node = initialNode;
                    for (String item : elements) {
                        if (item.equals("..")) {
                            assert node != null;
                            node = node.getParentNode();
                        } else {
                            int index = Integer.parseInt(item);

                            System.out.println("INDEX: " + index);
                            System.out.println("PARENT INITIAL NODE: " + node.getNodeName());
                            if (node != null) {
                                node = node.getFirstChild();
                                System.out.println("FIRST CHILD: " + node.getNodeName());
                                int i = 0;
                                while (i <= index) {
                                    node = node.getNextSibling();
                                    if (node.getNodeType() == Element.ELEMENT_NODE && !node.getNodeName().equals("documentation")
                                            && !node.getNodeName().equals("exit")
                                            && !node.getNodeName().equals("entry")
                                    ) {
                                        System.out.println("INITIAL STATE NAME: " + node.getNodeName());
                                        i++;
                                    }
                                }


                            }
                        }
                    }

                    String stateName = ((Element) node).getAttribute("name");

                    String glyPosition = getGlyphState(((Element) node));
                    addStatesId(stateName,getStatePosition(glyPosition));
                    String targetStateId = getStateId(stateName);
                    if (targetStateId != null) {
                        Transition transition = new Transition(initialState.getStateId(), targetStateId);
                        transitionList.add(transition);
                        if (action != null) {
                            setTransitionUpdate(transition.getTransitionId(), ((Element) action).getAttribute("brief"));
                        }
                    }


                    //rest of the states

                    NodeList locationsList = firstStatechart.getElementsByTagName("state");
                    for (int i = 0; i < locationsList.getLength(); i++) {
                        Node stateNode = locationsList.item(i);
                        if (stateNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element location = (Element) stateNode;
                            String nameOfState = location.getAttribute("name");
                            addStatesId(nameOfState,getStatePosition(getGlyphState(location)));
                            String stateId = getStateId(nameOfState);

                            Node entryNode = location.getElementsByTagName("entry").item(0);
                            Node existNode = location.getElementsByTagName("exit").item(0);
                            //outgoing transitions
                            NodeList outgoingTransitions = location.getElementsByTagName("tran");

                            if (entryNode != null) {
                                Transition transitionEntry = getEntryTransition(stateId);
                                if (transitionEntry != null) {
                                    setTransitionUpdate(transitionEntry.getTransitionId(), ((Element) entryNode).getTextContent());
                                }
                            }

                            //Iterate each outgoing transitions
                            handlingTransitions(outgoingTransitions, existNode, stateId, target);


                        }


                    }
                    handlingSubMachine(firstStatechart);

//                    handlingSubMachineV2(firstStatechart);


                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        // assign guards and updates to transitions
        System.out.println("=================DEBUG QUANTUM LEAPS===================");
        StringBuilder globalDeclarations = new StringBuilder();

        //event declarations
        if(!declarations.isEmpty()) {
            for (String declaration : declarations) {

                if(globalDeclarations.length() == 0) {
                    globalDeclarations = new StringBuilder("chan "+declaration).append(";\n");
                } else {
                    globalDeclarations.append("chan ").append(declaration).append(";\n");
                }

            }
        }

        //guard declarations to clock variables
        if(!guardDeclarations.isEmpty()) {
            for (String declaration : guardDeclarations) {

                if(globalDeclarations.length() == 0) {
                    globalDeclarations = new StringBuilder("clock "+declaration).append(";\n");
                } else {
                    globalDeclarations.append("clock ").append(declaration).append(";\n");
                }

            }
        }
       for (Transition transition : transitionList) {
            transition.setGuard(transitionGuards.get(transition.getTransitionId()) == null ? ""
                    : transitionGuards.get(transition.getTransitionId()));
//            transition.setUpdate(transitionUpdates.get(transition.getTransitionId()) == null ? ""
//                    : transitionUpdates.get(transition.getTransitionId()));

            transition.setEvent(transitionEvents.get(transition.getTransitionId()) == null ? ""
                    : transitionEvents.get(transition.getTransitionId()));
            System.out.println(transition.debug());

        }

        System.out.println("=================STATES===================");
        for (State state : stateList) {
            System.out.println(state.debug());
        }


        System.out.println("================END DEBUT QUANTUM LEAPS=================");


        this.app.setGlobalDeclarations(globalDeclarations.toString());

        // create the graph
        graph.getModel().beginUpdate();
        try {
            graph.removeCells(graph.getChildCells(graph.getDefaultParent()));

            // Hold all the vertices created
            HashMap<String, Object> verticesArray = new HashMap<String, Object>();

            // GET LOCATIONS
            State.NB = 0;
            for (State s : stateList) {
                Object vertex = graph.insertVertex(graph.getDefaultParent(), s.getStateId(), s, s.getPosition().getX(),
                        s.getPosition().getY(), 50, 50);

                if (s.isInitial()) {

                    ((mxCell) vertex).setStyle("fillColor=#888888;strokeColor=#dddddd");
                }

                verticesArray.put(s.getStateId(), vertex);
            }

            // GET EDGES
            for (Transition t : transitionList) {

                mxCell source = (mxCell) verticesArray.get(t.getSourceStateId());
                State sourceState = (State) source.getValue();
                double sourceX = sourceState.getPosition().getX();
                double sourceY = sourceState.getPosition().getY();

                mxCell target = (mxCell) verticesArray.get(t.getTargetStateId());
                State targetState = (State) target.getValue();
                double targetX = targetState.getPosition().getX();
                double targetY = targetState.getPosition().getY();

                mxCell newEdge = (mxCell) graph.insertEdge(graph.getDefaultParent(), t.getTransitionId(), t, source, target);

                mxGeometry edgeGeometry = new mxGeometry();
                edgeGeometry.setTerminalPoint(new mxPoint(sourceX, sourceY), true);
                edgeGeometry.setTerminalPoint(new mxPoint(targetX, targetY), false);
                edgeGeometry.setRelative(false);
                newEdge.setGeometry(edgeGeometry);
            }
        } finally {
            graph.setMultigraph(true);
            graph.setAllowLoops(true);
            graph.getModel().endUpdate();
        }

//         mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, true);
        layout.setLevelDistance(30);
        layout.setGroupPadding(50);
        layout.setNodeDistance(50);
        layout.setUseBoundingBox(false);
        layout.setEdgeRouting(true);
        layout.setHorizontal(true);
        layout.execute(graph.getDefaultParent());
        return layout.getGraph();
//        return graph;

    }

    private String getGlyphState(Element node) {
        if(node.getNodeName().equals("smstate")) {
            return getGliphSubMachine(node);
        } else {
            Node glyphNode = node.getElementsByTagName("state_glyph").item(0);
            return ((Element)glyphNode).getAttribute("node");
        }

    }

    private String getGlyphChoice(Element node) {
        Node glyphNode = node.getElementsByTagName("choice_glyph").item(0);
        return ((Element)glyphNode).getAttribute("conn");
    }

    private String getGliphSubMachine(Element node) {
        Node glyphNode = node.getElementsByTagName("smstate_glyph").item(0);
        return ((Element)glyphNode).getAttribute("node");
    }

    private String getGliphInitial(Element node) {
        Node glyphNode = node.getElementsByTagName("initial_glyph").item(0);
        return ((Element)glyphNode).getAttribute("conn");
    }




    private Position getStatePosition(String glyphPositions) {
        String[] positions = glyphPositions.split(",");
        return new Position(Double.parseDouble(positions[0])*2,Double.parseDouble(positions[1])*2);
    }



    private void handlingTransitions(NodeList outgoingTransitions, Node existNode, String stateId, String target) {
        for (int j = 0; j < outgoingTransitions.getLength(); j++) {
            Node transitionNode = outgoingTransitions.item(j);

            if (transitionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element transition = (Element) transitionNode;
                String transitionTrig = transition.getAttribute("trig");
                String transitionTarget = transition.getAttribute("target");
                System.out.println("TRANSITION TARGET: "+transitionTarget);

                Node transitionAction = transition.getElementsByTagName("action").item(0);

                if (transitionTarget != null && !transitionTarget.equals("..") && !transitionTarget.isEmpty()) {
                    Node targetState = getTargetNode(transitionTarget, transitionNode);
                    String targetStateName = ((Element) targetState).getAttribute("name");
                    System.out.println("TARGET STATE NAME: "+targetStateName);
                    String glyphPositions = getGlyphState(((Element) targetState));
                    addStatesId(targetStateName,getStatePosition(glyphPositions));
                    String targetStateId = getStateId(targetStateName);
                    if (targetStateId != null) {
                        Transition transitionOutgoing = new Transition(stateId, targetStateId);
                        transitionList.add(transitionOutgoing);

                        if (existNode != null) {
                            setTransitionUpdate(transitionOutgoing.getTransitionId(), ((Element) existNode).getTextContent());
                        }
                        if (transitionAction != null) {
                            Element transitionActionElement = (Element) transitionAction;
                            String actionContent = transitionActionElement.getTextContent() == null ?
                                    transitionActionElement.getAttribute("brief")
                                    : transitionActionElement.getTextContent();
                            setTransitionUpdate(transitionOutgoing.getTransitionId(), actionContent);
                        }
                        if (transitionTrig != null) {
                            setTransitionEvent(transitionOutgoing.getTransitionId(), transitionTrig);

                        }


                    }

                } else {
                    //Pseudo states list

                    NodeList choiceList = transition.getElementsByTagName("choice");
                    for (int k = 0; k < choiceList.getLength(); k++) {
                        if (choiceList.getLength() > 0) {
                            Node choiceNode = choiceList.item(k);
                            if (choiceNode.getNodeType() == Element.ELEMENT_NODE) {

                                Element choiceElement = (Element) choiceNode;
                                String glyphChoice = getGlyphChoice(choiceElement);
                                Node guardNode = choiceElement.getElementsByTagName("guard").item(0);
                                String guardChoice = ((Element) guardNode).getAttribute("brief");
                                String targetChoice = choiceElement.getAttribute("target");
                                if (targetChoice != null && !targetChoice.isEmpty()) {
                                    Node targetState = getTargetNode(targetChoice, choiceNode);
                                    String targetStateName = ((Element) targetState).getAttribute("name");
                                    addStatesId(targetStateName,getStatePosition(glyphChoice));
                                    String targetChoiceStateId = getStateId(targetStateName);
                                    if (targetChoiceStateId != null) {
                                        Transition transitionChoiceOutgoing = new Transition(stateId, targetChoiceStateId);
                                        transitionList.add(transitionChoiceOutgoing);


                                        if (guardChoice != null) {
                                            setTransitionGuard(transitionChoiceOutgoing.getTransitionId(), guardChoice);

                                        }


                                    }


                                }


                            }


                        }
                    }


                }


            }
        }
    }


    public void handlingSubMachineV2(Element firstStateChart) {
        NodeList smStates = firstStateChart.getElementsByTagName("smstate");
        if (smStates != null && smStates.getLength() > 0) {
            for (int i = 0; i < smStates.getLength(); i++) {
                Node smStateNode = smStates.item(i);
                if (smStateNode.getNodeType() == Element.ELEMENT_NODE) {
                    Element smStateElement = (Element) smStateNode;
                    String glyphPosition = getGliphSubMachine(smStateElement);


                    String sourceStateName = smStateElement.getAttribute("name");
                    System.out.println("SOURCE STATE: " + sourceStateName);
                    addStatesId(sourceStateName,getStatePosition(glyphPosition));
                    String sourceStateId = getStateId(sourceStateName);
                    String subMachine = smStateElement.getAttribute("submachine");

                    Node subMachineTarget = getTargetNode(subMachine, smStateNode);
                    System.out.println("SUBMACHINE: " + subMachineTarget.getNodeName());
                    if (subMachineTarget.getNodeType() == Element.ELEMENT_NODE) {
                        Element subMachineElement = (Element) subMachineTarget;
                        Node subStateInitial = subMachineElement.getElementsByTagName("initial").item(0);
                        Element subStateInitialElement = (Element) subStateInitial;

                        Node subStateInitialAction = subStateInitialElement.getElementsByTagName("action").item(0);
                        Node mainTarget = getTargetNode(subStateInitialElement.getAttribute("target"), subStateInitial);

                        Element mainTargetElement = (Element) mainTarget;
                        String targetStateName = mainTargetElement.getAttribute("name");
                        String glyphState = getGlyphState(mainTargetElement);
                        System.out.println("TARGET STATE NAME: " + targetStateName);
                        addStatesId(targetStateName,getStatePosition(glyphState));
                        String targetStateId = getStateId(targetStateName);
                        Transition transition = new Transition(sourceStateId, targetStateId);
                        transitionList.add(transition);

                        if (subStateInitialAction != null) {
                            if (subStateInitialAction.getNodeType() == Element.ELEMENT_NODE) {
                                Element subStateInitialActionElement = (Element) subStateInitialAction;
                                setTransitionUpdate(transition.getTransitionId(), subStateInitialActionElement.getTextContent());
                            }

                        }

                        NodeList transitions = smStateElement.getElementsByTagName("tran");
                        if (transitions != null && transitions.getLength() > 0) {
                            handlingTransitions(transitions, null, sourceStateId, "target");
//                            handlingTransitions(transitions,null,subStateInitial,"target");
                        }
                        handleSubState(subMachineElement,smStateElement);


                    }


                }
            }
        }


    }


    public void handleSubState(Element submachine,Element smStateElement) {
        NodeList states = submachine.getElementsByTagName("state");
        NodeList transitions = smStateElement.getElementsByTagName("tran");
        for (int i = 0; i < states.getLength(); i++) {
            Node stateNode = states.item(i);
            Element stateElement = (Element) stateNode;
            String stateName = stateElement.getAttribute("name");
            System.out.println("SUB STATE NAME " + stateName);
            String sourceStateId = getStateId(stateName);
            handlingTransitions(transitions, null, sourceStateId, "target");
        }

    }


    public void handlingSubMachine(Element firstStateChart) {
        NodeList smStates = firstStateChart.getElementsByTagName("smstate");
        if (smStates != null && smStates.getLength() > 0) {
            for (int i = 0; i < smStates.getLength(); i++) {
                Node smStateNode = smStates.item(i);
                if (smStateNode.getNodeType() == Element.ELEMENT_NODE) {
                    Element smStateElement = (Element) smStateNode;
                    String glyphPosition = getGliphSubMachine(smStateElement);

                    String sourceStateName = smStateElement.getAttribute("name");
                    System.out.println("SOURCE STATE: " + sourceStateName);
                    addStatesId(sourceStateName,getStatePosition(glyphPosition));
                    String sourceStateId = getStateId(sourceStateName);
                    String subMachine = smStateElement.getAttribute("submachine");

                    Node subMachineTarget = getTargetNode(subMachine, smStateNode);
                    System.out.println("SUBMACHINE: " + subMachineTarget.getNodeName());
                    if (subMachineTarget.getNodeType() == Element.ELEMENT_NODE) {
                        Element subMachineElement = (Element) subMachineTarget;
                        Node subStateInitial = subMachineElement.getElementsByTagName("initial").item(0);
                        Element subStateInitialElement = (Element) subStateInitial;

                        Node subStateInitialAction = subStateInitialElement.getElementsByTagName("action").item(0);
                        Node mainTarget = getTargetNode(subStateInitialElement.getAttribute("target"), subStateInitial);

                        Element mainTargetElement = (Element) mainTarget;
                        String targetStateName = mainTargetElement.getAttribute("name");
                        String glyState = getGlyphState(mainTargetElement);
                        System.out.println("TARGET STATE NAME: " + targetStateName);
                        addStatesId(targetStateName,getStatePosition(glyState));
                        String targetStateId = getStateId(targetStateName);
                        Transition transition = new Transition(sourceStateId, targetStateId);
                        transitionList.add(transition);

                        if (subStateInitialAction != null) {
                            if (subStateInitialAction.getNodeType() == Element.ELEMENT_NODE) {
                                Element subStateInitialActionElement = (Element) subStateInitialAction;
                                setTransitionUpdate(transition.getTransitionId(), subStateInitialActionElement.getTextContent());
                            }

                        }

                        NodeList transitions = smStateElement.getElementsByTagName("tran");
                        if (transitions != null && transitions.getLength() > 0) {
                            handlingTransitions(transitions, null, targetStateId, "target");
//                            handlingTransitions(transitions,null,subStateInitial,"target");
                        }


                    }


                }
            }
        }


    }


    public Node getTargetNode(String target, Node currentNode) {

        String[] elements = target.split("/");

        Node node = currentNode;
        for (String item : elements) {
            if (item.equals("..")) {
                assert node != null;
                node = node.getParentNode();
            } else {
                int index = Integer.parseInt(item);
                if (node != null) {
                    node = node.getFirstChild();
                    int i = 0;
                    while (i <= index) {
                        node = node.getNextSibling();
                        if (node.getNodeType() == Element.ELEMENT_NODE && !node.getNodeName().equals("documentation")
                        && !node.getNodeName().equals("exit")
                                && !node.getNodeName().equals("entry")


                        ) {
                            i++;
                        }

                    }


                }
            }
        }
        return node;
    }


    public String getStateId(String name) {
        for (Object o : statesId.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            // THIS IS THE IMPORTANT LINE
            if (entry.getValue().equals(name)) {
                return entry.getKey().toString();
            }
        }
        return null;
    }


    public Transition getEntryTransition(String targetStateId) {
        for (Transition transition : transitionList) {
            if (transition.getTargetStateId().equals(targetStateId)) {
                return transition;
            }
        }
        return null;
    }


    public void addStatesId(String name) {
        if (!this.statesId.containsValue(name)) {
            State state = new State(name);
            state.setInitial(false);
            stateList.add(state);
            statesId.put(state.getStateId(), name);
        }
    }

    public void addStatesId(String name,Position position) {
        if (!this.statesId.containsValue(name)) {
            State state = new State(name);
            state.setInitial(false);
            state.setPosition(position.getX(),position.getY());
            stateList.add(state);
            statesId.put(state.getStateId(), name);
        }
    }

    public void setTransitionUpdate(String transitionId, String update) {


        if (!update.isEmpty()) {

            if (this.transitionUpdates.containsKey(transitionId)) {

                String oldValue = this.transitionUpdates.get(transitionId);
                this.transitionUpdates.replace(transitionId, oldValue + ";" + update);

            } else {
                this.transitionUpdates.put(transitionId, update);

            }

        }


    }

    public void setTransitionGuard(String transitionId, String guard) {
        handleTransitionGuard(guard);
        if (this.transitionGuards.containsKey(transitionId)) {

            String oldValue = this.transitionGuards.get(transitionId);
            this.transitionGuards.replace(transitionId, oldValue + ";" + guard);

        } else {

            this.transitionGuards.put(transitionId, guard);

        }
    }

    public void handleTransitionGuard(String expression) {
        String variable = "";
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (Character.isAlphabetic(ch)) {
                variable += ch;
            } else if (!variable.isEmpty()) {
                if (!guardDeclarations.contains(variable)) {
                    guardDeclarations.add(variable);
                }
                variable = "";
            } else if (Character.isDigit(ch)) {
                while (Character.isDigit(ch)) {
                    ch = (i + 1 < expression.length()) ? expression.charAt(++i) : ' ';
                }
            } else if (ch == '(') {
                // ignore opening parenthesis
            } else if (ch == ')') {
                // ignore closing parenthesis
            } else if (ch == '&' || ch == '|') {
                // check if next character is also an operator
                char nextCh = (i + 1 < expression.length()) ? expression.charAt(i + 1) : ' ';
                if (nextCh == ch) {
                    // skip both characters
                    i++;
                }
            }
        }
        if (!variable.isEmpty() && !guardDeclarations.contains(variable)) {
            guardDeclarations.add(variable);
        }
//        System.out.println("GUARD DECLARATION FINALLY FOR  "+expression+" :"+guardDeclarations);
    }



    public void setTransitionEvent(String transitionId, String event) {
        this.transitionEvents.put(transitionId,event);
        if(!declarations.contains(event)) {
            declarations.add(event);
        }
    }








}
