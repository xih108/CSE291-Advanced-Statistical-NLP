//package edu.berkeley.nlp.assignments.parsing.student;
//
//import edu.berkeley.nlp.assignments.parsing.BinaryRule;
//import edu.berkeley.nlp.assignments.parsing.SimpleLexicon;
//import edu.berkeley.nlp.assignments.parsing.UnaryClosure;
//import edu.berkeley.nlp.assignments.parsing.UnaryRule;
//import edu.berkeley.nlp.ling.Tree;
//import edu.berkeley.nlp.util.Indexer;
//
//
//import java.util.*;
//import java.lang.*;
//
//public class BottomTop {
//
//    double[][][] bottom;
//    double[][][] top;
//
//    List<String> sentence;
//    NewGrammar grammar;
//    SimpleLexicon lexicon;
//    ArrayList<String> tags;
//    Indexer<String> labelIndexer;
//    UnaryClosure unaryClosure;
//
//    int terminals = 0;
//    int nonterminals = 0;
//    HashMap<Integer,List<Integer>>[][] biBP;
//    HashMap<Integer,Integer>[][] uniBP;
//
//    public BottomTop(List<String> sentence, NewGrammar grammar, SimpleLexicon lexicon, UnaryClosure unaryClosure) {
//        this.terminals = sentence.size();
//        this.labelIndexer = grammar.getLabelIndexer();
//        this.nonterminals = labelIndexer.size();
//        this.unaryClosure = unaryClosure;
//        this.sentence = sentence;
//        this.grammar = grammar;
//        this.lexicon = lexicon;
//        Set<String> tagset = lexicon.getAllTags();
//        this.tags = new ArrayList<String>(tagset);
//        System.out.println(terminals);
//        System.out.println(nonterminals);
//
//
//        bottom = new double[nonterminals][terminals+1][terminals+1];
//        top = new double[nonterminals][terminals+1][terminals+1];
//        for (double[][] row : bottom) {
//            for (double[] col : row) {
//                Arrays.fill(col, Double.NEGATIVE_INFINITY);
//            }
//        }
//        for (double[][] row : top) {
//            for (double[] col : row) {
//                Arrays.fill(col, Double.NEGATIVE_INFINITY);
//            }
//        }
//
//        this.biBP = new HashMap[terminals+1][terminals+1];
//        this.uniBP = new HashMap[terminals+1][terminals+1];
//        for (int i = 0; i < terminals + 1; i++) {
//            for (int j = 0; j < terminals + 1; j++) {
//                this.biBP[i][j] = new HashMap<Integer, List<Integer>>();
//                this.uniBP[i][j] = new HashMap<Integer, Integer>();
//            }
//        }
//    }
//
//    public void bestScore() {
//        for (int i = 0; i < terminals; i++) {
//            for (String tag : this.tags) {
//                Double tagscore = lexicon.scoreTagging(sentence.get(i), tag);
//                if (!tagscore.isNaN() && !tagscore.isInfinite()) {
//                    int X = labelIndexer.indexOf(tag);
//                    bottom[X][i][i+1] = tagscore;
//
//                    List<Integer> bpinfo = new ArrayList<>();
//                    bpinfo.add(-1);
//                    bpinfo.add(-1);
//                    bpinfo.add(0);
//                    biBP[i][i+1].put(X, bpinfo);
//                }
//            }
//        }
//
//
//        for (int diff = 1; diff <= terminals; diff ++){
//            if (diff >= 2 ) {
//                for (int i = 0; i <= terminals - diff; i++) {
//                    int j = i + diff;
////                    for (BinaryRule binaryRule : grammar.getBinaryRules()) {
////                        int X = binaryRule.getParent();
////                        int Y = binaryRule.getLeftChild();
////                        int Z = binaryRule.getRightChild();
////
////                        for (int k = i + 1; k <= j - 1; k++) {
////                            double tmpscore = binaryRule.getScore() + top[Y][i][k] + top[Z][k][j];
////
////                            if (tmpscore > bottom[X][i][j]) {
////
////                                List<Integer> bpinfo = new ArrayList<>();
////                                bpinfo.add(Y);
////                                bpinfo.add(Z);
////                                bpinfo.add(k);
////                                biBP[i][j].put(X, bpinfo);
////                                bottom[X][i][j] = tmpscore;
////                            }
////                        }
////                    }
//                    for (int k = i + 1; k <= j - 1; k ++){
//                        for (int Y = 0; Y < nonterminals; Y ++){
//                            Double leftscore = top[Y][i][k];
//                            if (!leftscore.isInfinite() && !leftscore.isNaN()){
//                                for (BinaryRule binaryRule: grammar.getBinaryRulesByLeftChild(Y)){
//                                    int X = binaryRule.getParent();
//                                    int Z = binaryRule.getRightChild();
//                                    double tmpscore = binaryRule.getScore() + top[Y][i][k] + top[Z][k][j];
//
//                                    if (tmpscore > bottom[X][i][j]) {
//
//                                        List<Integer> bpinfo = new ArrayList<>();
//                                        bpinfo.add(Y);
//                                        bpinfo.add(Z);
//                                        bpinfo.add(k);
//                                        biBP[i][j].put(X, bpinfo);
//                                        bottom[X][i][j] = tmpscore;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            for (int i = 0; i <= terminals - diff; i++) {
//                int j = i + diff;
////                for (int X = 0; X < nonterminals; X++){
////                    for (UnaryRule unaryClosure : unaryClosure.getClosedUnaryRulesByParent(X)) {
////                        int Y = unaryClosure.getChild();
////                        Double prev = bottom[Y][i][j];
////                        if (prev.isInfinite() || prev.isNaN()){
////                           continue;
////                        }
////                        double tmpscore = unaryClosure.getScore() + prev;
//////                        System.out.println("uc "+ unaryClosure.getScore() + "bottom" +bottom[Y][i][j]+"  X "+ X+" Y "+Y);
////                        if (tmpscore > top[X][i][j]) {
////                            uniBP[i][j].put(X, Y);
////                            top[X][i][j] = tmpscore;
////                        }
////                    }
////                }
//                for (int Y = 0; Y < nonterminals; Y ++){
//                    Double prev = bottom[Y][i][j];
//                    if (!prev.isInfinite() && !prev.isNaN()){
//                        for (UnaryRule unaryClosure : unaryClosure.getClosedUnaryRulesByChild(Y)) {
//                            int X = unaryClosure.getParent();
//                            double tmpscore = unaryClosure.getScore() + prev;
//                            if (tmpscore > top[X][i][j]) {
//                                uniBP[i][j].put(X, Y);
//                                top[X][i][j] = tmpscore;
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
//
//    }
//
//    public Tree<String> backTrack() {
//        bestScore();
//        System.out.println("done scoring");
//        int ROOT = labelIndexer.indexOf("ROOT");
//
//        int i = 0;
//        int j = terminals;
//
//        System.out.println("ROOT "+ROOT +"score" + top[ROOT][i][j]);
//
//        Double  topRoot = top[ROOT][i][j];
//        if (topRoot.isInfinite() || topRoot.isNaN()){
//            return new Tree<String>("ROOT", Collections.singletonList(new Tree<String>("JUNK")));
//        }
//
//        return buildUnary(i, j, ROOT);
//    }
//
//    public Tree<String> buildUnary(int i, int j, int X){
//
//        int Y = uniBP[i][j].get(X);
//        if (X == Y){
//            return buildBinary(i, j, Y);
//        }
//        UnaryRule unaryRule = new UnaryRule(X, Y);
//        List<Integer> path = unaryClosure.getPath(unaryRule);
////        System.out.println("X "+X+" Y "+Y);
//        if (path.size() > 2){
//            Tree<String> subtree = new Tree<String> (labelIndexer.get(X));
//            Tree<String> curr = subtree;
//            for (int p = 1; p < path.size()-1; p++){
////                System.out.println(path.get(p) +"->");
//                List<Tree<String>> child = new ArrayList<Tree<String>>();
//                child.add(new Tree<String>(labelIndexer.get(path.get(p))));
//                curr.setChildren(child);
//                curr = curr.getChildren().get(0);
//            }
//            curr.setChildren(Collections.singletonList((buildBinary(i, j, Y))));
//            return subtree;
//        }
//
//        return new Tree<String> (labelIndexer.get(X), Collections.singletonList((buildBinary(i, j, Y))));
//    }
//
//
//    public Tree<String> buildBinary(int i, int j, int X){
//
//        int Y = biBP[i][j].get(X).get(0);
//        int Z = biBP[i][j].get(X).get(1);
//
//        if (Y == -1 && Z == -1){
//
//            return new Tree<String>(labelIndexer.get(X),
//                    Collections.singletonList(new Tree<String >(sentence.get(i))));
//        }
//        List<Tree<String>> children = new ArrayList<Tree<String>>();
//
//        int k = biBP[i][j].get(X).get(2);
//        children.add(buildUnary(i, k, Y));
//        children.add(buildUnary(k, j, Z));
//
//        return new Tree<String> (labelIndexer.get(X), children);
//    }
//
//
//
//
//}

