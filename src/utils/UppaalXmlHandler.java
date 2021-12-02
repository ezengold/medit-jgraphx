package utils;

import app.App;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import models.Automata;
import models.State;
import models.Transition;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class UppaalXmlHandler {

    private static HashMap<String, String> excepts = new HashMap<String, String>();
    private String uppaalFilePath;
    private Document uppaalDoc;
    private Automata automata;


    public Document getUppaalDoc() {
        return uppaalDoc;
    }

    public String getUppaalFilePath() {
        return uppaalFilePath;
    }

    public void setUppaalFilePath(String uppaalFilePath) {
        this.uppaalFilePath = uppaalFilePath;
    }


    public Automata getAutomata() {
        return automata;
    }

    public UppaalXmlHandler(Automata automata, String uppaalFilePath) {
       this.automata = automata;
       this.uppaalFilePath = uppaalFilePath;
        this.uppaalDoc = null;
        excepts.put("<", "&lt;");
        excepts.put(">", "&gt;");
        excepts.put("&", "&amp;");
    }

    public void write() {
        this.uppaalDoc = createDoc();
        writeModel();
        writeDoc();

    }

    private Document createDoc() {
        Document document = null;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            document = docBuilder.newDocument();
        }catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return document;
    }

    private void writeDoc() {
        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("encoding", "UTF-8");
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("omit-xml-declaration", "no");
            transformer.setOutputProperty("method", "xml");
            DOMImplementation domImpl = this.uppaalDoc.getImplementation();
            DocumentType doctype = domImpl.createDocumentType("doctype","-//Uppaal Team//DTD Flat System 1.1//EN",
                    "http://www.it.uu.se/research/group/darts/uppaal/flat-1_2.dtd");
            transformer.setOutputProperty("doctype-public", doctype.getPublicId());
            transformer.setOutputProperty("doctype-system", doctype.getSystemId());
            this.uppaalDoc.setXmlStandalone(true);
            DOMSource source = new DOMSource(this.uppaalDoc);
            PrintWriter pw = new PrintWriter(this.uppaalFilePath, "utf-8");
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);


        }catch (TransformerException | FileNotFoundException | UnsupportedEncodingException te) {
            te.printStackTrace();
        }
    }





    public static String escapeStr(final String input) {
        String output = input;
        for (String token : excepts.keySet()) {
            output = output.replaceAll(token, excepts.get(token));
        }
        return output;
    }

    private void writeModel() {
        Element rootEle = this.uppaalDoc.createElement("nta");
        this.uppaalDoc.appendChild(rootEle);

        Element declarationEle = this.uppaalDoc.createElement("declaration");
        rootEle.appendChild(declarationEle);

        String declarations = "";
        for (int i = 0; i < this.automata.getDeclarationsList().size(); i++) {
            declarations = String.valueOf(declarations) + (String)this.automata.getDeclarationsList().get(i) + "\n";
        }

        declarationEle.appendChild(this.uppaalDoc.createTextNode(declarations));

        writeAutomata(rootEle);

        Element systemEle = this.uppaalDoc.createElement("system");
        rootEle.appendChild(systemEle);
        String name = automata.getName();
        String systemDeclaration = automata.getName().toLowerCase()+" = "+
                name.substring(0, 1).toUpperCase() + name.substring(1)+";";
        String systemStr = "system "+ automata.getName().toLowerCase()+";";
        systemEle.appendChild(this.uppaalDoc.createTextNode(systemDeclaration));
        systemEle.appendChild(this.uppaalDoc.createTextNode(systemStr));


    }


    private void writeAutomata(Element rootEle) {
        Element automataEle = this.uppaalDoc.createElement("template");
        rootEle.appendChild(automataEle);
        Element automataNameEle = this.uppaalDoc.createElement("name");
        automataNameEle.appendChild(this.uppaalDoc.createTextNode(automata.getName()));
        automataEle.appendChild(automataNameEle);

        //handling of states
        State state = null;
        for (int j = 0; j < automata.getStatesList().size(); j++) {
            state = automata.getStatesList().get(j);
            writeStates(automataEle,state);
        }

        Element initStateEle = this.uppaalDoc.createElement("init");
        initStateEle.setAttribute("ref", automata.getInitialStateId());
        automataEle.appendChild(initStateEle);


        //handling of transitions
        Transition transition = null;
        for (int k = 0; k < automata.getTransitionsList().size(); k++) {
            transition = automata.getTransitionsList().get(k);
            writeTransitions(automataEle, transition);

        }




    }

    private  void writeStates(Element automataEle, State state) {
        Element stateEle = this.uppaalDoc.createElement("location");
        stateEle.setAttribute("id", state.getStateId());
        stateEle.setAttribute("x", String.valueOf(state.getPosition().getX()));
        stateEle.setAttribute("y", String.valueOf(state.getPosition().getY()));
        automataEle.appendChild(stateEle);

        Element stateNameEle = this.uppaalDoc.createElement("name");
        stateNameEle.setAttribute("x", String.valueOf(state.getPosition().getX() - 8));
        stateNameEle.setAttribute("y", String.valueOf(state.getPosition().getY() - 8));
        stateNameEle.appendChild(this.uppaalDoc.createTextNode(state.getName()));
        stateEle.appendChild(stateNameEle);

        if(state.getInvariant()!= null && !state.getInvariant().isEmpty()) {
            Element invariantEle = this.uppaalDoc.createElement("label");
            invariantEle.setAttribute("kind", "invariant");
            invariantEle.setAttribute("x", String.valueOf(state.getPosition().getX() - 18));
            invariantEle.setAttribute("y", String.valueOf(state.getPosition().getY() + 10));
            invariantEle.appendChild(this.uppaalDoc.createTextNode(state.getInvariant()));



        }




    }


    private void writeTransitions(Element automataEle, Transition transition) {
        Element transitionEle = this.uppaalDoc.createElement("transition");
        automataEle.appendChild(transitionEle);

        Element transitionSourceEle = this.uppaalDoc.createElement("source");
        transitionSourceEle.setAttribute("ref", transition.getSourceStateId());
        transitionEle.appendChild(transitionSourceEle);


        Element transitionTargetEle = this.uppaalDoc.createElement("target");
        transitionTargetEle.setAttribute("ref", transition.getTargetStateId());
        transitionEle.appendChild(transitionTargetEle);


        Element transitionGuardEle = this.uppaalDoc.createElement("label");
        transitionGuardEle.setAttribute("kind", "guard");

        transitionGuardEle.setAttribute("x", "0");
        transitionGuardEle.setAttribute("y", "0");
        transitionGuardEle.appendChild(this.uppaalDoc.createTextNode(transition.getGuard()));
        transitionEle.appendChild(transitionGuardEle);


        if(transition.getUpdate()!=null && !transition.getUpdate().isEmpty() ) {
            Element transitionAssignmentEle = this.uppaalDoc.createElement("label");
            transitionAssignmentEle.setAttribute("kind", "assignment");
            transitionAssignmentEle.setAttribute("x", "0");
            transitionAssignmentEle.setAttribute("y", "15");
            transitionAssignmentEle.appendChild(this.uppaalDoc.createTextNode(transition.getUpdate()));
            transitionEle.appendChild(transitionAssignmentEle);
        }






    }









}
