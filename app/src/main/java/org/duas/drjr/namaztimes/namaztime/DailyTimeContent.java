package org.duas.drjr.namaztimes.namaztime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class DailyTimeContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DailyTime> ITEMS = new ArrayList<DailyTime>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DailyTime> ITEM_MAP = new HashMap<String, DailyTime>();

    private static final int COUNT = 5;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DailyTime item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.title, item);
    }

    private static DailyTime createDummyItem(int position) {
        return new DailyTime(String.valueOf(position), "Item " + position, makeDetails(position));
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
    public static class DailyTime {
        public final String title;
        public final String description;
        public final String time;

        public DailyTime(String title, String description, String time) {
            this.title = title;
            this.description = description;
            this.time = time;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
