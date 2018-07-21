/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_sigma;

/**
 *
 * @author HP
 */
public class main {
    public static void main(String[] args){
        RankingGraph rankingGraph = new RankingGraph();
        if(args.length != 4){
            System.out.println("Wrong number of arguments");
            System.exit(0);
        }
        rankingGraph.nodes_file = args[0];
        rankingGraph.edges_file = args[1];
        //System.out.println(System.getProperty("user.dir"));
        
        if(args[2].equals("inDegree") == false && args[2].equals("outDegree") == false && args[2].equals("Degree") == false){
            System.out.println("Invalid argument :" + args[2]);   
            System.exit(0);
        }
        else
        {
            rankingGraph.nodeSize  = args[2];
        }
        if(args[3].equals("ForceAtlas2") == false && args[3].equals("AutoLayout") == false){
            System.out.println("Invalid argument :" + args[3]);  
            System.exit(0);
        }
        else
        {
            rankingGraph.layoutType  = args[3];
        }
        rankingGraph.script();
    }
}
