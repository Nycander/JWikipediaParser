package com.findwise.thesis.qlustr.processing;

import java.util.*;

/**
 * @author Martin Nycander (martin.nycander@gmail.com)
 */
public class Pipeline implements PipelineStage {
	private final List<PipelineStage> steps;
	private final WeakHashMap<PipelineStage, List<TextData>> snapshotLists;
	
	public Pipeline() {
		this(new PipelineStage[0]);
	}
	
	public Pipeline(PipelineStage... steps) {
		this.steps = new ArrayList<PipelineStage>(Arrays.asList(steps));
		this.snapshotLists = new WeakHashMap<PipelineStage, List<TextData>>();
	}

	/**
	 * Adds a processing step to end of this pipeline.
	 * 
	 * @param step the step to add.
	 */
	public void addStep(final PipelineStage step) {
		steps.add(step);
	}
	
	/**
	 * Adds a {@link PipelineStage} and associates it with a snapshot list.
	 * After the step has been run its output will be copied into the snapshot
	 * list, so it can be processed separately.
	 * 
	 * @param step the step to add.
	 * @param snapshot the list to associate with the step.
	 */
	public void
			addStep(final PipelineStage step, final List<TextData> snapshot) {
		if (shouldSnapshot(step)) {
			throw new UnsupportedOperationException("The step '"
					+ step.toString() + "' has already a defined snapshot.");
		}
		addStep(step);
		snapshotLists.put(step, snapshot);
	}
	
	/**
	 * Removes a step from the pipeline, will also remove any associated
	 * snapshot list.
	 * 
	 * @param step the step to remove.
	 */
	public void removeStep(final PipelineStage step) {
		steps.remove(step);
		snapshotLists.remove(step);
	}
	
	/**
	 * Processes a list of {@link TextData} through the pipeline which runs the
	 * input in a chained manner through all added steps in the same order as
	 * they were added.
	 * 
	 * @param input the list of data to process.
	 * @return the processed data.
	 */
	public TextData[] process(final TextData... input) {
		TextData[] data = input;
		for (PipelineStage step : steps) {
			data = step.process(data);
			
			if (shouldSnapshot(step))
				snapshot(step, data);
		}
		return data;
	}

	/**
	 * @param input the list of data to process.
	 * 
	 * @see {@link Pipeline#process(TextData...)}
	 */
	public TextData[] process(final CharSequence... input) {
		TextData[] data = new TextData[input.length];
		for (int i = 0; i < input.length; i++)
			data[i] = new TextData(input[i].toString());
		
		return process(data);
	}
	
	/**
	 * Process a list of {@link String}s through the pipeline, see
	 * {@link Pipeline#process(TextData...)}. This construct should be used when
	 * properties are not relevant and the only expected output is a list of
	 * strings.
	 * 
	 * @param input the list of data to process.
	 * @return the processed data as strings.
	 */
	public List<CharSequence> stringProcess(final CharSequence... input) {
		return new ArrayList<CharSequence>(Arrays.asList(process(input)));
	}

	private boolean shouldSnapshot(final PipelineStage step) {
		return snapshotLists.containsKey(step);
	}
	
	private void snapshot(final PipelineStage step, final TextData[] data) {
		List<TextData> snapShot = new ArrayList<TextData>(data.length);
		for (int i = 0; i < data.length; i++)
			snapShot.add(new TextData(data[i]));

		snapshotLists.get(step).addAll(snapShot);
	}
	
}