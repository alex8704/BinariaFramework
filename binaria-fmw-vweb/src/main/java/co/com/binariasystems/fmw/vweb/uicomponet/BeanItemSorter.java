package co.com.binariasystems.fmw.vweb.uicomponet;

import com.vaadin.data.Container;

public interface BeanItemSorter {
	void sort(Container.Sortable container, Object[] propertyId, boolean[] ascending);
}
