/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_sigma;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.nio.charset.Charset;
import org.gephi.layout.spi.LayoutProperty;
import org.gephi.io.processor.plugin.AppendProcessor;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.io.processor.spi.Processor;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingLabelSizeTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.statistics.plugin.Degree;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;
import org.gephi.datalab.impl.AttributeColumnsControllerImpl;
import org.gephi.graph.api.Graph;
import org.gephi.io.importer.api.EdgeMergeStrategy;
import org.gephi.layout.plugin.rotate.RotateLayout;

import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.AbstractForce;
import org.gephi.layout.plugin.rotate.Rotate;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.layout.plugin.scale.ContractLayout;
import org.gephi.layout.plugin.scale.ExpandLayout;
import org.gephi.layout.plugin.noverlap.NoverlapLayout;
import org.gephi.layout.plugin.random.RandomLayout;

import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.labelAdjust.LabelAdjust;
import uk.ac.ox.oii.sigmaexporter.SigmaExporter;
import uk.ac.ox.oii.sigmaexporter.model.ConfigFile;
import uk.ac.ox.oii.jsonexporter.JSONExporter;
import java.io.BufferedReader;

import java.io.InputStreamReader;
/**
 *
 * @author HP
 */
public class RankingGraph {
    
    public Config cfg;
    public RankingGraph(Config cfg){
        this.cfg = cfg;
    }