package edu.berkeley.nlp.assignments.parsing.student;

import edu.berkeley.nlp.assignments.parsing.BinaryRule;
import edu.berkeley.nlp.assignments.parsing.SimpleLexicon;
import edu.berkeley.nlp.assignments.parsing.UnaryClosure;
import edu.berkeley.nlp.assignments.parsing.UnaryRule;
import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.util.Indexer;


import java.util.*;
import java.lang.*;

public class BottomTop {

    double[][][] bottom;
    double[][][] top;

    List<String> sentence;
    NewGrammar grammar;
    SimpleLexicon lexicon;
    ArrayList<String> tags;
    Indexer<String> labelIndexer;
    UnaryClosure unaryClosure;

    int terminals = 0;
    int nonterminals = 0;
    HashMap<Integer,List<Integer>>[][] biBP;
    HashMap<Integer,Integer>[][] uniBP;

    public BottomTop(List<String> sentence, NewGrammar grammar, SimpleLexicon lexicon, UnaryClosure unaryClosure) {
        this.terminals = sentence.size();
        this.labelIndexer = grammar.getLabelIndexer();
        this.nonterminals = labelIndexer.size();
        this.unaryClosure = unaryClosure;
        this.sentence = sentence;
        this.grammar = grammar;
        this.lexicon = lexicon;
        Set<String> tagset = lexicon.getAllTags();
        this.tags = new ArrayList<String>(tagset);
        System.out.println(terminals);
        System.out.println(nonterminals);


        bottom = new double[terminals+1][terminals+1][nonterminals];
        top = new double[terminals+1][terminals+1][nonterminals];
        for (double[][] row : bottom) {
            for (double[] col : row) {
                Arrays.fill(col, Double.NEGATIVE_INFINITY);
            }
        }
        for (double[][] row : top) {
            for (double[] col : row) {
                Arrays.fill(col, Double.NEGATIVE_INFINITY);
            }
        }

        this.biBP = new HashMap[terminals+1][terminals+1];
        this.uniBP = new HashMap[terminals+1][terminals+1];
        for (int i = 0; i < terminals + 1; i++) {
            for (int j = 0; j < terminals + 1; j++) {
                this.biBP[i][j] = new HashMap<Integer, List<Integer>>();
                this.uniBP[i][j] = new HashMap<Integer, Integer>();
            }
        }
    }

