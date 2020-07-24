package edu.berkeley.nlp.assignments.parsing.student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import edu.berkeley.nlp.assignments.parsing.*;
import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.ling.Trees;
import edu.berkeley.nlp.util.CounterMap;
import edu.berkeley.nlp.util.Indexer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;


public class GenerativeParserFactory implements ParserFactory {

	public Parser getParser(List<Tree<String>> trainTrees) {

		return new GenerativeParser(trainTrees);
	}


	CounterMap<List<String>, Tree<String>> knownParses;

	CounterMap<Integer, String> spanToCategories;

	SimpleLexicon lexicon;

	NewGrammar grammar;

	UnaryClosure unaryClosure;

	public class GenerativeParser implements Parser {

		public Tree<String> getBestParse(List<String> sentence) {

			BottomTop cky = new BottomTop(sentence, grammar, lexicon, unaryClosure);
			Tree<String> annotatedBestParse = cky.backTrack();
			return TreeAnnotations.unAnnotateTree(annotatedBestParse);

		}


		private List<Tree<String>> annotateTrees(List<Tree<String>> trees) {
			List<Tree<String>> annotatedTrees = new ArrayList<Tree<String>>();
			for (Tree<String> tree : trees) {
				annotatedTrees.add(TreeBinarization.annotateTree(tree));
//				annotatedTrees.add(TreeAnnotations.annotateTreeLosslessBinarization(tree));
			}

			return annotatedTrees;
		}


		public GenerativeParser(List<Tree<String>> trainTrees) {
			System.out.print("Annotating / binarizing training trees ... ");
			List<Tree<String>> annotatedTrainTrees = annotateTrees(trainTrees);
			System.out.println("done.");

			System.out.print("Building grammar ... ");
			grammar = NewGrammar.generativeGrammarFromTrees(annotatedTrainTrees);
			System.out.println("done. (" + grammar.getLabelIndexer().size() + " states)");

			Indexer<String> labelIndexer = grammar.getLabelIndexer();

			unaryClosure = new UnaryClosure(labelIndexer, grammar.getUnaryRules());

			System.out.print("Using grammar and setting up a generative parser ... ");
			// For FeaturizedLexiconDiscriminativeParserFactory, you should construct an instance of your own
			// of LexiconFeaturizer here.
			lexicon = new SimpleLexicon(annotatedTrainTrees);


//			knownParses = new CounterMap<List<String>, Tree<String>>();
//			spanToCategories = new CounterMap<Integer, String>();
//			for (Tree<String> trainTree : annotatedTrainTrees) {
//				List<String> tags = trainTree.getPreTerminalYield();
//				knownParses.incrementCount(tags, trainTree, 1.0);
//				tallySpans(trainTree, 0);
//			}
			System.out.println("done.");
		}


//		private int tallySpans(Tree<String> tree, int start) {
//			if (tree.isLeaf() || tree.isPreTerminal()) return 1;
//			int end = start;
//			for (Tree<String> child : tree.getChildren()) {
//				int childSpan = tallySpans(child, end);
//				end += childSpan;
//			}
//			String category = tree.getLabel();
//			if (!category.equals("ROOT")) spanToCategories.incrementCount(end - start, category, 1.0);
//			return end - start;
//		}
	}
}
