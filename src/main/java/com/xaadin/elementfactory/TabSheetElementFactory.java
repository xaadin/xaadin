package com.xaadin.elementfactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.xaadin.VisualTreeNode;

public class TabSheetElementFactory extends AbstractDefaultLayoutElementFactory {

	@Override
	public void addComponentToParent(VisualTreeNode parent, VisualTreeNode child) throws ElementFactoryException {
		TabSheet sheet = parent.getComponent();
		String caption = child.getAdditionalParameter("TabSheet.caption", "");
		sheet.addTab((Component) child.getComponent(), caption);
	}

	@Override
	public void processEvents(VisualTreeNode child, Object eventHandlerTarget) throws ElementFactoryException {
	}

	@Override
	public boolean isClassSupportedForElementFactory(String classname) {
		return classname.equals(TabSheet.class.getName());
	}
}
