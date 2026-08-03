package ds;

import java.util.Stack;

public class MenuStack extends Stack<String> {

    public String getPath() {
        if (isEmpty()) {
            return "";
        }

        StringBuilder path = new StringBuilder();
        int n = size();
        for (int i = 0; i < n; i++) {
            path.append(get(i));
            if (i != size() - 1) {
                path.append(" > ");
            }
        }

        return path.toString();
    }
}