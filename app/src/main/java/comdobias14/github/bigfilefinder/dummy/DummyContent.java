package comdobias14.github.bigfilefinder.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, DummyItem> ITEM_MAP = new HashMap<Integer, DummyItem>();

    private static final int COUNT = 25;
/*
    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }
    */

    public static void ClearItems(){
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.position, item);
    }

    public static DummyItem createDummyItem(String name, String path,int position, boolean checked, boolean isDirectory) {
        return new DummyItem(name, path, position ,checked,isDirectory);
        //return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position),false);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String name;
        public final String path;
        public final int position;
        public boolean checked;
        public boolean isDirectory;

        public DummyItem(String name, String path, int position, boolean checked, boolean isDirectory) {
            this.name = name;
            this.path = path;
            this.position = position;
            this.checked = checked;
            this.isDirectory = isDirectory;
        }

        @Override
        public String toString() {
            return position +". "+ path+" checked:"+checked+"directory? "+isDirectory;
        }
    }
}
