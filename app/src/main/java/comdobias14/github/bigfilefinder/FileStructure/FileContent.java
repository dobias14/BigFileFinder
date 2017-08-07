package comdobias14.github.bigfilefinder.FileStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class FileContent {

    public static final List<FileItem> ITEMS = new ArrayList<>();

    public static void clearItems(){
        ITEMS.clear();
    }

    public static void addItem(FileItem item) {
        ITEMS.add(item);
    }

    public static FileItem createFileItem(String name, String path, int position, boolean isDirectory) {
        return new FileItem(name, path, position , isDirectory);
    }

    public static class FileItem {
        public final String name;
        public final String path;
        final int position;
        public boolean checked;
        public final boolean isDirectory;

        public FileItem(String name, String path, int position, boolean isDirectory) {
            this.name = name;
            this.path = path;
            this.position = position;
            this.checked = false;
            this.isDirectory = isDirectory;
        }

        @Override
        public String toString() {
            return position +". "+ path+" checked:"+checked+"directory? "+isDirectory;
        }
    }
}
