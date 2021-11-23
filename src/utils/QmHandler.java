package utils;

import app.App;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class QmHandler {
    private static HashMap<String, String> excepts = new HashMap<String, String>();
    private static HashMap<String, String> globalDeclarationsExcepts = new HashMap<String, String>();
    protected String globalDeclarations = "";
    protected ArrayList<State> stateList = new ArrayList<State>();
    protected ArrayList<Transition> transitionList = new ArrayList<Transition>();
    protected HashMap<String, String> transitionUpdates = new HashMap<String, String>();
    protected HashMap<String, String> transitionGuards = new HashMap<String, String>();
    protected HashMap<String, String> statesId = new HashMap<String, String>();
    private App app;

    public QmHandler(App parent) {
        this.app = parent;
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
            Element model = (Element)doc.getElementsByTagName("model").item(0);
            Element  firstPackage = (Element)model.getElementsByTagName("package").item(0);
            Element firstStatechart = (Element)firstPackage.getElementsByTagName("statechart").item(0);
            if(firstStatechart != null) {

                //initial state
                Node initialNode = firstStatechart.getElementsByTagName("initial").item(0);
                Element initialElement = (Element) initialNode;
                State initialState = new State();
                initialState.setInitial(true);
                stateList.add(initialState);
                Node action = initialElement.getElementsByTagName("action").item(0);
                String target = ((Element) initialNode).getAttribute("target");

                String[] elements = target.split("/");
                Node node = initialNode;
                for (String item:elements) {
                    if(item.equals("..")) {
                        assert node != null;
                        node = node.getParentNode();
                    } else {
                       int index = Integer.parseInt(item);

                       System.out.println("INDEX: "+index);
                       System.out.println("PARENT INITIAL NODE: "+node.getNodeName());
                       if(node !=null) {
                           Node stateNode = node.getFirstChild();
                           int i = 0;
                           while (i <= index) {
                               stateNode = stateNode.getNextSibling();
                               if(stateNode.getNodeType() == Element.ELEMENT_NODE) {
                                   System.out.println("INITIAL STATE NAME: "+stateNode.getNodeName());
                                   i++;
                               }
                           }

                           String stateName = ((Element)stateNode).getAttribute("name");

                           addStatesId(stateName);
                           String targetStateId = getStateId(stateName);
                           if(targetStateId != null) {
                               Transition transition = new Transition(initialState.getStateId(),targetStateId);
                               transitionList.add(transition);
                               if(action != null) {
                                   setTransitionUpdate(transition.getTransitionId(),((Element)action).getAttribute("brief"));
                               }
                           }




                       }
                    }
                }


                //rest of the states

                NodeList locationsList = firstStatechart.getElementsByTagName("state");
                for (int i = 0; i < locationsList.getLength() ; i++) {
                    Node stateNode = locationsList.item(i);
                    if(stateNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element location = (Element) stateNode;
                        String stateName = location.getAttribute("name");
                        addStatesId(stateName);
                        String stateId = getStateId(stateName);

                        Node entryNode = location.getElementsByTagName("entry").item(0);
                        Node existNode = location.getElementsByTagName("exit").item(0);
                        //outgoing transitions
                        NodeList outgoingTransitions = location.getElementsByTagName("tran");

                        if(entryNode != null) {
                            Transition transitionEntry = getEntryTransition(stateId);
                            if(transitionEntry != null) {
                                setTransitionUpdate(transitionEntry.getTransitionId(), ((Element)entryNode).getAttribute("brief"));
                            }
                        }

                        //Iterate each outgoing transitions
                        for (int j = 0; j < outgoingTransitions.getLength() ; j++) {
                            Node transitionNode = outgoingTransitions.item(j);

                            if (transitionNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element transition  = (Element) transitionNode;
                                String transitionTrig = transition.getAttribute("trig");
                                String transitionTarget = transition.getAttribute("target");

                                Node transitionAction = transition.getElementsByTagName("action").item(0);

                                if(transitionTarget != null && !transitionTarget.equals("..") && !transitionTarget.isEmpty()) {
                                    Node targetState = getTargetNode(transitionTarget,transitionNode);
                                    String targetStateName = ((Element)targetState).getAttribute("name");
                                    addStatesId(targetStateName);
                                    String targetStateId = getStateId(targetStateName);
                                    if(targetStateId != null) {
                                        Transition transitionOutgoing = new Transition(stateId,targetStateId);
                                        transitionList.add(transitionOutgoing);

                                        if(existNode != null) {
                                            setTransitionUpdate(transitionOutgoing.getTransitionId(),((Element)existNode).getAttribute("brief"));
                                        }
                                        if(transitionAction != null) {
                                            setTransitionUpdate(transitionOutgoing.getTransitionId(),((Element)transitionAction).getAttribute("brief"));
                                        }
                                        if(transitionTrig != null) {
                                            setTransitionGuard(transitionOutgoing.getTransitionId(),transitionTrig);

                                        }



                                    }

                                } else {
                                    //Pseudo states list

                                    NodeList choiceList = transition.getElementsByTagName("choice");
                                    for (int k = 0; k < choiceList.getLength() ; k++) {
                                        if (choiceList.getLength() > 0) {
                                            Node choiceNode = choiceList.item(k);
                                            if(choiceNode.getNodeType() == Element.ELEMENT_NODE) {

                                                Element choiceElement = (Element) choiceNode;
                                                Node guardNode = choiceElement.getElementsByTagName("guard").item(0);
                                                String guardChoice = ((Element)guardNode).getAttribute("brief");
                                                String targetChoice = choiceElement.getAttribute("target");
                                                if (targetChoice !=null && !target.isEmpty()) {

                                                    Node targetState = getTargetNode(targetChoice,choiceNode);
                                                    String targetStateName = ((Element)targetState).getAttribute("name");
                                                    addStatesId(targetStateName);
                                                    String targetChoiceStateId = getStateId(targetStateName);
                                                    if(targetChoiceStateId != null) {
                                                        Transition transitionChoiceOutgoing = new Transition(stateId,targetChoiceStateId);
                                                        transitionList.add(transitionChoiceOutgoing);


                                                        if(guardChoice != null) {
                                                            setTransitionGuard(transitionChoiceOutgoing.getTransitionId(),guardChoice);

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


                }



            }







        }catch (Exception e) {
            e.printStackTrace();
        }

        // assign guards and updates to transitions
        System.out.println("=================DEBUG QUANTUM LEAPS===================");
        for (Transition transition : transitionList) {
            transition.setGuard(transitionGuards.get(transition.getTransitionId()) == null ? ""
                    : transitionGuards.get(transition.getTransitionId()));
            transition.setUpdate(transitionUpdates.get(transition.getTransitionId()) == null ? ""
                    : transitionUpdates.get(transition.getTransitionId()));
            System.out.println(transition.debug());

        }

        System.out.println("=================STATES===================");
        for (State state:stateList) {
            System.out.println(state.debug());
        }


        System.out.println("================END DEBUT QUANTUM LEAPS=================");





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
                        s.getPosition().getY(), 40, 40);

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

                mxCell newEdge = (mxCell) graph.insertEdge(graph.getDefaultParent(), t.getGuard(), t, source, target);

                mxGeometry edgeGeometry = new mxGeometry();
                edgeGeometry.setTerminalPoint(new mxPoint(sourceX, sourceY), true);
                edgeGeometry.setTerminalPoint(new mxPoint(targetX, targetY), false);
                edgeGeometry.setRelative(true);
                newEdge.setGeometry(edgeGeometry);
            }
        } finally {
            graph.getModel().endUpdate();
        }

        return graph;

    }



    public Node getTargetNode(String target,Node currentNode) {

        String[] elements = target.split("/");

        Node node = currentNode;
        for (String item:elements) {
            if(item.equals("..")) {
                assert node != null;
                node = node.getParentNode();
            } else {
                int index = Integer.parseInt(item);
                if(node !=null) {
                    Node nodeFound = node.getFirstChild();
                    int i = 0;
                    while (i <= index) {
                        nodeFound = nodeFound.getNextSibling();
                        if(nodeFound.getNodeType() == Element.ELEMENT_NODE) {
                            i++;
                        }

                    }

                    return nodeFound;
                }
            }
        }
        return null;
    }



    public String getStateId(String name) {
        for(Object o : statesId.entrySet()){
            Map.Entry entry = (Map.Entry) o;
            // THIS IS THE IMPORTANT LINE
            if(entry.getValue().equals(name))
            {
                return entry.getKey().toString();
            }
        }
        return null;
    }


    public Transition getEntryTransition(String targetStateId) {
        for (Transition transition:transitionList) {
            if (transition.getTargetStateId().equals(targetStateId)) {
                return transition;
            }
        }
        return null;
    }


    public void addStatesId(String name) {
        if(!this.statesId.containsValue(name)) {
            State state = new State(name);
            state.setInitial(false);
            stateList.add(state);
            statesId.put(state.getStateId(),name);
        }
    }

    public void setTransitionUpdate(String transitionId, String update) {
        if (this.transitionUpdates.containsKey(transitionId)) {

            String oldValue = this.transitionUpdates.get(transitionId);
            this.transitionUpdates.replace(transitionId, oldValue + ";" + update);

        } else {

            this.transitionUpdates.put(transitionId, update);

        }
    }

    public void setTransitionGuard(String transitionId, String guard) {
        if (this.transitionGuards.containsKey(transitionId)) {

            String oldValue = this.transitionGuards.get(transitionId);
            this.transitionGuards.replace(transitionId, oldValue + ";" + guard);

        } else {

            this.transitionGuards.put(transitionId, guard);

        }
    }


}
