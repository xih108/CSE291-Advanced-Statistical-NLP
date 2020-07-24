package edu.berkeley.nlp.assignments.assign1.student;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import edu.berkeley.nlp.io.SentenceCollection;
import edu.berkeley.nlp.langmodel.EnglishWordIndexer;
import edu.berkeley.nlp.langmodel.LanguageModelFactory;
import edu.berkeley.nlp.langmodel.NgramLanguageModel;

import javax.crypto.MacSpi;

public class LmFactory implements LanguageModelFactory
{
	static final String STOP = NgramLanguageModel.STOP;
	static final double D = 0.75;
	static int start_index = 0;
	static final double Prob = 1.0/Integer.MAX_VALUE;

	LongIntOpenHashMap trigram = new LongIntOpenHashMap(false);
	LongIntOpenHashMap bigram;

	int[] unigram;
	int[] unifreq;
	int[] unicount;

	public class TrigramModel implements NgramLanguageModel {

		public TrigramModel(Iterable<List<String>> sentenceCollection) {
			System.out.println("Building Kneser-Ney trigramLanguageModel . . .");

			int sent = 0;
			for (List<String> sentence : sentenceCollection) {
				sent++;
				if(sent % 1000000 == 0){
					System.out.println("On sentence "+sent);
				}

				List<String> stoppedSentence = new ArrayList<String>(sentence);
				stoppedSentence.add(0, NgramLanguageModel.START);
				stoppedSentence.add(0, NgramLanguageModel.START);
				stoppedSentence.add(STOP);
				long encode = 0;
				for (int i = 0; i < stoppedSentence.size() - 2; i++) {
					long w3 = EnglishWordIndexer.getIndexer().addAndGetIndex(stoppedSentence.get(i + 2));
					if (i == 0) {
						long w1 = EnglishWordIndexer.getIndexer().addAndGetIndex(stoppedSentence.get(i));
						long w2 = EnglishWordIndexer.getIndexer().addAndGetIndex(stoppedSentence.get(i + 1));
						encode = (w1 << 20) | w2;
					}
					encode = encode << 24 >>> 4 | w3;
					trigram.increment(encode, 1);
				}
			}
			start_index = EnglishWordIndexer.getIndexer().addAndGetIndex(NgramLanguageModel.START);
			System.out.println("Done building Kneser-Ney trigramLanguageModel.");

			bigram = new LongIntOpenHashMap(true);
//			System.out.println("Trigram size:" + trigram.size());

			for (LongIntOpenHashMap.Entry entry : trigram.entrySet()) {

				long key = entry.getKey();
				key = key << 24 >>> 24;
				bigram.increment(key, 1);
			}

			for (LongIntOpenHashMap.Entry entry : trigram.entrySet()) {

				long key = entry.getKey();
				int value = entry.getValue();
				key = key >>> 20;
				int val = bigram.get(key);
				if (val != 0) {
					int denom = value + bigram.getDenom(key);
					bigram.setDenom(key, denom);

					int alpha = bigram.getAlpha(key) + (value > 0 ? 1 : 0);
					bigram.setAlpha(key, alpha);
				}

			}


			unigram = new int[EnglishWordIndexer.getIndexer().size()];
			unifreq = new int[unigram.length];


			for (LongIntOpenHashMap.Entry entry : bigram.entrySet()) {
				long key = entry.getKey();

				key = key << 44 >>> 44;
				unigram[(int) key] += 1;
			}

			unicount = new int[unigram.length];

			for (LongIntOpenHashMap.Entry entry : bigram.entrySet()) {
				long key = entry.getKey();
				int value = entry.getValue();
				key = key >>> 20;
				if (key < unigram.length) {
					unicount[(int) key] += value;
					unifreq[(int) key] += 1;
				}

			}
//			System.out.println("sentence:"+sent);
//			perplexity(sentenceCollection);

		}

		@Override
		public int getOrder() {
			return 3;
		}

		@Override
		public double getNgramLogProbability(int[] ngram, int from, int to) {
			return Math.log(getNgramProbability(ngram, from, to));
		}

