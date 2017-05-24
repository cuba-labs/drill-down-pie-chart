package com.company.piechartdrilldown.web.screens;

import com.google.common.collect.ImmutableMap;
import com.haulmont.charts.gui.components.charts.PieChart;
import com.haulmont.charts.gui.data.DataItem;
import com.haulmont.charts.gui.data.DataProvider;
import com.haulmont.charts.gui.data.ListDataProvider;
import com.haulmont.charts.gui.data.MapDataItem;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.*;

public class DrillDownPieChart extends AbstractWindow {
    private static final String TITLE_FIELD = "name";
    private static final String VALUE_FIELD = "population";

    @Inject
    private ComponentsFactory componentsFactory;

    private PieChart pieChart;

    @Override
    public void ready() {
        initChart();

        final Map<DataItem, List<DataItem>> continentsAndCountries = new HashMap<>();

        continentsAndCountries.putAll(createAfricaDataSet());
        continentsAndCountries.putAll(createAsiaDataSet());
        continentsAndCountries.putAll(createEuropeDataSet());

        Set<DataItem> continents = continentsAndCountries.keySet();
        DataProvider continentsDataProvider = new ListDataProvider(new ArrayList<>(continents));

        pieChart.setDataProvider(continentsDataProvider);

        // drill-down on clicking the slice
        pieChart.addSliceClickListener(event -> {
            DataItem selectedSlice = event.getDataItem();
            if (!continents.contains(selectedSlice))
                return;

            DataProvider countriesDataProvider = new ListDataProvider(continentsAndCountries.get(selectedSlice));
            pieChart.setDataProvider(countriesDataProvider);
        });

        // go back on the right-click
        pieChart.addRightClickListener(event -> {
            if (pieChart.getDataProvider() != continentsDataProvider) {
                pieChart.setDataProvider(continentsDataProvider);
            }
        });
    }

    private void initChart() {
        pieChart = (PieChart) componentsFactory.createComponent(PieChart.NAME);
        pieChart.setSizeFull();
        pieChart.setTitleField(TITLE_FIELD);
        pieChart.setValueField(VALUE_FIELD);
        add(pieChart);
    }

    private Map<DataItem, List<DataItem>> createEuropeDataSet() {
        DataItem europe = createPopulationDataItem("Europe", 743);

        DataItem finland = createPopulationDataItem("Finland", 6);
        DataItem germany = createPopulationDataItem("Germany", 82);
        DataItem russia = createPopulationDataItem("Russia", 144);

        return ImmutableMap.of(europe, Arrays.asList(finland, germany, russia));
    }

    private Map<DataItem, List<DataItem>> createAsiaDataSet() {
        DataItem asia = createPopulationDataItem("Asia", 4100);

        DataItem japan = createPopulationDataItem("Japan", 127);
        DataItem india = createPopulationDataItem("India", 1311);
        DataItem china = createPopulationDataItem("China", 1371);

        return ImmutableMap.of(asia, Arrays.asList(japan, india, china));
    }

    private Map<DataItem, List<DataItem>> createAfricaDataSet() {
        DataItem africa = createPopulationDataItem("Africa", 1216);

        DataItem algeria = createPopulationDataItem("Algeria", 40);
        DataItem angola = createPopulationDataItem("Angola", 25);
        DataItem botswana = createPopulationDataItem("Botswana", 3);

        return ImmutableMap.of(africa, Arrays.asList(algeria, angola, botswana));
    }

    private DataItem createPopulationDataItem(String location, int value) {
        return MapDataItem.of(TITLE_FIELD, location, VALUE_FIELD, value);
    }
}