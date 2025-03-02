/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webnetvis;
import java.util.HashMap;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
/**
 *
 * @author HP
 */
public class Config {
    public HashMap<String, Object> layout;
    public HashMap<String, Object> node;
    public HashMap<String, Object> input;
    public HashMap<String, Object> edge;
//    public Config(){
//        this.layout = new HashMap<String, Object>();
//        this.node = new HashMap<String, Object>();
//        this.input = new HashMap<String, Object>();
//        this.edge = new HashMap<String, Object>();
//    }
    public Config(HashMap<String, Object> layout, HashMap<String, Object> node, HashMap<String, Object> input,
            HashMap<String, Object> edge)
    {
        this.edge = edge;
        this.node = node;
        this.input = input;
        this.layout = layout;
        
    }
    public void setProperties(){
        
        
        
    }
}