    public String script() {
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();

        //Import file
        //Import file
        Container container,container2;
        try {
            String nodesFile = (String)cfg.input.get("nodesFile");
            String edgesFile = (String)cfg.input.get("edgesFile");
            
            //System.out.println(posFile);
            File file_node = new File(nodesFile);
            container = importController.importFile(file_node);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
            container.getLoader().setAllowAutoNode(true);  //create missing nodes
            container.getLoader().setEdgesMergeStrategy(EdgeMergeStrategy.SUM);
            container.getLoader().setAutoScale(true);

            File file_edge = new File(edgesFile);
            container2 = importController.importFile(file_edge);
            container2.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
            container2.getLoader().setAllowAutoNode(true);  //create missing nodes
            container2.getLoader().setEdgesMergeStrategy(EdgeMergeStrategy.SUM);
            container2.getLoader().setAutoScale(true);
            
            
//            container3.getLoader().setEdgesMergeStrategy(EdgeMergeStrategy.SUM);
//            container3.getLoader().setAutoScale(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        importController.process(container, new DefaultProcessor(), workspace);
        importController.process(container2, new AppendProcessor(), workspace); 
        

        //Append imported data to GraphAPI
        //importController.process(container, new DefaultProcessor(), workspace);

        //See if graph is well imported
        DirectedGraph graph = graphModel.getDirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());

        //Rank Size by Degree
        Function sizeRanking = null;
        // required : nodeSize
        String nodeSize = (String)cfg.node.get("size");
        switch(nodeSize)
        {
            case "ranking": 
            case "uniform":
                
                String sizeRankingMethod = (String)cfg.node.get("sizeRankingMethod");
                
                switch(sizeRankingMethod){
                    case "inDegree":
                        sizeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_INDEGREE, RankingNodeSizeTransformer.class);
                        break;
                    case "outDegree":
                        sizeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_OUTDEGREE, RankingNodeSizeTransformer.class);
                        break;
                    case "Degree":
                        sizeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingNodeSizeTransformer.class);
                        break;
                    default:
                        try{
                            Column attributeColumn = graphModel.getNodeTable().getColumn(sizeRankingMethod);                       
                            sizeRanking = appearanceModel.getNodeFunction(graph, attributeColumn, RankingNodeSizeTransformer.class);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            return null;
                        }
                }
                RankingNodeSizeTransformer sizeTransformer = (RankingNodeSizeTransformer) sizeRanking.getTransformer();
                float minSize = 1f;
                float maxSize = 1f;
                if(nodeSize.equals("uniform")){
                    float size = Float.parseFloat((String)cfg.node.get("sizeValue"));
                    minSize = maxSize = size;

                }
                else{

                    minSize = Float.parseFloat((String)cfg.node.get("minSize"));                      
                    maxSize = Float.parseFloat((String)cfg.node.get("maxSize"));

                }
                sizeTransformer.setMaxSize(maxSize);
                sizeTransformer.setMinSize(minSize);
                appearanceController.transform(sizeRanking);
                 
        }
        String nodeColor = (String)cfg.node.get("color");
        Function colorRanking = null;
        switch(nodeColor){
            case "uniform":
            case "ranking":
                
                String colorRankingMethod = (String)cfg.node.get("colorRankingMethod");
                
                switch(colorRankingMethod){
                    case "inDegree":
                        colorRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_INDEGREE, RankingElementColorTransformer.class);
                        break;
                    case "outDegree":
                        colorRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_OUTDEGREE, RankingElementColorTransformer.class);
                        break;
                    case "Degree":
                        colorRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingElementColorTransformer.class);
                        break;
                    default:
                        try{
                            Column attributeColumn = graphModel.getNodeTable().getColumn(colorRankingMethod);                       
                            colorRanking = appearanceModel.getNodeFunction(graph, attributeColumn, RankingElementColorTransformer.class);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            return null;
                        }
                }
                RankingElementColorTransformer colorTransformer = (RankingElementColorTransformer) colorRanking.getTransformer();
                int minColor = 0x000000;
                int maxColor = 0xffffff;
                if(nodeColor.equals("uniform")){
                    int color = Integer.parseInt(((String)cfg.node.get("colorValue")).substring(1),16);
                    minColor = maxColor = color;

                }
                else{

                        minColor = Integer.parseInt(((String)cfg.node.get("minColor")).substring(1),16);                                            
                        maxColor = Integer.parseInt(((String)cfg.node.get("maxColor")).substring(1),16);

                }
                colorTransformer.setColors(new Color[]{new Color(minColor), new Color(maxColor)});
                colorTransformer.setColorPositions(new float[]{0f, 1f});
                appearanceController.transform(colorRanking);
                break;
            case "partition":
                try{
                    String colorPartitionMethod = (String)cfg.node.get("colorPartitionMethod");
                    Column column = graphModel.getNodeTable().getColumn(colorPartitionMethod);
                    
                    Function func = appearanceModel.getNodeFunction(graph, column, PartitionElementColorTransformer.class);
                    Partition partition = ((PartitionFunction) func).getPartition();
                    Palette palette = PaletteManager.getInstance().generatePalette(partition.size());
                    partition.setColors(palette.getColors());
                    appearanceController.transform(func);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
                
        }
        String edgeColor = (String)cfg.edge.get("color");
        Function edgeColorRanking = null;
        switch(edgeColor){
            case "uniform":
            case "ranking":
                
                String edgeColorRankingMethod = (String)cfg.edge.get("colorRankingMethod");
                
                if (edgeColorRankingMethod.equals("weight")){
                    edgeColorRanking = appearanceModel.getEdgeFunction(graph, AppearanceModel.GraphFunction.EDGE_WEIGHT, RankingElementColorTransformer.class);                
                }
                else{
                    try{
      
                        Column edgeAttributeColumn = graphModel.getEdgeTable().getColumn(edgeColorRankingMethod);
                        edgeColorRanking = appearanceModel.getEdgeFunction(graph, edgeAttributeColumn, RankingElementColorTransformer.class);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
                RankingElementColorTransformer edgeColorTransformer = (RankingElementColorTransformer) edgeColorRanking.getTransformer();
                int minEdgeColor = 0x000000;
                int maxEdgeColor = 0x000000;
                if(edgeColor.equals("uniform")){
                    int colorValue = Integer.parseInt(((String)cfg.edge.get("colorValue")).substring(1),16);
                    minEdgeColor = maxEdgeColor = colorValue;
                                               
                }
                else{
                        
                        minEdgeColor = Integer.parseInt(((String)cfg.edge.get("minColor")).substring(1),16);                  
                        maxEdgeColor = Integer.parseInt(((String)cfg.edge.get("maxColor")).substring(1),16);
                    
                }
                edgeColorTransformer.setColors(new Color[]{new Color(minEdgeColor), new Color(maxEdgeColor)});
            
                appearanceController.transform(edgeColorRanking);
                break;
            
                
                
                
        }
                
        //Set the layout
        
        String layoutType = (String) cfg.layout.get("type");
        System.out.println(layoutType);
        //return layoutType;
        switch(layoutType)
        {
            case "ForceAtlas":
                ForceAtlasLayout f1layout = new ForceAtlasLayout(null);
                f1layout.setGraphModel(graphModel);
                //f1layout.setCooling(Double.parseDouble((String)cfg.layout.get("cooling")));
                f1layout.setFreezeBalance(Boolean.parseBoolean((String)cfg.layout.get("freezeBalance")));
                f1layout.setFreezeInertia(Double.parseDouble((String)cfg.layout.get("freezeInertia")));
                f1layout.setFreezeStrength(Double.parseDouble((String)cfg.layout.get("freezeStrength")));
                f1layout.setRepulsionStrength(Double.parseDouble((String)cfg.layout.get("repulsionStrength")));
                f1layout.setSpeed(Double.parseDouble((String)cfg.layout.get("f1speed")));
                f1layout.setInertia(Double.parseDouble((String)cfg.layout.get("inertia")));
                f1layout.setMaxDisplacement(Double.parseDouble((String)cfg.layout.get("maxDisplacement")));
                f1layout.setGravity(Double.parseDouble((String)cfg.layout.get("f1gravity")));
                f1layout.initAlgo();
                for (int i = 0; i < 100 && f1layout.canAlgo(); i++) {
                    f1layout.goAlgo();
                }
                f1layout.endAlgo();
                break;
               
            case "ForceAtlas2":
                ForceAtlas2 f2layout = new ForceAtlas2(null);
                f2layout.resetPropertiesValues();
                double gravity = Double.parseDouble((String)cfg.layout.get("f2gravity"));
                f2layout.setGravity(gravity);     
                double scalingRatio = Double.parseDouble((String)cfg.layout.get("scalingRatio"));
                f2layout.setScalingRatio(scalingRatio);
                f2layout.setAdjustSizes(Boolean.parseBoolean((String)cfg.layout.get("adjustSizes")));
                f2layout.setBarnesHutOptimize(Boolean.parseBoolean((String)cfg.layout.get("barnesHutOptimize")));
                f2layout.setBarnesHutTheta(Double.parseDouble((String)cfg.layout.get("f2barnesHutTheta")));
                f2layout.setEdgeWeightInfluence(Double.parseDouble((String)cfg.layout.get("edgeWeightInfluence")));
                f2layout.setStrongGravityMode(Boolean.parseBoolean((String)cfg.layout.get("strongGravityMode")));
                f2layout.setGraphModel(graphModel);
                f2layout.initAlgo();
                for (int i = 0; i < 100 && f2layout.canAlgo(); i++) {
                    f2layout.goAlgo();
                }
                f2layout.endAlgo();
                break; 
            case "RandomLayout":
                RandomLayout ranlayout = new RandomLayout(null, Double.parseDouble((String)cfg.layout.get("sizes")));
                ranlayout.setGraphModel(graphModel);
                ranlayout.initAlgo();
                for (int i = 0; i < 100 && ranlayout.canAlgo(); i++) {
                    ranlayout.goAlgo();
                }
                ranlayout.endAlgo();
                break;
            case "NoverlapLayout":
                NoverlapLayout noverlayout = new NoverlapLayout(null);
                
                noverlayout.setMargin(Double.parseDouble((String)cfg.layout.get("margin")));
                noverlayout.setSpeed(Double.parseDouble((String)cfg.layout.get("lapspeed")));
                noverlayout.setRatio(Double.parseDouble((String)cfg.layout.get("ratio")));
                noverlayout.setGraphModel(graphModel);
                noverlayout.initAlgo();
                for (int i = 0; i < 100 && noverlayout.canAlgo(); i++) {
                    noverlayout.goAlgo();
                }
                noverlayout.endAlgo();
                break;
            case "RotateLayout":
                RotateLayout rotatelayout = new RotateLayout(null, Double.parseDouble((String)cfg.layout.get("angle")));
                rotatelayout.setGraphModel(graphModel);
                rotatelayout.initAlgo();
                for (int i = 0; i < 100 && rotatelayout.canAlgo(); i++) {
                    rotatelayout.goAlgo();
                }
                rotatelayout.endAlgo();
                break;
            case "ContractLayout":
                ContractLayout conlayout = new ContractLayout(null, Double.parseDouble((String)cfg.layout.get("contract")));
                conlayout.setGraphModel(graphModel);
                conlayout.initAlgo();
                for (int i = 0; i < 100 && conlayout.canAlgo(); i++) {
                    conlayout.goAlgo();
                }
                conlayout.endAlgo();
                break;
            case "ExpandLayout":
                ExpandLayout explayout = new ExpandLayout(null, Double.parseDouble((String)cfg.layout.get("expand")));
                explayout.setGraphModel(graphModel);
                explayout.initAlgo();
                for (int i = 0; i < 100 && explayout.canAlgo(); i++) {
                    explayout.goAlgo();
                }
                explayout.endAlgo();
                
                break;
            case "LabelAdjust":
                LabelAdjust labellayout = new LabelAdjust(null);
                labellayout.setGraphModel(graphModel);
                labellayout.setSpeed(Double.parseDouble((String)cfg.layout.get("labelspeed")));
                labellayout.setAdjustBySize(Boolean.parseBoolean((String)cfg.layout.get("adjustBySize")));
                labellayout.initAlgo();
                for (int i = 0; i < 100 && labellayout.canAlgo(); i++) {
                    labellayout.goAlgo();
                }
                labellayout.endAlgo();
                break;
            case "FruchtermanReingold":
                FruchtermanReingold ftrlayout = new FruchtermanReingold(null);
                ftrlayout.setGraphModel(graphModel);
                ftrlayout.setArea(Float.parseFloat((String)cfg.layout.get("area")));
                ftrlayout.setGravity(Double.parseDouble((String)cfg.layout.get("frgravity")));
                ftrlayout.setSpeed(Double.parseDouble((String)cfg.layout.get("frspeed")));
                for (int i = 0; i < 100 && ftrlayout.canAlgo(); i++) {
                    ftrlayout.goAlgo();
                }
                ftrlayout.endAlgo();
                break;
            case "YifanHuLayout":
                YifanHuLayout yfhlayout = new YifanHuLayout(null, new StepDisplacement(1f));
                yfhlayout.setGraphModel(graphModel);
                yfhlayout.setAdaptiveCooling(Boolean.parseBoolean((String)cfg.layout.get("adaptiveCooling")));
                yfhlayout.setOptimalDistance(Float.parseFloat((String)cfg.layout.get("optimalDistance")));
                yfhlayout.setRelativeStrength(Float.parseFloat((String)cfg.layout.get("relativeStrength")));
                yfhlayout.setBarnesHutTheta(Float.parseFloat((String)cfg.layout.get("yfhbarnesHutTheta")));
                yfhlayout.setInitialStep(Float.parseFloat((String)cfg.layout.get("initialStep")));
                yfhlayout.setQuadTreeMaxLevel(Integer.parseInt((String)cfg.layout.get("quadTreeMaxLevel")));
                yfhlayout.setConvergenceThreshold(Float.parseFloat((String)cfg.layout.get("convergenceThreshold")));
                //yfhlayout.setStep(Float.parseFloat((String)cfg.layout.get("step")));
                for (Column col : graphModel.getNodeTable()) {
                    System.out.println(col);
                }
                break;
            case "Customized":
                try{
                    String posFile = (String)cfg.input.get("posFile");
                    File file_pos = new File(posFile);
                    Container container3;
                    container3 = importController.importFile(file_pos);
                    container3.getLoader().getNodeColumn("x");  //create missing nodes 
                    importController.process(container3, new AppendProcessor(), workspace); 
//                    for (Column col : graphModel.getNodeTable()) {
//                        System.out.println(col);
//                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
//                for (Column col : graphModel.getNodeTable()) {
//                    System.out.println(col);
//                }
                break;
    
        }
               
        //Write to Sigma.js file
        Degree degree = new Degree();
        degree.execute(graphModel);
        myExporter sigmaExporter = new myExporter(this.cfg);
        ConfigFile cfg = new ConfigFile();
        HashMap infoPanel = cfg.getInformationPanel();
        
        infoPanel.put("groupByEdgeDirection", true);
        cfg.setInformationPanel(infoPanel);
//        HashMap sigma = cfg.getSigma();
//        HashMap drawProp = (HashMap)sigma.get("drawingProperties");
//        drawProp.put("defaultLabelSize", 50);
        HashMap features = cfg.getFeatures();
        features.put("hoverBehavior", "dim");
        String dest = String.valueOf(System.currentTimeMillis());
        String destfile = "/var/www/webnetvis/data/" + dest;
        File file = new File(destfile);
        if(!file.exists()){
            file.mkdir();
        }
        sigmaExporter.setConfigFile(cfg, destfile , false);
        //sigmaExporter.setConfigFile(cfg, "E:\\sigma.js" , false);
        sigmaExporter.setWorkspace(workspace);
        sigmaExporter.execute();
//        
        try {
            String[] args = new String[] {"python3", "/var/www/webnetvis/json_csv.py", destfile+"/data.json", destfile+"/data.csv"};
            
            //String[] args = new String[] {"python", "E:\\visualize\\test_sigma\\json_csv.py", "E:\\sigma.js\\data.json", "E:\\sigma.js\\data.csv"};
            Process pr = Runtime.getRuntime().exec(args);
            Runtime.getRuntime().exec("chmod 777 -R " + destfile);
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//            }
            in.close();
            pr.waitFor();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //convert csv file to html file
        try {            
            String[] args = new String[] {"csvtotable", destfile+"/data.csv", destfile+"/data.html"};
            //String[] args = new String[]{"csvtotable", "E:\\sigma.js\\data.csv", "E:\\sigma.js\\data.html"};
            Process pr = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//            }
            in.close();
            pr.waitFor();
            System.out.println("end");
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            String[] args= new String[] {"cp", "/var/www/webnetvis/test_json.html", destfile+"/index.html"};
            Process pr = Runtime.getRuntime().exec(args);
            pr.waitFor();
            System.out.println("end");
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("finish");
        //export the pdf file
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            //ec.exportFile(new File("E:\\sigma.js\\graph.pdf"));
            ec.exportFile(new File(destfile+"/graph.pdf"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return "data/" + dest;

        
    }
}