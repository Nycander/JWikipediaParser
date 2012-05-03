package com.findwise.thesis.qlustr.processing.steps;

import java.util.*;

import com.findwise.thesis.qlustr.processing.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class NgramBuilder extends SplittingStage {
	private final int[] n; // What combinations of terms do we allow?
	
	public NgramBuilder(int n) {
		this.n = new int[] { n };
	}
	
	public NgramBuilder(int... n) {
		this.n = n;
	}

	@Override
	public TextData[] split(TextData... input) {
		List<TextData> result = new ArrayList<TextData>();
		for (int i = 0; i < n.length; i++) {
			if (n[i] == 1) {
				for (TextData d : input)
					d.putField("n", 1);
				result.addAll(Arrays.asList(input));
			} else if (n[i] <= input.length) {
				result.addAll(createNGrams(n[i], input));
			}
		}

		return result.toArray(new TextData[0]);
	}

	protected List<TextData> createNGrams(int n, TextData... input) {
		final List<TextData> out = new ArrayList<TextData>();
		for (int start = 0, end = n; end <= input.length; start++, end++) {
			final String nGram = mergeTextDatas(start, end, input);
			TextData e = new TextData(nGram);
			e.putField("n", n);
			out.add(e);
		}
		return out;
	}

	protected static String mergeTextDatas(int start, int end,
			TextData... input) {
		final StringBuilder sb = new StringBuilder();
		for (int i = start; i < end; i++) {
			sb.append(input[i].getText());
			if (i + 1 < end)
				sb.append(' ');
		}
		return sb.toString();
	}
}
