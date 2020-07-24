package edu.berkeley.nlp.assignments.parsing.student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.tools.internal.ws.processor.generator.SeiGenerator;
import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.ling.Trees;
import edu.berkeley.nlp.util.Filter;

import javax.annotation.processing.SupportedSourceVersion;
import javax.swing.*;

/**
 * Class which contains code for annotating and binarizing trees for the
 * parser's use, and debinarizing and unannotating them for scoring.
 */
public class TreeBinarization
{

    public static Tree<String> annotateTree(Tree<String> unAnnotatedTree) {

        return binarizeTree(unAnnotatedTree);
    }

//    private static Tree<String> binarizeTree(Tree<String> tree) {
//        String label = tree.getLabel();
//        if (tree.isLeaf()) return new Tree<String>(label);
//        if (tree.getChildren().size() == 1) { return new Tree<String>(label, Collections.singletonList(binarizeTree(tree.getChildren().get(0)))); }
//        // otherwise, it's a binary-or-more local tree, so decompose it into a sequence of binary and unary trees.
//        String intermediateLabel = "@" + label + "->";
//        Tree<String> intermediateTree = binarizeTreeHelper(tree, 0, intermediateLabel);
//        return new Tree<String>(label, intermediateTree.getChildren());
//    }

    private static Tree<String> binarizeTree(Tree<String> tree) {
        String label = tree.getLabel();
        Tree<String> newbinarytree = binarizeTreeHelper(tree, 0, "");
//        return new Tree<String>(label, Collections.singletonList(newbinarytree));
        return newbinarytree;
    }


    private static Tree<String> binarizeTreeHelper(Tree<String> tree, int childindexd,
                                                   String intermediateLabel) {

        // already leaf
        if (tree.isLeaf()) return new Tree<String>(tree.getLabel());

        String curlabel="";
        if (intermediateLabel.equals("")){
            curlabel = tree.getLabel();
        }
        else{
            if (intermediateLabel.contains("^")){
                curlabel = tree.getLabel() + "^" + intermediateLabel.split("\\^", 0)[0];
            }
            else {
                curlabel = tree.getLabel() + "^" + intermediateLabel;
            }
        }

        // Unary
        if (tree.getChildren().size() == 1){
            return new Tree<String>(curlabel, Collections.singletonList(binarizeTreeHelper(
                    tree.getChildren().get(0), 0, curlabel)));
        }

        String rightlabel = "@" + curlabel + "->";
        Tree<String> newbinarytree = new Tree<String>(curlabel);
        Tree<String> curr = newbinarytree;
        for (int i = 0; i < tree.getChildren().size(); i++){

            List<Tree<String>> children = new ArrayList<Tree<String >>();
            Tree<String> child = tree.getChildren().get(i);
            String subrightlabel = rightlabel;
            if (i == 0) {
                subrightlabel += child.getLabel();
            }
            else if (i == 1){
                subrightlabel += tree.getChildren().get(0).getLabel() + "_" + child.getLabel();
            }
            else {
                subrightlabel += "..." + tree.getChildren().get(i-1).getLabel() + "_" + child.getLabel();
            }
//            System.out.println(subrightlabel);
            Tree<String> left = binarizeTreeHelper(tree.getChildren().get(i), i, curlabel);
            children.add(left);

            if (i == tree.getChildren().size()-1 ){
                curr.setChildren(children);
                break;
            }
            Tree<String> right = new Tree<String>(subrightlabel);
            children.add(right);
            curr.setChildren(children);

            curr = curr.getChildren().get(1);

        }


        return newbinarytree;
    }

//    private static Tree<String> binarizeTreeHelper(Tree<String> tree, int numChildrenGenerated, String intermediateLabel) {
//        Tree<String> leftTree = tree.getChildren().get(numChildrenGenerated);
//        List<Tree<String>> children = new ArrayList<Tree<String>>();
//        children.add(binarizeTree(leftTree));
//        if (numChildrenGenerated < tree.getChildren().size() - 1) {
//            Tree<String> rightTree = binarizeTreeHelper(tree, numChildrenGenerated + 1, intermediateLabel + "_" + leftTree.getLabel());
//            children.add(rightTree);
//        }
//        return new Tree<String>(intermediateLabel, children);
//    }

    public static Tree<String> unAnnotateTree(Tree<String> annotatedTree) {
        // Remove intermediate nodes (labels beginning with "@"
        // Remove all material on node labels which follow their base symbol (cuts anything after <,>,^,=,_ or ->)
        // Examples: a node with label @NP->DT_JJ will be spliced out, and a node with label NP^S will be reduced to NP
        Tree<String> debinarizedTree = Trees.spliceNodes(annotatedTree, new Filter<String>()
        {
            public boolean accept(String s) {
                return s.startsWith("@");
            }
        });
        Tree<String> unAnnotatedTree = (new Trees.LabelNormalizer()).transformTree(debinarizedTree);
        return unAnnotatedTree;
    }
}
