package com.xaadin.elementfactory;

import com.google.common.base.Strings;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.xaadin.VisualTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GridLayoutElementFactory extends AbstractDefaultElementFactory {

	final static Logger logger = LoggerFactory.getLogger(GridLayoutElementFactory.class);

	private int getIntFromVisualTreeNode(String parameterName, VisualTreeNode node) {
		try {
			return Integer.parseInt(node.getAdditionalParameter(parameterName, "-1"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private float[] parseExpandRatios(String ratios) {
		if (Strings.isNullOrEmpty(ratios)) {
			return new float[0];
		}

		StringTokenizer tokenizer = new StringTokenizer(ratios, ",");
		List<String> tokens = new ArrayList<>();
		while (tokenizer.hasMoreElements()) {
			tokens.add(tokenizer.nextToken());
		}
		float[] result = new float[tokens.size()];
		for (int i = 0; i < tokens.size(); i++) {
			try {
				result[i] = Float.parseFloat(tokens.get(i));
			} catch (NumberFormatException e) {
				logger.warn("Could not parse double in GridLayout.expandRatio", e);
				result[i] = 0.f;
			}
		}
		return result;
	}

	public void addComponentToParent(VisualTreeNode parent, VisualTreeNode child) {
		if (!(parent.getComponent() instanceof GridLayout)) {
			throw new IllegalArgumentException("parent is not a descendent of com.vaadin.ui.GridLayout");
		}

		GridLayout gridLayout = parent.getComponent();

		int row = getIntFromVisualTreeNode("GridLayout.row", child);
		int col = getIntFromVisualTreeNode("GridLayout.column", child);
		int rowSpan = getIntFromVisualTreeNode("GridLayout.rowSpan", child);
		int colSpan = getIntFromVisualTreeNode("GridLayout.columnSpan", child);

		// use automatic ordering
		if ((row < 0) && (col < 0)) {
			int currentRow = getIntFromVisualTreeNode("GridLayoutElementFactory.currentRowIndex", parent);
			int currentCol = getIntFromVisualTreeNode("GridLayoutElementFactory.currentColIndex", parent);
			if (currentRow < 0) {
				currentRow = 0;
			}
			if (currentCol < 0) {
				currentCol = 0;
			}

			row = currentRow;
			col = currentCol;

			if (colSpan < 1)
				colSpan = 1;

			for (int i = 0; i < colSpan; i++) {
				currentCol++;
				if (currentCol >= gridLayout.getColumns()) {
					currentCol = 0;
					currentRow++;
				}
			}
			parent.setAdditionalParameter("GridLayoutElementFactory.currentRowIndex", Integer.toString(currentRow));
			parent.setAdditionalParameter("GridLayoutElementFactory.currentColIndex", Integer.toString(currentCol));
		} else {
			if (row < 0)
				row = 0;
			if (col < 0)
				col = 0;
		}

		Alignment alignment = parseAlignment(child.getAdditionalParameter("alignment", ""));

		float[] rowExpandRatios = parseExpandRatios(parent.getAdditionalParameter("rowExpandRatio", ""));
		float[] columnExpandRatios = parseExpandRatios(parent.getAdditionalParameter("columnExpandRatio", ""));

		if (row >= gridLayout.getRows())
			gridLayout.setRows(row + 1);
		if (col >= gridLayout.getColumns())
			gridLayout.setColumns(col + 1);

		Component component = child.getComponent();
		if ((rowSpan > 1) || (colSpan > 1)) {
			gridLayout.addComponent(component, col, row, col + Math.max(0, colSpan - 1), row + Math.max(0, rowSpan - 1));
		} else {
			gridLayout.addComponent(component, col, row);
		}
		gridLayout.setComponentAlignment(component, alignment);

		if (rowExpandRatios.length > row) {
			gridLayout.setRowExpandRatio(row, rowExpandRatios[row]);
		}
		if (columnExpandRatios.length > col) {
			gridLayout.setColumnExpandRatio(col, columnExpandRatios[col]);
		}

	}

	public void processEvents(VisualTreeNode child, Object eventHandlerTarget) {
	}

	public boolean isClassSupportedForElementFactory(String classname) {
		return classname.equals(GridLayout.class.getName());
	}
}
