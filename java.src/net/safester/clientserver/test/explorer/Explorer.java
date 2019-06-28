/*
 * This file is part of Safester.                                    
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.                                
 *                                                                               
 * Safester is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Safester is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package net.safester.clientserver.test.explorer;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Classe permettant d afficher l'arborescence des disque dur
 */
public class Explorer extends JFrame
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private File[] _roots;

    private JTree _dirs;

    public Explorer()
    {
        // taille de la fenetre
        setSize(500, 500);

        // on recupere les lecteurs
        _roots = File.listRoots();

        // on definit notre premier noeud
        DefaultMutableTreeNode racine = new DefaultMutableTreeNode(
                "Poste de travail", true);

        // Creation du jtree
        _dirs = new JTree(racine);
        _dirs.setSize(1000, 500);

        // pour chaque lecteur
        for (int i = 1; i < _roots.length; i++)
        {
            // on recupere son contenu grace a getSubDirs
            DefaultMutableTreeNode root = getSubDirs(_roots[i]);
            // et on l ajoute a notre premier noeud
            racine.add(root);

        }

        // on met le jtree dans un jscrollpane
        JScrollPane scroll = new JScrollPane(_dirs);
        scroll.setPreferredSize(new Dimension(600, 600));
        scroll
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // on ajoute notre jscrollpane
        getContentPane().add(scroll);
    }

    /**
     * Methode recursive permettant de recuperer tous les fichiers et sous
     * dossiers d un autre
     * 
     * @param root
     *            un File qui represente le lecteur ou le repertoire de depart
     * @return DefaultMutableTreeNode
     */
    public DefaultMutableTreeNode getSubDirs(File root)
    {
        // on cree un noeud
        DefaultMutableTreeNode racine = new DefaultMutableTreeNode(root, true);

        // on recupere la liste des fichiers et sous rep
        File[] list = root.listFiles();

        if (list != null)
        {
            // pour chaque sous rep on appel cette methode => recursivit�
            for (int j = 1; j < list.length; j++)
            {
                DefaultMutableTreeNode file = null;
                
                if (list[j].isDirectory())
                {
                    file = getSubDirs(list[j]);
                    racine.add(file);
                }
            }
        }
        return racine;
    }

    // main
    public static void main(String[] args)
    {
        Explorer ex = new Explorer();
        ex.setVisible(true);
    }

}
