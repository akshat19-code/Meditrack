package ds;

import java.util.Stack;

// MenuStack now IS a java.util.Stack<String> - push(), pop(), peek(), isEmpty(),
// and size() are all inherited directly from Stack, so they are not re-written here.
// The only thing this class adds is getPath(), for printing the breadcrumb trail.
public class MenuStack extends Stack<String> {

    // getPath - builds the full breadcrumb, root menu first, current menu last
    // e.g. "MainMenu > AdminMenu > AddTestType"
    // Stack extends Vector, so element at index 0 is the FIRST item pushed (the root
    // menu) and the element at the last index is the TOP of the stack (current menu) -
    // that means we can just walk the Vector forward, no reversing needed.
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