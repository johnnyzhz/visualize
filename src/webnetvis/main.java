/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webnetvis;

import java.util.HashMap;

/**
 *
 * @author HP
 */
public class main {
    public static void main(String[] args){
        
        HashMap<String, Object> layout = new HashMap();
        HashMap<String, Object> node = new HashMap();
        HashMap<String, Object> input = new HashMap();
        HashMap<String, Object> edge = new HashMap();
        layout.put("type", "ForceAtlas2");
        layout.put("f2gravity", "2.0");
        layout.put("scalingRatio", "5.0"); 
        layout.put("strongGravityMode","true");     
        layout.put("edgeWeightInfluence", "1.0");        
        layout.put("f2barnesHutTheta", "1.0");
        layout.put("barnesHutOptimize", "false");
        layout.put("adjustSizes", "true");
        layout.put("cooling", "2.0");
        layout.put("freezeBalance", "true");
        layout.put("freezeInertia", "2.0");
        layout.put("freezeStrength", "80.0");
        layout.put("f1gravity", "1.0");
        layout.put("inertia", "1.0");
        layout.put("maxDisplacement", "10.0");
        layout.put("repulsionStrength", "200.0");
        layout.put("f1speed", "1.0");
        layout.put("size", "30.0");
        layout.put("margin", "5.0");
        layout.put("ratio", "1.2");
        layout.put("lapspeed", "3.0");
        layout.put("angle","90.0");
        layout.put("contract", "1.2");
        layout.put("expand", "20.0");
        layout.put("labelspeed", "1.0");
        layout.put("adjustBySize", "true");
        layout.put("frspeed", "1.0");
        layout.put("frgravity", "10.0");
        layout.put("area", "10000");
        layout.put("stepRatio", "2.0");
        layout.put("adaptiveCooling", "true");
        layout.put("step", "2.0");
        layout.put("relativeStrength", "80.0");
        layout.put("optimalDistance", "1.0");
        layout.put("convergenceThreshold", "1.0");
        layout.put("initialStep", "10.0");
        layout.put("yfhbarnesHutTheta", "200.0");
        layout.put("quadTreeMaxLevel", "1");
        
        
        node.put("size", "ranking");
        node.put("sizeRankingMethod", "inDegree");
        node.put("sizeValue", "8.0");    
        node.put("minSize", "1.0");
        node.put("maxSize", "8.0");
        node.put("color", "partition");
        node.put("colorPartitionMethod", "group");
        node.put("minColor","#0dde63");
        node.put("maxColor","#223344");
        input.put("nodesFile", "/tmp/nodes.csv");
        input.put("edgesFile", "/tmp/edges7.csv");
        input.put("posFile", "/tmp/data.csv");
        edge.put("color", "ranking");
        edge.put("colorRankingMethod", "weight");
        edge.put("minColor","#000663");
        edge.put("maxColor","#223344");
        Config cfg = new Config(layout, node, input, edge);              
        RankingGraph rankingGraph = new RankingGraph(cfg);       
        String result = rankingGraph.script();
        System.out.println(result);
    }
}