    public void bestScore() {
        for (int i = 0; i < terminals; i++) {
            for (String tag : this.tags) {
                Double tagscore = lexicon.scoreTagging(sentence.get(i), tag);
                if (!tagscore.isNaN() && !tagscore.isInfinite()) {
                    int X = labelIndexer.indexOf(tag);
                    bottom[i][i+1][X] = tagscore;

                    List<Integer> bpinfo = new ArrayList<>();
                    bpinfo.add(-1);
                    bpinfo.add(-1);
                    bpinfo.add(0);
                    biBP[i][i+1].put(X, bpinfo);
                }
            }
        }


        for (int diff = 1; diff <= terminals; diff ++){
            if (diff >= 2 ) {
                for (int i = 0; i <= terminals - diff; i++) {
                    int j = i + diff;
//                    for (BinaryRule binaryRule : grammar.getBinaryRules()) {
//                        int X = binaryRule.getParent();
//                        int Y = binaryRule.getLeftChild();
//                        int Z = binaryRule.getRightChild();
//
//                        for (int k = i + 1; k <= j - 1; k++) {
//                            double tmpscore = binaryRule.getScore() + top[Y][i][k] + top[Z][k][j];
//
//                            if (tmpscore > bottom[X][i][j]) {
//
//                                List<Integer> bpinfo = new ArrayList<>();
//                                bpinfo.add(Y);
//                                bpinfo.add(Z);
//                                bpinfo.add(k);
//                                biBP[i][j].put(X, bpinfo);
//                                bottom[X][i][j] = tmpscore;
//                            }
//                        }
//                    }
                    for (int k = i + 1; k <= j - 1; k ++){
                        for (int Y = 0; Y < nonterminals; Y ++){
                            Double leftscore = top[i][k][Y];
                            if (!leftscore.isInfinite() && !leftscore.isNaN()){
                                for (BinaryRule binaryRule: grammar.getBinaryRulesByLeftChild(Y)){
                                    int X = binaryRule.getParent();
                                    int Z = binaryRule.getRightChild();
                                    double tmpscore = binaryRule.getScore() + top[i][k][Y] + top[k][j][Z];

                                    if (tmpscore > bottom[i][j][X]) {

                                        List<Integer> bpinfo = new ArrayList<>();
                                        bpinfo.add(Y);
                                        bpinfo.add(Z);
                                        bpinfo.add(k);
                                        biBP[i][j].put(X, bpinfo);
                                        bottom[i][j][X] = tmpscore;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i <= terminals - diff; i++) {
                int j = i + diff;
//                for (int X = 0; X < nonterminals; X++){
//                    for (UnaryRule unaryClosure : unaryClosure.getClosedUnaryRulesByParent(X)) {
//                        int Y = unaryClosure.getChild();
//                        Double prev = bottom[Y][i][j];
//                        if (prev.isInfinite() || prev.isNaN()){
//                           continue;
//                        }
//                        double tmpscore = unaryClosure.getScore() + prev;
////                        System.out.println("uc "+ unaryClosure.getScore() + "bottom" +bottom[Y][i][j]+"  X "+ X+" Y "+Y);
//                        if (tmpscore > top[X][i][j]) {
//                            uniBP[i][j].put(X, Y);
//                            top[X][i][j] = tmpscore;
//                        }
//                    }
//                }
                for (int Y = 0; Y < nonterminals; Y ++){
                    Double prev = bottom[i][j][Y];
                    if (!prev.isInfinite() && !prev.isNaN()){
                        for (UnaryRule unaryClosure : unaryClosure.getClosedUnaryRulesByChild(Y)) {
                            int X = unaryClosure.getParent();
                            double tmpscore = unaryClosure.getScore() + prev;
                            if (tmpscore > top[i][j][X]) {
                                uniBP[i][j].put(X, Y);
                                top[i][j][X] = tmpscore;
                            }
                        }
                    }
                }
            }

        }

    }

    public Tree<String> backTrack() {
        bestScore();
        System.out.println("done scoring");
        int ROOT = labelIndexer.indexOf("ROOT");

        int i = 0;
        int j = terminals;

        System.out.println("ROOT "+ROOT +"score" + top[ROOT][i][j]);

        Double  topRoot = top[i][j][ROOT];
        if (topRoot.isInfinite() || topRoot.isNaN()){
            return new Tree<String>("ROOT", Collections.singletonList(new Tree<String>("JUNK")));
        }

        return buildUnary(i, j, ROOT);
    }

    public Tree<String> buildUnary(int i, int j, int X){

        int Y = uniBP[i][j].get(X);
        if (X == Y){
            return buildBinary(i, j, Y);
        }
        UnaryRule unaryRule = new UnaryRule(X, Y);
        List<Integer> path = unaryClosure.getPath(unaryRule);
//        System.out.println("X "+X+" Y "+Y);
        if (path.size() > 2){
            Tree<String> subtree = new Tree<String> (labelIndexer.get(X));
            Tree<String> curr = subtree;
            for (int p = 1; p < path.size()-1; p++){
//                System.out.println(path.get(p) +"->");
                List<Tree<String>> child = new ArrayList<Tree<String>>();
                child.add(new Tree<String>(labelIndexer.get(path.get(p))));
                curr.setChildren(child);
                curr = curr.getChildren().get(0);
            }
            curr.setChildren(Collections.singletonList((buildBinary(i, j, Y))));
            return subtree;
        }

        return new Tree<String> (labelIndexer.get(X), Collections.singletonList((buildBinary(i, j, Y))));
    }


    public Tree<String> buildBinary(int i, int j, int X){

        int Y = biBP[i][j].get(X).get(0);
        int Z = biBP[i][j].get(X).get(1);

        if (Y == -1 && Z == -1){

            return new Tree<String>(labelIndexer.get(X),
                    Collections.singletonList(new Tree<String >(sentence.get(i))));
        }
        List<Tree<String>> children = new ArrayList<Tree<String>>();

        int k = biBP[i][j].get(X).get(2);
        children.add(buildUnary(i, k, Y));
        children.add(buildUnary(k, j, Z));

        return new Tree<String> (labelIndexer.get(X), children);
    }

}