		public double getNgramProbability(int[] ngram, int from, int to) {
			// check for <s>
//			if (ngram[from] == start_index) {
//				return getNgramProbability(ngram, from + 1, to);
//			}

			// base case
			if (to - from == 1) {
				int w3 = ngram[from];

				return (w3 < 0 || w3 >= unigram.length) ? Prob : unigram[w3] * 1.0 / bigram.size();


			}

			if (to - from == 2) {
				long w2 = ngram[from];
				long w3 = ngram[from + 1];
				double alpha = 1.0;

				long key = (w2 << 20) | w3;
				int c_w2w3 = bigram.get(key);
				int c_w2v = (w2 >= unigram.length) ? 0 : unicount[(int) w2];
				long alpha_up = (w2 >= unigram.length) ? 0 : unifreq[(int) w2];

				double probN_1 = getNgramProbability(ngram, from + 1, to);
				if (c_w2v != 0) {
					alpha = D * alpha_up / c_w2v;
					probN_1 = Math.max(c_w2w3 - D, 0) / c_w2v + alpha * probN_1;
				}
				return probN_1;
			}

			if (to - from == 3) {
				long w1 = ngram[from];
				long w2 = ngram[from + 1];
				long w3 = ngram[from + 2];
				double alpha = 1.0;

				long key = (w1 << 40) | (w2 << 20) | w3;
				long c_w1w2w3 = trigram.get(key);
				long c_w1w2v = bigram.getDenom(w1 << 20 | w2);
				long alpha_up = bigram.getAlpha(w1 << 20 | w2);

				double probN_1 = getNgramProbability(ngram, from + 1, to);
				if (c_w1w2v != 0) {
					alpha = D * alpha_up / c_w1w2v;
					probN_1 = Math.max(c_w1w2w3 - D, 0) / c_w1w2v + alpha * probN_1;
				}
				return probN_1;
			}
			return 0;
		}


		@Override
		public long getCount(int[] ngram) {
			if (ngram.length == 1) {
				int w = ngram[0];
				int count = 0;

				for (LongIntOpenHashMap.Entry entry : trigram.entrySet()) {
					long key = entry.getKey();
					if ((key << 44 >> 44) == w) {
						count += entry.getValue();
					}
				}
				return count;
			}
			if (ngram.length == 2) {
				long w1 = ngram[0];
				long w2 = ngram[1];
				long key = (w1 << 20) | w2;
				return bigram.getDenom(key);
			}
			if (ngram.length == 3) {
				long w1 = ngram[0];
				long w2 = ngram[1];
				long w3 = ngram[2];
				long key = (w1 << 40) | (w2 << 20) | w3;
				return trigram.get(key);
			}
			return 0;
		}

//		public void perplexity(Iterable<List<String >> sentenceCollection) {
//			String basePath = "./assign1_data";
////			File testFrench = new File(basePath,  "test.fr");
//			File testEnglish= new File(basePath,  "test.en");
////			final String frenchData = (testFrench).getPath();
////			Iterable<List<String>> frenchSentences = SentenceCollection.Reader.readSentenceCollection(frenchData);
//			final String englishData = (testEnglish).getPath();
//			Iterable<List<String>> englishSentences = SentenceCollection.Reader.readSentenceCollection(englishData);
//
////			englishSentences = sentenceCollection;
//			System.out.println("Testing Kneser-Ney trigramLanguageModel . . .");
//
//			int sent = 0;
//			long numword = 0;
//			double sumlogP = 0.0;
//			for (List<String> sentence : englishSentences) {
//				sent++;
////				if (sent < 8000001){ continue;}
////				if (sent == 600001){break;}
//
//				List<String> stoppedSentence = new ArrayList<String>(sentence);
////				stoppedSentence.add(0, NgramLanguageModel.START);
////				stoppedSentence.add(0, NgramLanguageModel.START);
////				stoppedSentence.add(STOP);
//
//				numword += stoppedSentence.size();
////				System.out.println(stoppedSentence.size());
//				int[] ngram = new int[stoppedSentence.size()];
//				for (int i = 0; i < stoppedSentence.size(); i++) {
//					String word = stoppedSentence.get(i);
//					ngram[i] = EnglishWordIndexer.getIndexer().addAndGetIndex(word);
//				}
//
//				for (int i = 0; i < stoppedSentence.size() - 2; i++) {
//					double tmp = 0.0;
//					if (i == 0){
//						tmp = getNgramLogProbability(ngram, i, i + 1);
//					}
//					else if (i == 1){
//						tmp = getNgramLogProbability(ngram, i, i + 2);
//					}
//					else {
//						tmp = getNgramLogProbability(ngram, i, i + 3);
//					}
//
//					sumlogP += tmp;
////					System.out.println(sumlogP);
//				}
//			}
//			System.out.println("*******sumlogP"+sumlogP);
//			System.out.println("*******numword"+numword);
//			double numerator = - sumlogP / Math.log(2.0) / numword;
//			System.out.println("******* numerator"+numerator);
//			double perplexity = Math.pow(2, numerator);
//			System.out.println("Perplexity: " + perplexity);
//
//		}
	}

		/**
		 * Returns a new NgramLanguageModel; this should be an instance of a class that you implement.
		 * Please see edu.berkeley.nlp.langmodel.NgramLanguageModel for the interface specification.
		 *
		 * @param trainingData
		 */
		public NgramLanguageModel newLanguageModel(Iterable<List<String>> trainingData) {
			return new TrigramModel(trainingData); // TODO Construct an exact LM implementation here.

		}


}
