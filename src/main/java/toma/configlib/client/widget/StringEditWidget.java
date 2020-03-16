package toma.configlib.client.widget;

import toma.configlib.config.display.DisplayEntry;

public class StringEditWidget extends EditableWidget<DisplayEntry.Str> {

    public StringEditWidget(int x, int y, int w, int h, String name, DisplayEntry.Str entry) {
        super(x, y, w, h, name, entry, Filters.STRING_FILTER);
    }

    @Override
    public void updateEntry() {
        getEntry().set(text);
    }
}
