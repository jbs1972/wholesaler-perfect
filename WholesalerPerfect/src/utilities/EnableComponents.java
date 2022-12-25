/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.awt.Component;
import java.awt.Container;

/**
 *
 * @author Jayanta B. Sen
 */
public class EnableComponents {
    
    public static void enableComponents(Container container, boolean enable) 
    {
        Component[] components = container.getComponents();
        for (Component component : components) 
        {
            component.setEnabled(enable);
            if (component instanceof Container) 
            {
                enableComponents((Container)component, enable);
            }
        }
    }
    
}
