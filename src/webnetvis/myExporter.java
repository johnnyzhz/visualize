/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webnetvis;
import uk.ac.ox.oii.sigmaexporter.SigmaExporter;
import uk.ac.ox.oii.sigmaexporter.model.ConfigFile;
/**
 *
 * @author HP
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.gephi.statistics.plugin.Degree;
import uk.ac.ox.oii.sigmaexporter.model.ConfigFile;
import uk.ac.ox.oii.sigmaexporter.model.GraphEdge;
import uk.ac.ox.oii.sigmaexporter.model.GraphElement;
import uk.ac.ox.oii.sigmaexporter.model.GraphNode;
import uk.ac.ox.oii.sigmaexporter.ZipHandler;

public class myExporter extends SigmaExporter{
    private ConfigFile config;
    private String path;
    private boolean renumber;
    private Workspace workspace;
    private ProgressTicket progress;
    private boolean cancel = false;
    public Config graphCfg;
    public myExporter(Config cfg)
    {
        this.graphCfg = cfg;
    }
    public boolean execute() {
        try {
            final File pathFile = new File(path);
            if (pathFile.exists()) {

                
                OutputStreamWriter writer = null;
                FileOutputStream outStream = null;
                final Charset utf8 = Charset.forName("UTF-8");

                

                //Gson to handle JSON writing and escape
                Gson gson = new Gson();
                Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
                    
                HashMap<String,String> nodeIdMap = new HashMap<String,String>();
                int nodeId=0;
                EdgeColor colorMixer = new EdgeColor(EdgeColor.Mode.MIXED);
                //Write data.json
                Graph graph = null;
                try {
                    GraphModel graphModel = workspace.getLookup().lookup(GraphModel.class);
                    graph = graphModel.getGraphVisible();
                    graph.readLock();

                    //Count the number of tasks (nodes + edges) and start the progress
                    int tasks = graph.getNodeCount() + graph.getEdgeCount();
                    Progress.start(progress, tasks);
                    
                    
                    
                    
                    Table attModel = graphModel.getNodeTable();
                    HashSet<GraphElement> jNodes = new HashSet<GraphElement>();
                    Node[] nodeArray = graph.getNodes().toArray();
                    for (Node n : nodeArray) {
                        String id = n.getId().toString();
                        String label = n.getLabel();
                       
                        float x = 0f;
                        float y = 0f;
                        System.out.println(graphCfg.layout.get("type"));
                        if(graphCfg.layout.get("type").equals("Customized")){
                            System.out.println("position in file!");
                            //x = (float)n.getAttribute("x");
                            String typeX = n.getAttribute("x").getClass().getName();
                            String typeY = n.getAttribute("y").getClass().getName();
                            if(typeX.equals("java.lang.Double")){
                                double xd = (double)n.getAttribute("x"); 
                                x = (float)xd;
                            }
                            else
                            {
                                int xd = (int)n.getAttribute("x"); 
                                x = (float)xd;
                            }
                            if(typeY.equals("java.lang.Double")){
                                double yd = (double)n.getAttribute("y"); 
                                y = (float)yd;
                            }
                            else
                            {
                                int yd = (int)n.getAttribute("y"); 
                                y = (float)yd;
                            }
                            
                        }
                        else{
                            System.out.println("position calculated!");
                            x = n.x();
                            y = n.y();
                        }
                        System.out.println(label +"   "+ String.valueOf(x) + "   " + String.valueOf(y));
                        float size = n.size();
                        String color = "rgb(" + (int) (n.r() * 255) + "," + (int) (n.g() * 255) + "," + (int) (n.b() * 255) + ")";

                        if (renumber) {
                           String newId=String.valueOf(nodeId);
                           nodeIdMap.put(id,newId); 
                           id=newId;
                           nodeId++;
                        }
                        
                        GraphNode jNode = new GraphNode(id);
                        jNode.setLabel(label);
                        jNode.setX(x);
                        jNode.setY(y);
                        jNode.setSize(size);
                        jNode.setColor(color);

                        for (Column col : attModel) {
                            String cid = col.getId();
                            if (cid.equalsIgnoreCase("id") || cid.equalsIgnoreCase("label")) {
                                continue;
                            }

                            Object valObj = n.getAttribute(col);
                            if (valObj == null) {
                                continue;
                            }
                            String name = col.getTitle();
                            System.out.println(name);
                            
                            String val = valObj.toString();
                            jNode.putAttribute(name, val);
                        }

                        jNodes.add(jNode);

                        if (cancel) {
                            return false;
                        }
                        Progress.progress(progress);
                    }


                    //Export edges. Progress is incremented at each step.
                    HashSet<GraphElement> jEdges = new HashSet<GraphElement>();
                    Edge[] edgeArray = graph.getEdges().toArray();
                    for (Edge e : edgeArray) {
                        String sourceId = e.getSource().getId().toString();
                        String targetId = e.getTarget().getId().toString();
                        
                        if (renumber) {
                            sourceId = nodeIdMap.get(sourceId);
                            targetId = nodeIdMap.get(targetId);
                        }
                        

                        //GraphEdge jEdge = new GraphEdge();
                        GraphEdge jEdge = new GraphEdge(String.valueOf(e.getId()));
                        jEdge.setSource(sourceId);
                        jEdge.setTarget(targetId);
                        jEdge.setSize(e.getWeight());
                        jEdge.setLabel(e.getLabel());
                        
                        float r=e.r();
                        float g=e.g();
                        float b=e.b();

                        Iterator<Column> eAttr = e.getAttributeColumns().iterator();
                        while (eAttr.hasNext()) {
                            Column col = eAttr.next();
//                            if (col.isProperty() || "weight".equalsIgnoreCase(col.getId())) {
//                                //isProperty() excludes id, label, but not weight
//                                continue;
//                            }
                            String name = col.getTitle();
                            Object valObj = e.getAttribute(col);
                            if (valObj == null) {
                                continue;
                            }
                            String val = valObj.toString();
                            jEdge.putAttribute(name, val);
                        }

                        String color;
                        if (e.alpha()!=0) {
                            color = "rgb(" + (int) (r* 255) + "," + (int) (g* 255) + "," + (int) (b* 255) + ")";
                        } else {
                            //no colour has been set. Colour will be mix of connected nodes
                            Node n = e.getSource();
                            Color source = new Color(n.r(),n.g(),n.b());
                            n = e.getTarget();
                            Color target = new Color(n.r(),n.g(),n.b());
                            Color result = colorMixer.getColor(null, source, target);
                            color = "rgb(" + result.getRed() + "," + result.getGreen() + "," + result.getBlue() + ")";
                        }
                        jEdge.setColor(color);

                        jEdges.add(jEdge);

                        if (cancel) {
                            return false;
                        }
                        Progress.progress(progress);
                    }


                    outStream = new FileOutputStream(pathFile.getAbsolutePath() + "/data.json");
                    writer = new OutputStreamWriter(outStream,utf8);
                    
                    HashMap<String, HashSet<GraphElement>> json = new HashMap<String, HashSet<GraphElement>>();
                    json.put("nodes", jNodes);
                    json.put("edges", jEdges);
                    
                    gson.toJson(json, writer);
                    
                } catch (Exception e) {
                    Logger.getLogger(SigmaExporter.class.getName()).log(Level.SEVERE, null, e);
                } finally {
                    if (graph!=null) {
                        graph.readUnlock();
                    }
                    if (writer != null) {
                        writer.close();
                        writer = null;
                    }
                    if (outStream != null) {
                        outStream.close();
                        outStream = null;
                    }
                }
            } else {
                throw new Exception("Invalid path. Please make sure the specified directory exists. The network will be exported into a new 'network' directory in this directory.");
            }
        } catch (Exception e) {
            Logger.getLogger(SigmaExporter.class.getName()).log(Level.SEVERE, null, e);
        }
        //Finish progress
        Progress.finish(progress);
        return !cancel; //true if task has not been cancelled and we've gotten to the end
    }
    public ConfigFile getConfigFile() {
        return config;
    }

    public List<String> getNodeAttributes() {
        List<String> attr = new ArrayList<String>();
        GraphModel graphModel = workspace.getLookup().lookup(GraphModel.class);
        Table attModel = graphModel.getNodeTable();
        for (Column col : attModel) {
            attr.add(col.getTitle());
        }
        return attr;
    }

    public void setConfigFile(ConfigFile cfg, String path, boolean renumber) {
        this.config = cfg;
        this.path = path;
        this.renumber = renumber;
    }

    @Override
    public void setWorkspace(Workspace wrkspc) {
        this.workspace = wrkspc;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket pt) {
        this.progress = pt;
    }   
}
