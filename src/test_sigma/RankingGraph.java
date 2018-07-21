/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_sigma;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import org.gephi.io.processor.plugin.AppendProcessor;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingLabelSizeTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
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
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;
import org.gephi.datalab.impl.AttributeColumnsControllerImpl;
import org.gephi.graph.api.Graph;
import org.gephi.io.importer.api.EdgeMergeStrategy;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import uk.ac.ox.oii.sigmaexporter.SigmaExporter;
import uk.ac.ox.oii.sigmaexporter.model.ConfigFile;
/**
 *
 * @author HP
 */
public class RankingGraph {
    public String nodes_file;
    public String edges_file;
    public String nodeSize;
    public String layoutType;
    
    

    public void script() {
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
            System.out.println(nodes_file);
            File file_node = new File(nodes_file);
            container = importController.importFile(file_node);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
            container.getLoader().setAllowAutoNode(true);  //create missing nodes
            container.getLoader().setEdgesMergeStrategy(EdgeMergeStrategy.SUM);
            container.getLoader().setAutoScale(true);

            File file_edge = new File(edges_file);
            container2 = importController.importFile(file_edge);
            container2.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
            container2.getLoader().setAllowAutoNode(true);  //create missing nodes
            container2.getLoader().setEdgesMergeStrategy(EdgeMergeStrategy.SUM);
            container2.getLoader().setAutoScale(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
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
        Function degreeRanking = null;
        switch(nodeSize)
        {
                case "inDegree":       
                    degreeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_INDEGREE, RankingNodeSizeTransformer.class);
                    break;
                case "outDegree":
                    degreeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_OUTDEGREE, RankingNodeSizeTransformer.class);
                    break;
                case "Degree":                   
                    degreeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingNodeSizeTransformer.class);
                    break;
                    
        }
        RankingNodeSizeTransformer degreeTransformer = (RankingNodeSizeTransformer) degreeRanking.getTransformer();
        degreeTransformer.setMinSize(1);
        degreeTransformer.setMaxSize(6);
        appearanceController.transform(degreeRanking);
        
        
        // Set the layout
        switch(layoutType)
        {
            case "AutoLayout":
                AutoLayout autoLayout = new AutoLayout(30, TimeUnit.SECONDS);
                autoLayout.setGraphModel(graphModel);
                YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
                ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
                AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
                AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 500., 0f);//500 for the complete period
                autoLayout.addLayout(firstLayout, 0.5f);
                autoLayout.addLayout(secondLayout, 0.5f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
                autoLayout.execute();
                break;
            case "ForceAtlas2":
                ForceAtlas2 layout = new ForceAtlas2(null);
                layout.setGraphModel(graphModel);
                layout.resetPropertiesValues();

                layout.initAlgo();
                for (int i = 0; i < 100 && layout.canAlgo(); i++) {
                    layout.goAlgo();
                }
                layout.endAlgo();
                break;
                
            
        }
        
        
        //Rank edge by weight
       
        Function weightRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.EDGE_WEIGHT, RankingElementColorTransformer.class);
        
        if (weightRanking!=null){
            RankingElementColorTransformer colorTransformer = (RankingElementColorTransformer) weightRanking.getTransformer();       
            colorTransformer.setColors(new Color[]{new Color(0xFEF0D9), new Color(0xB30000)});
            colorTransformer.setColorPositions(new float[]{0f, 1f});
            appearanceController.transform(degreeRanking);
        }
        
        
        
        

        

        

        
           
        
        //Write to Sigma.js file
        SigmaExporter sigmaExporter = new SigmaExporter();
        ConfigFile cfg = new ConfigFile();
        HashMap infoPanel = cfg.getInformationPanel();
        infoPanel.put("groupByEdgeDirection", true);
        cfg.setInformationPanel(infoPanel);
        sigmaExporter.setConfigFile(cfg, System.getProperty("user.dir"), true);
        sigmaExporter.setWorkspace(workspace);
        sigmaExporter.execute();
        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("ranking.pdf"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }
}